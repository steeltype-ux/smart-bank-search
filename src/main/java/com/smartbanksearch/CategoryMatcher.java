package com.smartbanksearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Offline, deterministic category rules. Never fetches data or reads user inventory.
 * The plugin supplies RuneLite's item metadata where available.
 *
 * Normal: `herb` ADDs semantic matches to vanilla RuneLite/Bank Tags search.
 * Exclusive: `@herb` displays only this category (overrides vanilla matches).
 * Composite: `@ranged boots` combines category predicates with AND.
 */
public final class CategoryMatcher
{
    private static final class Category
    {
        final String title;
        Predicate<ItemFacts> predicate;
        final List<String> aliases;
        Category(String title, Predicate<ItemFacts> predicate, String... aliases)
        {
            this.title = title;
            this.predicate = predicate;
            this.aliases = Collections.unmodifiableList(Arrays.asList(aliases));
        }
    }

    public static final class Query
    {
        private final String source;
        private final boolean exclusive;
        private final List<Category> categories;
        private Query(String source, boolean exclusive, List<Category> categories)
        {
            this.source = source;
            this.exclusive = exclusive;
            this.categories = categories;
        }
        public boolean isCategory() { return !categories.isEmpty(); }
        public boolean isExclusive() { return exclusive && isCategory(); }
        public boolean matches(ItemFacts item)
        {
            if (!isCategory() || item == null || normalize(item.name).isEmpty()) { return false; }
            for (Category category : categories)
            {
                if (!category.predicate.test(item)) { return false; }
            }
            return true;
        }
        public String getSource() { return source; }
        public String getCategoryName()
        {
            if (categories.isEmpty()) { return ""; }
            StringBuilder b = new StringBuilder();
            for (Category c : categories)
            {
                if (b.length() > 0) { b.append(" + "); }
                b.append(c.title);
            }
            return b.toString();
        }
    }

    private static final Map<String, Category> BY_ALIAS = new LinkedHashMap<>();
    private static final List<Category> CATEGORIES = new ArrayList<>();
    private static final Predicate<ItemFacts> EQUIPPED = f -> f.worn();
    private static final Predicate<ItemFacts> WEAPON = f -> f.slot == 3 || matchesName(f.name, "whip", "scimitar", "sword", "dagger", "mace", "staff", "wand", "bow", "crossbow", "spear", "hasta", "axe", "halberd", "trident", "blowpipe", "claws", "rapier", "maul", "lance", "sceptre", "salamander", "warhammer", "flail", "glaive");
    private static final Predicate<ItemFacts> MELEE = f -> (f.worn() && (f.meleeAttack > 0 || f.meleeStrength > 0)) ||
        matchesName(f.name, "torva", "bandos", "justiciar", "obsidian", "ferocious gloves", "fury", "berserker ring", "primordial", "neitiznot", "fighter torso", "barrows gloves", "dragon defender", "aver nic defender", "avernic defender", "fire cape", "infernal cape", "abyssal whip", "ghrazi rapier", "dharok", "verac", "guthan", "torag");
    private static final Predicate<ItemFacts> RANGED = f -> (f.worn() && (f.rangedAttack > 0 || f.rangedStrength > 0)) ||
        matchesName(f.name, "d'hide", "dragonhide", "armadyl", "masori", "karil", "crystal body", "crystal legs", "crystal helm", "bowfa", "bow of faerdhinen", "toxic blowpipe", "assembler", "accumulator", "anguish", "pegasian", "archer ring", "ranger boots", "zaryte", "quiver", "crossbow", "shortbow", "longbow", "dart", "chinchompa", "dragon arrow", "dragon bolts", "ranger", "snakeskin");
    private static final Predicate<ItemFacts> MAGIC = f -> (f.worn() && (f.magicAttack > 0 || f.magicDamage > 0)) ||
        matchesName(f.name, "ancestral", "virtus", "ahrim", "mystic", "splitbark", "bloodbark", "blue moon", "occult necklace", "tormented bracelet", "eternal boots", "saturated heart", "imbued god cape", "trident", "sang uinesti staff", "sanguinesti staff", "shadow", "kodai", "nightmare staff", "mage book", "mal ward", "arcane spirit shield");
    private static final Predicate<ItemFacts> PRAYER_GEAR = f -> (f.worn() && f.prayerBonus > 0) ||
        matchesName(f.name, "proselyte", "initiate", "monk's robe", "vestment", "mitre", "holy sandals", "devout boots", "god book", "holy blessing", "unholy blessing", "ancient blessing", "peaceful blessing", "zealot", "sunfire fanatic");

    private CategoryMatcher() { }

    private static Predicate<ItemFacts> names(String... terms)
    {
        return f -> matchesName(f.name, terms);
    }
    private static Predicate<ItemFacts> prefix(String... terms)
    {
        return f -> {
            String name = normalize(f.name);
            for (String term : terms)
            {
                String t = normalize(term);
                if (name.equals(t) || name.startsWith(t + " ") || name.startsWith(t + "(")) { return true; }
            }
            return false;
        };
    }
    private static Predicate<ItemFacts> suffix(String... terms)
    {
        return f -> {
            String name = normalize(f.name);
            for (String term : terms)
            {
                String t = normalize(term);
                if (name.equals(t) || name.endsWith(" " + t)) { return true; }
            }
            return false;
        };
    }
    @SafeVarargs private static Predicate<ItemFacts> any(Predicate<ItemFacts>... rules)
    {
        return f -> { for (Predicate<ItemFacts> p : rules) { if (p.test(f)) { return true; } } return false; };
    }
    @SafeVarargs private static Predicate<ItemFacts> all(Predicate<ItemFacts>... rules)
    {
        return f -> { for (Predicate<ItemFacts> p : rules) { if (!p.test(f)) { return false; } } return true; };
    }
    private static Predicate<ItemFacts> slot(int target, String... fallbackNames)
    {
        return f -> f.slot == target || (f.slot < 0 && matchesName(f.name, fallbackNames));
    }
    private static void add(String title, Predicate<ItemFacts> rule, String... aliases)
    {
        Category category = new Category(title, rule, aliases);
        CATEGORIES.add(category);
        for (String alias : aliases)
        {
            String a = normalize(alias);
            Category existing = BY_ALIAS.putIfAbsent(a, category);
            if (existing != null && existing != category)
            {
                throw new IllegalStateException("Duplicate alias '" + a + "' in " + title + "; already in " + existing.title);
            }
        }
    }
    /** Add richer skill-specific matches without replacing existing public search aliases.
     * This preserves v1.3 searches while extending what a player's skill name returns.
     */
    private static void extend(String existingTitle, Predicate<ItemFacts> extra, String... aliases)
    {
        Category target = null;
        for (Category category : CATEGORIES)
        {
            if (category.title.equals(existingTitle)) { target = category; break; }
        }
        if (target == null) { throw new IllegalStateException("Missing category: " + existingTitle); }
        final Predicate<ItemFacts> original = target.predicate;
        target.predicate = f -> original.test(f) || extra.test(f);
        for (String alias : aliases)
        {
            String normalized = normalize(alias);
            Category clash = BY_ALIAS.putIfAbsent(normalized, target);
            if (clash != null && clash != target)
            {
                throw new IllegalStateException("Duplicate skill alias '" + normalized + "' from " + existingTitle + " / " + clash.title);
            }
        }
    }

