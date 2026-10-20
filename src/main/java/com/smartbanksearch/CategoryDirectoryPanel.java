package com.smartbanksearch;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import net.runelite.client.config.ConfigManager;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.PluginPanel;

/**
 * Small, searchable help panel. No bank actions, network access or account data.
 * Entries come from CategoryMatcher.catalog(), so new aliases appear automatically.
 */
public final class CategoryDirectoryPanel extends PluginPanel
{
    private final CategoryDirectoryModel directory = new CategoryDirectoryModel();
    private final ConfigManager configManager;
    private final Set<String> favorites = new LinkedHashSet<>();
    private final JCheckBox favoritesOnly = new JCheckBox("Favorites only");
    private final JButton favoriteButton = new JButton("Favorite");
    private final JTextField searchField = new JTextField();
    private final DefaultListModel<CategoryDirectoryModel.Entry> results = new DefaultListModel<>();
    private final JList<CategoryDirectoryModel.Entry> categoryList = new JList<>(results);
    private final JLabel countLabel = new JLabel();
    private final JTextArea detail = new JTextArea();

    public CategoryDirectoryPanel(ConfigManager configManager)
    {
        super();
        this.configManager = configManager;
        String saved = configManager.getConfiguration("smartbanksearch", "favoriteCategories");
        if (saved != null && !saved.trim().isEmpty())
        {
            favorites.addAll(Arrays.asList(saved.split("\\|")));
        }
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.add(new JLabel("Find a bank-search category"), BorderLayout.NORTH);
        searchField.setToolTipText("Type a skill, boss, activity, or shortcut, e.g. @gotr");
        top.add(searchField, BorderLayout.CENTER);
        // Stack status and filter vertically so labels never collide in narrow
        // RuneLite sidebars, regardless of font scaling or category count.
        // Stacked controls keep both labels readable on narrow RuneLite sidebars.
        JPanel filters = new JPanel();
        filters.setLayout(new javax.swing.BoxLayout(filters, javax.swing.BoxLayout.Y_AXIS));
        filters.setOpaque(false);
        filters.setBorder(new EmptyBorder(3, 0, 0, 0));
        countLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        favoritesOnly.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        favoritesOnly.setOpaque(false);
        favoritesOnly.setToolTipText("Show only categories you've starred");
        filters.add(countLabel);
        filters.add(javax.swing.Box.createVerticalStrut(5));
        filters.add(favoritesOnly);
        top.add(filters, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public java.awt.Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryDirectoryModel.Entry)
                {
                    CategoryDirectoryModel.Entry entry = (CategoryDirectoryModel.Entry) value;
                    setText(entry.displayLabel(favorites.contains(entry.title)));
                }
                return this;
            }
        });
        categoryList.setVisibleRowCount(25);
        JScrollPane scroll = new JScrollPane(categoryList);
        // Use more of the available RuneLite sidebar height, while leaving
        // the details and Favorite button visible underneath the list.
        scroll.setPreferredSize(new Dimension(200, 560));
        add(scroll, BorderLayout.CENTER);

        detail.setEditable(false);
        detail.setLineWrap(true);
        detail.setWrapStyleWord(true);
        detail.setRows(5);
        detail.setToolTipText("Choose a category to see its search shortcuts");

        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.add(new JScrollPane(detail), BorderLayout.CENTER);
        bottom.add(favoriteButton, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        favoritesOnly.addActionListener(e -> updateResults());
        favoriteButton.addActionListener(e -> toggleFavorite());
        categoryList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting()) { updateDetail(); }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent e) { updateResults(); }
            @Override public void removeUpdate(DocumentEvent e) { updateResults(); }
            @Override public void changedUpdate(DocumentEvent e) { updateResults(); }
        });

        updateResults();
    }

    private void updateResults()
    {
        List<CategoryDirectoryModel.Entry> filtered = new java.util.ArrayList<>(directory.filter(searchField.getText()));
        if (favoritesOnly.isSelected())
        {
            filtered.removeIf(entry -> !favorites.contains(entry.title));
        }
        results.clear();
        for (CategoryDirectoryModel.Entry entry : filtered)
        {
            results.addElement(entry);
        }
        countLabel.setText(filtered.size() + " of " + directory.size() + " categories");
        if (!results.isEmpty())
        {
            categoryList.setSelectedIndex(0);
        }
        else
        {
            updateDetail();
        }
    }

    private void updateDetail()
    {
        CategoryDirectoryModel.Entry selected = categoryList.getSelectedValue();
        favoriteButton.setEnabled(selected != null);
        favoriteButton.setText(selected != null && favorites.contains(selected.title) ? "Unfavorite" : "Favorite");
        if (selected == null)
        {
            detail.setText("No matching categories. Try a shorter word or activity name.");
            return;
        }
        StringBuilder sb = new StringBuilder(selected.title);
        boolean focusedBoost = selected.title.equals("Strength boosts")
            || selected.title.equals("Attack boosts") || selected.title.equals("Defence boosts")
            || selected.title.equals("Magic boosts") || selected.title.equals("Ranged boosts")
            || selected.title.equals("Prayer restores");
        String prefix = focusedBoost ? "" : "@";
        sb.append("\n\nSuggested search: ").append(prefix).append(selected.primaryAlias);
        sb.append("\n\nOther shortcuts:");
        for (String alias : selected.aliases)
        {
            if (!alias.equals(selected.primaryAlias))
            {
                sb.append("\n").append(prefix).append(alias);
            }
        }
        if (focusedBoost)
        {
            String skill = selected.title.split(" ")[0].toLowerCase(java.util.Locale.ROOT);
            sb.append("\n\nUse @").append(skill)
                .append(" for the broad category (equipment and other useful items).");
        }
        else
        {
            sb.append("\n\n@ shows category-only results. Without @, normal bank searches remain additive.");
        }
        sb.append("\n\n").append(CategoryDescriptions.forTitle(selected.title));
        detail.setText(sb.toString());
        detail.setCaretPosition(0);
    }

    private void toggleFavorite()
    {
        CategoryDirectoryModel.Entry selected = categoryList.getSelectedValue();
        if (selected == null) { return; }
        if (!favorites.add(selected.title)) { favorites.remove(selected.title); }
        configManager.setConfiguration("smartbanksearch", "favoriteCategories",
            String.join("|", favorites));
        if (favoritesOnly.isSelected()) { updateResults(); }
        else
        {
            categoryList.repaint(); // Refresh star immediately without changing selection.
            updateDetail();
        }
    }
}
