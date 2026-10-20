package com.smartbanksearch;

/** Short, offline guidance for interpreting category results. */
final class CategoryDescriptions
{
    private CategoryDescriptions() { }

    static String forTitle(String title)
    {
        switch (title)
        {
            case "Strength boosts": return "Strength potions, combat potions and multi-stat boosts. Example: Super strength(4).";
            case "Attack boosts": return "Attack potions, combat potions and multi-stat boosts. Example: Super attack(4).";
            case "Defence boosts": return "Defence potions, bastion/battlemage and multi-stat boosts. Example: Super defence(4).";
            case "Magic boosts": return "Magic-boosting potions, brews and hearts. Example: Magic potion(4).";
            case "Ranged boosts": return "Ranging potions, bastion potions and selected boosts. Example: Ranging potion(4).";
            case "Prayer restores": return "Prayer-restoring consumables. Example: Prayer potion(4). For broad Prayer supplies, type @prayer.";
            case "Herb run kit": return "Herb seeds, farming tools, compost and commonly used patch teleports. Example: Magic secateurs.";
            case "Tree run kit": return "Saplings, planting supplies and common transport. Example: Yew sapling.";
            case "Guardians of the Rift kit": return "Essence pouches, pickaxes, Raiments of the Eye and optional rune combinations. Example: Colossal pouch.";
            case "Tempoross kit": return "Harpoons, Angler outfit and optional tools. Example: Dragon harpoon.";
            case "Wintertodt kit": return "Cold-weather and Firemaking supplies, food and recovery options. Example: Warm gloves.";
            case "Giant's Foundry kit": return "Smithing input metals, eligible recyclable equipment and foundry kit. Example: Mithril bar.";
            case "Wyrms task": return "Karuulm: equip boots of stone, boots of brimstone or granite boots unless Elite Kourend & Kebos Diary is complete. Bring Slayer gear, Magic protection and dragonbane or Ranged weapons. Wyrms do not require antifire. The boots are not needed for Wyrmscraig Cavern.";
            case "Drakes task": return "Karuulm: wear protective stone/brimstone/granite boots unless Elite Diary is complete. Consider antifire protection for drakes' volcanic breath special and Protect from Missiles for standard attacks.";
            case "Hydras task": return "Normal Karuulm Hydras: bring heat-protective boots unless Elite Diary is complete, plus poison protection and combat supplies. For the boss kit, search @hydra or @hydra boss.";
            case "Sulphur lizards task": return "Karuulm alternative to desert Lizards: heat-protective boots unless Elite Diary is complete; optional cannon supplies. No ice coolers are needed.";
            case "Alchemical Hydra preparation": return "Boss preparation at Karuulm. Stone, brimstone or granite boots protect against floor heat unless Elite Kourend & Kebos Diary is complete; use @hydras for normal Hydras.";
            case "Slayer gear": return "Slayer utility equipment and task supplies. Example: Slayer helmet (i).";
            case "Prayer potions": return "Prayer-restoring drinks and related restoratives. Example: Prayer potion(4).";
            case "Combat potions": return "Common combat-boosting drinks across Attack, Strength, Defence, Ranged and Magic.";
            case "Divine combat boosts": return "Divine variants of combat-stat boosting potions. Example: Divine ranging potion(4).";
            default:
                if (title.toLowerCase(java.util.Locale.ROOT).contains("kit"))
                {
                    return "Curated activity suggestions, not a guaranteed or complete loadout. Results depend on items in your bank.";
                }
                return "Shows matching items currently in your bank. Use @ for category-only matches; regular queries are additive.";
        }
    }
}
