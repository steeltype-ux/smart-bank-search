package com.smartbanksearch;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Curated, optional bank-search activity kits. These are candidate supplies
 * from the player's actual bank, never an optimal or guaranteed loadout.
 * No withdrawals, inventory actions, client/network access or account state.
 */
final class ActivityCategories
{
    @FunctionalInterface
    interface Registrar
    {
        void add(String title, Predicate<ItemFacts> rule, String... aliases);
    }

    private ActivityCategories() { }

    private static Predicate<ItemFacts> names(String... terms)
    {
        return f -> CategoryMatcher.matchesName(f.name, terms);
    }

    @SafeVarargs
    private static Predicate<ItemFacts> any(Predicate<ItemFacts>... rules)
    {
        return f -> { for (Predicate<ItemFacts> rule : rules) { if (rule.test(f)) { return true; } } return false; };
    }

    // Reusable supply groups. No generic 'potion', 'gear', 'seed', or 'food'
    // substring matching: avoid dragging unrelated items into every kit.
    private static final Predicate<ItemFacts> HEALS = f -> !CategoryMatcher.matchesName(f.name, "raw", "burnt", "uncooked", "rotten") && CategoryMatcher.matchesName(f.name, "shark", "anglerfish", "manta ray", "dark crab", "sea turtle", "cooked karambwan", "karambwan", "tuna potato", "monkfish", "purple sweets", "pineapple pizza", "summer pie", "wild pie");
    private static final Predicate<ItemFacts> PRAYER = names("prayer potion", "super restore", "sanfew serum", "holy wrench", "blighted super restore", "prayer regeneration potion");
    private static final Predicate<ItemFacts> BREWS = names("saradomin brew", "guthix rest");
    private static final Predicate<ItemFacts> ENERGY = names("stamina potion", "super energy", "energy potion", "ring of endurance", "graceful");
    private static final Predicate<ItemFacts> MELEE_BOOST = names("super combat potion", "divine super combat", "super attack", "divine super attack", "super strength", "divine super strength", "combat potion", "attack potion", "strength potion");
    private static final Predicate<ItemFacts> RANGE_BOOST = names("ranging potion", "ranging mix", "divine ranging", "bastion potion", "divine bastion");
    private static final Predicate<ItemFacts> MAGE_BOOST = names("magic potion", "magic mix", "divine magic", "battlemage potion", "divine battlemage", "forgotten brew", "ancient brew", "imbued heart", "saturated heart");
    private static final Predicate<ItemFacts> SLAYER = names("slayer helmet", "slayer helm", "black mask", "bracelet of slaughter", "expeditious bracelet");
    // These boots prevent lethal floor heat in Karuulm Slayer Dungeon.
    // Elite Kourend & Kebos Diary completion makes them optional; the plugin
    // cannot inspect diary progress, so they remain candidate kit items.
    private static final Predicate<ItemFacts> KARUULM_BOOTS =
        names("boots of stone", "boots of brimstone", "granite boots");
    private static final Predicate<ItemFacts> KARUULM_TRAVEL =
        names("rada's blessing 3", "rada's blessing 4", "skills necklace", "farming cape");
    private static final Predicate<ItemFacts> CANNON_SUPPLIES =
        names("cannonball", "granite cannonball", "cannon base", "cannon stand",
            "cannon barrels", "cannon furnace");

    private static final Predicate<ItemFacts> WILDY = names("blighted", "royal seed pod", "burning amulet", "looting bag", "wilderness sword", "amulet of glory", "ring of dueling");

    // Wintertodt warmth: each food bite that normally heals >=4 HP restores 35%
    // warmth. Multi-bite cakes (3) and pizzas/pies (2) are especially slot-efficient.
    // Avoid uncooked components that share the same item-name substrings.
    private static final Predicate<ItemFacts> WINTERTODT_MULTIBITE_FOOD = f ->
        (CategoryMatcher.matchesName(f.name, "cake", "pizza", "pie")
            && !CategoryMatcher.matchesName(f.name, "raw", "uncooked", "burnt", "dough",
                "mix", "tin", "dish", "shell", "base", "flour"))
        || CategoryMatcher.matchesName(f.name, "cooked sunlight antelope",
            "cooked moonlight antelope", "cooked dashing kebbit", "cooked wild kebbit",
            "cooked larupia", "cooked graahk", "cooked kyatt", "cooked fox");


    // Giant's Foundry requires 28 metal units per sword. Eligible recycled armour
    // and weapons must be smithable from 2+ bars; they contribute smithing bars - 1.
    // Exact item-name patterns avoid unrelated ores, unprocessable 1-bar weapons,
    // gilded/trimmed equipment and poisoned/ornamented variants.
    private static final Pattern FOUNDRY_BAR = Pattern.compile(
        "^(bronze|iron|steel|mithril|adamantite|runite) bar$");
    private static final Pattern FOUNDRY_RECYCLABLE = Pattern.compile(
        "^(bronze|iron|steel|mithril|adamant|rune) " +
        "(scimitar|longsword|full helm|sq shield|square shield|claws|" +
        "warhammer|battleaxe|chainbody|kiteshield|2h sword|" +
        "platelegs|plateskirt|platebody)$");

    private static final Predicate<ItemFacts> FOUNDRY_METAL = f -> {
        String n = f.name.toLowerCase(Locale.ROOT).trim();
        return FOUNDRY_BAR.matcher(n).matches() || FOUNDRY_RECYCLABLE.matcher(n).matches();
    };

    private static final Predicate<ItemFacts> FOUNDRY_EQUIPMENT = any(
        names("smiths' tunic", "smiths' trousers", "smiths' gloves", "smiths' boots",
            "smiths gloves (i)", "ice gloves", "ring of dueling", "amulet of glory"),
        f -> "bucket".equalsIgnoreCase(f.name) || "bucket of water".equalsIgnoreCase(f.name));

    // Tempoross preparation is different from general Fishing gear. The Cove
    // supplies basic tools, but a player's own upgraded harpoon and the Angler
    // outfits are worthwhile. The full Spirit angler set replaces the rope.
    // Do not show fish barrels (cannot hold harpoonfish), tackle boxes (storage),
    // or generic stamina items (unnecessary for normal rounds).
    private static final Predicate<ItemFacts> TEMPOROSS_TOOLS = f -> {
        String n = f.name.toLowerCase(Locale.ROOT).trim();
        return n.equals("rope") || n.equals("hammer")
            || n.equals("bucket") || n.equals("bucket of water")
            || CategoryMatcher.matchesName(n, "harpoon", "imcando hammer");
    };

    private static final Predicate<ItemFacts> TEMPOROSS_OUTFITS = names(
        "angler hat", "angler top", "angler waders", "angler boots",
        "spirit angler headband", "spirit angler top",
        "spirit angler waders", "spirit angler boots");

    // Advanced setups: harpoon-special energy, crystal charges, Humidify and
    // optional fishing boosts. The filter cannot tell what a player has unlocked.
    private static final Predicate<ItemFacts> TEMPOROSS_OPTIONAL = names(
        "lightbearer", "elven signet", "rune pouch", "astral rune",
        "fire rune", "water rune", "steam rune", "tome of fire",
        "tome of water", "fishing potion", "admiral pie", "fish pie");


    // Guardians of the Rift normal setup: equipment that helps mine, carry and
    // craft guardian essence, plus ways to avoid or repair pouch degradation.
    // Pouch names are "Small pouch" etc., not "Essence pouch" in actual banks.
    private static final Pattern GOTR_POUCH = Pattern.compile(
        "^(?:essence|small|medium|large|giant|colossal) pouch(?: [(]degraded[)])?$");

