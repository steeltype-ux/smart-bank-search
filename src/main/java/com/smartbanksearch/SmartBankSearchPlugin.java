package com.smartbanksearch;

import com.google.inject.Provides;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Non-invasive integration with RuneLite's built-in bank search filter. */
@PluginDescriptor(
    name = "Smart Bank Search",
    description = "Automatic semantic bank categories, equipment slots, combined and category-only searches",
    tags = {"bank", "search", "category", "herb", "gear", "equipment"}
)
public class SmartBankSearchPlugin extends Plugin
{
    private static final Logger LOG = LoggerFactory.getLogger(SmartBankSearchPlugin.class);
    private static final int MAX_CACHE = 4000;

    @Inject private Client client;
    @Inject private ItemManager itemManager;
    @Inject private SmartBankSearchConfig config;
    @Inject private ConfigManager configManager;
    @Inject private ClientToolbar clientToolbar;

    private volatile NavigationButton categoryDirectoryButton;
    private volatile boolean directoryEnabled;

    private String lastQuery = null;
    private boolean lastStrict;
    private CategoryMatcher.Query parsedQuery;
    private int diagnosticsForQuery = 0;

    // Item definitions and stats are stable for a client's lifetime. Avoid recomputing
    // composition / stats for thousands of bank items on every search keystroke.
    private final Map<Integer, ItemFacts> itemCache = new LinkedHashMap<Integer, ItemFacts>(256, 0.75f, true)
    {
        @Override protected boolean removeEldestEntry(Map.Entry<Integer, ItemFacts> entry)
        {
            return size() > MAX_CACHE;
        }
    };

    @Provides
    SmartBankSearchConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(SmartBankSearchConfig.class);
    }

    @Override protected void startUp()
    {
        directoryEnabled = true;
        // One compact sidebar tab; categories are read directly from the
        // existing offline matcher rather than stored in a second catalogue.
        SwingUtilities.invokeLater(() ->
        {
            if (!directoryEnabled) { return; }
            categoryDirectoryButton = NavigationButton.builder()
                .tooltip("Smart Bank Search categories")
                .icon(ImageUtil.loadImageResource(SmartBankSearchPlugin.class,
                    "/smartbanksearch/icon.png"))
                .panel(new CategoryDirectoryPanel(configManager))
                .priority(80)
                .build();
            clientToolbar.addNavigation(categoryDirectoryButton);
        });
        LOG.info("[SmartBankSearch] v1.5 started; {} categories", CategoryMatcher.catalog().size());
    }

    @Override protected void shutDown()
    {
        // The callback may still be queued when a plugin is disabled quickly.
        // Keep it from creating an orphaned sidebar button after shutdown.
        directoryEnabled = false;
        NavigationButton directory = categoryDirectoryButton;
        categoryDirectoryButton = null;
        if (directory != null)
        {
            SwingUtilities.invokeLater(() -> clientToolbar.removeNavigation(directory));
        }
        itemCache.clear();
        parsedQuery = null;
        lastQuery = null;
        LOG.info("[SmartBankSearch] stopped");
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!"bankSearchFilter".equals(event.getEventName()) || !config.enabled())
        {
            return;
        }

        int intSize = client.getIntStackSize();
        int objSize = client.getObjectStackSize();
        if (intSize < 2 || objSize < 1)
        {
            return;
        }
        Object searchObject = client.getObjectStack()[objSize - 1];
        if (!(searchObject instanceof String))
        {
            return;
        }
        String search = (String) searchObject;
        if (!search.equals(lastQuery) || lastStrict != config.strict() || parsedQuery == null)
        {
            lastQuery = search;
            lastStrict = config.strict();
            parsedQuery = CategoryMatcher.parse(search, lastStrict);
            diagnosticsForQuery = 0;
            if (config.diagnostics())
            {
                LOG.info("[SmartBankSearch] query='{}' resolved='{}' exclusive={}",
                    search, parsedQuery.getCategoryName(), parsedQuery.isExclusive());
            }
        }
        if (!parsedQuery.isCategory())
        {
            // Unknown searches, advanced GE/qty filters and Bank Tags are untouched.
            return;
        }

        int[] stack = client.getIntStack();
        int itemId = stack[intSize - 1];
        if (itemId < 0) { return; }
        int previous = stack[intSize - 2];
        ItemFacts item = itemCache.computeIfAbsent(itemId, this::readItem);
        boolean matched = parsedQuery.matches(item);

        if (parsedQuery.isExclusive())
        {
            // Explicit `@` opt-in: suppress native name matches unrelated to category.
            stack[intSize - 2] = matched ? 1 : 0;
        }
        else if (matched)
        {
            // Default is additive, like the successful v1.2 fix: never hide vanilla hits.
            stack[intSize - 2] = 1;
        }

        if (config.diagnostics() && diagnosticsForQuery++ < 25)
        {
            LOG.info("[SmartBankSearch] id={} name='{}' slot={} categoryMatch={} native={} final={}",
                itemId, item.name, item.slot, matched, previous, stack[intSize - 2]);
        }
    }

    private ItemFacts readItem(int id)
    {
        ItemComposition composition = itemManager.getItemComposition(id);
        String name = composition == null ? "" : composition.getName();
        // RuneLite returns null stats for notes. Canonical ID resolves the base item.
        int canonicalId = itemManager.canonicalize(id);
        ItemStats stats = itemManager.getItemStats(canonicalId);
        if (stats == null || stats.getEquipment() == null)
        {
            return ItemFacts.nameOnly(name);
        }
        ItemEquipmentStats gear = stats.getEquipment();
        int attack = Math.max(gear.getAstab(), Math.max(gear.getAslash(), gear.getAcrush()));
        int defence = Math.max(gear.getDstab(), Math.max(gear.getDslash(),
            Math.max(gear.getDcrush(), Math.max(gear.getDrange(), gear.getDmagic()))));
        return new ItemFacts(name, gear.getSlot(), stats.isEquipable(), gear.isTwoHanded(),
            attack, gear.getArange(), gear.getAmagic(), gear.getStr(), gear.getRstr(),
            gear.getMdmg(), gear.getPrayer(), defence);
    }
}
