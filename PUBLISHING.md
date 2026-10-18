# Smart Bank Search — Plugin Hub update guide

Smart Bank Search is **already published**: [initial Plugin Hub PR #18725](https://github.com/runelite/plugin-hub/pull/18725) merged October 17, 2026. The maintainer confirmed that the plugin works in the official RuneLite client. The [existing manifest](https://github.com/runelite/plugin-hub/blob/master/plugins/smart-bank-search) pins a specific source commit.

## Updating the existing plugin

1. Develop and review changes in a separate branch of [steeltype-ux/smart-bank-search](https://github.com/steeltype-ux/smart-bank-search). Keep the BSD 2-Clause LICENSE and run the [regression checklist](TESTING.md).
2. Only report automated or manual tests that actually ran. Do not assume successful compilation proves all search categories work.
3. Merge reviewed source changes, then copy the **full 40-character source commit SHA**. Do not copy a file/blob SHA.
4. Sync your existing fork of [runelite/plugin-hub](https://github.com/runelite/plugin-hub) with upstream and create a **new update branch**.
5. Edit the existing `plugins/smart-bank-search` in the fork, replacing only `commit=` with your new tested source SHA, retaining the repository URL:

   ```ini
   repository=https://github.com/steeltype-ux/smart-bank-search.git
   commit=YOUR_NEW_40_CHARACTER_SOURCE_COMMIT_SHA
   ```

6. Open a **new pull request** from that branch into `runelite/plugin-hub` `master`, using [the update PR template](PULL_REQUEST_TEMPLATE.md). Review build/check results and any maintainer feedback. Wait for the update PR to merge.

Do **not** create a second manifest, reopen the already-merged initial PR, or assume that pushing code to the source repository immediately updates RuneLite. The Plugin Hub serves the pinned source commit until the new manifest is merged.

The current Gradle source version is `1.4.0`. Pick a later version such as `1.5.0` when preparing a meaningful feature release, not for documentation-only housekeeping.

## Security and attribution

Keep `author=steeltype-ux` and the GitHub Issues support URL in `runelite-plugin.properties` accurate. Never commit `.runelite/credentials.properties`, tokens, keys, or `.env` files. See [RuneLite's official contributor instructions](https://github.com/runelite/plugin-hub/blob/master/README.md).
