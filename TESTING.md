# Smart Bank Search release regression checklist

**Do not mark a test passed unless it was actually performed.** The initial plugin was merged in [PR #18725](https://github.com/runelite/plugin-hub/pull/18725) and confirmed working in the official RuneLite client. This is a reusable checklist for **future updates**. Unchecked items are not claims of past failure. Record the source SHA, date, tester, logs, and manual results for each release.

## v1.5 activity-kit regression checks

- [ ] **Category directory**: the original chest icon appears in RuneLite's sidebar; filter `@gotr`, `cerb`, `attack`, `chinchompa`, and `fishing`. Confirm alias list and selection, copying valid `@search` to the clipboard, and proper sidebar removal/recreation on disable/re-enable. The full list must contain 208 groups.
- [ ] **Real-name regression gate**: run `python3 tools/audit_osrs_item_names.py --strict --report build/reports/osrs-item-name-audit.md` with JDK 17 and network access. Verify all category names, boost combinations, and curated false positives. Check the GitHub Actions audit artifact, but do not confuse it with real RuneLite equipment-stat or bank testing.
- [ ] **Skill boost coverage**: ensure regular, mixed, super/divine combat potions, Fishing potion/mix and pies, Dwarven stouts, Hunter potion, botanical/garden/mushroom/dragonfruit pies, Ancient/Forgotten brew, and spicy stew are found in their appropriate skill searches.
- [ ] **Clue tiers**: `@easyclue` through `@masterclue` should find respective **Scroll box** items but must **exclude Reward caskets**, which are received after completion.

- [ ] **Attack & combat potion search:** check `attack` and `@attack` against actual doses of regular Attack potion, Super attack, Divine super attack, Attack mix, Superattack mix, Combat potion/mix, Super combat and Divine super combat. Confirm `@combat pots` also finds regular Strength and Defence potions; `@strength` finds Combat potions, while `@defence` finds Super combat but not normal Combat potions. Non-Attack boost potions should not appear for `@attack`.

**One-shot audit:** [All 51 remaining optimized activity filters](ONE_SHOT_ACTIVITY_AUDIT.md) must be individually reviewed in addition to the 16 minigame kits.

- [ ] For all **51 boss/Slayer/farming/Wilderness/clue/quest categories**, verify a relevant banked weapon/tool, a situational alternative and a distinct false positive in RuneLite (normal and `@` mode). Run all 67 categories before publishing.
- [ ] Boss/Slayer false-positive checks: Cerberus crystal drops, Basilisk jaw, Scurrrius spine, ToA thread, ToB Dawnbringer, nonexistent Ice rune and unbanked spell names must not be presented as prepared bank gear.
- [ ] Hunter/Wilderness false-positive checks: black-chinchompa preparation finds box traps and escape gear without touching legacy `@chins`; Revenants show charged bracelet and ether; Artio escape shows actual runes or blighted sacks rather than spell-name text.
- [ ] Farming/clue/quest checks: contract-specific seeds and saplings, seaweed spores rather than harvested seaweed, all five clue tiers, clue boxes for risky Wilderness tiers, and real fairy-ring transport items (not scenery).

- [ ] Verify `@cannon` and `@cannon tasks` yield identical Slayer-trip candidates including cannon supplies, Slayer helm and prayer potions; `@cannon supplies`/`@cannonballs` remain restricted to cannon equipment, ammunition and moulds.
- [ ] Verify `@chinchompa` and `@chinchompa hunting` match box traps and Hunter gear, while legacy `@chins`/`@chinchompas` still select thrown weapons, not traps.
- [ ] Verify common aliases (`wt`, `temp`, `mh`, `ba`, `vm`, `bf`, `cerb`, `hydra`, `gargs`, `burst`, `revs`, `calv`, `contracts`) and farming shortcuts (`birdhouse`, `treerun`) in normal and `@` search modes.

- [ ] Run `ActivityCategoriesSelfTest` with Java 11, alongside existing category self-tests.
- [ ] Run `ActivityCategoriesTest` through `gradle clean test`; capture the output and any failures.
- [ ] Check `@herbrun` and `@herb run` show identical results in a bank with representative herb seeds, compost and teleports.
- [ ] Test `@wintertodt`, `@tempoross`, `@gotr`, `@giants foundry`, `@mahogany homes`, `@zulrah`, `@vorkath`, `@jad`, `@revenants`, `@cluekit`, `@questprep` with actual bank contents.
- [ ] Mahogany Homes: `@mh` and `@mahogany homes` should match all four Carpenter's outfit items, plank variants and steel bars, Amy's saw and Imcando hammer, city teleports, rune pouch/Astral Contact supplies and optional Plank Make kit. Confirm no unrelated logs, nails, generic stamina or other bars.
- [ ] Hallowed Sepulchre: `@sepulchre` should return Hallowed upgrades plus Mith grapple/Dorgeshuun crossbow, plain planks, appropriate nails, saw/hammer, vampyre dust, lockpick, holy symbol, poison cure and enchant runes. Confirm no ordinary logs, planked furniture, Hallowed marks or irrelevant gear.
- [ ] Pyramid Plunder: `@plunder` should include snake charm, Pharaoh's sceptre, poison protection, prayer book and holy symbol, regen bracelet/HP cape, sensible travel/restoration items and food. Exclude ordinary energy potions and snake drops.
- [ ] Tithe Farm: `@tithe` should include the spade, seed dibber, Gricoller's can, watering cans, every Farmer outfit piece, Humidify rune setups and optional stamina/teleports. Exclude supplied on-site seeds/fertiliser and unrelated farming supplies.
- [ ] Pest Control: `@pest` and `@pest control` should show Void/Elite Void, melee/ranged/spec weapons, chinchompas and optional combat boosts, but not food, prayer potion or super restores.
- [ ] Barbarian Assault: `@ba` should find a good Attacker loadout, defensive armour, Imcando hammer and games necklaces. No rune pouch, outside runes/ammunition, Prayer potion, generic food, or Horn of Glory.
- [ ] Soul Wars: `@soulwars` should show anti-magic armour, Ancient Magicks equipment/runes and PvP weapons without any banked food, Prayer potion, stat boosts, or match-provided bandages/explosives.
- [ ] Nightmare Zone: `@nmz` should return rock cake/orb, all Dharok pieces, Obsidian + Toktz-xil-ak, usual melee and Prayer equipment, coins. Exclude unbankable Absorption and Overload and generic food.
- [ ] Shades of Mort'ton: `@shades` / `@shades of mortton` should include Flamtaer hammer/bracelet/bag, swamp paste, timber beams/limestone bricks, Sacred oil/Serum 207/208, all six named remains, named pyre logs, properly coloured catacomb keys and tinderbox; exclude unrelated plain silver/bronze/gold keys.
- [ ] Fishing Trawler: `@trawler` and `@fishing trawler` should include swamp paste, rope, bailing bucket, hammer/Imcando hammer and actual axes. Ensure **Angler outfit, ordinary buckets, fish barrels and general fishing equipment** are excluded.
- [ ] Volcanic Mine: `@vm` should find a pickaxe, numulite, Prospector gear, celestial ring, prayer potion/food, Regeneration / Prayer alternatives and optional stamina / teleports. Exclude mining ores, volcanic ash, and generic energy potions.
- [ ] Blast Furnace: `@bf` should include all eight ores, coal, coal bag, goldsmith gauntlets, ice gloves/Smiths gloves (i), coins, stamina and bucket of water. Confirm finished bars and standard Smiths' uniform pieces aren't selected.
- [ ] Guardians of the Rift: `@gotr` should include small/medium/large/giant/colossal pouches (and degraded variants), each piece of the eye outfit including recolours, pickaxes, chisel, lantern, Varrock armour, celestial ring/signet, binding necklaces, rune pouch, Astral/air/cosmic/combination runes. Confirm it excludes talismans, Abyssal needle, pure essence, stamina potions and Ring of endurance. Manually check whether alternative optional rune setups overwhelm everyday searches.
- [ ] Tempoross: `@tempoross` and `@temp` must include all four **Spirit angler** outfit pieces, regular Angler alternatives, rope/hammer/buckets, harpoons, Imcando hammer, and optional Humidify/harpoon-special items if banked. Confirm stamina/energy potions, Ring of endurance, fish barrel, tackle box, bucket of sand and unrelated fishing gear are not returned.
- [ ] Giant's Foundry: compare `@giants foundry`, `@giant's foundry`, and `@foundry` for identical results. Include runite bars, steel warhammers, mithril claws, rune platebodies, Smiths' outfit, ice gloves and buckets. Confirm gold/silver bars, raw ores, dragon/black metal items, decorative trims, Imcando hammer, and one-bar swords are excluded.
- [ ] For `@wintertodt`, verify regen bracelet, cakes/chocolate cakes, pizza and pie variants, cooked hunter meat, rejuvenation potions, and ordinary healing food where banked; ensure cake tins, pizza bases and uncooked/burnt food are excluded.
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