    private static final Predicate<ItemFacts> GOTR_ESSENCE_POUCH = f ->
        GOTR_POUCH.matcher(f.name.toLowerCase(Locale.ROOT).trim()).matches();

    private static final Predicate<ItemFacts> GOTR_MAIN_GEAR = names(
        "pickaxe", "chisel", "abyssal lantern",
        "hat of the eye", "robe top of the eye", "robe bottoms of the eye",
        "boots of the eye", "varrock armour", "celestial ring",
        "celestial signet", "ghommal's lucky penny", "mining cape",
        "runecraft cape", "runecrafting cape", "max cape",
        "prospector", "graceful", "penance gloves", "spottier cape",
        "lightbearer", "amulet of the eye", "necklace of passage");

    // Higher-effort combination-rune method: binding necklace and Magic Imbue
    // (Lunar spellbook), plus Astral Contact to repair degraded pouches.
    // Most players do not need these for normal/relaxed rounds.
    private static final Predicate<ItemFacts> GOTR_COMBINATION = names(
        "binding necklace", "rune pouch", "divine rune pouch",
        "air rune", "water rune", "earth rune", "fire rune",
        "steam rune", "smoke rune", "astral rune", "cosmic rune",
        "tome of fire", "tome of water", "steam battlestaff",
        "twinflame staff");


    // Mahogany Homes: actual contract supplies, individual Carpenter's pieces,
    // contractor contact, transport to the four towns and optional Plank Make.
    // Match planks/tools exactly to avoid adding unrelated planked objects.
    private static final Predicate<ItemFacts> MAHOGANY_HOMES_CORE = f -> {
        String n = f.name.toLowerCase(Locale.ROOT).trim();
        return n.equals("plank") || n.equals("oak plank")
            || n.equals("teak plank") || n.equals("mahogany plank")
            || n.equals("steel bar") || n.equals("saw") || n.equals("hammer")
            || CategoryMatcher.matchesName(n, "amy's saw", "imcando hammer",
                "plank sack", "carpenter's helmet", "carpenter's shirt",
                "carpenter's trousers", "carpenter's boots");
    };

    private static final Predicate<ItemFacts> MAHOGANY_HOMES_TRAVEL = names(
        "rune pouch", "divine rune pouch",
        "air rune", "astral rune", "cosmic rune",
        "varrock teleport", "ardougne teleport", "falador teleport",
        "xeric's talisman", "xeric's glade", "ring of wealth",
        "skills necklace", "teleport to house", "house teleport",
        "achievement diary cape", "ardougne cloak",
        "book of the dead", "kharedst's memoirs", "graceful");

    private static final Predicate<ItemFacts> MAHOGANY_HOMES_ADVANCED = names(
        "law rune", "dust rune", "steam rune", "nature rune",
        "staff of air", "air battlestaff", "dust battlestaff",
        "steam battlestaff", "log basket", "sawmill voucher");

    // Hallowed Sepulchre: agility-only laps can skip coffin supplies; full-loot
    // runs require construction, Ranged, Prayer and Magic challenge materials.
    private static final Predicate<ItemFacts> SEPULCHRE_PLANKS_AND_TOOLS = f -> {
        String n = f.name.toLowerCase(Locale.ROOT).trim();
        return n.equals("plank") || n.equals("oak plank")
            || n.equals("teak plank") || n.equals("mahogany plank")
            || n.equals("hammer") || n.equals("saw")
            || n.matches("^(bronze|iron|steel|mithril|adamant|rune) nails$");
    };

    private static final Predicate<ItemFacts> SEPULCHRE_CORE = names(
        "hallowed grapple", "hallowed hammer", "hallowed focus",
        "hallowed ring", "hallowed symbol", "hallowed token",
        "hallowed crystal shard", "strange old lockpick", "lockpick",
        "vampyre dust", "mith grapple", "dorgeshuun crossbow",
        "rune crossbow", "adamant crossbow", "dragon crossbow",
        "holy symbol", "holy blessing", "holy book", "prayer book",
        "serpentine helm", "antipoison", "superantipoison",
        "antidote", "graceful", "boots of lightness", "spottier cape",
        "spotted cape", "hitpoints cape", "regen bracelet",
        "stamina potion", "ring of endurance", "drakan's medallion",
        "cosmic rune", "fire rune", "nature rune", "rune pouch",
        "tome of fire", "saradomin stole");

    // Pyramid Plunder poison + melee protection, shortcuts to Sophanem and
    // stat restoration. Snake charm is primarily useful at lower Thieving.
    private static final Predicate<ItemFacts> PYRAMID_PLUNDER_CORE = names(
        "pharaoh's sceptre", "snake charm", "serpentine helm",
        "antipoison", "superantipoison", "antidote", "menaphite remedy",
        "prayer potion", "super restore", "prayer book", "holy symbol",
        "stamina potion", "ring of dueling", "desert amulet",
        "teleport to house", "graceful", "boots of lightness",
        "spottier cape", "spotted cape", "regen bracelet",
        "hitpoints cape", "ardougne cloak", "dragonbone necklace",
        "guthan's");
    private static final Predicate<ItemFacts> PYRAMID_PLUNDER_LOCKPICK = f ->
        "lockpick".equalsIgnoreCase(f.name.trim());

    // Tithe Farm gives players its seeds and fertiliser inside the minigame:
    // bank searches should focus on carried watering/planting equipment,
    // the individual Farmer's outfit pieces, and optional Humidify and travel.
    private static final Predicate<ItemFacts> TITHE_FARM_CORE = names(
        "gricoller's can", "watering can", "seed dibber", "spade",
        "farmer's strawhat", "farmer's jacket", "farmer's shirt",
        "farmer's boro trousers", "farmer's boots", "graceful",
        "stamina potion", "super energy", "ring of endurance",
        "explorer's ring", "xeric's talisman", "skills necklace",
        "kharedst's memoirs", "book of the dead",
        "teleport to house", "kourend castle teleport");

    private static final Predicate<ItemFacts> TITHE_FARM_HUMIDIFY = names(
        "rune pouch", "divine rune pouch", "astral rune",
        "fire rune", "water rune", "steam rune", "tome of fire",
        "tome of water", "water battlestaff", "steam battlestaff");


    // Pest Control restores combat stats and Prayer between rounds. Players
    // normally take combat gear and use their offensive prayers freely rather
    // than withdrawing food or Prayer potions for short mass-world games.
    private static final Predicate<ItemFacts> PEST_CONTROL_GEAR = names(
        "void knight", "elite void", "dragon scimitar", "abyssal whip",
        "abyssal tentacle", "dragon defender", "avernic defender",
        "ghrazi rapier", "osmumten's fang", "scythe of vitur",
        "bow of faerdhinen", "crystal bow", "magic shortbow",
        "venator bow", "toxic blowpipe", "chinchompa",
        "ancient staff", "ancient sceptre", "kodai wand",
        "bandos godsword", "dragon claws", "dragon dagger",
        "voidwaker", "pest control teleport");
    private static final Predicate<ItemFacts> PEST_CONTROL_SPECIALIST = names(
        "rune pouch", "divine rune pouch", "blood rune", "death rune",
        "water rune", "ice ancient sceptre", "super combat potion",
        "divine super combat potion", "combat potion",
        "ranging potion", "divine ranging potion");

