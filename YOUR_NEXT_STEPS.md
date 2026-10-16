# Publish Smart Bank Search as steeltype-ux

1. Run `gradle clean test` and `gradle run` with the full v1.4 project in IntelliJ, and complete `TESTING.md` with real OSRS bank checks. Core standalone matcher checks are not a substitute for the full RuneLite test.
2. Create a **public** repository called **smart-bank-search** while signed in as **steeltype-ux**: https://github.com/new?name=smart-bank-search
3. Upload the **contents of this smart-bank-search folder** to the repository root, not the enclosing folder and not the ZIP. GitHub Desktop makes this simplest.
4. Get the full 40-character SHA from the latest GitHub commit.
5. Fork https://github.com/runelite/plugin-hub, create a branch, and create `plugins/smart-bank-search` containing only:

   repository=https://github.com/steeltype-ux/smart-bank-search.git
   commit=PASTE_THE_REAL_40_CHARACTER_SHA

6. Open a pull request to the upstream `runelite/plugin-hub` master branch. Use `PULL_REQUEST_TEMPLATE.md` for the PR body.
7. Wait for RuneLite's CI and reviewers. If they request code changes, update the source repo and then update the manifest SHA in the same pull request.

**This package is not yet uploaded or submitted, and a working full RuneLite build/in-game test is still pending.**
