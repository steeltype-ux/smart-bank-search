package com.smartbanksearch;
import java.util.HashSet;
import java.util.Set;

/** Run with JUnit/Gradle or plain javac/java to verify the v1.5 activity catalog. */
public final class ActivityCategoriesSelfTest
{
    private static int checks;
    private static void check(boolean value, String message)
    {
        checks++;
        if (!value) { throw new AssertionError(message); }
    }
    private static void yes(String query, String item)
    {
        check(CategoryMatcher.matches(query, item, true), query + " should include " + item);
    }
    private static void no(String query, String item)
    {
        check(!CategoryMatcher.matches(query, item, true), query + " should exclude " + item);
    }
    public static void runAll()
    {
        checks = 0;
        int[] count = {0};
        Set<String> aliases = new HashSet<>();
        ActivityCategories.register((title, predicate, names) -> {
            count[0]++;
            for (String alias : names)
            {
                check(aliases.add(alias), "Duplicate new alias: " + alias);
                CategoryMatcher.Query q = CategoryMatcher.parse("@" + alias, true);
                check(q.isExclusive(), "Unregistered alias: " + alias);
                check(q.getCategoryName().equals(title), "Incorrect alias owner: " + alias);
            }
        });
        check(count[0] == 67, "Expected 67 new activities");
        check(CategoryMatcher.catalog().size() == 208, "Expected 208 total categories");
        yes("@herb run", "Ranarr seed");
        yes("@herbrun", "Ranarr seed");
        yes("@herbruns", "Bottomless compost bucket");
        yes("herbrun", "Ectophial");
        no("@herbrun", "Magic seed");
        no("@herbrun", "Grimy ranarr weed");
        check(CategoryMatcher.parse("@herbrun", true).getCategoryName()
            .equals(CategoryMatcher.parse("@herb run", true).getCategoryName()), "herbrun must use existing kit");

        // Two curated inclusions and negative controls for all 67 activity kits.
        yes("@zulrah", "anti venom"); yes("@zulrah", "antivenom"); no("@zulrah", "Grimy ranarr weed"); no("@zulrah", "Rune platebody");
        yes("@vorkath", "dragon hunter crossbow"); yes("@vorkath", "dragon hunter lance"); no("@vorkath", "Grimy ranarr weed"); no("@vorkath", "Rune platebody");
        yes("@muspah", "sapphire dragon bolts"); yes("@muspah", "sapphire bolts"); no("@muspah", "Grimy ranarr weed"); no("@muspah", "Rune platebody");
        yes("@jad", "toxic blowpipe"); yes("@jad", "dragon dart"); no("@jad", "Grimy ranarr weed"); no("@jad", "Rune platebody");
        yes("@inferno", "twisted bow"); yes("@inferno", "bow of faerdhinen"); no("@inferno", "Grimy ranarr weed"); no("@inferno", "Rune platebody");
        yes("@nex", "zaryte crossbow"); yes("@nex", "armadyl crossbow"); no("@nex", "Grimy ranarr weed"); no("@nex", "Rune platebody");
        yes("@nightmare boss", "inquisitor"); yes("@nightmare boss", "bludgeon"); no("@nightmare boss", "Grimy ranarr weed"); no("@nightmare boss", "Rune platebody");
        yes("@cerberus", "primordial crystal"); yes("@cerberus", "pegasian crystal"); no("@cerberus", "Grimy ranarr weed"); no("@cerberus", "Rune platebody");
        yes("@hydra boss", "dragon hunter crossbow"); yes("@hydra boss", "dragon hunter lance"); no("@hydra boss", "Grimy ranarr weed"); no("@hydra boss", "Rune platebody");
        yes("@scurrius", "bone mace"); yes("@scurrius", "bone shortbow"); no("@scurrius", "Grimy ranarr weed"); no("@scurrius", "Rune platebody");
        yes("@cox prep", "dragon hunter lance"); yes("@cox prep", "dragon hunter crossbow"); no("@cox prep", "Grimy ranarr weed"); no("@cox prep", "Rune platebody");
        yes("@tob prep", "scythe of vitur"); yes("@tob prep", "sanguinesti staff"); no("@tob prep", "Grimy ranarr weed"); no("@tob prep", "Rune platebody");
        yes("@toa prep", "osmumten's fang"); yes("@toa prep", "keris partisan"); no("@toa prep", "Grimy ranarr weed"); no("@toa prep", "Rune platebody");
        yes("@barrows prep", "barrows teleport"); yes("@barrows prep", "drakan's medallion"); no("@barrows prep", "Grimy ranarr weed"); no("@barrows prep", "Rune platebody");
        yes("@gwd prep", "ecumenical key"); yes("@gwd prep", "frozen key"); no("@gwd prep", "Grimy ranarr weed"); no("@gwd prep", "Rune platebody");
        yes("@gargoyles", "rock hammer"); yes("@gargoyles", "rock thrownhammer"); yes("@gargoyles", "Dual macuahuitl"); no("@gargoyles", "Grimy ranarr weed"); no("@gargoyles", "Rune platebody");
        yes("@dust devils", "facemask"); yes("@dust devils", "masked earmuffs"); no("@dust devils", "Grimy ranarr weed"); no("@dust devils", "Rune platebody");
        yes("@nechryaels", "slayer helmet"); yes("@nechryaels", "black mask"); no("@nechryaels", "Grimy ranarr weed"); no("@nechryaels", "Rune platebody");
        yes("@aberrant spectres", "nose peg"); yes("@aberrant spectres", "slayer helmet"); no("@aberrant spectres", "Grimy ranarr weed"); no("@aberrant spectres", "Rune platebody");
        yes("@basilisks", "mirror shield"); yes("@basilisks", "v's shield"); no("@basilisks", "Grimy ranarr weed"); no("@basilisks", "Rune platebody");
        yes("@kurasks", "leaf bladed sword"); yes("@kurasks", "leaf bladed axe"); no("@kurasks", "Grimy ranarr weed"); no("@kurasks", "Rune platebody");
        yes("@skeletal wyverns", "elemental shield"); yes("@skeletal wyverns", "mind shield"); no("@skeletal wyverns", "Grimy ranarr weed"); no("@skeletal wyverns", "Rune platebody");
        yes("@abyssal demons", "arclight"); yes("@abyssal demons", "emberlight"); no("@abyssal demons", "Grimy ranarr weed"); no("@abyssal demons", "Rune platebody");
        yes("@demonic gorillas", "arclight"); yes("@demonic gorillas", "emberlight"); no("@demonic gorillas", "Grimy ranarr weed"); no("@demonic gorillas", "Rune platebody");
        yes("@barrage tasks", "ancient staff"); yes("@barrage tasks", "ancient sceptre"); no("@barrage tasks", "Grimy ranarr weed"); no("@barrage tasks", "Rune platebody");
        yes("@cannon tasks", "cannonball"); yes("@cannon tasks", "dwarf multicannon"); no("@cannon tasks", "Grimy ranarr weed"); no("@cannon tasks", "Rune platebody");
        yes("@kraken boss", "trident of the seas"); yes("@kraken boss", "trident of the swamp"); no("@kraken boss", "Grimy ranarr weed"); no("@kraken boss", "Rune platebody");
        yes("@wintertodt", "tinderbox"); yes("@wintertodt", "bruma torch"); no("@wintertodt", "Grimy ranarr weed"); no("@wintertodt", "Rune platebody");
        yes("@tempoross", "harpoon"); yes("@tempoross", "hammer"); no("@tempoross", "Grimy ranarr weed"); no("@tempoross", "Rune platebody");
        yes("@gotr", "pickaxe"); yes("@gotr", "chisel"); no("@gotr", "Grimy ranarr weed"); no("@gotr", "Rune platebody");
        yes("@giants foundry", "bronze bar"); yes("@giants foundry", "iron bar"); no("@giants foundry", "Grimy ranarr weed");
        yes("@mahogany homes", "plank"); yes("@mahogany homes", "oak plank"); no("@mahogany homes", "Grimy ranarr weed"); no("@mahogany homes", "Rune platebody");
        yes("@hallowed sepulchre", "hallowed grapple"); yes("@hallowed sepulchre", "hallowed hammer"); no("@hallowed sepulchre", "Grimy ranarr weed"); no("@hallowed sepulchre", "Rune platebody");
        yes("@pyramid plunder", "pharaoh's sceptre"); yes("@pyramid plunder", "lockpick"); no("@pyramid plunder", "Grimy ranarr weed"); no("@pyramid plunder", "Rune platebody");
        yes("@tithe farm", "gricoller's can"); yes("@tithe farm", "watering can"); no("@tithe farm", "Grimy ranarr weed"); no("@tithe farm", "Rune platebody");
        yes("@pest control", "void knight"); yes("@pest control", "elite void"); no("@pest control", "Grimy ranarr weed"); no("@pest control", "Rune platebody");
        yes("@barbarian assault", "fighter torso"); yes("@barbarian assault", "granite body"); no("@barbarian assault", "Grimy ranarr weed"); no("@barbarian assault", "Rune platebody");
        yes("@soul wars", "soul cape"); yes("@soul wars", "soul wars"); no("@soul wars", "Grimy ranarr weed"); no("@soul wars", "Rune platebody");
        yes("@nightmare zone", "dwarven rock cake"); yes("@nightmare zone", "locator orb"); no("@nightmare zone", "Grimy ranarr weed"); no("@nightmare zone", "Rune platebody");
        yes("@shades of mortton", "olive oil"); yes("@shades of mortton", "sacred oil"); no("@shades of mortton", "Grimy ranarr weed"); no("@shades of mortton", "Rune platebody");
        yes("@fishing trawler", "swamp paste"); yes("@fishing trawler", "rope"); no("@fishing trawler", "Grimy ranarr weed"); no("@fishing trawler", "Rune platebody");
        yes("@volcanic mine", "pickaxe"); yes("@volcanic mine", "dragon pickaxe"); no("@volcanic mine", "Grimy ranarr weed"); no("@volcanic mine", "Rune platebody");
        yes("@blast furnace", "iron ore"); yes("@blast furnace", "gold ore"); no("@blast furnace", "Grimy ranarr weed"); no("@blast furnace", "Rune platebody");
        yes("@farming contracts", "spade"); yes("@farming contracts", "rake"); no("@farming contracts", "Grimy ranarr weed"); no("@farming contracts", "Rune platebody");
        yes("@giant seaweed", "seaweed spore"); yes("@giant seaweed", "giant seaweed"); no("@giant seaweed", "Grimy ranarr weed"); no("@giant seaweed", "Rune platebody");
        yes("@motherlode mine", "pickaxe"); yes("@motherlode mine", "prospector"); no("@motherlode mine", "Grimy ranarr weed"); no("@motherlode mine", "Rune platebody");
        yes("@bird snaring", "bird snare"); yes("@bird snaring", "hunter cape"); no("@bird snaring", "Grimy ranarr weed"); no("@bird snaring", "Rune platebody");
        yes("@chinchompa hunting", "box trap"); yes("@chinchompa hunting", "chinchompa"); no("@chinchompa hunting", "Grimy ranarr weed"); no("@chinchompa hunting", "Rune platebody");
        yes("@revenants", "bracelet of ethereum"); yes("@revenants", "revenant ether"); no("@revenants", "Grimy ranarr weed"); no("@revenants", "Rune platebody");
        yes("@calvarion", "ursine chainmace"); yes("@calvarion", "viggora's chainmace"); no("@calvarion", "Grimy ranarr weed"); no("@calvarion", "Rune platebody");
        yes("@spindel", "ursine chainmace"); yes("@spindel", "viggora's chainmace"); no("@spindel", "Grimy ranarr weed"); no("@spindel", "Rune platebody");
        yes("@artio", "webweaver bow"); yes("@artio", "craw's bow"); no("@artio", "Grimy ranarr weed"); no("@artio", "Rune platebody");
        yes("@chaos elemental", "webweaver bow"); yes("@chaos elemental", "craw's bow"); no("@chaos elemental", "Grimy ranarr weed"); no("@chaos elemental", "Rune platebody");
        yes("@chaos altar", "dragon bones"); yes("@chaos altar", "superior dragon bones"); no("@chaos altar", "Grimy ranarr weed"); no("@chaos altar", "Rune platebody");
        yes("@wildy bosses", "webweaver bow"); yes("@wildy bosses", "craw's bow"); no("@wildy bosses", "Grimy ranarr weed"); no("@wildy bosses", "Rune platebody");
        yes("@wildy escape", "royal seed pod"); yes("@wildy escape", "amulet of glory"); no("@wildy escape", "Grimy ranarr weed"); no("@wildy escape", "Rune platebody");
        yes("@anti pk", "dragon claws"); yes("@anti pk", "voidwaker"); no("@anti pk", "Grimy ranarr weed"); no("@anti pk", "Rune platebody");
        yes("@clue kit", "spade"); yes("@clue kit", "clue scroll"); no("@clue kit", "Grimy ranarr weed"); no("@clue kit", "Rune platebody");
        yes("@easy clue kit", "clue scroll (easy)"); yes("@easy clue kit", "reward casket (easy)"); no("@easy clue kit", "Grimy ranarr weed"); no("@easy clue kit", "Rune platebody");
        yes("@medium clue kit", "clue scroll (medium)"); yes("@medium clue kit", "reward casket (medium)"); no("@medium clue kit", "Grimy ranarr weed"); no("@medium clue kit", "Rune platebody");
        yes("@hard clue kit", "clue scroll (hard)"); yes("@hard clue kit", "reward casket (hard)"); no("@hard clue kit", "Grimy ranarr weed"); no("@hard clue kit", "Rune platebody");
        yes("@elite clue kit", "clue scroll (elite)"); yes("@elite clue kit", "reward casket (elite)"); no("@elite clue kit", "Grimy ranarr weed"); no("@elite clue kit", "Rune platebody");
        yes("@master clue kit", "clue scroll (master)"); yes("@master clue kit", "reward casket (master)"); no("@master clue kit", "Grimy ranarr weed"); no("@master clue kit", "Rune platebody");
        yes("@quest prep", "rope"); yes("@quest prep", "spade"); no("@quest prep", "Grimy ranarr weed"); no("@quest prep", "Rune platebody");
        yes("@quest travel", "teleport to house"); yes("@quest travel", "varrock teleport"); no("@quest travel", "Grimy ranarr weed"); no("@quest travel", "Rune platebody");
        yes("@quest combat", "rune pouch"); yes("@quest combat", "anti dragon shield"); no("@quest combat", "Grimy ranarr weed"); no("@quest combat", "Rune platebody");
        yes("@fairy rings", "dramen staff"); yes("@fairy rings", "lunar staff"); no("@fairy rings", "Grimy ranarr weed"); no("@fairy rings", "Rune platebody");

        yes("@wintertodt", "Bruma torch");
        no("@wintertodt", "Raw shark");
        yes("@tempoross", "Crystal harpoon");
        yes("@gotr", "Colossal pouch");
        yes("@giants foundry", "Rune platebody");
        yes("@mahogany homes", "Mahogany plank");
        yes("@zulrah", "Anti-venom+(4)");
        yes("@vorkath", "Extended super antifire(4)");
        no("@vorkath", "Dragon platelegs");
        yes("@gargoyles", "Rock hammer");
        yes("@dustdevils", "Death rune");
        yes("@kurasks", "Leaf-bladed axe");
        yes("@revenants", "Bracelet of ethereum");
        yes("@chaos altar", "Superior dragon bones");
        yes("@cluekit", "Spade");
        yes("@easy clue kit", "Clue scroll (easy)");
        no("@easy clue kit", "Clue scroll (medium)");
        yes("@questprep", "Rope");
        yes("@herb", "Grimy ranarr weed");
        no("@herb", "Ranarr seed");
        yes("@tree run", "Yew sapling");
        yes("@birdhouse run", "Clockwork");
        yes("@toa", "Osmumten's fang");
        yes("@cox", "Twisted bow");
        yes("@tob", "Scythe of vitur");
        check(!CategoryMatcher.parse("qty > 10", true).isCategory(), "Native quantity search overridden");
        check(!CategoryMatcher.parse("ge > 1m", true).isCategory(), "Native GE search overridden");
        check(!CategoryMatcher.parse("tag:raid", true).isCategory(), "Bank Tags syntax overridden");
    }
    public static void main(String[] args)
    {
        runAll();
        System.out.println("PASS " + checks + " activity and compatibility assertions");
    }
}