    // Barbarian Assault uses its own arrows, runes, horns, bait and food.
    // Prayers are disabled, and rune pouches cannot enter the arena. Equip
    // attacker damage and defender/healer/collector survivability options.
    private static final Predicate<ItemFacts> BARBARIAN_ASSAULT_GEAR = names(
        "fighter torso", "granite body", "penance", "dragon scimitar",
        "abyssal whip", "abyssal tentacle", "dragon defender",
        "avernic defender", "zamorakian hasta", "dinh's bulwark",
        "excalibur", "dragonfire shield", "rune kiteshield",
        "imcando hammer", "magic shortbow", "crystal bow",
        "bow of faerdhinen", "venator bow", "toxic blowpipe",
        "void knight", "elite void", "karil's", "torag's",
        "bandos chestplate", "justiciar", "torva",
        "games necklace", "combat bracelet");
    private static final Predicate<ItemFacts> BARBARIAN_ASSAULT_BOOSTS = names(
        "super combat potion", "divine super combat potion",
        "super attack", "super strength", "super defence",
        "ranging potion", "divine ranging potion", "bastion potion");

    // Soul Wars gives bandages, combat potions, barricades and explosives in
    // the match. Before entering, players primarily bank-switch armour,
    // spec weapons and the runes needed for Ancient Magicks.
    private static final Predicate<ItemFacts> SOUL_WARS_GEAR = names(
        "soul cape", "soul wars", "black d'hide", "blessed d'hide",
        "karil's", "armadyl", "masori", "void knight", "elite void",
        "bandos", "torva", "dragon defender", "avernic defender",
        "ancient staff", "ancient sceptre", "kodai wand", "master wand",
        "nightmare staff", "ahrim's staff", "rune pouch", "divine rune pouch",
        "blood rune", "death rune", "water rune", "soul rune",
        "ancient godsword", "armadyl godsword", "dragon claws",
        "dragon dagger", "voidwaker", "toxic blowpipe",
        "bow of faerdhinen", "crystal bow", "twisted bow",
        "rune crossbow", "zaryte crossbow", "magic shortbow",
        "amulet of glory", "combat bracelet");

    // Nightmare Zone overload/absorption are minigame-only doses drawn from
    // Dominic's barrels, NOT bankable supplies. Instead match banked low-HP
    // tools, common training sets, ordinary consumables and spec weapons.
    private static final Predicate<ItemFacts> NIGHTMARE_ZONE_GEAR = names(
        "dwarven rock cake", "locator orb", "dharok's",
        "obsidian helmet", "obsidian platebody", "obsidian platelegs",
        "toktz-xil-ak", "berserker necklace", "berserker ring",
        "abyssal whip", "abyssal tentacle", "dragon scimitar",
        "dragon defender", "avernic defender", "soulreaper axe",
        "torva", "bandos chestplate", "neitiznot faceguard",
        "amulet of torture", "amulet of rancour", "ferocious gloves",
        "sarasword", "saradomin sword", "saradomin godsword",
        "ancient mace", "dragon claws", "void knight", "elite void",
        "proselyte", "initiate", "venator bow", "toxic blowpipe",
        "rune pouch", "divine rune pouch", "holy wrench");
    private static final Predicate<ItemFacts> NIGHTMARE_ZONE_CONSUMABLES = f ->
        !CategoryMatcher.matchesName(f.name, "nightmare zone", "absorption", "overload")
            && CategoryMatcher.matchesName(f.name,
                "super combat potion", "divine super combat potion",
                "prayer potion", "super restore", "ranging potion",
                "magic potion", "coins");


    // Shades of Mort'ton supports temple repair, oil production, cremation and
    // catacomb rewards. Use actual remains/key names: generic "shade remains"
    // and "silver key" name matches catch unrelated items.
    private static final Pattern SHADES_COMPONENT = Pattern.compile(
        "^(?:logs|(?:oak|willow|teak|arctic pine|maple|mahogany|yew|magic|redwood) logs|"
        + "(?:(?:oak|willow|teak|arctic|arctic pine|maple|mahogany|yew|magic|redwood) )?pyre logs|"
        + "(?:loar|phrin|riyl|asyn|fiyr|urium) remains)$");
    private static final Pattern SHADES_CHEST_KEY = Pattern.compile(
        "^(?:bronze|steel|black|silver|gold) key "
        + "(?:red|brown|crimson|black|purple)$");
    private static final Pattern SHADES_AXE = Pattern.compile(
        "^(?:bronze|iron|steel|black|mithril|adamant|rune|dragon|"
        + "crystal|infernal) axe(?: [(]uncharged[)])?$");

    private static final Predicate<ItemFacts> SHADES_ITEMS = f -> {
        String name = f.name.toLowerCase(Locale.ROOT).trim();
        return SHADES_COMPONENT.matcher(name).matches()
            || SHADES_CHEST_KEY.matcher(name).matches()
            || SHADES_AXE.matcher(name).matches();
    };

    private static final Predicate<ItemFacts> SHADES_TEMPLE_AND_TRAVEL = names(
        "olive oil", "sacred oil", "serum 207", "serum 208",
        "flamtaer hammer", "flamtaer bracelet", "flamtaer bag",
        "imcando hammer", "swamp paste", "timber beam", "limestone brick",
        "tinderbox", "mortar and pestle", "hammer",
        "mort'ton teleport", "morton teleport", "barrows teleport",
        "morytania legs", "drakan's medallion");

    // Trawler rewards (Angler outfit) are not preparation tools. A common
    // mass-world method is ten swamp-paste fixes for 50 contribution points.
    // Ordinary buckets cannot replace bailing buckets.
    private static final Pattern TRAWLER_AXE = Pattern.compile(
        "^(?:bronze|iron|steel|black|mithril|adamant|rune|dragon|"
        + "crystal|infernal|3rd age) axe(?: [(]uncharged[)])?$");

    private static final Predicate<ItemFacts> TRAWLER_TOOLS = f -> {
        String name = f.name.toLowerCase(Locale.ROOT).trim();
        return name.equals("swamp paste") || name.equals("rope")
            || name.equals("bailing bucket") || name.equals("hammer")
            || name.equals("imcando hammer")
            || TRAWLER_AXE.matcher(name).matches();
    };

    // Volcanic Mine usually needs food and protection from ranged damage.
    // Stamina is primarily for solo/capping routes. No extra energy-potion
    // category or mined resources; volcanic ore itself is not banked.
    private static final Predicate<ItemFacts> VOLCANIC_MINE_GEAR = names(
        "pickaxe", "numulite", "volcanic mine teleport", "digsite pendant",
        "prospector", "mining cape", "mining hood", "celestial ring",
        "celestial signet", "varrock armour 4", "regen bracelet",
        "hitpoints cape", "max cape", "dragonbone necklace",
        "bonecrusher necklace", "blessing", "ardougne cloak",
        "falador shield", "ring of the gods", "ring of suffering",
        "explorer's ring", "proselyte", "mitre", "stole",
        "prayer potion", "super restore", "stamina potion",
        "ring of endurance", "graceful", "desert amulet",
        "teleport to house");

    // Only actual furnace inputs, not the bars it produces or any other
    // item whose name contains "coal", "ore", etc.
    private static final Pattern BLAST_FURNACE_INPUT = Pattern.compile(
        "^(?:coal|(?:copper|tin|iron|silver|gold|mithril|"
        + "adamantite|runite) ore)$");

