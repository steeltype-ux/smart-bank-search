# Smart Bank Search

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
| `herb run` | Herb seeds, farming tools, compost and common patch teleports |
| `dragon fighting equipment` | Dragonbane weapons, antifire protection and related items |
| `@rune` | Rune resources exclusively, not Rune platebodies |
| `@ranged boots` | Items matching both ranged gear and the boots slot |

### Search modes

- **Ordinary search** (`herb`): adds automatic category hits without intentionally removing native RuneLite or Bank Tags matches.
- **Exclusive category** (`@herb`): only show items matching that category, overriding ordinary name matches.
- **Two-category intersection** (`@prayer legs`): both categories must match.
- An unknown query is left to RuneLite's normal bank search.

The plugin provides **141 category groups**, covering all 24 OSRS skills, equipment slots, weapons, resources, combat, skilling and selected activity kits. See [CATEGORY_REFERENCE.md](CATEGORY_REFERENCE.md) and [SKILL_SEARCHES.md](SKILL_SEARCHES.md) for the complete guide.

## How it works

Smart Bank Search listens to the `bankSearchFilter` callback to extend the bank's normal item filter. Item names, equipment slots and combat/prayer stats are retrieved locally through RuneLite's API and cached within the client. It **does not** read account credentials, communicate with external servers, automate gameplay, or send bank contents anywhere.

Classification uses curated rules and may have false positives or omissions, especially for newer items and activity-based searches. Activity searches are *candidate supplies*, not guaranteed optimal loadouts. Search matching is case-insensitive. The `@` feature can interact with other plugins that modify the same bank-search callback.

## Installation

After RuneLite Plugin Hub approval, open RuneLite through the Jagex Launcher, select **Plugin Hub**, search **Smart Bank Search**, and install it.

Before approval, developers can run this project in IntelliJ using the Gradle `run` task (JDK 17 recommended for Gradle, Java 11 source compatibility).

## Development / testing

- `gradle test`: JUnit matcher tests (requires RuneLite dependencies and Gradle).
- `gradle run`: launches RuneLite in development mode with this plugin preloaded.
- Dependency-free core matcher tests can run using a JDK with:

  ```sh
  javac --release 11 -d out src/main/java/com/smartbanksearch/{CategoryMatcher,ItemFacts}.java src/test/java/com/smartbanksearch/{CategoryMatcherSelfTest,CategoryMatcherSkillSelfTest}.java
  java -cp out com.smartbanksearch.CategoryMatcherSelfTest
  java -cp out com.smartbanksearch.CategoryMatcherSkillSelfTest
  ```

For Windows PowerShell, prefer running the test task through IntelliJ's Gradle panel. See [TESTING.md](TESTING.md) for a release smoke-test checklist and [PUBLISHING.md](PUBLISHING.md) for official Plugin Hub submission instructions.

## Compatibility and support

Designed for the normal bank interface, not a replacement bank UI. The core category matcher is unit tested, and an earlier v1.2 bank-filter implementation was verified manually in-game. **The v1.4 expanded category rules still require a full RuneLite build and in-game regression testing before final submission.**

Please use [GitHub Issues](https://github.com/steeltype-ux/smart-bank-search/issues) to report false positives, missing items or compatibility problems.

## License

MIT. See [LICENSE](LICENSE).
