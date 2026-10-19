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
    private static final Predicate<ItemFacts> PRAYER = names("prayer potion", "super restore", "sanfew serum", "holy wrench", "blighted super restore");
    private static final Predicate<ItemFacts> BREWS = names("saradomin brew", "guthix rest");
    private static final Predicate<ItemFacts> ENERGY = names("stamina potion", "super energy", "energy potion", "ring of endurance", "graceful");
    private static final Predicate<ItemFacts> MELEE_BOOST = names("super combat potion", "divine super combat", "super attack", "super strength", "combat potion");
    private static final Predicate<ItemFacts> RANGE_BOOST = names("ranging potion", "divine ranging", "bastion potion", "divine bastion");
    private static final Predicate<ItemFacts> MAGE_BOOST = names("magic potion", "divine magic", "imbued heart", "saturated heart");
    private static final Predicate<ItemFacts> SLAYER = names("slayer helmet", "slayer helm", "black mask", "bracelet of slaughter", "expeditious bracelet");
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

    static void register(Registrar registry)
    {
        // Bosses and raids
        registry.add("Zulrah preparation", any(names("anti venom", "antivenom", "ring of suffering", "ring of recoil", "zul andra teleport", "zulrah's scales", "toxic blowpipe", "bow of faerdhinen", "crystal armour", "crystal armor", "armadyl", "masori", "ahrim", "ancestral", "trident of the swamp", "trident of the seas", "sanguinesti staff", "occult necklace", "tormented bracelet", "tome of fire", "rune pouch"), HEALS, PRAYER, BREWS, RANGE_BOOST, MAGE_BOOST),
            "zulrah", "zulrah prep", "zulrah gear");
        registry.add("Vorkath preparation", any(names("dragon hunter crossbow", "dragon hunter lance", "dragonfire ward", "dragonfire shield", "anti dragon shield", "anti-dragon shield", "antifire", "super antifire", "salve amulet", "ruby dragon bolts", "diamond dragon bolts", "bandos godsword", "dragon warhammer", "void knight", "elite void", "slayer staff", "crumble undead", "lunar isle teleport", "waterbirth teleport", "rune pouch"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST),
            "vorkath", "vorkath prep", "vorkath gear");
        registry.add("Phantom Muspah preparation", any(names("sapphire dragon bolts", "sapphire bolts", "crossbow", "bow of faerdhinen", "crystal armour", "crystal armor", "masori", "ancestral", "ahrim", "trident", "tumeken's shadow", "ancient sceptre", "ancient staff", "ring of shadows", "icy basalt", "rune pouch", "blood rune", "death rune", "water rune", "soul rune"), HEALS, PRAYER, BREWS, RANGE_BOOST, MAGE_BOOST, ENERGY),
            "muspah", "phantom muspah", "muspah prep");
        registry.add("Fight Caves preparation", any(names("toxic blowpipe", "dragon dart", "rune dart", "amethyst dart", "rune arrow", "dragon arrow", "crystal bow", "bow of faerdhinen", "twisted bow", "crossbow", "diamond dragon bolts", "ava's assembler", "ava's accumulator", "masori", "armadyl", "blessed d'hide", "holy wrench"), HEALS, PRAYER, BREWS, RANGE_BOOST),
            "jad", "fight caves", "jad prep", "fight caves prep");
        registry.add("Inferno preparation", any(names("twisted bow", "bow of faerdhinen", "crystal armour", "crystal armor", "ancestral", "masori", "justiciar", "toxic blowpipe", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "rune pouch", "elysian spirit shield", "crystal shield", "occult necklace"), HEALS, PRAYER, BREWS, RANGE_BOOST, MAGE_BOOST),
            "inferno", "inferno prep");
        registry.add("Nex preparation", any(names("zaryte crossbow", "armadyl crossbow", "dragon crossbow", "ruby dragon bolts", "diamond dragon bolts", "torva", "masori", "armadyl", "ancient ceremonial", "frozen key", "ecumenical key", "ancient godsword", "bandos godsword", "fang", "osmumten's fang", "lightbearer", "blood fury", "amulet of blood fury", "dragon warhammer"), HEALS, PRAYER, BREWS, RANGE_BOOST, MELEE_BOOST, ENERGY),
            "nex", "nex prep", "nex trip");
        registry.add("Nightmare preparation", any(names("inquisitor", "bludgeon", "scythe of vitur", "sanguinesti staff", "harmonised nightmare staff", "harmonized nightmare staff", "trident", "mace", "crush weapon", "crush weapons", "eldritch nightmare staff", "slepey tablet", "drakan's medallion", "ectophial", "dragon warhammer"), HEALS, PRAYER, BREWS, MELEE_BOOST, ENERGY),
            "nightmare boss", "nightmare prep", "phosani", "phosani's nightmare");
        registry.add("Cerberus preparation", any(names("primordial crystal", "pegasian crystal", "pegasian crystal", "eternal crystal", "spectral spirit shield", "arclight", "emberlight", "scythe of vitur", "abyssal bludgeon", "dragon claws", "slayer helmet", "black mask", "key master teleport"), HEALS, PRAYER, BREWS, MELEE_BOOST),
            "cerberus", "cerberus prep", "cerb");
        registry.add("Alchemical Hydra preparation", any(names("dragon hunter crossbow", "dragon hunter lance", "twisted bow", "bow of faerdhinen", "toxic blowpipe", "ranging potion", "antipoison", "slayer helmet", "brimstone ring", "brimstone boots", "konar", "kourend"), HEALS, PRAYER, BREWS, RANGE_BOOST),
            "hydra boss", "alchemical hydra", "hydra prep", "hydra");
        registry.add("Scurrius preparation", any(names("bone mace", "bone shortbow", "bone staff", "rat bone", "ratbone", "rune pouch", "scurrius spine", "varrock teleport", "prayer potion"), HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST),
            "scurrius", "scurrius prep");
        registry.add("CoX raid preparation", any(names("dragon hunter lance", "dragon hunter crossbow", "twisted bow", "bow of faerdhinen", "scythe of vitur", "ancestral", "masori", "trident", "occult necklace", "dragon warhammer", "elder maul", "dragon claws", "xeric's talisman", "xeric's honour", "xeric's honor", "rune pouch", "stamina potion", "antipoison"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST, MAGE_BOOST),
            "cox prep", "chambers prep", "olm prep");
        registry.add("ToB raid preparation", any(names("scythe of vitur", "sanguinesti staff", "toxic blowpipe", "bandos godsword", "dragon warhammer", "dragon claws", "ghrazi rapier", "avernic defender", "void knight", "elite void", "dawnbringer", "blood rune", "death rune", "rune pouch", "drakan's medallion"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST),
            "tob prep", "theatre of blood prep", "theater of blood prep");
        registry.add("ToA raid preparation", any(names("osmumten's fang", "keris partisan", "tumeken's shadow", "bow of faerdhinen", "toxic blowpipe", "lightbearer", "masori", "ancestral", "dragon claws", "bandos godsword", "dragon warhammer", "rune pouch", "pharaoh's sceptre", "menaphite remedy", "stamina potion", "thread of elidinis"), HEALS, PRAYER, BREWS, MELEE_BOOST, RANGE_BOOST, MAGE_BOOST),
            "toa prep", "tombs of amascut prep", "raids 3 prep");
        registry.add("Barrows run preparation", any(names("barrows teleport", "drakan's medallion", "morytania legs", "spade", "strange old lockpick", "trident of the seas", "trident of the swamp", "ibans staff", "iban's staff", "slayer staff", "occult necklace", "rune pouch", "prayer potion"), HEALS, PRAYER, ENERGY),
            "barrows prep", "barrows run", "barrows trip");
        registry.add("God Wars trip preparation", any(names("ecumenical key", "frozen key", "trollheim teleport", "stony basalt", "icy basalt", "rope", "bandos", "armadyl", "saradomin", "zamorak", "ancient ceremonial", "god blessing", "blessed d'hide", "amulet of blood fury", "dragon warhammer", "bandos godsword"), HEALS, PRAYER, BREWS, ENERGY, MELEE_BOOST, RANGE_BOOST),
            "gwd prep", "god wars prep", "godwars prep");

        // Slayer tasks
        registry.add("Gargoyles task", any(names("rock hammer", "rock thrownhammer", "granite hammer", "granite maul", "dual macuahuitl", "slayer ring", "guthan", "abyssal whip", "rune pouch"), SLAYER, HEALS, PRAYER, MELEE_BOOST),
            "gargoyles", "gargoyle task", "gargoyles prep", "gargoyle", "gargs");
        registry.add("Dust devils task", any(names("facemask", "masked earmuffs", "slayer helmet", "black mask", "ancient staff", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "ice rune", "rune pouch", "bonecrusher", "ash sanctifier"), PRAYER, MAGE_BOOST),
            "dust devils", "dustdevils", "dust devil task", "dust devil");
        registry.add("Nechryaels task", any(names("slayer helmet", "black mask", "ancient staff", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "ice rune", "rune pouch", "bonecrusher", "ash sanctifier", "bracelet of slaughter"), PRAYER, MAGE_BOOST),
            "nechryaels", "nechs", "nech task", "nechryael task", "nech");
        registry.add("Aberrant spectres task", any(names("nose peg", "slayer helmet", "black mask", "salve graveyard teleport", "slayer ring", "herb sack", "ranging potion"), HEALS, PRAYER, RANGE_BOOST),
            "aberrant spectres", "spectres task", "aberrants", "spectres");
        registry.add("Basilisks task", any(names("mirror shield", "v's shield", "slayer helmet", "black mask", "slayer ring", "rune pouch", "basilisk jaw"), SLAYER, HEALS, PRAYER, MELEE_BOOST),
            "basilisks", "basilisk knights", "basilisk task", "basilisk");
        registry.add("Kurasks task", any(names("leaf bladed sword", "leaf bladed axe", "leaf bladed spear", "leaf-bladed sword", "leaf-bladed axe", "broad bolts", "broad arrows", "magic shortbow", "slayer helmet", "black mask", "herb sack"), SLAYER, HEALS, PRAYER),
            "kurasks", "kurask task", "kurask");
        registry.add("Skeletal wyverns task", any(names("elemental shield", "mind shield", "ancient wyvern shield", "dragonfire shield", "dragonfire ward", "slayer helmet", "black mask", "crossbow", "ranged potion", "anti venom"), HEALS, PRAYER, RANGE_BOOST),
            "skeletal wyverns", "wyvern task", "skeletal wyvern");
        registry.add("Abyssal demons task", any(names("arclight", "emberlight", "abyssal whip", "abyssal dagger", "slayer helmet", "black mask", "scythe of vitur", "ancient staff", "blood rune", "death rune", "water rune", "rune pouch", "bracelet of slaughter"), HEALS, PRAYER, MELEE_BOOST),
            "abyssal demons", "abby demons", "abyssal task", "abbys");
        registry.add("Demonic gorillas trip", any(names("arclight", "emberlight", "demonbane", "toxic blowpipe", "bow of faerdhinen", "crossbow", "rune pouch", "royal seed pod", "ring of dueling", "slayer helmet", "black mask"), HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST),
            "demonic gorillas", "demonics", "demonic gorilla prep", "gorillas");
        registry.add("Burst and barrage Slayer", any(names("ancient staff", "ancient sceptre", "kodai wand", "master wand", "blood rune", "death rune", "water rune", "chaos rune", "rune pouch", "occult necklace", "ancestral", "virtus", "prayer potion", "bracelet of slaughter"), PRAYER, MAGE_BOOST),
            "barrage tasks", "barrage", "barrage slayer", "burst tasks", "burst slayer", "burst");
        registry.add("Cannon Slayer trips", any(names("cannonball", "dwarf multicannon", "cannon base", "cannon stand", "cannon barrels", "cannon furnace", "double ammo mould", "slayer helmet", "black mask", "stamina potion", "expeditious bracelet", "bracelet of slaughter"), HEALS, PRAYER),
            "cannon tasks", "cannon slayer", "cannon trip", "cannon");
        registry.add("Kraken task", any(names("trident of the seas", "trident of the swamp", "sanguinesti staff", "occult necklace", "ancestral", "ahrim", "tome of fire", "slayer helmet", "black mask", "fishing explosive", "magic potion"), HEALS, PRAYER, MAGE_BOOST),
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
        registry.add("Shades of Mort'ton kit", names("olive oil", "sacred oil", "pyre log", "pyre logs", "shade remains", "serum 207", "silver key", "bronze key", "black key", "steel key", "gold key", "yew logs", "magic logs", "redwood logs", "tinderbox"),
            "shades of mortton", "shades of mort'ton", "shades minigame", "shades");
        registry.add("Fishing Trawler kit", names("swamp paste", "rope", "bailing bucket", "bucket", "angler outfit", "angler hat", "angler top", "angler waders", "angler boots", "fishing cape", "fishing hood"),
            "fishing trawler", "trawler", "fishing trawler prep");
        registry.add("Volcanic Mine kit", any(names("pickaxe", "dragon pickaxe", "crystal pickaxe", "rune pickaxe", "numulite", "mining cape", "mining hood", "prospector", "stamina potion", "energy potion", "celestial ring", "ring of endurance"), HEALS),
            "volcanic mine", "volcanicmine", "vm mining", "vm");
        registry.add("Blast Furnace kit", names("iron ore", "gold ore", "coal", "mithril ore", "adamantite ore", "runite ore", "coal bag", "ice gloves", "goldsmith gauntlets", "stamina potion", "ring of endurance", "smiths' uniform", "graceful", "bucket of water"),
            "blast furnace", "blastfurnace", "bf smithing", "bf");

        // Farming and skilling
        registry.add("Farming contracts kit", names("spade", "rake", "seed dibber", "magic secateurs", "ultracompost", "bottomless compost bucket", "seed box", "gardening trowel", "farming guild teleport", "skills necklace", "seed", "sapling", "plant cure", "amulet of bounty", "farming cape"),
            "farming contracts", "farm contracts", "farm contract", "contracts");
        registry.add("Giant seaweed runs", names("seaweed spore", "giant seaweed", "diving apparatus", "fishbowl helmet", "merfolk trident", "brine sabre", "flippers", "drift net", "digsite pendant", "fossil island", "stamina potion", "spade", "ultracompost", "bottomless compost bucket"),
            "giant seaweed", "seaweed run", "seaweed farming");
        registry.add("Motherlode Mine kit", names("pickaxe", "prospector", "coal bag", "gem bag", "mining gloves", "expert mining gloves", "celestial ring", "mining cape", "stamina potion", "soft clay pack"),
            "motherlode mine", "motherlode", "mlm");
        registry.add("Bird snaring and hunter kit", names("bird snare", "hunter cape", "hunter hood", "stamina potion", "spotted cape", "spottier cape", "ring of endurance"),
            "bird snaring", "bird snare run", "hunter birds");
        registry.add("Chinchompa hunting kit", names("box trap", "chinchompa", "red chinchompa", "black chinchompa", "hunter cape", "hunter hood", "stamina potion", "spottier cape", "spotted cape", "ring of endurance", "graceful"),
            "chinchompa hunting", "red chins", "black chins", "chin hunting", "chinchompa", "chin hunt");

        // Wilderness and PvP
        registry.add("Revenant caves kit", any(names("bracelet of ethereum", "revenant ether", "webweaver bow", "craw's bow", "viggora's chainmace", "ursine chainmace", "thammaron's sceptre", "accursed sceptre", "looting bag", "salve amulet", "amulet of avarice", "blighted"), WILDY, HEALS, PRAYER),
            "revenants", "revenant caves", "rev caves", "revs prep", "revs");
        registry.add("Calvar'ion kit", any(names("ursine chainmace", "viggora's chainmace", "viggoras chainmace", "salve amulet", "dragon mace", "inquisitor's mace", "blighted", "royal seed pod", "burning amulet", "looting bag"), WILDY, HEALS, PRAYER, MELEE_BOOST),
            "calvarion", "calvar'ion", "calvarion prep", "calv");
        registry.add("Spindel kit", any(names("ursine chainmace", "viggora's chainmace", "dragon mace", "inquisitor's mace", "webweaver bow", "blighted", "antipoison", "anti venom", "antivenom", "looting bag"), WILDY, HEALS, PRAYER, MELEE_BOOST),
            "spindel", "spindel prep");
        registry.add("Artio kit", any(names("webweaver bow", "craw's bow", "bow of faerdhinen", "crossbow", "entangle", "ice barrage", "blood rune", "death rune", "water rune", "blighted", "looting bag"), WILDY, HEALS, PRAYER, RANGE_BOOST),
            "artio", "artio prep");
        registry.add("Chaos Elemental kit", any(names("webweaver bow", "craw's bow", "bow of faerdhinen", "black d'hide", "looting bag", "blighted", "royal seed pod", "burning amulet", "stamina potion"), WILDY, HEALS, PRAYER, RANGE_BOOST),
            "chaos elemental", "chaosele", "chaos ele");
        registry.add("Chaos Altar prayer kit", any(names("dragon bones", "superior dragon bones", "wyvern bones", "hydra bones", "dagannoth bones", "burning amulet", "wilderness sword", "looting bag", "royal seed pod", "stamina potion"), WILDY, HEALS),
            "chaos altar", "wildy altar", "wilderness altar");
        registry.add("Wilderness bosses kit", any(names("webweaver bow", "craw's bow", "ursine chainmace", "viggora's chainmace", "accursed sceptre", "thammaron's sceptre", "salve amulet", "blighted", "looting bag", "burning amulet", "royal seed pod"), WILDY, HEALS, PRAYER),
            "wildy bosses", "wilderness bosses", "wildybosses");
        registry.add("Wilderness escape kit", any(names("royal seed pod", "amulet of glory", "ring of wealth", "ring of dueling", "burning amulet", "wilderness sword", "entangle", "ice barrage", "blood rune", "death rune", "water rune", "saradomin brew", "black d'hide", "looting bag"), WILDY, HEALS, PRAYER),
            "wildy escape", "wilderness escape", "pvp escape");
        registry.add("Anti-PK kit", any(names("dragon claws", "voidwaker", "abyssal dagger", "dragon dagger", "granite maul", "ags", "armadyl godsword", "ice barrage", "entangle", "black d'hide", "blighted", "rune pouch", "looting bag", "royal seed pod"), WILDY, HEALS, PRAYER),
            "anti pk", "antipk", "antipking");

        // Clues and quests
        registry.add("Clue solving kit", names("spade", "clue scroll", "strange device", "light box", "puzzle box", "challenge scroll", "rope", "tinderbox", "dramen staff", "lunar staff", "strange old lockpick", "games necklace", "skills necklace", "ring of wealth", "amulet of glory", "teleport to house", "stamina potion", "explorer's ring", "ardougne cloak", "ectophial"),
            "clue kit", "cluekit", "clue prep", "clue solving");
        registry.add("Easy clue kit", names("clue scroll (easy)", "reward casket (easy)", "spade", "teleport to house", "stamina potion", "games necklace", "amulet of glory", "ring of dueling", "staff of air"),
            "easy clue kit", "easy clue prep", "easyclue");
        registry.add("Medium clue kit", names("clue scroll (medium)", "reward casket (medium)", "spade", "teleport to house", "stamina potion", "games necklace", "skills necklace", "amulet of glory", "fairy ring", "dramen staff"),
            "medium clue kit", "medium clue prep", "mediumclue");
        registry.add("Hard clue kit", any(names("clue scroll (hard)", "reward casket (hard)", "spade", "stamina potion", "royal seed pod", "burning amulet", "amulet of glory", "games necklace", "teleport to house", "rune pouch", "strange device"), HEALS, PRAYER),
            "hard clue kit", "hard clue prep", "hardclue");
        registry.add("Elite clue kit", any(names("clue scroll (elite)", "reward casket (elite)", "spade", "stamina potion", "rune pouch", "strange device", "royal seed pod", "burning amulet", "amulet of glory", "teleport to house"), HEALS, PRAYER),
            "elite clue kit", "elite clue prep", "eliteclue");
        registry.add("Master clue kit", any(names("clue scroll (master)", "reward casket (master)", "spade", "stamina potion", "rune pouch", "strange device", "light box", "puzzle box", "pharaoh's sceptre", "dramen staff", "royal seed pod", "teleport to house"), HEALS, PRAYER),
            "master clue kit", "master clue prep", "masterclue");
        registry.add("General quest preparation", any(names("rope", "spade", "knife", "chisel", "tinderbox", "hammer", "bucket of water", "bucket", "pot", "bowl", "needle", "thread", "axe", "pickaxe", "antipoison", "stamina potion", "rune pouch", "coins", "teleport to house", "amulet of glory", "games necklace", "ring of dueling", "light source", "lantern"), HEALS, PRAYER),
            "quest prep", "questprep", "quest toolkit");
        registry.add("Quest travel kit", any(names("teleport to house", "varrock teleport", "falador teleport", "ardougne teleport", "camelot teleport", "lumbridge teleport", "ectophial", "games necklace", "ring of dueling", "amulet of glory", "skills necklace", "xeric's talisman", "dramen staff", "lunar staff", "fairy ring", "quest point cape", "royal seed pod", "pharaoh's sceptre"), ENERGY),
            "quest travel", "quest teleports", "questtravel");
        registry.add("Quest combat supplies", any(names("rune pouch", "anti dragon shield", "dragonfire shield", "antifire", "antipoison", "antivenom", "sanfew serum", "slayer helmet", "magic shortbow", "rune crossbow", "prayer potion", "super restore", "super combat potion", "ranging potion"), HEALS, BREWS),
            "quest combat", "quest fight prep", "questcombat");
        registry.add("Fairy ring travel kit", names("dramen staff", "lunar staff", "quest point cape", "fairy enchantment", "ardougne cloak", "explorer's ring", "teleport to house", "stamina potion", "energy potion"),
            "fairy rings", "fairy ring kit", "fairyrings");

    }
}
