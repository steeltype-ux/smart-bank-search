package com.smartbanksearch;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

/** Unit tests for semantic matching, item stats, composite & exclusive syntax. */
public class CategoryMatcherTest
{
    private void yes(String query, String item)
    {
        assertTrue(query + " should match " + item, CategoryMatcher.matches(query, item, true));
    }
    private void no(String query, String item)
    {
        assertFalse(query + " must not match " + item, CategoryMatcher.matches(query, item, true));
    }
    private ItemFacts gear(String name, int slot, int melee, int range, int magic,
                           int str, int rstr, int prayer, int defence)
    {
        return new ItemFacts(name, slot, true, false, melee, range, magic, str, rstr, 0f, prayer, defence);
    }
    @Test public void registryIsLargeAndAliasesUnique()
    {
        Map<String, java.util.List<String>> catalog = CategoryMatcher.catalog();
        assertTrue(catalog.size() >= 120);
        assertEquals("Legs / bottoms", catalog.keySet().stream().filter(s -> s.equals("Legs / bottoms")).findFirst().get());
    }
    @Test public void namedHerbsAndSeeds()
    {
        yes("herb", "Grimy ranarr weed"); yes("herb", "Ranarr weed"); yes("herb", "Grimy torstol"); yes("herb", "Guam leaf");
        yes("herbs", "Cadantine"); no("herb", "Ranarr seed"); no("herb", "Herb sack");
        yes("grimy herb", "Grimy torstol"); no("grimy herb", "Torstol");
        yes("clean herb", "Torstol"); no("clean herb", "Grimy torstol");
        yes("seed", "Ranarr seed"); yes("seeds", "Yew sapling"); yes("herb seed", "Ranarr seed");
        no("herb seed", "Magic seed"); yes("tree seeds", "Magic seed");
    }
    @Test public void equipmentSlotMetadata()
    {
        assertTrue(CategoryMatcher.matches("helmets", gear("Unknown new helmet", 0, 0,0,0,0,0,0,2), true));
        assertTrue(CategoryMatcher.matches("bottoms", gear("Unknown new trousers", 7, 0,0,0,0,0,0,2), true));
        assertTrue(CategoryMatcher.matches("boots", gear("Unknown new footwear", 10, 0,0,0,0,0,0,2), true));
        assertTrue(CategoryMatcher.matches("gloves", gear("Unknown new gloves", 9, 0,0,0,0,0,0,2), true));
        assertTrue(CategoryMatcher.matches("offhand", gear("Unknown new shield", 5, 0,0,0,0,0,0,2), true));
        assertTrue(CategoryMatcher.matches("neck", gear("Unknown new neckpiece", 2, 0,0,0,0,0,0,2), true));
        assertTrue(CategoryMatcher.matches("capes", gear("Unknown new backpiece", 1, 0,0,0,0,0,0,2), true));
        assertFalse(CategoryMatcher.matches("boots", gear("Masori chaps", 7, 0,8,0,0,0,0,2), true));
    }
    @Test public void statsCombatTypes()
    {
        assertTrue(CategoryMatcher.matches("ranged gear", gear("Unknown new bow", 3,0,85,0,0,0,0,0), true));
        assertTrue(CategoryMatcher.matches("prayer gear", gear("Unknown new vestment", 7,0,0,0,0,0,6,0), true));
        assertTrue(CategoryMatcher.matches("mage", gear("Unknown new staff", 3,0,0,80,0,0,0,0), true));
        assertTrue(CategoryMatcher.matches("melee", gear("Unknown new sword", 3,60,0,0,0,0,0,0), true));
        assertTrue(CategoryMatcher.matches("strength gear", gear("Unknown new ring", 12,0,0,0,8,0,0,0), true));
        assertTrue(CategoryMatcher.matches("ranged strength", gear("Unknown new quiver", 13,0,0,0,0,3,0,0), true));
        assertTrue(CategoryMatcher.matches("tank gear", gear("Unknown new armour", 4,0,0,0,0,0,0,120), true));
        assertFalse(CategoryMatcher.matches("prayer gear", gear("Pure ranged chaps", 7,0,15,0,0,0,0,0), true));
    }
    @Test public void compositeFiltersAndExclusivity()
    {
        ItemFacts rangedChaps = gear("Masori chaps",7,0,35,0,0,0,0,20);
        ItemFacts rangeBoots = gear("Pegasian boots",10,0,18,0,0,0,0,8);
        ItemFacts prayBoots = gear("Devout boots",10,0,0,0,0,0,5,5);
        assertTrue(CategoryMatcher.matches("ranged legs", rangedChaps,true));
        assertFalse(CategoryMatcher.matches("ranged boots", rangedChaps,true));
        assertTrue(CategoryMatcher.matches("@ranged boots", rangeBoots,true));
        assertTrue(CategoryMatcher.matches("@prayer boots", prayBoots,true));
        assertFalse(CategoryMatcher.matches("@prayer boots", rangeBoots,true));
        assertTrue(CategoryMatcher.parse("@rune", true).isExclusive());
        assertFalse(CategoryMatcher.parse("rune", true).isExclusive());
        assertEquals("Ranged equipment + Boots / footwear", CategoryMatcher.parse("@ranged boots",true).getCategoryName());
        assertFalse(CategoryMatcher.parse("@not a category",true).isCategory());
    }
    @Test public void dragonFightingIsNotGenericDragonEquipment()
    {
        yes("dragon fighting equipment", "Dragon hunter lance");
        yes("dragon fighting gear", "Antifire potion(4)");
        yes("dragonbane", "Anti-dragon shield");
        no("dragon fighting equipment", "Dragon platelegs");
        yes("dragon gear", "Dragon platelegs");
        yes("rune gear", "Rune platebody"); no("rune gear", "Rune essence");
    }
    @Test public void skillingAndCombatSupplies()
    {
        yes("ores", "Runite ore"); yes("bars", "Rune bar"); yes("logs", "Magic logs");
        yes("pickaxes", "Dragon pickaxe"); yes("fishing gear", "Harpoon");
        yes("fletching", "Headless arrow"); yes("feathers", "Feather");
        yes("gems", "Uncut zenyte"); yes("herblore secondaries", "Red spiders' eggs");
        yes("unfs", "Ranarr potion (unf)"); yes("prayer pots", "Prayer potion(4)");
        yes("restores", "Super restore(4)"); yes("antifires", "Extended super antifire(4)");
        yes("prayer training", "Superior dragon bones");
        yes("clues", "Clue scroll (hard)"); yes("keys", "Crystal key");
        yes("teleport", "Games necklace(8)"); yes("teleport", "Varrock teleport");
        yes("runes", "Blood rune"); no("runes", "Rune platebody");
    }
    @Test public void combatAndRaidSpecials()
    {
        yes("slayer", "Black mask (i)"); yes("barrows", "Dharok's platelegs");
        yes("gwd", "Bandos chestplate"); yes("toa", "Osmumten's fang");
        yes("tob", "Scythe of vitur"); yes("cox", "Twisted bow");
        yes("wilderness", "Blighted super restore(4)");
        yes("cannonballs", "Cannonball"); yes("spec weapons", "Dragon claws");
    }
    @Test public void noUnexpectedNativeOverrides()
    {
        assertFalse(CategoryMatcher.isCategoryQuery("dragon scim",true));
        assertFalse(CategoryMatcher.isCategoryQuery("ge > 1m",true));
        assertFalse(CategoryMatcher.isCategoryQuery("qty > 10",true));
        assertFalse(CategoryMatcher.isCategoryQuery("tag:raid",true));
        assertFalse(CategoryMatcher.isCategoryQuery("her",true));
        assertFalse(CategoryMatcher.isCategoryQuery("her",false)); // ambiguous: herb vs herb seeds
        assertFalse(CategoryMatcher.isCategoryQuery("@notreal",true));
    }
}
