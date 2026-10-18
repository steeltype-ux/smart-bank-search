# Smart Bank Search — next release steps

The plugin is **already available in RuneLite Plugin Hub**. [PR #18725](https://github.com/runelite/plugin-hub/pull/18725) was merged, and the initial plugin was confirmed working in the official client.

1. Develop proposed activity features on a separate branch; preserve `@herb run` and all existing searches.
2. Add positive and negative tests for every new category, and update the category reference with verified aliases.
3. Run and document the [release checklist](TESTING.md), including in-client regression checks.
4. Review and merge the tested source changes. Copy the **full 40-character source commit SHA**.
5. In a fresh branch of your existing `runelite/plugin-hub` fork, update `plugins/smart-bank-search` to point to the new source SHA.
6. Open a **new update PR** and wait for its checks, review, and merge.

The current source version is `1.4.0`. Consider `1.5.0` for the next substantive feature release. Documentation cleanup alone does not need a version bump.

See [PUBLISHING.md](PUBLISHING.md) for full instructions.
