package com.smartbanksearch;

/** Dependency-free regression suite for 24 OSRS skill searches and activity loadouts. */
public final class CategoryMatcherSkillSelfTest
{
    private static int count = 0;
    private static void check(boolean truth, String message)
    {
        count++;
        if (!truth) { throw new AssertionError(message); }
    }
    private static void yes(String category, String item)
    {
        check(CategoryMatcher.matches(category, item, true), category + " should include " + item);
    }
    private static void no(String category, String item)
    {
        check(!CategoryMatcher.matches(category, item, true), category + " should exclude " + item);
    }
    public static void main(String[] args)
    {
        // All 24 skill names are valid complete category searches, including Sailing.
        String[] skills = { "attack", "strength", "defence", "ranged", "prayer", "magic", "hitpoints",
            "agility", "herblore", "thieving", "crafting", "fletching", "slayer", "hunter",
            "mining", "smithing", "fishing", "cooking", "firemaking", "woodcutting",
            "farming", "construction", "runecrafting", "sailing" };
        for (String skill : skills)
        {
            check(CategoryMatcher.parse(skill, true).isCategory(), "Missing skill search: " + skill);
            check(CategoryMatcher.parse("@" + skill, true).isExclusive(), "Missing @ skill search: " + skill);
        }
        // Skills find equipment and relevant supplies that do not literally include the skill word.
        yes("attack", "Super attack(4)");
        yes("strength", "Berserker ring (i)");
        yes("defence", "Super defence(4)");
        yes("ranged", "Masori chaps");
        yes("prayer", "Superior dragon bones");
        yes("magic", "Occult necklace");
        yes("hitpoints", "Shark");
        yes("agility", "Graceful boots");
        yes("herblore", "Pestle and mortar");
        yes("thieving", "Dodgy necklace");
        yes("crafting", "Uncut ruby");
        yes("fletching", "Knife");
        yes("slayer", "Black mask (i)");
        yes("hunter", "Box trap");
        yes("mining", "Prospector jacket");
        yes("smithing", "Gold bar");
        yes("fishing", "Karambwan vessel");
        yes("cooking", "Cooking gauntlets");
        yes("firemaking", "Tinderbox");
        yes("woodcutting", "Crystal axe");
        yes("farming", "Bottomless compost bucket");
        yes("construction", "Mahogany plank");
        yes("runecrafting", "Pure essence");
        yes("sailing", "Basic repair kit");
        // Additional variants of the newly expanded skilling groups.
        yes("mining", "Expert mining gloves");
        yes("mining", "Celestial ring");
        yes("mining", "Runite ore");
        yes("@mining gear", "Dragon pickaxe");
        yes("@mining gear", "Varrock armour 4");
        no("@mining gear", "Runite ore");
        yes("fletching", "Yew logs");
        yes("fletching", "Yew longbow (u)");
        yes("fletching", "Bow string");
        yes("fletching", "Headless arrow");
        yes("fletching gear", "Knife");
        no("@fletching gear", "Yew logs");
        no("@fletching", "Ranarr potion (unf)");
        yes("sailing", "Steel nails");
        yes("sailing", "Boat schematics");
        yes("sailing", "Bronze salvaging hook");
        yes("sailing", "Rope");
        yes("sailing salvage", "Bronze salvaging hook");
        yes("sailing repair", "Teak plank");
        no("@sailing repair", "Grimy ranarr weed");
        yes("@herb run", "Ranarr seed");
        yes("herb run", "Magic secateurs");
        yes("herb run", "Bottomless compost bucket");
        yes("herb run", "Stony basalt");
        yes("herb run", "Ectophial");
        yes("herb run", "Ardougne cloak 4");
        yes("herblore run", "Snapdragon seed");
        no("@herb run", "Magic seed");
        no("@herb run", "Grimy ranarr weed");
        yes("tree run", "Yew sapling");
        yes("tree run", "Palm tree seed");
        yes("birdhouse run", "Clockwork");
        yes("birdhouse run", "Yew logs");
        no("@birdhouse run", "Bandos chestplate");
        yes("herblore", "Ranarr weed");
        yes("herblore", "Vial of water");
        yes("herblore", "Unfinished potion (unf)");
        no("@herblore", "Rune platebody");
        yes("runecrafting", "Blood rune");
        no("@runecrafting", "Rune platebody");
        no("@herb", "Ranarr seed"); // existing semantics must not regress
        yes("@herb", "Grimy ranarr weed");
        check(!CategoryMatcher.parse("qty > 10", true).isCategory(), "RuneLite qty syntax unchanged");
        check(!CategoryMatcher.parse("tag:herb", true).isCategory(), "Bank Tags syntax unchanged");
        System.out.println("PASS " + count + " skill + loadout regression checks, " + CategoryMatcher.catalog().size() + " categories.");
    }
}
