# Official RuneLite Plugin Hub publication

Source: https://github.com/runelite/plugin-hub/blob/master/README.md

## Prerequisites and attribution

1. Sign into GitHub as **steeltype-ux**.
2. The public attribution `author=steeltype-ux` and GitHub Issues support URL are already configured in `runelite-plugin.properties`.
3. Confirm the license (existing MIT), README and code accurately describe the plugin. Never upload `.runelite/credentials.properties`, login tokens or `.env` files.
4. Run `gradle clean test` in IntelliJ or a terminal with Gradle configured, and smoke-test the v1.4 skill categories in a real bank. Fix errors *before* you submit.

## Put your plugin on GitHub

Recommended low-friction Windows route: **GitHub Desktop**.

1. Extract this project ZIP to a permanent folder, e.g. `Documents\Smart-Bank-Search`.
2. Install GitHub Desktop from https://desktop.github.com/ and sign in.
3. File > Add local repository > Choose the extracted `smart-bank-search` folder. If prompted, click **create a repository here**.
4. Set repository name `smart-bank-search`, commit your files as `Initial Smart Bank Search Plugin Hub release`.
5. Click **Publish repository** and **uncheck Keep this code private**. RuneLite needs a public repository.
6. Verify it appears at `https://github.com/steeltype-ux/smart-bank-search` and that `runelite-plugin.properties`, `src/`, `LICENSE`, and `README.md` are at the repository root.
7. From the GitHub repository's Commits page, open the latest commit and copy its **full 40-character SHA**. Do not use the abbreviated hash.

## Submit to RuneLite's Plugin Hub

1. Open https://github.com/runelite/plugin-hub and click **Fork** (fork into your GitHub account).
2. In your fork, create a branch, e.g. `add-smart-bank-search` (branch selector > View all branches > New branch).
3. In **your fork** (not the plugin source repository), click `Add file` > `Create new file` and enter the filename `plugins/smart-bank-search`.
4. Paste **only** these two lines, replacing the placeholders with the actual repository URL and exact 40-character commit hash:

   ```ini
   repository=https://github.com/steeltype-ux/smart-bank-search.git
   commit=REPLACE_WITH_THE_REAL_40_CHARACTER_COMMIT_SHA
   ```

5. Commit this new manifest to your fork branch.
6. Open the official https://github.com/runelite/plugin-hub and select **New pull request** > **compare across forks**. Base: `runelite/plugin-hub` `master`; head: your fork `add-smart-bank-search`.
7. Use the title `Add Smart Bank Search` and the description in `PULL_REQUEST_TEMPLATE.md` in this plugin repository.
8. Submit the pull request and follow RuneLite's CI status and reviewer comments. **Approval is by RuneLite, not automatic**. When you change plugin code, push a new source commit, then update the `commit=` value on the **same** Plugin Hub pull request branch.

## Before users can install it

The RuneLite team needs to merge your manifest pull request, after which the plugin should become available through the Plugin Hub in the normal Jagex Launcher-managed client. This package is **not** submitted or approved by itself.

## Existing plugins

RuneLite recommends contributing to an existing plugin where possible. Better Bank Search and Semantic Bank Search address related use cases. A fresh submission should explain the added functionality: 141 categories, all 24 skills, activity kits, native bank-bar integration, and category intersections. RuneLite may still prefer consolidation.
