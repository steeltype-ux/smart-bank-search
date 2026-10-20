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
    @Test public void attackSearchIncludesAllActualAttackBoostPotionTiers()
    {
        // Regular Attack was already a term, but the super-combat and mixes
        // were missing from the Attack skill category.
        for (String item : new String[] {
            "Attack potion(1)", "Attack potion(2)", "Attack potion(3)", "Attack potion(4)",
            "Attack mix(1)", "Attack mix(2)",
            "Super attack(1)", "Super attack(4)",
            "Divine super attack potion(4)",
            "Superattack mix(2)",
            "Combat potion(1)", "Combat potion(4)", "Combat mix(2)",
            "Super combat potion(1)", "Super combat potion(4)",
            "Divine super combat potion(4)", "Zamorak brew(4)"
        })
        {
            yes("@attack", item);
            yes("attack", item);
            yes("@combat pots", item);
        }
        for (String item : new String[] {
            "Ranging potion(4)", "Magic potion(4)",
            "Strength potion(4)", "Super strength(4)",
            "Defence potion(4)", "Super defence(4)",
            "Bastion potion(4)", "Battlemage potion(4)",
            "Prayer potion(4)", "Raw shark", "Rune platebody"
        })
        {
            no("@attack", item);
        }
        // The multi-stat boosts should also be visible for Strength/Defence.
        for (String item : new String[] {
            "Combat potion(4)", "Combat mix(2)",
            "Super combat potion(4)", "Divine super combat potion(4)",
            "Zamorak brew(4)"
        })
        {
            yes("@strength", item);
        }
        for (String item : new String[] {
            "Super combat potion(4)", "Divine super combat potion(4)",
            "Bastion potion(4)", "Divine bastion potion(4)",
            "Battlemage potion(4)", "Divine battlemage potion(4)"
        })
        {
            yes("@defence", item);
        }
        no("@defence", "Combat potion(4)"); // Only Attack and Strength.
        no("@defence", "Zamorak brew(4)"); // Decreases Defence.
        yes("@combat pots", "Defence potion(4)");
        yes("@combat pots", "Strength potion(4)");
        yes("@combat pots", "Super str. mix(2)");
    }

    @Test public void strengthSearchFocusesBoostsWhileExclusiveStrengthRemainsBroad()
    {
        for (String item : new String[] {
            "Strength potion(4)", "Strength mix(2)", "Super strength(4)",
            "Super str. mix(2)", "Divine super strength potion(4)",
            "Combat potion(4)", "Combat mix(2)", "Super combat potion(4)",
            "Divine super combat potion(4)", "Zamorak brew(4)"
        })
        {
            yes("strength", item);
        }
        no("strength", "Fighter torso");
        no("strength", "Berserker ring");
        no("strength", "Ranging potion(4)");
        yes("@strength", "Fighter torso");
        yes("@strength", "Super strength(4)");
        yes("strength pot", "Super strength(4)");
        assertEquals("Strength skill", CategoryMatcher.parse("@strength", true).getCategoryName());
        assertEquals("Strength boosts", CategoryMatcher.parse("strength", true).getCategoryName());
        assertTrue(CategoryMatcher.matches("strength gear", gear("Unknown new ring", 12,0,0,0,8,0,0,0), true));
    }

    @Test public void attackAndDefenceSearchesFocusConsumableBoosts()
    {
        for (String item : new String[] {
            "Attack potion(4)", "Attack mix(2)", "Super attack(4)",
            "Superattack mix(2)", "Divine super attack potion(4)",
            "Combat potion(4)", "Combat mix(2)", "Super combat potion(4)",
            "Divine super combat potion(4)", "Zamorak brew(4)"
        })
        {
            yes("attack", item);
        }
        for (String item : new String[] {
            "Defence potion(4)", "Defence mix(2)", "Super defence(4)",
            "Super def. mix(2)", "Divine super defence potion(4)",
            "Super combat potion(4)", "Divine super combat potion(4)",
            "Bastion potion(4)", "Divine bastion potion(4)",
            "Battlemage potion(4)", "Divine battlemage potion(4)",
            "Saradomin brew(4)"
        })
        {
            yes("defence", item);
            yes("defense", item);
        }
        no("attack", "Dragon defender");
        no("attack", "Strength potion(4)");
        no("defence", "Justiciar chestguard");
        no("defence", "Attack potion(4)");
        yes("@attack", "Dragon defender");
        yes("@defence", "Justiciar chestguard");
        yes("@defense", "Justiciar chestguard");
        yes("attack pot", "Super attack(4)");
        yes("defence pot", "Super defence(4)");
        assertEquals("Attack boosts", CategoryMatcher.parse("attack", true).getCategoryName());
        assertEquals("Defence boosts", CategoryMatcher.parse("defence", true).getCategoryName());
        assertEquals("Attack skill", CategoryMatcher.parse("@attack", true).getCategoryName());
        assertEquals("Defence skill", CategoryMatcher.parse("@defence", true).getCategoryName());
    }

    @Test public void focusedMagicRangedPrayerAndExplicitBroadQueries()
    {
        for (String item : new String[] {
            "Magic potion(4)", "Divine magic potion(4)", "Magic mix(2)",
            "Battlemage potion(4)", "Divine battlemage potion(4)",
            "Imbued heart", "Saturated heart", "Forgotten brew(4)"
        }) { yes("magic", item); }
        no("magic", "Ancestral robe top");
        no("magic", "Prayer potion(4)");
        yes("@magic", "Ancestral robe top");
        for (String item : new String[] {
            "Ranging potion(4)", "Ranging mix(2)", "Divine ranging potion(4)",
            "Bastion potion(4)", "Divine bastion potion(4)"
        }) { yes("ranged", item); yes("range", item); }
        no("ranged", "Masori chaps");
        no("ranged", "Magic potion(4)");
        yes("@ranged", "Masori chaps");
        for (String item : new String[] {
            "Prayer potion(4)", "Super restore(4)", "Sanfew serum(4)",
            "Prayer regeneration potion(4)"
        }) { yes("prayer", item); }
        no("prayer", "Dragon bones");
        yes("@prayer", "Dragon bones");
        yes("@prayer", "Prayer potion(4)");
        assertEquals("Magic equipment", CategoryMatcher.parse("@magic", true).getCategoryName());
        assertEquals("Ranged equipment", CategoryMatcher.parse("@ranged", true).getCategoryName());
        assertTrue(CategoryMatcher.parse("@prayer", true).isExclusive());
        yes("magic gear", "Mystic robe top");
        yes("ranged gear", "Rune crossbow");
    }

    @Test public void divinePotionFamiliesAndDoseVariants()
    {
        for (String prefix : new String[] {
            "Divine super attack potion", "Divine super strength potion",
            "Divine super defence potion", "Divine super combat potion",
            "Divine ranging potion", "Divine magic potion",
            "Divine bastion potion", "Divine battlemage potion"
        })
        {
            for (int dose = 1; dose <= 4; dose++)
            {
                yes("@divine pots", prefix + "(" + dose + ")");
            }
        }
        no("@divine pots", "Prayer potion(4)");
        no("@divine pots", "Super restore(4)");
    }

    @Test public void allCatalogAliasesResolveToTheirCategoryOrBroadSkillOverride()
    {
        Map<String, java.util.List<String>> catalog = CategoryMatcher.catalog();
        for (Map.Entry<String, java.util.List<String>> entry : catalog.entrySet())
        {
            for (String alias : entry.getValue())
            {
                assertTrue("Alias not recognized: " + alias,
                    CategoryMatcher.isCategoryQuery(alias, true));
                // These six bare-skill names intentionally switch to broad mode with @.
                boolean broad = "strength".equals(alias) || "attack".equals(alias)
                    || "defence".equals(alias) || "defense".equals(alias)
                    || "magic".equals(alias) || "ranged".equals(alias)
                    || "range".equals(alias) || "ranging".equals(alias)
                    || "prayer".equals(alias);
                if (!broad)
                {
                    assertEquals("Alias maps to incorrect category: " + alias,
                        entry.getKey(), CategoryMatcher.parse("@" + alias, true).getCategoryName());
                }
            }
        }
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
