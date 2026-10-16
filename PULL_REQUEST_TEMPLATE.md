# Add Smart Bank Search

Smart Bank Search extends RuneLite's normal OSRS bank search with automatic item category filtering. Players can find herbs, seeds, equipment slots, combat-style gear, skilling tools, and activity supplies without manually tagging every item.

**Features**
- Native bank search integration through `bankSearchFilter`; no extra banking interface.
- 141 category groups and aliases spanning all 24 OSRS skills.
- Equipment slot / combat-stat matching with item-name fallbacks.
- Ordinary additive category queries (`herb`); opt-in exclusive (`@herb`) and two-category intersection queries (`@ranged boots`).
- Local-only processing; no network requests, credential handling, bank-data transmission, or gameplay automation.

**Similar plugins**
Better Bank Search and Semantic Bank Search cover related needs. This plugin additionally emphasizes broad skill/activity categorization and composable native bank searches.

**Testing**
- Dependency-free Java category regression tests: 148 passed locally against v1.4 sources.
- Full RuneLite build and in-game verification of v1.4: **pending**; update this paragraph with actual results before opening the PR.

**Maintainer**
Your public GitHub handle goes here, and you can respond to reviewer requests/issues.