    private static String normalize(String s)
    {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).replace('’', '\'').replace('-', ' ').replace('_', ' ').trim().replaceAll("\\s+", " ");
    }
    static boolean matchesName(String name, String... terms)
    {
        String n = normalize(name);
        for (String term : terms)
        {
            String t = normalize(term);
            if (t.isEmpty()) { continue; }
            int i = n.indexOf(t);
            while (i >= 0)
            {
                int end = i + t.length();
                boolean before = i == 0 || !Character.isLetterOrDigit(n.charAt(i - 1));
                boolean after = end == n.length() || !Character.isLetterOrDigit(n.charAt(end));
                if (before && after) { return true; }
                i = n.indexOf(t, i + 1);
            }
        }
        return false;
    }
    private static boolean herb(String name)
    {
        String n = normalize(name);
        if (n.startsWith("grimy ")) { n = n.substring(6); }
        if (n.startsWith("clean ")) { n = n.substring(6); }
        // Herbs whose names don't actually contain the word 'herb'.
        return matchesName(n, "guam leaf", "guam", "marrentill", "tarromin", "harralander", "ranarr weed", "ranarr", "toadflax", "irit leaf", "irit", "avantoe", "kwuarm", "snapdragon", "cadantine", "lantadyme", "dwarf weed", "torstol", "arbit", "buchu leaf", "rogue's purse", "snake weed", "sito foil", "volencia moss", "sanfew", "cave nightshade")
            && !matchesName(n, "seed", "potion", "unfinished", "mix", "tar", "incense", "herb box", "herb sack", "herbiboar", "grimy key");
    }
    private static boolean edible(String name)
    {
        String n = normalize(name);
        return matchesName(n, "shrimp", "anchovies", "sardine", "herring", "trout", "salmon", "tuna", "lobster", "swordfish", "monkfish", "shark", "manta ray", "dark crab", "anglerfish", "karambwan", "sea turtle", "bass", "cod", "rainbow fish", "cooked meat", "cooked chicken", "potato", "pizza", "pie", "cake", "stew", "kebab", "curry", "bread", "chocolate cake", "pineapple rings", "pineapple chunks", "strawberry", "watermelon slice", "banana", "bwana", "purple sweets", "jubbly", "tuna potato", "summer pie", "wild pie", "admiral pie", "botanical pie", "seaweed sandwich", "sailfish")
            && !matchesName(n, "raw", "burnt", "uncooked", "rotten", "pie dish", "cake tin", "cake mix", "flour", "seed", "unfired", "chocolate dust", "potato seed", "curry leaf");
    }
    private static boolean dragonFighting(String name)
    {
        return matchesName(name, "dragon hunter lance", "dragon hunter crossbow", "dragon hunter wand", "dragonfire shield", "dragonfire ward", "dragonfire deflector", "anti dragon shield", "antifire", "super antifire", "extended antifire", "dragonbane", "dragon hunter", "wyrmfire", "dragonfire potion");
    }

    static
    {
        // EQUIPMENT AND EQUIPMENT SLOTS — uses actual RuneLite equipment metadata.
        add("Equipment", any(EQUIPPED, WEAPON), "equipment", "equip", "wearables", "gear", "wearable items");
        add("Armour", any(slot(0, "helmet", "helm", "hood", "coif"), slot(4, "platebody", "body", "robe top", "torso"), slot(5, "shield", "defender", "ward"), slot(7, "platelegs", "plateskirt", "chaps", "robe bottom", "skirt"), slot(9, "gloves", "gauntlets", "bracers"), slot(10, "boots", "shoes")), "armour", "armor", "armour pieces", "armor pieces");
        add("Helmets / headgear", slot(0, "helm", "helmet", "hood", "coif", "mask", "hat", "circlet", "tiara", "mitre", "headband", "headgear"), "helmet", "helmets", "helm", "helms", "head", "headgear", "head slot", "hats", "hat", "hood", "hoods", "coifs");
        add("Chest / body", slot(4, "platebody", "chainbody", "body", "chestplate", "torso", "robe top", "shirt", "hauberk", "tunic"), "body", "bodies", "chest", "chest gear", "tops", "top", "torso", "chest armour", "chest armor");
        add("Legs / bottoms", slot(7, "platelegs", "plateskirt", "chaps", "robe bottom", "skirt", "trousers", "leggings", "greaves", "tassets"), "legs", "leg", "legwear", "bottom", "bottoms", "pants", "skirts", "skirt", "trousers", "leg slot", "chaps", "tassets");
        add("Boots / footwear", slot(10, "boots", "shoes", "sandals", "slippers", "footwraps", "footwear"), "boots", "boot", "feet", "footwear", "shoes", "shoe", "foot slot", "sandals", "slippers");
        add("Gloves / hands", slot(9, "gloves", "gauntlets", "bracers", "vambraces", "bracelet of slaughter", "expeditious bracelet"), "gloves", "glove", "hands", "hand slot", "gauntlets", "bracers", "vambraces");
        add("Capes / backs", slot(1, "cape", "cloak", "backpack", "ava's assembler", "ava's accumulator", "quiver"), "capes", "cape", "cloaks", "cloak", "back", "back slot");
        add("Necklaces / amulets", slot(2, "amulet", "necklace", "pendant", "stole", "beads of the dead"), "amulets", "amulet", "neck", "necklaces", "necklace", "neck slot", "pendants");
        add("Rings", slot(12, "ring", "lightbearer", "ultor", "venator ring", "bellator", "magus"), "ring", "rings", "finger", "ring slot");
        add("Shields / offhands", slot(5, "shield", "defender", "ward", "buckler", "god book", "tome of", "spirit shield"), "shield", "shields", "offhand", "off hand", "offhands", "defenders", "defender", "wards", "ward");
        add("Weapons", WEAPON, "weapon", "weapons", "main hand", "mainhand", "weapon slot");
        add("Ammunition", any(slot(13, "arrow", "bolt", "dart", "javelin", "blessing", "quiver"), names("arrow", "arrows", "bolts", "dart", "javelin", "throwing knife", "cannonball", "chinchompa")), "ammo", "ammunition", "projectiles", "projectile");
        add("Two handed", f -> (f.slot == 3 && f.twoHanded) || matchesName(f.name, "2h sword", "two handed sword", "godsword", "halberd", "longbow", "shortbow", "maul", "scythe of vitur", "twisted bow", "bow of faerdhinen", "battlestaff"), "2h", "two handed", "two hand", "two handed weapons", "2h weapons");
        add("Melee equipment", MELEE, "melee", "melee gear", "melee equipment", "melee armour", "melee armor", "melee setup");
        add("Ranged equipment", RANGED, "ranged", "range gear", "ranged gear", "range equipment", "ranged equipment", "range armour", "range armor", "ranged armour", "ranged armor", "range setup", "ranged setup", "ranging gear", "range");
        add("Magic equipment", MAGIC, "magic gear", "mage", "mage gear", "magic equipment", "mage equipment", "magic armour", "magic armor", "mage armour", "mage armor", "mage setup", "magic setup", "magic");
        add("Prayer-bonus equipment", PRAYER_GEAR, "prayer gear", "prayer equipment", "prayer armour", "prayer armor", "prayer bonus", "prayer bonus gear", "prayer outfit");
        add("Strength-bonus gear", f -> f.worn() && (f.meleeStrength > 0 || matchesName(f.name, "ferocious gloves", "bandos tassets", "torva", "berserker ring", "strength amulet", "fighter torso", "primordial boots")), "strength gear", "str gear", "strength bonus", "str bonus");
        add("Magic-damage gear", f -> f.worn() && (f.magicDamage > 0 || matchesName(f.name, "ancestral", "occult necklace", "tormented bracelet", "magus ring", "virtus")), "magic damage", "mage damage", "magic damage gear", "mage damage gear");
        add("Ranged-strength gear", f -> f.worn() && (f.rangedStrength > 0 || matchesName(f.name, "masori", "zaryte vambraces", "ava's assembler", "quiver")), "ranged strength", "range strength", "ranged strength gear", "range str");
        add("Defensive / tank gear", f -> f.worn() && (f.defence >= 60 || matchesName(f.name, "justiciar", "bulwark", "elysian spirit shield", "dinh's bulwark", "guardian boots")), "tank", "tank gear", "tank armour", "tank armor", "defensive gear");
        add("Dragon-slaying supplies", f -> dragonFighting(f.name), "dragon fighting equipment", "dragon fighting gear", "dragon slayer gear", "dragon slaying", "dragonbane", "dragonbane gear", "anti dragon", "dragon killing gear", "anti dragon gear", "dragon setup");
        add("Demon-slaying supplies", names("arclight", "emberlight", "silverlight", "darklight", "demonbane", "purging staff", "scorching bow", "burning claws", "demon hunter", "demonbane staff"), "demonbane", "demon gear", "demon fighting gear", "demon slayer gear");
        add("Undead-slaying supplies", names("salve amulet", "ivandis flail", "blisterwood flail", "blisterwood", "silver sickle", "wolfbane", "ghoulbane", "crumble undead", "empyrean staff"), "undead gear", "undead fighting gear", "vampyre gear", "vampire gear", "salve gear");
        add("Slayer gear", names("slayer helmet", "slayer helm", "black mask", "nose peg", "earmuffs", "facemask", "masked earmuffs", "witchwood icon", "spiny helmet", "rock hammer", "leaf bladed", "fungicide", "bag of salt", "ice cooler", "mirror shield", "slayer ring", "bracelet of slaughter", "expeditious bracelet", "recoil", "herb sack", "rune pouch", "cannonball", "dwarf multicannon"), "slayer", "slayer gear", "slayer items", "slayer equipment", "slayer supplies");
        add("Wilderness / PvP equipment", names("blighted", "revenant ether", "bracelet of ethereum", "looting bag", "trident of the seas (e)", "webweaver bow", "ursine chainmace", "accursed sceptre", "vig gora's chainmace", "viggora's chainmace", "thammaron's sceptre", "craw's bow", "claws of callisto", "morrigan's", "statius's", "vesta's", "zuriel's"), "wildy", "wilderness", "wildy gear", "pvp", "pvp gear", "pk gear", "pking");
        // Weapon families and ranged consumables.
        add("Bows", names("shortbow", "longbow", "twisted bow", "crystal bow", "bow of faerdhinen", "venator bow", "seercull", "dark bow", "rain bow", "webweaver bow", "craw's bow", "composite bow"), "bow", "bows", "archery bows");
        add("Crossbows", names("crossbow", "karil's crossbow", "zaryte crossbow", "ballista"), "crossbow", "crossbows", "cbow", "cbows");
        add("Arrows", names("arrow", "arrows"), "arrow", "arrows", "arrow ammo");
        add("Bolts", names("bolt", "bolts", "bolt tips"), "bolt", "bolts", "bolt ammo");
        add("Darts", names("dart", "darts", "dart tip", "dart tips"), "dart", "darts", "dart tips");
        add("Thrown weapons", names("throwing knife", "javelin", "chinchompa", "throwing axe", "throwing ring", "darts"), "throwing", "throwing weapons", "knives", "javelins", "chins", "chinchompas");
        add("Blowpipes", names("toxic blowpipe", "blowpipe"), "blowpipe", "blowpipes", "bp");
        add("Cannon supplies", names("cannonball", "granite cannonball", "cannon base", "cannon stand", "cannon barrels", "cannon furnace", "dwarf multicannon", "cannonball mould", "double ammo mould"), "cannons", "cannonballs", "cannon supplies", "multicannon");
        add("Staves / wands", names("staff", "staves", "wand", "battlestaff", "trident", "sceptre", "scepter", "kodai", "harmonised", "sanguinesti", "tumeken's shadow", "purging staff"), "staff", "staves", "wands", "wand", "magic staffs", "magic staves");
        add("Swords", names("sword", "scimitar", "longsword", "shortsword", "godsword", "rapier", "sabre", "saber", "blade of saeldor", "whip"), "sword", "swords", "sword weapons");
        add("Scimitars", names("scimitar", "blade of saeldor"), "scimitar", "scimitars", "scim", "scims");
        add("Daggers", names("dagger", "abyssal dagger", "dragon dagger"), "dagger", "daggers", "dds");
        add("Whips", names("whip", "abyssal tentacle"), "whip", "whips");
        add("Spears / hastas", names("spear", "hasta", "lance", "partisan"), "spear", "spears", "hasta", "hastas", "lance", "lances");
        add("Halberds", names("halberd", "noxious halberd"), "halberd", "halberds", "hally", "hallys");
        add("Crush weapons", names("warhammer", "mace", "maul", "cudgel", "bulwark", "anchor", "bludgeon", "elder maul", "morningstar", "inquisitor's mace"), "crush weapon", "crush weapons", "crush gear");
        add("Claws / special weapons", names("claws", "d claws", "dragon dagger", "abyssal dagger", "voidwaker", "godsword", "granite maul", "dragon warhammer", "elder maul", "burning claws", "dinh's bulwark"), "spec", "spec weapons", "special attack weapons", "special weapons", "ko weapons");
        // Combat consumables and utilities.
        add("Herbs", f -> herb(f.name), "herb", "herbs", "herbalism", "herblore herbs");
        add("Grimy herbs", f -> normalize(f.name).startsWith("grimy ") && herb(f.name), "grimy", "dirty herbs", "grimy herbs", "grimy herb");
        add("Clean herbs", f -> !normalize(f.name).startsWith("grimy ") && herb(f.name), "clean herbs", "cleaned herbs", "clean herb");
        add("Seeds and saplings", f -> matchesName(f.name, "seed", "seeds", "sapling") && !matchesName(f.name, "seed pod", "seed box", "seed dibber", "seed nest", "bird seed"), "seed", "seeds", "farming seeds", "saplings", "seedlings");
        add("Herb seeds", f -> matchesName(f.name, "seed") && matchesName(f.name, "guam", "marrentill", "tarromin", "harralander", "ranarr", "toadflax", "irit", "avantoe", "kwuarm", "snapdragon", "cadantine", "lantadyme", "dwarf weed", "torstol"), "herb seeds", "herb seed");
        add("Tree seeds and saplings", f -> matchesName(f.name, "sapling") || (matchesName(f.name, "seed") && matchesName(f.name, "oak", "willow", "maple", "yew", "magic", "teak", "mahogany", "redwood", "apple", "banana", "orange", "curry", "pineapple", "papaya", "palm", "dragonfruit", "calquat", "celastrus", "spirit", "crystal")), "tree seeds", "tree seed", "tree saplings", "fruit trees", "fruit tree seeds");
        add("Farming produce", names("snape grass", "limpwurt root", "potato", "onion", "cabbage", "sweetcorn", "strawberry", "watermelon", "tomato", "jute fibre", "barley", "hops", "belladonna", "cactus spine", "coconut", "papaya fruit", "dragonfruit", "white berries", "poison ivy berries", "mushroom", "giant seaweed"), "crops", "farm produce", "farming produce", "harvest", "farming crops");
        add("Compost and fertilizer", names("compost", "ultracompost", "supercompost", "compost potion", "fertiliser", "fertilizer", "volcanic ash", "compost bin"), "compost", "fertilizer", "fertiliser", "ultracompost", "supercompost");
        add("Farming tools", names("seed dibber", "rake", "spade", "watering can", "secateurs", "magic secateurs", "gardening trowel", "plant pot", "plant cure", "basket", "sack", "bottomless compost bucket", "seed box", "leprechaun"), "farming tools", "gardening tools", "farm tools");
        add("Potions", names("potion", "brew", "restore", "antipoison", "antidote", "overload", "stamina", "super energy", "rejuvenation", "serum 207", "guthix rest", "sanfew", "divine super combat"), "potion", "potions", "pots", "drinkable potions");
        add("Unfinished potions", names("unfinished potion", "potion (unf)", "potion(unf)", "(unf)"), "unfinished potions", "unf", "unfs", "unf potions");
        add("Herblore secondaries", names("eye of newt", "red spiders' eggs", "red spider eggs", "limpwurt root", "white berries", "snape grass", "mort myre fungus", "potato cactus", "wine of zamorak", "crushed nest", "unicorn horn", "unicorn horn dust", "blue dragon scale", "dragon scale dust", "lava scale", "crushed superior dragon bones", "coconut milk", "goat horn dust", "toad's legs", "jangerberries", "kebbit teeth dust", "ashes", "chocolate dust", "amylase crystal"), "secondaries", "herblore secondary", "herblore secondaries", "potion ingredients", "herblore ingredients");
        add("Vials and containers", names("vial", "vial of water", "pestle and mortar", "amulet mould", "flask", "waterskin", "jug of water", "bowl of water"), "vials", "vial", "potion vials", "empty vials", "containers");
        add("Prayer potions", names("prayer potion", "super restore", "sanfew serum", "prayer regeneration potion"), "prayer pots", "prayer potions", "ppot", "ppots");
        add("Restoration potions", names("super restore", "restore potion", "sanfew serum", "rejuvenation potion"), "restores", "restore pots", "restoration pots", "super restores");
        add("Antifire potions", names("antifire", "dragonfire potion", "wyrmfire potion"), "antifire", "antifires", "anti fire", "dragonfire protection", "super antifire");
        add("Antipoisons", names("antipoison", "superantipoison", "antidote", "anti venom", "antivenom", "sanfew serum", "cure me"), "antipoison", "antipoisons", "anti poison", "antivenom", "anti venom", "poison cure");
        // Broad boost-drink category: include the ordinary/Barbarian-mix versions,
        // not only expensive super/divine variants. These are item names rather
        // than a loose "attack" substring that would pull in unrelated gear.
        add("Combat potions", names("attack potion", "attack mix", "super attack", "superattack mix",
            "strength potion", "strength mix", "super strength", "super str. mix",
            "defence potion", "defence mix", "super defence", "super def. mix",
            "combat potion", "combat mix", "super combat", "zamorak brew",
            "ranging potion", "bastion potion", "battlemage potion", "magic potion",
            "divine ranging", "divine magic", "super ranging", "super magic"), "combat pots", "combat potions", "boost pots", "stat boosts", "boosting potions");
        add("Energy / stamina", names("stamina potion", "energy potion", "super energy", "strange fruit", "purple sweets", "summer pie", "ring of endurance"), "stamina", "staminas", "run energy", "energy pots", "energy potions", "run restoration");
        add("Food", f -> edible(f.name), "food", "foods", "healing food", "eats", "edible food");
        add("Raw food / fish", names("raw", "uncooked", "raw fish", "raw shark", "raw karambwan", "raw lobster", "raw meat", "raw bird meat"), "raw food", "raw fish", "uncooked food", "uncooked fish");
        add("Fish", names("shrimp", "anchovies", "sardine", "herring", "trout", "salmon", "tuna", "lobster", "swordfish", "monkfish", "shark", "manta ray", "dark crab", "anglerfish", "karambwan", "sea turtle", "bass", "cod", "rainbow fish"), "fish", "fishes", "seafood");
        add("High healing food", names("shark", "manta ray", "anglerfish", "dark crab", "sea turtle", "tuna potato", "karambwan", "wild pie", "summer pie", "sailfish"), "high healing food", "high heal", "pvm food", "bossing food");
        add("Emergency healing", names("sara domin brew", "saradomin brew", "anglerfish", "purple sweets", "karambwan", "phoenix necklace", "redemption"), "combo food", "emergency food", "combo eats", "brews and food");
        add("Runes", suffix("rune", "runes"), "rune", "runes", "spell runes", "magic runes");
        add("Runecrafting essence", names("rune essence", "pure essence", "daeyalt essence", "dense essence", "dark essence", "dark essence fragments", "guardian essence", "essence pouches"), "essence", "rc essence", "runecrafting essence", "pure ess", "daeyalt");
        add("Runecrafting tools", names("talisman", "tiara", "runecraft pouch", "small pouch", "medium pouch", "large pouch", "giant pouch", "colossal pouch", "binding necklace", "ring of the elements", "elemental ring", "abyssal needle"), "rc tools", "runecrafting tools", "talismans", "tiaras", "rc pouches");
        add("Teleportation", names("teleport", "tele tab", "teleport tab", "games necklace", "ring of dueling", "amulet of glory", "skills necklace", "combat bracelet", "digsite pendant", "burning amulet", "necklace of passage", "xeric's talisman", "drakan's medallion", "ectophial", "royal seed pod", "seed pod", "teleport crystal", "slayer ring", "ring of wealth", "ring of returning", "chronicle", "pharaoh's sceptre", "camulet", "construction cape", "max cape", "quest point cape", "crafting cape", "farming cape", "radas blessing", "rada's blessing", "morytania legs", "ardougne cloak", "explorer's ring", "ring of the elements", "ectotoken"), "teleport", "teleports", "tp", "tele", "teleportation", "travel gear", "transportation");
        add("Teleport jewellery", names("games necklace", "ring of dueling", "amulet of glory", "skills necklace", "combat bracelet", "digsite pendant", "burning amulet", "necklace of passage", "slayer ring", "ring of wealth", "ring of returning", "ring of the elements", "enlightened journey"), "teleport jewellery", "teleport jewelry", "tp jewelry", "tp jewellery", "travel jewelry");
        add("Teleport tablets", names("teleport to house", "teleport tablet", "teleport tab", "varrock teleport", "falador teleport", "camelot teleport", "ardougne teleport", "lumbridge teleport", "trollheim teleport", "wat erbirth teleport", "waterbirth teleport", "barrows teleport", "fenkenstrain's castle teleport", "salve graveyard teleport", "mind altar teleport", "west ardougne teleport", "kharyrll teleport"), "tele tabs", "teleport tabs", "tabs", "tablets", "tp tabs", "teleport tablets");
        add("Jewellery", names("ring", "necklace", "amulet", "bracelet", "pendant", "earrings", "tiara"), "jewellery", "jewelry", "gem jewelry", "gem jewellery");
        // Mining, smithing, crafting, fletching, construction, WC.
        add("Ores", suffix("ore", "ores"), "ore", "ores", "mining ores", "metal ores");
        add("Bars", suffix("bar", "bars"), "bar", "bars", "metal bars", "smithing bars", "ingots");
        add("Gems", names("uncut", "sapphire", "emerald", "ruby", "diamond", "dragonstone", "onyx", "zenyte", "opal", "jade", "red topaz", "amethyst", "lapis lazuli", "topaz", "cut gem", "gemstone"), "gem", "gems", "gemstones", "uncut gems", "cut gems");
        add("Pickaxes", names("pickaxe", "crystal pickaxe", "infernal pickaxe", "dragon pickaxe", "3rd age pickaxe"), "pickaxe", "pickaxes", "mining tools", "picks");
        add("Smithing tools", names("hammer", "imcando hammer", "smiths' uniform", "smithing catalyst", "double ammo mould", "cannonball mould", "steel nails", "goldsmith gauntlets", "ice gloves", "smithing gloves", "ore pack"), "smithing tools", "smithing gear", "smithing supplies");
        add("Mining resources", names("ore", "coal", "nugget", "minerals", "stardust", "amethyst", "gem bag", "saltpetre", "sandstone", "granite", "golden nugget", "calcified deposits", "pay dirt", "pay dirt", "pay-dirt"), "mining", "mining resources", "mining supplies");
        add("Logs", suffix("log", "logs"), "log", "logs", "wood", "woodcutting logs", "wc logs");
        add("Axes / woodcutting", names("axe", "hatchet", "infernal axe", "crystal axe", "dragon axe", "felling axe"), "axes", "axe", "hatchet", "hatchets", "woodcutting tools", "wc tools");
        add("Forestry", names("forestry", "forester", "anima infused", "felling axe", "secateurs attachment", "log basket", "forestry kit", "fox whistle", "pheasant egg", "padded spoon", "nature offering", "leprechaun charm", "smoker fuel", "bee on a stick"), "forestry", "forestry supplies", "forestry items");
        add("Planks", names("plank", "planks"), "planks", "plank", "construction planks");
        add("Construction supplies", names("plank", "nail", "bolt of cloth", "saw", "hammer", "gold leaf", "marble block", "magic stone", "bagged plant", "house tablet", "flatpack", "stew", "spicy stew"), "construction", "construction supplies", "con supplies", "house materials", "poh materials");
        add("Crafting supplies", names("thread", "needle", "chisel", "mould", "mold", "glassblowing pipe", "molten glass", "bucket of sand", "soda ash", "seaweed", "gold bar", "silver bar", "gem", "leather", "hide", "wool", "flax", "spinning", "battlestaff", "orb", "unpowered orb"), "crafting", "crafting supplies", "crafting materials");
        add("Hides and leathers", names("cowhide", "dragonhide", "d'hide", "leather", "hide", "snakeskin", "fur", "wolf skin", "kebbit skin"), "hide", "hides", "leathers", "leather", "dragonhides", "dhides");
        add("Glass and orbs", names("molten glass", "soda ash", "bucket of sand", "unpowered orb", "water orb", "air orb", "earth orb", "fire orb", "glassblowing pipe", "lens", "lantern lens"), "glass", "molten glass", "orbs", "glassmaking");
        add("Fletching supplies", names("arrow shaft", "feather", "arrowtip", "arrow tip", "bow string", "bowstring", "unstrung", "crossbow stock", "crossbow string", "limbs", "dart tip", "bolt tip", "javelin shaft", "headless arrow", "broad arrowhead"), "fletching", "fletching supplies", "fletching materials");
        add("Feathers", names("feather", "feathers", "stripy feather"), "feathers", "feather");
        add("Fishing supplies", names("fishing rod", "fly fishing rod", "barbarian rod", "small fishing net", "big fishing net", "harpoon", "lobster pot", "fishing bait", "sandworm", "feather", "karambwan vessel", "angler outfit", "fishing barrel", "tackle box", "drift net", "fish sack"), "fishing gear", "fishing supplies", "fishing tools", "fishing equipment");
        add("Bait", names("fishing bait", "sandworms", "sandworm", "feather", "dark fishing bait", "karambwanji", "fish chunks"), "bait", "fishing bait");
        add("Hunting supplies", names("box trap", "bird snare", "butterfly net", "butterfly jar", "noose wand", "falconer's glove", "impling jar", "magic box", "rabbit snare", "hunter spear", "teasing stick", "pitfall", "hunting knife"), "hunter", "hunter gear", "hunting gear", "hunter supplies");
        add("Implings", names("impling jar", "impling", "implings"), "impling", "implings", "imp jars");
        add("Firemaking supplies", names("tinderbox", "pyre logs", "bruma root", "bruma torch", "warm gloves", "wintertodt", "light source", "lantern", "firelighter", "oil lamp", "oil lantern", "bullseye lantern"), "firemaking", "fm", "fm gear", "firemaking supplies");
        add("Agility gear", names("graceful", "mark of grace", "stamina potion", "agility potion", "summer pie", "boots of lightness", "spotted cape", "spottier cape", "ring of endurance"), "agility", "agility gear", "agility supplies", "graceful gear");
        add("Thieving gear", names("lockpick", "rogue outfit", "rogue mask", "dodgy necklace", "shadow veil", "thieving cape", "gloves of silence", "coin pouch", "blackjack"), "thieving", "thieving gear", "thieving supplies", "pickpocket");
        add("Skilling outfits", names("graceful", "prospector", "angler", "lumberjack", "rogue", "farmer's outfit", "farmer's strawhat", "pyromancer", "smiths'", "smithing uniform", "raiments of the eye", "zealot", "carpenter", "desert robes", "varrock armour", "miner", "fishing outfit", "ranger outfit", "golden prospector", "spirit angler", "infinity", "farming outfit"), "skilling outfit", "skilling outfits", "xp outfits", "xp gear", "skilling clothes");
        add("Skill-boosting items", names("spicy stew", "dragon pickaxe", "dragon axe", "crystal pickaxe", "imbued heart", "saturated heart", "botanical pie", "admiral pie", "summer pie", "wild pie", "garden pie", "chef's delight", "mature dwarven stout", "dwarven stout", "wizard's mind bomb", "fletching potion", "agility potion", "hunter potion", "stew", "mushroom pie"), "skill boosts", "boosting items", "skilling boosts", "boosts");
        // Prayer and quest / activity supplies.
        add("Bones", names("bones", "big bones", "dragon bones", "wyvern bones", "superior dragon bones", "hydra bones", "dagannoth bones", "our g bones", "ourg bones", "babydragon bones"), "bone", "bones", "prayer bones");
        add("Ashes", suffix("ashes", "ash"), "ashes", "demonic ashes", "prayer ashes");
        add("Ensouled heads", names("ensouled", "ensouled head"), "ensouled", "ensouled heads", "reanimation", "reanimate heads");
        add("Prayer supplies", names("bones", "ashes", "ensouled", "prayer potion", "super restore", "sanfew serum", "burner", "marentill", "marrentill", "ectotoken", "ecto token", "dragonbone necklace", "bonecrusher", "ash sanctifier", "sanctifier", "zealot", "offering", "sunfire fanatic"), "prayer supplies", "prayer items", "prayer training", "prayer xp");
        add("Prayer - all", any(PRAYER_GEAR, names("bones", "ashes", "ensouled", "prayer potion", "super restore", "sanfew serum", "bonecrusher", "ash sanctifier", "offering")), "prayer", "prayer stuff", "all prayer");
        add("Clue scrolls / rewards", names("clue scroll", "reward casket", "clue casket", "clue bottle", "clue geode", "clue nest", "mimic", "master clue", "challenge scroll", "puzzle box", "light box", "ornament kit", "trimmed", "gilded", "3rd age", "third age"), "clues", "clue", "treasure trails", "clue scrolls", "clue rewards", "caskets");
        add("Keys", suffix("key", "keys"), "key", "keys", "keyrings", "key items");
        add("Quest items (recognizable)", names("quest item", "quest journal", "quest cape", "quest point cape", "silverlight", "darklight", "excalibur", "holy grail", "m'speak amulet", "ghostspeak amulet", "ghost speak amulet", "lunar staff", "dramen staff", "ivandis", "blisterwood"), "quest items", "quests", "quest gear");
        add("Diaries / achievements", names("ardougne cloak", "explorer's ring", "morytania legs", "falador shield", "varrock armour", "kandarin headgear", "desert amulet", "western banner", "rada's blessing", "fremennik sea boots", "wilderness sword", "diary", "achievement diary"), "diary items", "diary gear", "achievement diary gear", "diaries");
        add("Raids supplies", names("xeric's", "twisted bow", "twisted buckler", "ancestral", "dragon hunter", "dinh's bulwark", "dragon claws", "scythe of vitur", "sanguinesti", "justiciar", "ghrazi rapier", "aver nic", "avernic", "osm umten's fang", "osmumten's fang", "tumeken's shadow", "masori", "lightbearer", "elidinis' ward", "thread of elidinis", "breach of the scarab", "ambrosia", "nectar", "silk dressing", "salt", "scarab", "tbow"), "raids", "raid gear", "raid supplies", "raids gear");
        add("Chambers of Xeric", names("xeric's", "twisted bow", "twisted buckler", "ancestral", "dinh's bulwark", "dragon hunter crossbow", "dragon claws", "elder maul", "kodai", "olm", "cox"), "cox", "chambers of xeric", "chambers", "olm gear");
        add("Theatre of Blood", names("scythe of vitur", "sanguinesti", "justiciar", "ghrazi rapier", "avernic", "vial of blood", "sanguine", "tob"), "tob", "theatre of blood", "theater of blood", "verzik gear");
        add("Tombs of Amascut", names("osmumten's fang", "tumeken's shadow", "masori", "lightbearer", "elidinis' ward", "thread of elidinis", "keris partisan", "scarab", "toa"), "toa", "tombs of amascut", "amascut", "raids 3");
        add("God Wars", names("bandos", "armadyl", "saradomin", "zamorak", "ancient godsword", "nex", "zaros", "ecumenical key", "frozen key", "god wars"), "gwd", "god wars", "god wars dungeon", "nex gear", "godwars");
        add("Barrows", names("ahrim", "dharok", "guthan", "karil", "torag", "verac", "barrows"), "barrows", "barrows gear", "barrows armour", "barrows armor");
        add("Crystal / Prifddinas", names("crystal", "enhanced crystal", "bow of faerdhinen", "blade of saeldor", "crystal shard", "crystal armour seed", "crystal tool seed", "singing bowl", "gauntlet"), "crystal gear", "crystal items", "prif", "prifddinas", "gauntlet gear");
        add("Dragon-tier equipment", f -> prefix("dragon").test(f) && matchesName(f.name,
            "platelegs", "plateskirt", "platebody", "chainbody", "boots", "gloves", "full helm", "med helm", "scimitar", "longsword", "mace", "dagger", "spear", "hasta", "sword", "crossbow", "2h sword", "claws", "halberd", "defender", "kiteshield", "square shield", "warhammer", "knife", "dart", "arrow", "bolts", "axe", "pickaxe"),
            "dragon gear", "dragon equipment", "dragon tier", "dragon weapons", "dragon armour", "dragon armor");
        add("Rune-tier equipment", f -> prefix("rune").test(f) && !matchesName(f.name, "rune essence", "runecraft", "rune pouch"), "rune gear", "rune equipment", "runite gear", "rune armour", "rune armor");
        add("God-aligned items", names("saradomin", "zamorak", "guthix", "armadyl", "bandos", "ancient", "zaros", "blessed d'hide", "blessed dragonhide", "holy blessing", "unholy blessing"), "god gear", "god items", "god armour", "god armor", "god d'hide", "blessed d'hide");
        // Currency, storage, materials, misc.
        add("Currencies", names("coins", "platinum token", "tokkul", "trading sticks", "ecto token", "stardust", "golden nugget", "marks of grace", "mark of grace", "numulite", "shards", "crystal shard", "blood money", "pieces of eight", "graceful token", "reward points", "ticket"), "currency", "currencies", "money", "tokens", "reward currency");
        add("Pouches / storage", names("pouch", "rune pouch", "herb sack", "seed box", "gem bag", "coal bag", "fish barrel", "plank sack", "looting bag", "tackle box", "log basket", "quiver", "bolt pouch", "soul bearer", "bone sack", "essence pouch", "hunters' kit"), "pouches", "pouch", "storage", "storage items", "bags", "sacks");
        add("Tools", names("hammer", "saw", "chisel", "tinderbox", "knife", "shears", "spade", "rake", "secateurs", "seed dibber", "pickaxe", "axe", "hatchet", "fishing rod", "harpoon", "needle", "pestle and mortar", "blowing pipe", "glassblowing pipe", "mould", "lockpick", "sickle", "wrench", "watering can", "machete", "bruma torch"), "tool", "tools", "utility items", "utility tools");
        add("Light sources", names("light source", "lantern", "bullseye lantern", "oil lamp", "candle", "mining helmet", "firemaking cape", "bruma torch", "firelighter"), "light", "lights", "light sources", "lanterns", "torches");
        add("Skulls / death items", names("skull", "death rune", "bones", "gravestone", "death's coffer", "death coffer", "reclaim token", "bloody key"), "death items", "death gear", "death supplies");
        add("Cosmetics / ornament kits", names("ornament kit", "orn kit", "fashion", "partyhat", "santa hat", "halloween mask", "scarf", "gilded", "3rd age", "holiday", "pumpkin", "dye", "transmogrification", "pet", "mole slippers"), "cosmetic", "cosmetics", "fashionscape", "ornament kits", "orn kits", "fashion");
        add("Pets and pet items", names("pet", "kitten", "puppy", "cat", "fishbowl", "pet rock", "pet house", "overgrown cat", "chinchompa pet", "toy cat"), "pets", "pet items", "pet supplies");
        add("Consumables", any(names("potion", "brew", "restore", "antidote", "antipoison", "food", "stamina", "purple sweets", "teleport scroll"), f -> edible(f.name)), "consumables", "supplies", "useables", "usables");

        // 1.4 SKILL SEARCHES — all 24 OSRS skills, including Sailing. Broad skill searches
        // intentionally cover useful gear + materials; narrower "... gear" aliases keep results lean.
        // Regular Attack, super/divine, Barbarian mixes, and multi-stat drinks:
        // combat boosts Attack+Strength, super combat boosts Attack+Strength+Defence.
        add("Attack skill", any(MELEE, names(
            "attack cape", "attack hood",
            "attack potion", "attack mix", "super attack", "superattack mix",
            "combat potion", "combat mix", "super combat", "divine super combat",
            "zamorak brew",
            "warrior ring", "warrior helm", "dragon defender", "avernic defender")),
            "attack", "attack skill", "attack training", "attack gear", "attack equipment");
        // Bare "strength" is a focused potion/boost search; @strength remains the broad skill category.
        add("Strength boosts", names(
            "strength potion", "strength mix", "super strength", "super str. mix",
            "divine super strength", "combat potion", "combat mix", "super combat",
            "divine super combat", "zamorak brew"),
            "strength", "strength pot", "strength pots", "str pots", "strength potions");
        add("Strength skill", any(f -> f.worn() && f.meleeStrength > 0, names(
            "strength cape", "strength hood", "strength potion", "strength mix",
            "super strength", "super str. mix", "divine super strength",
            "combat potion", "combat mix", "super combat", "zamorak brew",
            "dragon battleaxe", "berserker ring", "fighter torso",
            "ferocious gloves", "inquisitor's mace")),
            "strength skill", "strength training", "strength equipment");
        add("Defence skill", any(f -> f.worn() && f.defence > 0, names(
            "defence cape", "defence hood", "defence potion", "defence mix",
            "super defence", "super def. mix", "divine super defence",
            "super combat", "bastion potion", "battlemage potion", "saradomin brew",
            "justiciar", "dinh's bulwark", "elysian spirit shield",
            "ring of suffering", "dragonfire shield")),
            "defence", "defense", "defence skill", "defence training", "defense gear", "defence equipment");
        add("Hitpoints skill", any(f -> edible(f.name), names("hitpoints cape", "hitpoints hood", "regen bracelet", "rapid heal", "rejuvenation pool", "sara domin brew", "saradomin brew", "anglerfish", "rejuvenation potion", "guthix rest", "phoenix necklace", "amulet of blood fury", "dwarven rock cake", "locator orb", "purple sweets")),
            "hitpoints", "hitpoints skill", "hp gear", "hp training", "hp supplies", "healing gear");
        add("Herblore skill", any(f -> herb(f.name), names("pestle and mortar", "vial", "eye of newt", "limpwurt root", "red spiders' eggs", "snape grass", "wine of zamorak", "white berries", "mort myre fungus", "potato cactus", "unicorn horn", "dragon scale dust", "amylase crystal", "crushed nest", "goat horn dust", "kebbit teeth dust", "coconut milk", "chocolate dust", "potion (unf)", "potion(unf)", "herblore cape", "herblore hood", "botanical pie", "spicy stew", "amulet of chemistry", "alchemist's amulet", "prescription goggles", "mixology", "mox", "aga", "lye", "harralander tar")),
            "herblore", "herblore training", "herblore gear", "herblore supplies", "potion making", "potion brewing", "making potions");
        add("Cooking skill", any(f -> edible(f.name), names("raw", "uncooked", "cooking gauntlets", "cooking cape", "cooking hood", "chef's hat", "chef's delight", "cooking apple", "cooking range", "spicy stew", "pot", "jug", "bowl", "pie dish", "cake tin", "flour", "pizza base", "wine", "grapes", "potato", "fish", "meat", "karambwan", "mushroom", "pastry dough", "burnt")),
            "cooking", "cooking gear", "cooking supplies", "cooking ingredients", "cooking training");
        add("Sailing skill", names("sailing cape", "sailing hood", "sailor", "captain's log", "captain's logbook", "ship's log", "sailing log", "navigation chart", "sea chart", "charting", "spyglass", "sextant", "compass", "sailcloth", "sail cloth", "rigging", "shipbuilding", "boat building supplies", "boat deed", "boat schematics", "repair kit", "plank", "nails", "hammer", "saw", "swamp paste", "salvaging hook", "salvage", "keel parts", "mast", "wind catcher", "wind-catcher", "cargo hold", "boat cannon", "ship cannon", "shipwright", "rope", "anchor", "hull", "navigation tool"),
            "sailing", "sailing gear", "sailing equipment", "sailing supplies", "sailing skill", "shipbuilding", "sailing training", "sailing kit");

        // Extend existing v1.3 categories instead of shadowing a working category.
        extend("Ranged equipment", names("ranging potion", "ranging mix", "divine ranging", "bastion potion", "ranged cape", "ranging cape", "ranged hood"), "ranged skill", "ranged training", "ranging skill", "ranging supplies");
        extend("Magic equipment", names("rune pouch", "runes", "fire rune", "water rune", "air rune", "earth rune", "blood rune", "death rune", "law rune", "nature rune", "soul rune", "chaos rune", "mind rune", "wrath rune", "magic cape", "magic hood", "magic potion", "magic mix", "divine magic potion", "battlemage potion", "ancient brew", "forgotten brew", "wizard's mind bomb", "mature wizard's mind bomb", "imbued heart", "saturated heart"), "magic skill", "magic training", "spellcasting");
        extend("Prayer - all", names("prayer cape", "prayer hood", "bonecrusher necklace", "dragonbone necklace", "ranging prayer potion"), "prayer skill", "prayer skilling");
        extend("Agility gear", names("agility cape", "agility hood", "hallowed", "graceful", "strange fruit", "marks of grace", "mark of grace", "shortcut"), "agility skill", "agility training");
        extend("Thieving gear", names("spicy stew", "spring sq'irkjuice", "summer sq'irkjuice", "autumn sq'irkjuice", "winter sq'irkjuice", "rogue's", "rogues", "rogue equipment", "thieving hood", "thieving cape", "dodgy necklace", "lockpick", "blackjacking", "stethoscope"), "thieving skill", "thieving training");
        extend("Farming tools", any(names("seed", "sapling", "ultracompost", "supercompost", "compost", "farmer's", "farming cape", "farming hood", "amulet of nature", "amulet of bounty", "gardening trowel", "plant cure", "watering can", "bottomless bucket", "bottomless compost bucket", "garden pie", "spicy stew"), names("rake", "seed dibber", "spade", "magic secateurs", "herb sack", "seed box")), "farming", "farming skill", "farming gear", "farming supplies", "farming training");
        extend("Fishing supplies", names("fishing potion", "fishing mix", "fish pie", "admiral pie", "spicy stew", "fishing cape", "fishing hood", "dragon harpoon", "crystal harpoon", "infernal harpoon", "spirit angler", "rada's blessing", "anglerfish", "fishing bait", "fish barrel"), "fishing", "fishing skill", "fishing training");
        extend("Hunting supplies", names("hunter potion", "hunter mix", "spicy stew", "hunter cape", "hunter hood", "guild hunter", "hunter's kit", "hunters' kit", "hunter's spear", "hunter spear", "ring of pursuit", "strung rabbit foot", "moonlight moth", "butterfly", "impling jar", "birdhouse", "bird house", "kebbit", "box trap", "bird snare"), "hunting", "hunting skill", "hunter skill", "hunter training");
        extend("Mining resources", names("pickaxe", "prospector", "varrock armour", "golden prospector", "mining gloves", "expert mining gloves", "superior mining gloves", "celestial ring", "celestial signet", "mining cape", "mining hood", "gem bag", "coal bag", "amethyst", "dwarven stout", "mining potion", "spicy stew", "dragon pickaxe"), "mining skill", "mining training", "mining kit");
        extend("Smithing tools", names("dwarven stout", "spicy stew", "ore", "coal", "bar", "smithing cape", "smithing hood", "smiths'", "smiths uniform", "goldsmith gauntlets", "ice gloves", "blast furnace", "coal bag", "smithing catalyst", "mould", "anvil", "ammo mould"), "smithing", "smithing skill", "smithing training");
        extend("Crafting supplies", names("mushroom pie", "spicy stew", "crafting cape", "crafting hood", "chisel", "molten glass", "uncut", "gem", "mould", "sapphire", "ruby", "diamond", "dragonstone", "onyx", "zenyte", "opal", "jade", "topaz", "sand", "soda ash", "giant seaweed", "battlestaff", "orb", "silver bar", "gold bar", "molten glass", "leather", "dragonhide"), "crafting skill", "crafting training");
        extend("Fletching supplies", names("dragonfruit pie", "spicy stew", "knife", "fletching cape", "fletching hood", "fletching potion", "longbow (u)", "shortbow (u)", "crossbow (u)", "unstrung", "maple logs", "yew logs", "magic logs", "willow logs", "oak logs", "redwood logs", "logs", "arrow shafts", "broad arrowhead", "broad bolts", "feather", "feathers", "arrowtip", "arrowtips", "headless arrow", "bow string", "bowstring", "crossbow string", "dart tip", "bolt tip", "dragon dart tip", "crossbow limbs"), "fletching skill", "fletching training", "fletch supplies");
        extend("Slayer gear", names("wild pie", "spicy stew", "slayer cape", "slayer hood", "slayer staff", "bonecrusher", "ash sanctifier", "dragonbone necklace", "expeditious bracelet", "bracelet of slaughter", "cannon base", "cannon barrels", "cannon stand", "cannon furnace", "cannonball"), "slayer skill", "slayer training");
        extend("Firemaking supplies", names("spicy stew", "firemaking cape", "firemaking hood", "pyromancer", "burnt pages", "tinderbox", "logs", "bruma root", "bruma torch", "warm gloves", "firelighter", "oil lantern"), "firemaking skill", "firemaking training");
        extend("Axes / woodcutting", names("spicy stew", "woodcutting cape", "woodcutting hood", "lumberjack", "forester's", "forestry", "forestry kit", "log basket", "felling axe", "strung rabbit foot", "nature offerings", "secateurs attachment", "logs", "bird nest", "rations"), "woodcutting", "woodcutting gear", "woodcutting skill", "woodcutting training", "wc gear");
        extend("Construction supplies", names("crystal saw", "spicy stew", "cup of tea", "construction cape", "construction hood", "carpenter", "carpenter's outfit", "plank sack", "mahogany homes", "steel bar", "nails", "butler", "teleport to house"), "construction gear", "construction skill", "construction training");
        extend("Runecrafting tools", any(suffix("rune", "runes"), names("spicy stew", "pure essence", "rune essence", "daeyalt essence", "guardian essence", "runecraft cape", "runecraft hood", "raiments of the eye", "abyssal needle", "colossal pouch", "runecraft pouch", "binding necklace", "elemental talisman", "talismans", "essence pouches", "rune pouch", "tiara")), "runecrafting", "runecraft", "runecrafting skill", "runecraft training", "rc gear");

        // More precise gear-only or activity kits for planning a quick trip from the bank.
        add("Mining gear only", names("pickaxe", "prospector", "varrock armour", "mining gloves", "expert mining gloves", "superior mining gloves", "celestial ring", "celestial signet", "mining cape", "mining hood", "gem bag", "coal bag", "amulet of glory", "dwarven stout"),
            "mining gear", "mining equipment", "mining outfit", "mining setup");
        add("Fletching gear only", names("knife", "fletching cape", "fletching hood", "fletching potion", "dragonfruit pie"),
            "fletching equipment", "fletching gear", "fletching tools", "fletching setup");
        add("Herb run kit", any(
            // Tools, supplies and harvest storage.
            names("spade", "seed dibber", "magic secateurs", "secateurs", "bottomless compost bucket", "ultracompost", "supercompost", "compost", "herb sack", "seed box", "amulet of bounty", "farming cape", "farming hood", "farming outfit", "farmer's", "rake", "stamina potion", "energy potion"),
            // HERB seeds only: do not show all tree/allotment seeds or harvested herbs.
            f -> matchesName(f.name, "seed") && matchesName(f.name, "guam", "marrentill", "tarromin", "harralander", "ranarr", "toadflax", "irit", "avantoe", "kwuarm", "snapdragon", "cadantine", "lantadyme", "dwarf", "torstol"),
            // Common quick herb-patch teleports. Avoid showing every single teleport item.
            names("ectophial", "ardougne cloak", "explorer's ring", "stony basalt", "icy basalt", "trollheim teleport", "catherby teleport", "camelot teleport", "falador teleport", "ardougne teleport", "fenkenstrain's castle teleport", "salve graveyard teleport", "kharyrll teleport", "xeric's talisman", "xeric's glade", "skills necklace", "ring of the elements", "amulet of glory", "farming guild teleport", "spirit tree", "drakan's medallion", "harmony island teleport", "teleport to house", "rune pouch")),
            "herb run", "herb runs", "herbrun", "herbruns", "herb farming run", "herb farm run", "quick herb run", "herblore run", "farm run", "farming run", "herb run gear", "herb run setup", "herb run kit");
        add("Tree run kit", any(names("spade", "gardening trowel", "plant pot", "watering can", "ultracompost", "supercompost", "compost", "basket", "sack", "farming cape", "farming hood", "stamina potion", "seed dibber", "ring of wealth", "skills necklace", "teleport to house", "spirit tree", "crystal teleport seed", "amulet of glory"),
            f -> matchesName(f.name, "sapling") || (matchesName(f.name, "seed") && matchesName(f.name, "oak", "willow", "maple", "yew", "magic", "palm", "dragonfruit", "papaya", "pineapple", "calquat", "teak", "mahogany", "redwood", "celastrus", "hespori"))),
            "tree run", "tree runs", "treerun", "treeruns", "fruit tree run", "tree farm run", "tree farming run", "tree run gear", "tree run setup");
        add("Birdhouse run kit", names("bird house", "birdhouse", "clockwork", "hammer", "chisel", "logs", "seed", "digsite pendant", "fossil island", "mushroom meadow", "verdant valley", "ruby necklace", "stamina potion", "hunter cape", "hunter hood", "teleport to house"),
            "birdhouse run", "birdhouse runs", "birdhouse", "birdhouses", "bird house run", "birdhouse kit", "birdhouse setup");
        add("Sailing repair kit", names("repair kit", "sailcloth", "rigging", "boat building supplies", "hammer", "saw", "nails", "plank", "swamp paste", "shipwright", "sailing cape"),
            "sailing repair", "boat repairs", "ship repair", "boat repair kit");
        add("Sailing salvage kit", names("salvaging hook", "salvage", "shipwreck", "cargo hold", "repair kit", "sailing cape", "ship's log", "captain's logbook"),
            "sailing salvage", "salvaging gear", "shipwreck salvage", "salvaging setup");

        // Activity preparation kits are kept separate from general item categories.
        ActivityCategories.register(CategoryMatcher::add);
    }

    private static Category find(String q, boolean strict)
    {
        Category exact = BY_ALIAS.get(q);
        if (exact != null) { return exact; }
        if (strict || q.length() < 3) { return null; }
        Category candidate = null;
        for (Map.Entry<String, Category> entry : BY_ALIAS.entrySet())
        {
            if (entry.getKey().startsWith(q))
            {
                if (candidate != null && candidate != entry.getValue()) { return null; }
                candidate = entry.getValue();
            }
        }
        return candidate;
    }

    public static Query parse(String input, boolean strict)
    {
        String normalized = normalize(input);
        boolean exclusive = normalized.startsWith("@");
        String q = exclusive ? normalize(normalized.substring(1)) : normalized;
        if (q.isEmpty()) { return new Query(input, false, Collections.emptyList()); }
        // Explicit @strength retains the complete Strength skill category.
        if (exclusive && q.equals("strength"))
        {
            return new Query(input, true, Collections.singletonList(BY_ALIAS.get("strength skill")));
        }
        Category direct = find(q, strict);
        if (direct != null) { return new Query(input, exclusive, Collections.singletonList(direct)); }
        // Two-category intersections: `ranged boots`, `prayer legs`, `magic helmets`.
        for (int i = 0; i < q.length(); i++)
        {
            if (q.charAt(i) != ' ') { continue; }
            // Composite terms require COMPLETE aliases: avoids transient search flicker.
            Category left = BY_ALIAS.get(q.substring(0, i));
            Category right = BY_ALIAS.get(q.substring(i + 1));
            if (left != null && right != null && left != right)
            {
                return new Query(input, exclusive, Arrays.asList(left, right));
            }
        }
        return new Query(input, false, Collections.emptyList());
    }

    public static boolean isCategoryQuery(String search, boolean strict)
    {
        return parse(search, strict).isCategory();
    }
    public static boolean matches(String search, String itemName, boolean strict)
    {
        return parse(search, strict).matches(ItemFacts.nameOnly(itemName));
    }
    public static boolean matches(String search, ItemFacts item, boolean strict)
    {
        return parse(search, strict).matches(item);
    }
    public static Map<String, List<String>> catalog()
    {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Category c : CATEGORIES) { result.put(c.title, c.aliases); }
        return Collections.unmodifiableMap(result);
    }
}
