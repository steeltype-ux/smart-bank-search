package com.smartbanksearch;

import org.junit.Test;
import static org.junit.Assert.*;

/** Checks real consumable names against the skill each item boosts. */
public class SkillBoostCoverageTest
{
    private void yes(String query, String item)
    {
        assertTrue(query + " should find " + item, CategoryMatcher.matches(query, item, true));
    }
    private void no(String query, String item)
    {
        assertFalse(query + " must exclude " + item, CategoryMatcher.matches(query, item, true));
    }

    @Test public void combatSkillConsumablesIncludeLowerTiersAndCrossStatEffects()
    {
        yes("@attack", "Attack potion(3)");
        yes("@attack", "Combat potion(4)");
        yes("@attack", "Super combat potion(4)");
        yes("@attack", "Divine super combat potion(4)");
        yes("@strength", "Strength potion(3)");
        yes("@strength", "Combat potion(4)");
        yes("@strength", "Super combat potion(4)");
        yes("@defence", "Defence potion(3)");
        yes("@defence", "Super combat potion(4)");
        yes("@defence", "Saradomin brew(4)");
        no("@defence", "Combat potion(4)");
        no("@attack", "Saradomin brew(4)");
        yes("@ranged", "Ranging potion(4)");
        yes("@ranged", "Ranging mix(2)");
        yes("@ranged", "Bastion potion(4)");
        yes("@magic", "Magic potion(4)");
        yes("@magic", "Magic mix(2)");
        yes("@magic", "Battlemage potion(4)");
        yes("@magic", "Divine battlemage potion(4)");
        yes("@magic", "Ancient brew(4)");
        yes("@magic", "Forgotten brew(4)");
        yes("@magic", "Wizard's mind bomb");
        yes("@magic", "Saturated heart");
    }

    @Test public void productionAndGatheringSkillsFindRealBoosts()
    {
        yes("@mining", "Dwarven stout");
        yes("@mining", "Dwarven stout(m)");
        yes("@mining", "Spicy stew");
        yes("@fishing", "Fishing potion(3)");
        yes("@fishing", "Fishing mix(2)");
        yes("@fishing", "Fish pie");
        yes("@fishing", "Admiral pie");
        yes("@woodcutting", "Spicy stew");
        yes("@farming", "Garden pie");
        yes("@farming", "Spicy stew");
        yes("@herblore", "Botanical pie");
        yes("@herblore", "Spicy stew");
        yes("@hunter", "Hunter potion(4)");
        yes("@hunter", "Spicy stew");
        yes("@thieving", "Spring sq'irkjuice");
        yes("@thieving", "Summer sq'irkjuice");
        yes("@thieving", "Spicy stew");
        yes("@crafting", "Mushroom pie");
        yes("@crafting", "Spicy stew");
        yes("@fletching", "Fletching potion(4)");
        yes("@fletching", "Dragonfruit pie");
        yes("@smithing", "Dwarven stout(m)");
        yes("@smithing", "Spicy stew");
        yes("@construction", "Crystal saw");
        yes("@construction", "Cup of tea");
        yes("@construction", "Spicy stew");
        yes("@runecrafting", "Spicy stew");
        yes("@agility", "Agility potion(4)");
        yes("@agility", "Summer pie");
        yes("@firemaking", "Spicy stew");
        yes("@slayer", "Wild pie");
    }

    @Test public void broadNameFalsePositivesStayOut()
    {
        // A staff is useful in Crafting, not farming. Dragon battleaxe is an
        // Attack/Strength spec weapon, not a potion.
        no("@farming", "Air battlestaff");
        no("@combat pots", "Dragon battleaxe");
        no("@combat pots", "Overload(4)"); // nonbankable NMZ and CoX doses
        no("@fishing", "Raw shark"); // fishing tools/boosts, not raw fish
        no("@hunter", "Rune platebody");
        no("@attack", "Magic potion(4)");
    }
}
