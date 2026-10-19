# Smart Bank Search

![Smart Bank Search Plugin Hub icon](icon.png)

Maintained by **[@steeltype-ux](https://github.com/steeltype-ux)**.

**Search OSRS bank items by what they are, not just what they're called.**

Smart Bank Search extends RuneLite's **existing bank search bar** with automatic categories; no manual tagging, separate search interface, or online service is needed.

## Examples

| Type in the bank search | Matching items in your bank (examples) |
| --- | --- |
| `herb` | Ranarr weed, grimy torstol, guam leaf |
| `seeds` | Herb seeds, tree seeds, saplings |
| `boots`, `gloves`, `legs` | Equipment matched by actual equipment slot when RuneLite provides stats |
| `ranged gear`, `prayer gear` | Equipment identified using combat and prayer bonuses plus curated fallback names |
| `mining`, `fletching`, `sailing` | Relevant skill tools and supplies |
| `herb run`, `herbrun` | Herb seeds, farming tools, compost and common patch teleports |
| `dragon fighting equipment` | Dragonbane weapons, antifire protection and related items |
| `@rune` | Rune resources exclusively, not Rune platebodies |
| `@ranged boots` | Items matching both ranged gear and the boots slot |
| `@wintertodt`, `@tempoross`, `@gotr` | Curated minigame preparation supplies in your bank |
| `@zulrah`, `@vorkath`, `@jad` | Boss preparation supplies and suitable equipment |
| `@gargoyles`, `@dustdevils`, `@revenants` | Activity-specific Slayer and Wilderness supplies |
| `@cluekit`, `@questprep` | Travel equipment, general tools and preparation supplies |
| `@giants foundry`, `@foundry` | Eligible bars, recyclable metal weapons/armour, Smiths' uniform and cooling items |
| `cannon`, `chinchompa` | Slayer cannon-trip supplies and Hunter chinchompa-trapping supplies |
| `wt`, `temp`, `mh`, `ba`, `vm`, `bf` | Fast minigame aliases for Wintertodt, Tempoross, Mahogany Homes, Barbarian Assault, Volcanic Mine and Blast Furnace |

### Search modes

For quick activity searches, use the full name or intuitive shortcut: `@cannon` and `@cannon tasks` use the same Slayer trip kit; `@chinchompa` and `@chinchompa hunting` show the same trapping supplies. The existing `@cannon supplies`/`@cannonballs` still show cannon components only; `@chins`/`@chinchompas` still mean thrown weapons.

- **Ordinary search** (`herb`): adds automatic category hits without intentionally removing native RuneLite or Bank Tags matches.
- **Exclusive category** (`@herb`): only show items matching that category, overriding ordinary name matches.
- **Two-category intersection** (`@prayer legs`): both categories must match.
- An unknown query is left to RuneLite's normal bank search.

The v1.5 source branch expands the plugin to **208 category groups** (141 original + 67 activity kits), covering all 24 OSRS skills, equipment slots, weapons, resources, combat, skilling and selected activity kits. See [CATEGORY_REFERENCE.md](CATEGORY_REFERENCE.md) and [SKILL_SEARCHES.md](SKILL_SEARCHES.md) for the complete guide.

## How it works

Smart Bank Search listens to the `bankSearchFilter` callback to extend the bank's normal item filter. Item names, equipment slots and combat/prayer stats are retrieved locally through RuneLite's API and cached within the client. It **does not** read account credentials, communicate with external servers, automate gameplay, or send bank contents anywhere.

Classification uses curated rules and may have false positives or omissions, especially for newer items and activity-based searches. Activity searches are *candidate supplies*, not guaranteed optimal loadouts. Search matching is case-insensitive. The `@` feature can interact with other plugins that modify the same bank-search callback.

## v1.5 development status

This activity expansion is **under review on a feature branch**, not yet released through RuneLite Plugin Hub. The official client continues using the approved v1.4 code until a separate Plugin Hub update PR is merged. Activity searches are candidate supplies, not optimized loadouts. New aliases include `herbrun` and `herbruns` for the existing herb-run category. To review exactly which activities and example supplies are included, see [the v1.5 activity review guide](ACTIVITY_REVIEW.md).

## Installation

Smart Bank Search is available in the official RuneLite Plugin Hub ([initial submission merged in PR #18725](https://github.com/runelite/plugin-hub/pull/18725)). Open RuneLite, select **Plugin Hub**, search **Smart Bank Search**, and install it.

For local development, use IntelliJ with the Gradle `run` task (JDK 17 recommended for Gradle, Java 11 source compatibility).

## Development / testing

- `gradle test`: JUnit matcher tests (requires RuneLite dependencies and Gradle).
- `gradle run`: launches RuneLite in development mode with this plugin preloaded.
- Dependency-free core matcher tests can run using a JDK with:

  ```sh
  javac --release 11 -d out src/main/java/com/smartbanksearch/{CategoryMatcher,ItemFacts}.java src/test/java/com/smartbanksearch/{CategoryMatcherSelfTest,CategoryMatcherSkillSelfTest}.java
  java -cp out com.smartbanksearch.CategoryMatcherSelfTest
  java -cp out com.smartbanksearch.CategoryMatcherSkillSelfTest
  ```

For Windows PowerShell, prefer running the test task through IntelliJ's Gradle panel. See [TESTING.md](TESTING.md) for a release smoke-test checklist and [PUBLISHING.md](PUBLISHING.md) for future Plugin Hub update instructions.

## Compatibility and support

Designed for the normal bank interface, not a replacement bank UI. The core category matcher is unit tested, and an earlier v1.2 bank-filter implementation was verified manually in-game. **The initial Plugin Hub build passed, and the plugin has been confirmed working in the official RuneLite client. This does not prove every category or compatibility case has been regression-tested. Follow the release checklist for every update.**

Please use [GitHub Issues](https://github.com/steeltype-ux/smart-bank-search/issues) to report false positives, missing items or compatibility problems.

## License

BSD 2-Clause. See [LICENSE](LICENSE).
