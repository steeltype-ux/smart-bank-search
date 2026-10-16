# Smart Bank Search pre-publication test checklist

**Do not mark a test passed unless it was actually performed.** This checklist is for Windows IntelliJ RuneLite and the official RuneLite Plugin Hub CI.

## Automated

- [ ] Run `gradle clean test` on a machine with RuneLite dependencies installed; capture Gradle success output.
- [ ] Run `gradle run` with the project configured for a development RuneLite client; confirm the plugin loads and can be enabled.
- [ ] After pushing the public repository and creating Plugin Hub PR, confirm the RuneLite `build` and `Plugin Hub Checks` statuses.

## Manual bank checks

Keep **Bank Tags** installed to exercise real-world coexistence. Bank results should include only items currently held in the bank.

- [ ] `herb`: clean/grimy ranarr and torstol appear if owned.
- [ ] `herb`: ranarr seeds excluded unless their native item names already match.
- [ ] `@herb`: clean/grimy herbs shown, herb sack and herb seeds excluded.
- [ ] `seed`: multiple farm seed families appear.
- [ ] `@rune`: runes appear; Rune platebody does not.
- [ ] `@boots`, `@gloves`, `@legs`: correctly use equipped slots.
- [ ] `@ranged boots`: ranged boots appear; melee-only boots do not.
- [ ] `@prayer legs`: prayer-bonus legs appear where available.
- [ ] `mining` and `@mining gear`: identify gear vs mining resource behavior.
- [ ] `fletching`: knife, unfinished bows, arrows/feathers when banked.
- [ ] `sailing`: relevant supplies for the current game version.
- [ ] `@herb run`: supplies and known patch-teleport equipment.
- [ ] `dragon fighting equipment`: anti-dragon shield/antifire, not Dragon platelegs.
- [ ] `dragon scim`: native name search still works.
- [ ] `qty > 10`, `ge > 1m`: advanced RuneLite bank searches still work.
- [ ] `tag:<customtag>`: Bank Tags-specific search still works.
- [ ] Toggle plugin off, confirm vanilla search behavior returns.
- [ ] Search rapidly, switch bank tabs, open/close bank, relog; confirm no freezes or stack errors.

## Known limitation

Names-based broad categories, skill and activity kits are curated classifications, not canonical lists. Some items might be misclassified. The plugin does not withdraw or equip items.
