# Smart Bank Search release regression checklist

**Do not mark a test passed unless it was actually performed.** The initial plugin was merged in [PR #18725](https://github.com/runelite/plugin-hub/pull/18725) and confirmed working in the official RuneLite client. This is a reusable checklist for **future updates**. Unchecked items are not claims of past failure. Record the source SHA, date, tester, logs, and manual results for each release.

## v1.5 activity-kit regression checks

- [ ] Run `ActivityCategoriesSelfTest` with Java 11, alongside existing category self-tests.
- [ ] Run `ActivityCategoriesTest` through `gradle clean test`; capture the output and any failures.
- [ ] Check `@herbrun` and `@herb run` show identical results in a bank with representative herb seeds, compost and teleports.
- [ ] Test `@wintertodt`, `@tempoross`, `@gotr`, `@giants foundry`, `@mahogany homes`, `@zulrah`, `@vorkath`, `@jad`, `@revenants`, `@cluekit`, `@questprep` with actual bank contents.
- [ ] Inspect 2+ expected positives and 2+ expected negatives per new activity; log any omissions or overly broad items. The standalone tests include fixtures but do not replace in-game testing.

## Automated

- [ ] Record the exact source commit SHA to be published.

- [ ] Run `gradle clean test` on a machine with RuneLite dependencies installed; capture Gradle success output.
- [ ] Run `gradle run` with the project configured for a development RuneLite client; confirm the plugin loads and can be enabled.
- [ ] After opening the new update PR against the existing Plugin Hub entry, confirm the RuneLite `build` and `Plugin Hub Checks` statuses.

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

## Activity regression checks

- [ ] `@herb run`: herb seeds, patch teleports, tools and compost match; tree seeds and harvested herbs are excluded.
- [ ] `@tree run`: saplings and planting supplies match.
- [ ] `@birdhouse run`: clockworks and relevant supplies match, unrelated armour is excluded.
- [ ] `@cox`, `@tob`, `@toa`: verify representative items while noting these are not full boss loadouts.
- [ ] For every newly added activity, test at least two intended inclusions, two exclusions, and its supported aliases.

## Test record

- Commit SHA: (record)
- Date, environment and tester: (record)
- Automated test results and logs: (record)
- Manual test results and limitations: (record)
- Plugin Hub update PR and checks: (record)

## Known limitation

Names-based broad categories, skill and activity kits are curated classifications, not canonical lists. Some items might be misclassified. The plugin does not withdraw or equip items.
