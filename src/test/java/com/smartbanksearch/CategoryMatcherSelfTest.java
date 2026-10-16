package com.smartbanksearch;

/** Standalone assertions that can run with plain javac/java, no RuneLite dependencies. */
public final class CategoryMatcherSelfTest
{
    private static int tests;
    private static void test(boolean condition, String description)
    {
        tests++;
        if (!condition) { throw new AssertionError("FAILED #" + tests + ": " + description); }
    }
    private static void yes(String query, String item)
    {
        test(CategoryMatcher.matches(query, item, true), query + " -> " + item);
    }
    private static void no(String query, String item)
    {
        test(!CategoryMatcher.matches(query, item, true), query + " -/-> " + item);
    }
    public static void main(String[] args)
    {
        test(CategoryMatcher.catalog().size() >= 120, "at least 120 categories");
        yes("herb", "Grimy ranarr weed"); yes("herb", "Torstol"); yes("herb", "Guam leaf"); no("herb", "Ranarr seed");
        yes("seeds", "Yew sapling"); yes("herb seeds", "Ranarr seed"); no("herb seeds", "Magic seed");
        yes("boots", "Primordial boots"); yes("gloves", "Barrows gloves"); yes("bottoms", "Masori chaps");
        yes("dragon fighting equipment", "Anti-dragon shield"); yes("dragon fighting equipment", "Dragon hunter lance"); no("dragon fighting equipment", "Dragon platelegs");
        yes("ore", "Runite ore"); yes("logs", "Yew logs"); yes("bars", "Gold bar"); yes("runes", "Blood rune"); no("runes", "Rune platebody");
        yes("prayer pots", "Prayer potion(4)"); yes("antipoisons", "Antidote++(4)"); yes("teleport", "Games necklace(8)"); yes("teleport", "Varrock teleport");
        yes("cannon", "Cannonball"); yes("gwd", "Bandos chestplate"); yes("toa", "Osmumten's fang");
        ItemFacts c = new ItemFacts("Masori chaps",7,true,false,0,50,0,0,0,0,0,50);
        test(CategoryMatcher.matches("ranged legs",c,true), "combined ranged legs");
        test(!CategoryMatcher.matches("ranged boots",c,true), "combined excludes unrelated slot");
        ItemFacts p = new ItemFacts("Devout boots",10,true,false,0,0,0,0,0,0,5,2);
        test(CategoryMatcher.matches("@prayer boots",p,true), "exclusive prayer boots");
        test(CategoryMatcher.parse("@rune",true).isExclusive(), "exclusive @rune recognized");
        test(!CategoryMatcher.parse("tag:raid",true).isCategory(), "bank tag syntax untouched");
        test(!CategoryMatcher.parse("ge > 1m",true).isCategory(), "GE search untouched");
        System.out.println("PASS " + tests + " self-tests; " + CategoryMatcher.catalog().size() + " categories available.");
    }
}
