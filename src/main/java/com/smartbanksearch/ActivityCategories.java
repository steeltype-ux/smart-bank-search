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
            "cerberus", "cerberus prep");
        registry.add("Alchemical Hydra preparation", any(names("dragon hunter crossbow", "dragon hunter lance", "twisted bow", "bow of faerdhinen", "toxic blowpipe", "ranging potion", "antipoison", "slayer helmet", "brimstone ring", "brimstone boots", "konar", "kourend"), HEALS, PRAYER, BREWS, RANGE_BOOST),
            "hydra boss", "alchemical hydra", "hydra prep");
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
            "gargoyles", "gargoyle task", "gargoyles prep");
        registry.add("Dust devils task", any(names("facemask", "masked earmuffs", "slayer helmet", "black mask", "ancient staff", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "ice rune", "rune pouch", "bonecrusher", "ash sanctifier"), PRAYER, MAGE_BOOST),
            "dust devils", "dustdevils", "dust devil task");
        registry.add("Nechryaels task", any(names("slayer helmet", "black mask", "ancient staff", "ancient sceptre", "kodai wand", "blood rune", "death rune", "water rune", "ice rune", "rune pouch", "bonecrusher", "ash sanctifier", "bracelet of slaughter"), PRAYER, MAGE_BOOST),
            "nechryaels", "nechs", "nech task", "nechryael task");
        registry.add("Aberrant spectres task", any(names("nose peg", "slayer helmet", "black mask", "salve graveyard teleport", "slayer ring", "herb sack", "ranging potion"), HEALS, PRAYER, RANGE_BOOST),
            "aberrant spectres", "spectres task", "aberrants");
        registry.add("Basilisks task", any(names("mirror shield", "v's shield", "slayer helmet", "black mask", "slayer ring", "rune pouch", "basilisk jaw"), SLAYER, HEALS, PRAYER, MELEE_BOOST),
            "basilisks", "basilisk knights", "basilisk task");
        registry.add("Kurasks task", any(names("leaf bladed sword", "leaf bladed axe", "leaf bladed spear", "leaf-bladed sword", "leaf-bladed axe", "broad bolts", "broad arrows", "magic shortbow", "slayer helmet", "black mask", "herb sack"), SLAYER, HEALS, PRAYER),
            "kurasks", "kurask task");
        registry.add("Skeletal wyverns task", any(names("elemental shield", "mind shield", "ancient wyvern shield", "dragonfire shield", "dragonfire ward", "slayer helmet", "black mask", "crossbow", "ranged potion", "anti venom"), HEALS, PRAYER, RANGE_BOOST),
            "skeletal wyverns", "wyvern task", "skeletal wyvern");
        registry.add("Abyssal demons task", any(names("arclight", "emberlight", "abyssal whip", "abyssal dagger", "slayer helmet", "black mask", "scythe of vitur", "ancient staff", "blood rune", "death rune", "water rune", "rune pouch", "bracelet of slaughter"), HEALS, PRAYER, MELEE_BOOST),
            "abyssal demons", "abby demons", "abyssal task");
        registry.add("Demonic gorillas trip", any(names("arclight", "emberlight", "demonbane", "toxic blowpipe", "bow of faerdhinen", "crossbow", "rune pouch", "royal seed pod", "ring of dueling", "slayer helmet", "black mask"), HEALS, PRAYER, MELEE_BOOST, RANGE_BOOST),
            "demonic gorillas", "demonics", "demonic gorilla prep");
        registry.add("Burst and barrage Slayer", any(names("ancient staff", "ancient sceptre", "kodai wand", "master wand", "blood rune", "death rune", "water rune", "chaos rune", "rune pouch", "occult necklace", "ancestral", "virtus", "prayer potion", "bracelet of slaughter"), PRAYER, MAGE_BOOST),
            "barrage tasks", "barrage", "barrage slayer", "burst tasks", "burst slayer");
        registry.add("Cannon Slayer trips", any(names("cannonball", "dwarf multicannon", "cannon base", "cannon stand", "cannon barrels", "cannon furnace", "slayer helmet", "black mask", "stamina potion", "expeditious bracelet", "bracelet of slaughter"), HEALS, PRAYER),
            "cannon tasks", "cannon slayer", "cannon trip");
        registry.add("Kraken task", any(names("trident of the seas", "trident of the swamp", "sanguinesti staff", "occult necklace", "ancestral", "ahrim", "tome of fire", "slayer helmet", "black mask", "fishing explosive", "magic potion"), HEALS, PRAYER, MAGE_BOOST),
            "kraken boss", "kraken task", "cave kraken");

        // Minigames
        registry.add("Wintertodt kit", any(names("tinderbox", "bruma torch", "pyromancer", "clue hunter", "warm gloves", "firemaking cape", "firemaking hood", "axe", "hammer", "knife", "regen bracelet", "hitpoints cape", "yak hide", "santa hat", "jug of wine", "rejuvenation potion"), WINTERTODT_MULTIBITE_FOOD, HEALS),
            "wintertodt", "wintertodt prep", "wt prep");
        registry.add("Tempoross kit", any(names("harpoon", "hammer", "rope", "bucket", "bucket of water", "angler", "spirit angler", "fishing cape", "fishing hood", "fishing barrel", "fish barrel", "infernal harpoon", "dragon harpoon", "crystal harpoon", "imcando hammer", "tackle box", "ring of endurance"), ENERGY),
            "tempoross", "tempoross prep");
        registry.add("Guardians of the Rift kit", any(names("pickaxe", "chisel", "essence pouch", "colossal pouch", "raiments of the eye", "abyssal lantern", "abyssal needle", "binding necklace", "elemental talisman", "catalytic talisman", "celestial ring", "runecraft cape", "runecrafting cape", "hat of the eye"), ENERGY),
            "gotr", "guardians of the rift", "guardians rift");
        // Metal material candidates plus only the relevant worn/portable equipment.
        // Ice gloves / smiths gloves (i) are convenient for handling the hot preform.
        // Buckets are optional because the minigame provides them on site.
        registry.add("Giant's Foundry kit", any(FOUNDRY_METAL, FOUNDRY_EQUIPMENT),
            "giants foundry", "giantsfoundry", "giant foundry");
        registry.add("Mahogany Homes kit", any(names("plank", "oak plank", "teak plank", "mahogany plank", "steel bar", "saw", "amy's saw", "hammer", "imcando hammer", "plank sack", "rune pouch", "air rune", "astral rune", "cosmic rune", "varrock teleport", "ardougne teleport", "falador teleport", "xeric's talisman", "ring of wealth", "skills necklace", "teleport to house", "carpenter's outfit"), ENERGY),
            "mahogany homes", "mahoganyhomes", "mahogany homes prep");
        registry.add("Hallowed Sepulchre kit", names("hallowed grapple", "hallowed hammer", "hallowed focus", "hallowed ring", "hallowed symbol", "strange old lockpick", "graceful", "stamina potion", "vampyre dust", "plank", "nails", "ring of endurance", "drakan's medallion"),
            "hallowed sepulchre", "sepulchre", "sepulchre prep");
        registry.add("Pyramid Plunder kit", any(names("pharaoh's sceptre", "lockpick", "antipoison", "antidote", "serpentine helm", "stamina potion", "energy potion", "prayer potion", "super restore", "ring of dueling", "menaphite remedy"), HEALS),
            "pyramid plunder", "pyramidplunder", "plunder prep");
        registry.add("Tithe Farm kit", names("gricoller's can", "watering can", "seed dibber", "spade", "farmer's outfit", "farmer's strawhat", "farming cape", "farming hood", "stamina potion", "graceful", "ring of endurance"),
            "tithe farm", "tithefarm", "tithe farming");
        registry.add("Pest Control kit", any(names("void knight", "elite void", "dragon scimitar", "abyssal whip", "dragon defender", "chinchompa", "toxic blowpipe", "rune pouch", "prayer potion", "ranging potion", "super combat potion", "combat potion"), HEALS),
            "pest control", "pestcontrol", "pc minigame");
        registry.add("Barbarian Assault kit", any(names("fighter torso", "granite body", "penance", "barbarian assault", "horn of glory", "dragon scimitar", "abyssal whip", "ranged potion", "prayer potion", "rune pouch"), HEALS, MELEE_BOOST, RANGE_BOOST),
            "barbarian assault", "barb assault", "ba minigame");
        registry.add("Soul Wars kit", any(names("soul cape", "soul wars", "barrage", "ancient staff", "ancient sceptre", "blood rune", "death rune", "water rune", "rune pouch", "dragon claws", "toxic blowpipe", "black d'hide", "ranging potion", "super combat potion"), HEALS, PRAYER),
            "soul wars", "soulwars");
        registry.add("Nightmare Zone kit", any(names("dwarven rock cake", "locator orb", "absorption", "overload", "berserker ring", "black mask", "abyssal whip", "obsidian", "dharok", "rune pouch", "super combat potion", "ranging potion", "prayer potion"), HEALS),
            "nightmare zone", "nmz", "nightmarezone");
        registry.add("Shades of Mort'ton kit", names("olive oil", "sacred oil", "pyre log", "pyre logs", "shade remains", "serum 207", "silver key", "bronze key", "black key", "steel key", "gold key", "yew logs", "magic logs", "redwood logs", "tinderbox"),
            "shades of mortton", "shades of mort'ton", "shades minigame");
        registry.add("Fishing Trawler kit", names("swamp paste", "rope", "bailing bucket", "bucket", "angler outfit", "angler hat", "angler top", "angler waders", "angler boots", "fishing cape", "fishing hood"),
            "fishing trawler", "trawler", "fishing trawler prep");
        registry.add("Volcanic Mine kit", any(names("pickaxe", "dragon pickaxe", "crystal pickaxe", "rune pickaxe", "numulite", "mining cape", "mining hood", "prospector", "stamina potion", "energy potion", "celestial ring", "ring of endurance"), HEALS),
            "volcanic mine", "volcanicmine", "vm mining");
        registry.add("Blast Furnace kit", names("iron ore", "gold ore", "coal", "mithril ore", "adamantite ore", "runite ore", "coal bag", "ice gloves", "goldsmith gauntlets", "stamina potion", "ring of endurance", "smiths' uniform", "graceful", "bucket of water"),
            "blast furnace", "blastfurnace", "bf smithing");

        // Farming and skilling
        registry.add("Farming contracts kit", names("spade", "rake", "seed dibber", "magic secateurs", "ultracompost", "bottomless compost bucket", "seed box", "gardening trowel", "farming guild teleport", "skills necklace", "seed", "sapling", "plant cure", "amulet of bounty", "farming cape"),
            "farming contracts", "farm contracts", "farm contract");
        registry.add("Giant seaweed runs", names("seaweed spore", "giant seaweed", "diving apparatus", "fishbowl helmet", "merfolk trident", "brine sabre", "flippers", "drift net", "digsite pendant", "fossil island", "stamina potion", "spade", "ultracompost", "bottomless compost bucket"),
            "giant seaweed", "seaweed run", "seaweed farming");
        registry.add("Motherlode Mine kit", names("pickaxe", "prospector", "coal bag", "gem bag", "mining gloves", "expert mining gloves", "celestial ring", "mining cape", "stamina potion", "soft clay pack"),
            "motherlode mine", "motherlode", "mlm");
        registry.add("Bird snaring and hunter kit", names("bird snare", "hunter cape", "hunter hood", "stamina potion", "spotted cape", "spottier cape", "ring of endurance"),
            "bird snaring", "bird snare run", "hunter birds");
        registry.add("Chinchompa hunting kit", names("box trap", "chinchompa", "red chinchompa", "black chinchompa", "hunter cape", "hunter hood", "stamina potion", "spottier cape", "spotted cape", "ring of endurance", "graceful"),
            "chinchompa hunting", "red chins", "black chins", "chin hunting");

        // Wilderness and PvP
        registry.add("Revenant caves kit", any(names("bracelet of ethereum", "revenant ether", "webweaver bow", "craw's bow", "viggora's chainmace", "ursine chainmace", "thammaron's sceptre", "accursed sceptre", "looting bag", "salve amulet", "amulet of avarice", "blighted"), WILDY, HEALS, PRAYER),
            "revenants", "revenant caves", "rev caves", "revs prep");
        registry.add("Calvar'ion kit", any(names("ursine chainmace", "viggora's chainmace", "viggoras chainmace", "salve amulet", "dragon mace", "inquisitor's mace", "blighted", "royal seed pod", "burning amulet", "looting bag"), WILDY, HEALS, PRAYER, MELEE_BOOST),
            "calvarion", "calvar'ion", "calvarion prep");
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