    private static final Predicate<ItemFacts> BLAST_FURNACE_ORE = f ->
        BLAST_FURNACE_INPUT.matcher(f.name.toLowerCase(Locale.ROOT).trim()).matches();

    private static final Predicate<ItemFacts> BLAST_FURNACE_GEAR = names(
        "coal bag", "ice gloves", "smiths gloves (i)",
        "goldsmith gauntlets", "bucket of water", "stamina potion",
        "ring of endurance", "graceful", "boots of lightness",
        "spottier cape", "penance gloves", "explorer's ring",
        "agility cape", "ring of charos (a)", "coins",
        "smithing cape");

    static void register(Registrar registry)
    {
        // Bosses and raids
        registry.add("Zulrah preparation", any(names("anti venom", "antivenom", "ring of suffering", "ring of recoil", "zul andra teleport", "zulrah's scales", "toxic blowpipe", "bow of faerdhinen", "crystal armour", "crystal armor", "armadyl", "masori", "ahrim", "ancestral", "trident of the swamp", "trident of the seas", "sanguinesti staff", "occult necklace", "tormented bracelet", "tome of fire", "rune pouch", "serpentine helm", "ava's assembler", "trident", "saturated heart", "imbued heart", "divine ranging potion"), HEALS, PRAYER, BREWS, RANGE_BOOST, MAGE_BOOST),
            "zulrah", "zulrah prep", "zulrah gear");
        registry.add("Vorkath preparation", any(names("dragon hunter crossbow", "dragon hunter lance", "dragonfire ward", "dragonfire shield", "anti dragon shield", "anti-dragon shield", "antifire", "super antifire", "salve amulet", "ruby dragon bolts", "diamond dragon bolts", "bandos godsword", "dragon warhammer", "void knight", "elite void", "slayer staff", "lunar isle teleport", "waterbirth teleport", "rune pouch", "salve amulet(ei)", "salve amulet(e)", "dragon bolts", "ruby bolts", "diamond bolts", "earth rune", "air rune", "chaos rune", "dust rune", "fremennik sea boots", "home teleport", "super combat potion"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST),
            "vorkath", "vorkath prep", "vorkath gear");
        registry.add("Phantom Muspah preparation", any(names("sapphire dragon bolts", "sapphire bolts", "crossbow", "bow of faerdhinen", "crystal armour", "crystal armor", "masori", "ancestral", "ahrim", "trident", "tumeken's shadow", "ancient sceptre", "ancient staff", "ring of shadows", "icy basalt", "rune pouch", "blood rune", "death rune", "water rune", "soul rune", "sapphire bolts (e)", "sapphire dragon bolts (e)", "forgotten brew", "venator bow", "twisted bow", "diamond dragon bolts", "smoke rune", "fire rune", "magic potion", "stamina potion", "ghorrock teleport"), HEALS, PRAYER, BREWS, RANGE_BOOST, MAGE_BOOST, ENERGY),
            "muspah", "phantom muspah", "muspah prep");
        registry.add("Fight Caves preparation", any(names("toxic blowpipe", "dragon dart", "rune dart", "amethyst dart", "rune arrow", "dragon arrow", "crystal bow", "bow of faerdhinen", "twisted bow", "crossbow", "diamond dragon bolts", "ava's assembler", "ava's accumulator", "masori", "armadyl", "blessed d'hide", "holy wrench", "ranging cape", "ava's attractor", "sapphire bolts", "dragon crossbow", "rune crossbow", "sanguinesti staff", "ancient staff", "rune pouch", "blood rune", "death rune", "water rune", "saradomin godsword", "stamina potion"), HEALS, PRAYER, BREWS, RANGE_BOOST),
            "jad", "fight caves", "jad prep", "fight caves prep");
        registry.add("Inferno preparation", any(names("twisted bow", "bow of faerdhinen", "crystal armour", "crystal armor", "ancestral", "masori", "justiciar", "toxic blowpipe", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "rune pouch", "elysian spirit shield", "crystal shield", "occult necklace", "rune dart", "dragon dart", "amethyst dart", "crystal bow", "blowpipe", "lightbearer", "virtus", "blood ancient sceptre", "ice ancient sceptre", "stamina potion"), HEALS, PRAYER, BREWS, RANGE_BOOST, MAGE_BOOST),
            "inferno", "inferno prep");
        registry.add("Nex preparation", any(names("zaryte crossbow", "armadyl crossbow", "dragon crossbow", "ruby dragon bolts", "diamond dragon bolts", "torva", "masori", "armadyl", "ancient ceremonial", "frozen key", "ecumenical key", "ancient godsword", "bandos godsword", "fang", "osmumten's fang", "lightbearer", "blood fury", "amulet of blood fury", "dragon warhammer", "twisted bow", "amulet of rancour", "zaryte vambraces", "saturated heart", "ruby bolts", "dragon bolts"), HEALS, PRAYER, BREWS, RANGE_BOOST, MELEE_BOOST, ENERGY),
            "nex", "nex prep", "nex trip");
        registry.add("Nightmare preparation", any(names("inquisitor", "bludgeon", "scythe of vitur", "sanguinesti staff", "harmonised nightmare staff", "harmonized nightmare staff", "trident", "eldritch nightmare staff", "slepey tablet", "drakan's medallion", "ectophial", "dragon warhammer", "inquisitor's mace", "abyssal bludgeon", "soulreaper axe", "soulreaper axe (uncharged)", "magic potion", "amulet of rancour", "ghrazi rapier", "lightbearer"), HEALS, PRAYER, BREWS, MELEE_BOOST, ENERGY),
            "nightmare boss", "nightmare prep", "phosani", "phosani's nightmare");
        registry.add("Cerberus preparation", any(names("spectral spirit shield", "arclight", "emberlight", "scythe of vitur", "abyssal bludgeon", "dragon claws", "slayer helmet", "black mask", "key master teleport", "devout boots", "primordial boots", "pegasian boots", "eternal boots", "lightbearer", "abyssal whip", "prayer potion"), HEALS, PRAYER, BREWS, MELEE_BOOST),
            "cerberus", "cerberus prep", "cerb");
        registry.add("Alchemical Hydra preparation", any(names("dragon hunter crossbow", "dragon hunter lance", "twisted bow", "bow of faerdhinen", "toxic blowpipe", "ranging potion", "antipoison", "slayer helmet", "brimstone ring", "boots of stone", "boots of brimstone", "granite boots", "dragon arrow", "amethyst arrow", "avernic defender", "divine ranging potion", "ring of suffering", "dragon hunter wand", "manta ray"), HEALS, PRAYER, BREWS, RANGE_BOOST),
            "hydra boss", "alchemical hydra", "hydra prep", "hydra");
        registry.add("Scurrius preparation", any(names("bone mace", "bone shortbow", "bone staff", "rune pouch", "varrock teleport", "prayer potion", "ancient mace", "divine super combat", "ranging potion", "monk's robe"), HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST),
            "scurrius", "scurrius prep");
        registry.add("CoX raid preparation", any(names("dragon hunter lance", "dragon hunter crossbow", "twisted bow", "bow of faerdhinen", "scythe of vitur", "ancestral", "masori", "trident", "occult necklace", "dragon warhammer", "elder maul", "dragon claws", "xeric's talisman", "xeric's honour", "xeric's honor", "rune pouch", "stamina potion", "antipoison", "osmumten's fang", "tumeken's shadow", "salve amulet", "pickaxe", "axe", "hammer", "chisel", "salve amulet(ei)", "ghrazi rapier", "soulreaper axe", "prayer potion", "dragon pickaxe", "divine rune pouch"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST, MAGE_BOOST),
            "cox prep", "chambers prep", "olm prep");
        registry.add("ToB raid preparation", any(names("scythe of vitur", "sanguinesti staff", "toxic blowpipe", "bandos godsword", "dragon warhammer", "dragon claws", "ghrazi rapier", "avernic defender", "void knight", "elite void", "blood rune", "death rune", "rune pouch", "drakan's medallion", "osmumten's fang", "dragon dagger", "voidwaker", "eldritch nightmare staff", "saturated heart", "book of the dead", "soul rune", "divine rune pouch", "stamina potion", "sanfew serum", "salve amulet"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST),
            "tob prep", "theatre of blood prep", "theater of blood prep");
        registry.add("ToA raid preparation", any(names("osmumten's fang", "keris partisan", "tumeken's shadow", "bow of faerdhinen", "toxic blowpipe", "lightbearer", "masori", "ancestral", "dragon claws", "bandos godsword", "dragon warhammer", "rune pouch", "pharaoh's sceptre", "menaphite remedy", "stamina potion", "elidinis' ward", "elidinis' ward (f)", "divine rune pouch", "dragon dagger", "voidwaker", "book of the dead", "blood rune", "death rune", "soul rune", "water rune", "amulet of blood fury", "venator bow"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST, MAGE_BOOST),
            "toa prep", "tombs of amascut prep", "raids 3 prep");
        registry.add("Barrows run preparation", any(names("barrows teleport", "drakan's medallion", "morytania legs", "spade", "strange old lockpick", "trident of the seas", "trident of the swamp", "ibans staff", "iban's staff", "slayer staff", "occult necklace", "rune pouch", "prayer potion", "ghommal's hilt 2", "ghommal's hilt 3", "ghommal's hilt 4", "ring of dueling", "blood rune", "death rune", "air rune", "chaos rune", "crossbow", "toxic blowpipe", "saturated heart", "fremennik sea boots", "falador shield", "house teleport"), HEALS, PRAYER, ENERGY),
            "barrows prep", "barrows run", "barrows trip");
        registry.add("God Wars trip preparation", any(names("ecumenical key", "frozen key", "trollheim teleport", "stony basalt", "icy basalt", "rope", "bandos", "armadyl", "saradomin", "zamorak", "ancient ceremonial", "god blessing", "blessed d'hide", "amulet of blood fury", "dragon warhammer", "bandos godsword", "bones to peaches", "super restore", "ring of suffering", "avernic defender", "tumeken's shadow", "bow of faerdhinen", "stamina potion", "insulated boots", "ancient godsword", "saturated heart", "lightbearer"), HEALS, PRAYER, BREWS, ENERGY, MELEE_BOOST, RANGE_BOOST),
            "gwd prep", "god wars prep", "godwars prep");

        // Slayer tasks
        // Karuulm Slayer Dungeon: floor heat is independent of combat damage.
        // Wyrms can also be fought outside Karuulm, where these boots are not needed.
        registry.add("Wyrms task", any(
            KARUULM_BOOTS, KARUULM_TRAVEL, SLAYER, HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST,
            names("dragon hunter lance", "dragon hunter crossbow", "dragon hunter wand",
                "rune crossbow", "toxic blowpipe", "bow of faerdhinen",
                "blessed d'hide", "black d'hide", "dragon defender",
                "earth rune", "air rune", "rune pouch", "nature rune", "fire rune",
                "bonecrusher", "slayer ring", "herb sack", "seed box")),
            "wyrms", "wyrm", "wyrm task", "wyrms task", "wyrm prep", "wyrms prep");

        // Drakes' ranged attacks and volcanic breath differ from Wyrms.
        // Optional antifire supplies mitigate their volcanic breath.
        registry.add("Drakes task", any(
            KARUULM_BOOTS, KARUULM_TRAVEL, SLAYER, HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST,
            names("dragon hunter lance", "dragon hunter crossbow", "dragon hunter wand",
                "dragonfire shield", "dragonfire ward", "anti-dragon shield",
                "antifire potion", "super antifire", "extended antifire",
                "extended super antifire", "rune crossbow", "toxic blowpipe",
                "dragon defender", "rune pouch", "bonecrusher", "herb sack",
                "nature rune", "fire rune", "cannonball")),
            "drakes", "drake", "drake task", "drakes task", "drake prep", "drakes prep");

        // Standard Hydras are a separate kit from the Alchemical Hydra boss.
        // Keep @hydra mapped to the boss for backwards compatibility; use
        // @hydras or @hydra task for ordinary Slayer Hydras.
        registry.add("Hydras task", any(
            KARUULM_BOOTS, KARUULM_TRAVEL, SLAYER, HEALS, PRAYER, RANGE_BOOST,
            CANNON_SUPPLIES,
            names("dragon hunter lance", "dragon hunter crossbow", "dragon hunter wand",
                "bow of faerdhinen", "toxic blowpipe", "rune crossbow",
                "antipoison", "superantipoison", "antidote", "antivenom", "anti-venom",
                "sanfew serum", "serpentine helm", "rune pouch",
                "nature rune", "fire rune", "bonecrusher")),
            "hydras", "hydra task", "hydras task", "regular hydras", "normal hydras",
            "normal hydra", "hydra slayer");

        // Sulphur lizards are the Karuulm alternative for a Lizards task.
        // Unlike desert lizards, they do NOT need ice coolers.
        registry.add("Sulphur lizards task", any(
            KARUULM_BOOTS, KARUULM_TRAVEL, SLAYER, HEALS, PRAYER, MELEE_BOOST,
            CANNON_SUPPLIES,
            names("dragon scimitar", "abyssal whip", "dragon defender", "herb sack",
                "gem bag", "slayer ring", "rune pouch", "nature rune", "fire rune")),
            "sulphur lizards", "sulphur lizard", "sulphur lizards task",
            "sulphur lizard task", "sulfur lizards", "sulfur lizard task",
            "karuulm lizards");

        registry.add("Gargoyles task", any(names("rock hammer", "rock thrownhammer", "granite hammer", "dual macuahuitl", "slayer ring", "guthan", "abyssal whip", "rune pouch", "herb sack", "gem bag", "nature rune", "fire rune", "dust rune", "ash sanctifier", "bracelet of slaughter", "expeditious bracelet", "dragon defender", "dragon scimitar", "strength potion", "guthan's"), SLAYER, HEALS, PRAYER, MELEE_BOOST),
            "gargoyles", "gargoyle task", "gargoyles prep", "gargoyle", "gargs");
        registry.add("Dust devils task", any(names("facemask", "masked earmuffs", "slayer helmet", "black mask", "ancient staff", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "rune pouch", "ash sanctifier", "smoke battlestaff", "virtus", "ancestral", "occult necklace", "bracelet of slaughter", "expeditious bracelet", "lightbearer", "teleport to house", "dust rune", "smoke rune", "fire rune"), PRAYER, MAGE_BOOST),
            "dust devils", "dustdevils", "dust devil task", "dust devil");
        registry.add("Nechryaels task", any(names("slayer helmet", "black mask", "ancient staff", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "rune pouch", "ash sanctifier", "bracelet of slaughter", "occult necklace", "virtus", "ancestral", "expeditious bracelet", "soul rune", "smoke rune", "herb sack", "seed box"), PRAYER, MAGE_BOOST),
            "nechryaels", "nechs", "nech task", "nechryael task", "nech");
        registry.add("Aberrant spectres task", any(names("nose peg", "slayer helmet", "black mask", "salve graveyard teleport", "slayer ring", "herb sack", "ranging potion", "seed box", "expeditious bracelet", "bracelet of slaughter", "black d'hide"), HEALS, PRAYER, RANGE_BOOST),
            "aberrant spectres", "spectres task", "aberrants", "spectres");
        registry.add("Basilisks task", any(names("mirror shield", "v's shield", "slayer helmet", "black mask", "slayer ring", "rune pouch", "lightbearer", "dragon defender", "avernic defender", "ranging potion", "ruby bolts", "diamond bolts", "venator bow", "prayer potion", "super restore"), SLAYER, HEALS, PRAYER, MELEE_BOOST),
            "basilisks", "basilisk knights", "basilisk task", "basilisk");
        registry.add("Kurasks task", any(names("leaf bladed sword", "leaf bladed axe", "leaf bladed spear", "leaf-bladed sword", "leaf-bladed axe", "broad bolts", "broad arrows", "magic shortbow", "slayer helmet", "black mask", "herb sack", "leaf-bladed battleaxe", "leaf bladed battleaxe", "leaf-bladed spear", "slayer's staff", "nature rune", "death rune", "seed box", "expeditious bracelet", "bracelet of slaughter"), SLAYER, HEALS, PRAYER),
            "kurasks", "kurask task", "kurask");
        registry.add("Skeletal wyverns task", any(names("elemental shield", "mind shield", "ancient wyvern shield", "dragonfire shield", "dragonfire ward", "slayer helmet", "black mask", "crossbow", "ranged potion", "dragon hunter crossbow", "dragon hunter lance", "rune crossbow", "broad bolts", "ruby dragon bolts", "diamond dragon bolts", "teleport to house"), HEALS, PRAYER, RANGE_BOOST),
            "skeletal wyverns", "wyvern task", "skeletal wyvern");
        registry.add("Abyssal demons task", any(names("arclight", "emberlight", "abyssal whip", "abyssal dagger", "slayer helmet", "black mask", "scythe of vitur", "ancient staff", "blood rune", "death rune", "water rune", "rune pouch", "bracelet of slaughter", "soulreaper axe", "ancient sceptre", "kodai wand", "virtus", "occult necklace", "ash sanctifier", "venator bow", "expeditious bracelet"), HEALS, PRAYER, MELEE_BOOST),
            "abyssal demons", "abby demons", "abyssal task", "abbys");
        registry.add("Demonic gorillas trip", any(names("arclight", "emberlight", "demonbane", "toxic blowpipe", "bow of faerdhinen", "crossbow", "rune pouch", "royal seed pod", "ring of dueling", "slayer helmet", "black mask", "masori", "blessed d'hide", "black d'hide", "magic shortbow", "dragon crossbow", "zaryte crossbow", "ranging potion", "divine ranging", "stamina potion", "expeditious bracelet", "sanfew serum", "anti venom", "amulet of torture"), HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST),
            "demonic gorillas", "demonics", "demonic gorilla prep", "gorillas");
        registry.add("Burst and barrage Slayer", any(names("ancient staff", "ancient sceptre", "kodai wand", "master wand", "blood rune", "death rune", "water rune", "chaos rune", "rune pouch", "occult necklace", "ancestral", "virtus", "prayer potion", "bracelet of slaughter", "smoke battlestaff", "ice ancient sceptre", "soul rune", "smoke rune", "fire rune", "bonecrusher", "ash sanctifier", "lightbearer", "magic potion"), PRAYER, MAGE_BOOST),
            "barrage tasks", "barrage", "barrage slayer", "burst tasks", "burst slayer", "burst");
        registry.add("Cannon Slayer trips", any(names("cannonball", "dwarf multicannon", "cannon base", "cannon stand", "cannon barrels", "cannon furnace", "double ammo mould", "slayer helmet", "black mask", "stamina potion", "expeditious bracelet", "bracelet of slaughter", "ranging potion", "divine ranging", "super combat potion", "rune pouch", "ring of dueling", "granite cannonball"), HEALS, PRAYER),
            "cannon tasks", "cannon slayer", "cannon trip", "cannon");
        registry.add("Kraken task", any(names("trident of the seas", "trident of the swamp", "sanguinesti staff", "occult necklace", "ancestral", "ahrim", "tome of fire", "slayer helmet", "black mask", "fishing explosive", "magic potion", "bracelet of slaughter", "expeditious bracelet", "imbued heart", "saturated heart", "staff of the dead", "mist battlestaff", "trident", "eternal boots", "seers ring", "crystal shield"), HEALS, PRAYER, MAGE_BOOST),
            "kraken boss", "kraken task", "cave kraken", "kraken");

        // Minigames
        registry.add("Wintertodt kit", any(names("tinderbox", "bruma torch", "pyromancer", "clue hunter", "warm gloves", "firemaking cape", "firemaking hood", "axe", "hammer", "knife", "regen bracelet", "hitpoints cape", "yak hide", "santa hat", "jug of wine", "rejuvenation potion"), WINTERTODT_MULTIBITE_FOOD, HEALS),
            "wintertodt", "wintertodt prep", "wt prep", "wt");
        registry.add("Tempoross kit",
            any(TEMPOROSS_TOOLS, TEMPOROSS_OUTFITS, TEMPOROSS_OPTIONAL),
            "tempoross", "tempoross prep", "temp");
        registry.add("Guardians of the Rift kit",
            any(GOTR_ESSENCE_POUCH, GOTR_MAIN_GEAR, GOTR_COMBINATION),
            "gotr", "guardians of the rift", "guardians rift");
        // Metal material candidates plus only the relevant worn/portable equipment.
        // Ice gloves / smiths gloves (i) are convenient for handling the hot preform.
        // Buckets are optional because the minigame provides them on site.
        registry.add("Giant's Foundry kit", any(FOUNDRY_METAL, FOUNDRY_EQUIPMENT),
            "giants foundry", "giantsfoundry", "giant foundry", "giant's foundry", "foundry");
        registry.add("Mahogany Homes kit", any(MAHOGANY_HOMES_CORE, MAHOGANY_HOMES_TRAVEL, MAHOGANY_HOMES_ADVANCED),
            "mahogany homes", "mahoganyhomes", "mahogany homes prep", "mh");
        registry.add("Hallowed Sepulchre kit", any(SEPULCHRE_CORE, SEPULCHRE_PLANKS_AND_TOOLS, HEALS),
            "hallowed sepulchre", "sepulchre", "sepulchre prep");
        registry.add("Pyramid Plunder kit", any(PYRAMID_PLUNDER_CORE, PYRAMID_PLUNDER_LOCKPICK, HEALS),
            "pyramid plunder", "pyramidplunder", "plunder prep", "plunder");
        registry.add("Tithe Farm kit", any(TITHE_FARM_CORE, TITHE_FARM_HUMIDIFY),
            "tithe farm", "tithefarm", "tithe farming", "tithe");
        registry.add("Pest Control kit", any(PEST_CONTROL_GEAR, PEST_CONTROL_SPECIALIST),
            "pest control", "pestcontrol", "pc minigame", "pest");
        registry.add("Barbarian Assault kit", any(BARBARIAN_ASSAULT_GEAR, BARBARIAN_ASSAULT_BOOSTS),
            "barbarian assault", "barb assault", "ba minigame", "ba");
        registry.add("Soul Wars kit", SOUL_WARS_GEAR,
            "soul wars", "soulwars");
        registry.add("Nightmare Zone kit", any(NIGHTMARE_ZONE_GEAR, NIGHTMARE_ZONE_CONSUMABLES),
            "nightmare zone", "nmz", "nightmarezone");
        registry.add("Shades of Mort'ton kit",
            any(SHADES_ITEMS, SHADES_TEMPLE_AND_TRAVEL, HEALS),
            "shades of mortton", "shades of mort'ton", "shades minigame", "shades");
        registry.add("Fishing Trawler kit", TRAWLER_TOOLS,
            "fishing trawler", "trawler", "fishing trawler prep");
        registry.add("Volcanic Mine kit", any(VOLCANIC_MINE_GEAR, HEALS),
            "volcanic mine", "volcanicmine", "vm mining", "vm");
        registry.add("Blast Furnace kit", any(BLAST_FURNACE_ORE, BLAST_FURNACE_GEAR),
            "blast furnace", "blastfurnace", "bf smithing", "bf");

        // Farming and skilling
        registry.add("Farming contracts kit", names("spade", "rake", "seed dibber", "magic secateurs", "ultracompost", "bottomless compost bucket", "seed box", "gardening trowel", "farming guild teleport", "skills necklace", "sapling", "amulet of bounty", "farming cape", "white lily seed", "snape grass seed", "watermelon seed", "potato cactus seed", "poison ivy seed", "whiteberry seed", "jangerberry seed", "snapdragon seed", "cadantine seed", "lantadyme seed", "ranarr seed", "dwarf weed seed", "torstol seed", "maple seed", "yew seed", "magic seed", "dragonfruit tree seed", "dragonfruit sapling", "redwood seed", "redwood sapling", "celastrus seed", "celastrus sapling", "hespori seed", "cactus seed", "papaya tree seed", "palm tree seed", "curry tree seed", "pineapple seed", "teak seed", "mahogany seed", "garden pie"),
            "farming contracts", "farm contracts", "farm contract", "contracts");
        registry.add("Giant seaweed runs", names("seaweed spore", "diving apparatus", "fishbowl helmet", "merfolk trident", "brine sabre", "flippers", "digsite pendant", "stamina potion", "spade", "ultracompost", "bottomless compost bucket", "mermaid's tear", "fossil island teleport", "supercompost", "magic secateurs"),
            "giant seaweed", "seaweed run", "seaweed farming");
        registry.add("Motherlode Mine kit", names("pickaxe", "prospector", "coal bag", "gem bag", "mining gloves", "expert mining gloves", "celestial ring", "mining cape", "stamina potion", "varrock armour 4", "charged celestial ring", "celestial signet", "varrock armour"),
            "motherlode mine", "motherlode", "mlm");
        registry.add("Bird snaring and hunter kit", names("bird snare", "hunter cape", "hunter hood", "stamina potion", "spotted cape", "spottier cape", "ring of endurance", "hunter potion", "hunter's spear", "camouflage", "ring of dueling", "games necklace"),
            "bird snaring", "bird snare run", "hunter birds");
        registry.add("Chinchompa hunting kit", names("box trap", "chinchompa", "red chinchompa", "black chinchompa", "hunter cape", "hunter hood", "stamina potion", "spottier cape", "spotted cape", "ring of endurance", "graceful", "hunter's spear", "royal seed pod", "amulet of glory", "ring of wealth", "dinh's bulwark", "black d'hide", "karil's", "prayer potion", "super restore", "saradomin brew", "steel dart", "imp-in-a-box", "knife", "teak logs", "mahogany logs", "hunter potion", "anti venom"),
            "chinchompa hunting", "red chins", "black chins", "chin hunting", "chinchompa", "chin hunt");

        // Wilderness and PvP
        registry.add("Revenant caves kit", any(names("bracelet of ethereum", "revenant ether", "webweaver bow", "craw's bow", "viggora's chainmace", "ursine chainmace", "thammaron's sceptre", "accursed sceptre", "looting bag", "salve amulet", "amulet of avarice", "blighted", "revenant cave teleport", "royal seed pod", "amulet of glory", "ring of wealth", "burning amulet", "ring of recoil", "ring of suffering", "black d'hide", "super restore", "antipoison", "stamina potion", "sanfew serum"), WILDY, HEALS, PRAYER),
            "revenants", "revenant caves", "rev caves", "revs prep", "revs");
        registry.add("Calvar'ion kit", any(names("ursine chainmace", "viggora's chainmace", "viggoras chainmace", "salve amulet", "dragon mace", "inquisitor's mace", "blighted", "royal seed pod", "burning amulet", "looting bag", "salve amulet(e)", "salve amulet(ei)", "monk's robe", "black d'hide", "ring of dueling", "stamina potion", "sanfew serum", "drakan's medallion", "anti venom", "rune pouch", "earth rune", "nature rune"), WILDY, HEALS, PRAYER, MELEE_BOOST),
            "calvarion", "calvar'ion", "calvarion prep", "calv");
        registry.add("Spindel kit", any(names("ursine chainmace", "viggora's chainmace", "dragon mace", "inquisitor's mace", "webweaver bow", "blighted", "antipoison", "anti venom", "antivenom", "looting bag", "serpentine helm", "anti venom+", "antidote++", "bow of faerdhinen", "dragon defender", "ranging potion", "super restore", "royal seed pod", "burning amulet", "stamina potion", "blighted super restore"), WILDY, HEALS, PRAYER, MELEE_BOOST),
            "spindel", "spindel prep");
        registry.add("Artio kit", any(names("webweaver bow", "craw's bow", "bow of faerdhinen", "crossbow", "blood rune", "death rune", "water rune", "blighted", "looting bag", "nature rune", "earth rune", "law rune", "blighted ice sack", "blighted entangle sack", "royal seed pod", "burning amulet", "stamina potion", "crystal armour", "ice ancient sceptre", "void knight", "blighted super restore"), WILDY, HEALS, PRAYER, RANGE_BOOST),
            "artio", "artio prep");
        registry.add("Chaos Elemental kit", any(names("webweaver bow", "craw's bow", "bow of faerdhinen", "black d'hide", "looting bag", "blighted", "royal seed pod", "burning amulet", "stamina potion", "sack of potatoes", "sack of cabbages", "antipoison", "prayer potion", "super restore", "blighted super restore", "ring of recoil", "ring of suffering", "dragon crossbow"), WILDY, HEALS, PRAYER, RANGE_BOOST),
            "chaos elemental", "chaosele", "chaos ele");
        registry.add("Chaos Altar prayer kit", any(names("dragon bones", "superior dragon bones", "wyvern bones", "hydra bones", "dagannoth bones", "burning amulet", "wilderness sword", "looting bag", "royal seed pod", "stamina potion", "big bones", "lava dragon bones", "wyrm bones", "babydragon bones", "demonic ashes", "malicious ashes", "infernal ashes", "black d'hide", "prayer potion", "super restore"), WILDY, HEALS),
            "chaos altar", "wildy altar", "wilderness altar");
        registry.add("Wilderness bosses kit", any(names("webweaver bow", "craw's bow", "ursine chainmace", "viggora's chainmace", "accursed sceptre", "thammaron's sceptre", "salve amulet", "blighted", "looting bag", "burning amulet", "royal seed pod", "salve amulet(ei)", "salve amulet(e)", "dragon mace", "blighted super restore", "super restore", "stamina potion", "anti venom", "antipoison", "black d'hide", "rune pouch", "nature rune", "earth rune", "revenant ether", "bracelet of ethereum"), WILDY, HEALS, PRAYER),
            "wildy bosses", "wilderness bosses", "wildybosses");
        registry.add("Wilderness escape kit", any(names("royal seed pod", "amulet of glory", "ring of wealth", "ring of dueling", "burning amulet", "wilderness sword", "blood rune", "death rune", "water rune", "saradomin brew", "black d'hide", "looting bag", "stamina potion", "super energy potion", "super restore", "sanfew serum", "antidote++", "dinh's bulwark", "elysian spirit shield", "mage's book", "earth rune", "nature rune", "law rune", "rune pouch", "divine rune pouch", "karil's", "ring of recoil", "ring of suffering"), WILDY, HEALS, PRAYER),
            "wildy escape", "wilderness escape", "pvp escape");
        registry.add("Anti-PK kit", any(names("dragon claws", "voidwaker", "abyssal dagger", "dragon dagger", "granite maul", "armadyl godsword", "black d'hide", "blighted", "rune pouch", "looting bag", "royal seed pod", "ancient godsword", "ice ancient sceptre", "kodai wand", "ancient staff", "blood rune", "death rune", "water rune", "earth rune", "nature rune", "law rune", "antidote++", "sanfew serum", "saradomin brew", "super restore", "ring of recoil", "revenant cave teleport"), WILDY, HEALS, PRAYER),
            "anti pk", "antipk", "antipking");

        // Clues and quests
        registry.add("Clue solving kit", names("spade", "clue scroll", "strange device", "light box", "puzzle box", "challenge scroll", "rope", "tinderbox", "dramen staff", "lunar staff", "strange old lockpick", "games necklace", "skills necklace", "ring of wealth", "amulet of glory", "teleport to house", "stamina potion", "explorer's ring", "ardougne cloak", "ectophial", "clue box", "scroll sack", "book of the dead", "ring of visibility", "ring of shadows", "ghostspeak amulet", "teleport crystal", "royal seed pod", "burning amulet", "wilderness sword", "rune pouch", "ring of dueling", "pharaoh's sceptre", "quest point cape", "achievement diary cape"),
            "clue kit", "cluekit", "clue prep", "clue solving");
        registry.add("Easy clue kit", names("clue scroll (easy)", "scroll box (easy)", "spade", "teleport to house", "stamina potion", "games necklace", "amulet of glory", "ring of dueling", "staff of air", "explorer's ring", "ardougne cloak", "skills necklace", "dramen staff", "lunar staff", "ectophial", "falador teleport", "varrock teleport", "lumbridge teleport", "camelot teleport", "large spade"),
            "easy clue kit", "easy clue prep", "easyclue");
        registry.add("Medium clue kit", names("clue scroll (medium)", "scroll box (medium)", "spade", "teleport to house", "stamina potion", "games necklace", "skills necklace", "amulet of glory", "dramen staff", "ring of dueling", "ectophial", "ardougne cloak", "explorer's ring", "varrock teleport", "falador teleport", "camelot teleport", "xeric's talisman", "slayer ring", "quest point cape", "strange device", "light box", "puzzle box", "large spade"),
            "medium clue kit", "medium clue prep", "mediumclue");
        registry.add("Hard clue kit", any(names("clue scroll (hard)", "scroll box (hard)", "spade", "stamina potion", "royal seed pod", "burning amulet", "amulet of glory", "games necklace", "teleport to house", "rune pouch", "strange device", "clue box", "wilderness sword", "puzzle box", "light box", "dramen staff", "lunar staff", "ring of dueling", "slayer ring", "skills necklace", "ectophial", "ranging potion", "sanfew serum", "book of the dead"), HEALS, PRAYER),
            "hard clue kit", "hard clue prep", "hardclue");
        registry.add("Elite clue kit", any(names("clue scroll (elite)", "scroll box (elite)", "spade", "stamina potion", "rune pouch", "strange device", "royal seed pod", "burning amulet", "amulet of glory", "teleport to house", "clue box", "wilderness sword", "puzzle box", "light box", "challenge scroll", "dramen staff", "lunar staff", "ring of dueling", "skills necklace", "slayer ring", "ectophial", "ranging potion", "sanfew serum", "book of the dead", "quest point cape", "achievement diary cape"), HEALS, PRAYER),
            "elite clue kit", "elite clue prep", "eliteclue");
        registry.add("Master clue kit", any(names("clue scroll (master)", "scroll box (master)", "spade", "stamina potion", "rune pouch", "strange device", "light box", "puzzle box", "pharaoh's sceptre", "dramen staff", "royal seed pod", "teleport to house", "clue box", "wilderness sword", "challenge scroll", "ring of visibility", "ring of shadows", "ghostspeak amulet", "scroll sack", "lunar staff", "ring of dueling", "skills necklace", "slayer ring", "quest point cape", "achievement diary cape", "burning amulet", "ancient staff"), HEALS, PRAYER),
            "master clue kit", "master clue prep", "masterclue");
        registry.add("General quest preparation", any(names("rope", "spade", "knife", "chisel", "tinderbox", "hammer", "bucket of water", "bucket", "pot", "bowl", "needle", "thread", "axe", "pickaxe", "antipoison", "stamina potion", "rune pouch", "coins", "teleport to house", "amulet of glory", "games necklace", "ring of dueling", "light source", "lantern", "shears", "mortar and pestle", "red dye", "blue dye", "yellow dye", "empty vial", "vial of water", "swamp tar", "bullseye lantern", "oil lantern", "antidote++", "law rune", "air rune", "earth rune", "water rune", "fire rune", "dramen staff", "lunar staff", "ghostspeak amulet", "super restore"), HEALS, PRAYER),
            "quest prep", "questprep", "quest toolkit");
        registry.add("Quest travel kit", any(names("teleport to house", "varrock teleport", "falador teleport", "ardougne teleport", "camelot teleport", "lumbridge teleport", "ectophial", "games necklace", "ring of dueling", "amulet of glory", "skills necklace", "xeric's talisman", "dramen staff", "lunar staff", "quest point cape", "royal seed pod", "pharaoh's sceptre", "ardougne cloak", "explorer's ring", "slayer ring", "drakan's medallion", "digsite pendant", "book of the dead", "kharedst's memoirs", "morytania legs", "desert amulet", "achievement diary cape", "ring of wealth", "necklace of passage"), ENERGY),
            "quest travel", "quest teleports", "questtravel");
        registry.add("Quest combat supplies", any(names("rune pouch", "anti dragon shield", "dragonfire shield", "antifire", "antipoison", "antivenom", "sanfew serum", "slayer helmet", "magic shortbow", "rune crossbow", "prayer potion", "super restore", "super combat potion", "ranging potion", "dragon defender", "rune defender", "dragon crossbow", "dragon dagger", "mirror shield", "elemental shield", "blood rune", "death rune", "water rune", "chaos rune", "air rune", "law rune", "ruby bolts", "diamond bolts", "antidote++", "amulet of fury", "ring of suffering", "swordfish", "stamina potion"), HEALS, BREWS),
            "quest combat", "quest fight prep", "questcombat");
        registry.add("Fairy ring travel kit", names("dramen staff", "lunar staff", "quest point cape", "ardougne cloak", "explorer's ring", "teleport to house", "stamina potion", "energy potion", "slayer ring", "slayer ring (eternal)", "necklace of passage", "salve graveyard teleport", "achievement diary cape", "ring of dueling", "digsite pendant"),
            "fairy rings", "fairy ring kit", "fairyrings");

    }
}
