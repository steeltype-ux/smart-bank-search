#!/usr/bin/env python3
"""Read-only, reproducible OSRS item-name audit for Smart Bank Search.

Downloads an independently compiled item-name snapshot *only in CI/development*.
The RuneLite plugin never downloads data, and the snapshot is not bundled in its JAR.
Checks all CategoryMatcher.catalog() groups through the actual Java predicates.

Usage: python3 tools/audit_osrs_item_names.py --report /tmp/osrs-name-audit.md
"""
import argparse
from collections import Counter, defaultdict
from pathlib import Path
import json
import subprocess
import tempfile
from urllib.request import Request, urlopen

ROOT = Path(__file__).resolve().parent.parent
SNAPSHOT = (
    "https://raw.githubusercontent.com/MisterTriangle/osrs-item-reference-data/"
    "d2027cf80d71566e3fcbf826cfa611eec8fbbb69/osrs-item-id-map.json"
)

# Each family is a proven OSRS item-name stem that genuinely boosts its skill.
# These are *name-only* checks; equipment metadata cannot be inferred here.
BOOST_FAMILIES = {
    "Attack boosts": ("Attack potion", "Super attack", "Combat potion", "Super combat potion", "Zamorak brew"),
    "Strength boosts": ("Strength potion", "Super strength", "Combat potion", "Super combat potion"),
    "Defence boosts": ("Defence potion", "Super defence", "Super combat potion", "Bastion potion", "Saradomin brew"),
    "Magic boosts": ("Magic potion", "Battlemage potion", "Forgotten brew"),
    "Ranged boosts": ("Ranging potion", "Bastion potion"),
    "Prayer restores": ("Prayer potion", "Super restore", "Sanfew serum"),
    "Attack skill": ("Attack potion", "Super attack", "Combat potion", "Super combat potion", "Zamorak brew"),
    "Strength skill": ("Strength potion", "Super strength", "Combat potion", "Super combat potion", "Zamorak brew"),
    "Defence skill": ("Defence potion", "Super defence", "Super combat potion", "Bastion potion", "Saradomin brew"),
    "Ranged equipment": ("Ranging potion", "Ranging mix", "Bastion potion"),
    "Magic equipment": ("Magic potion", "Magic mix", "Battlemage potion", "Ancient brew", "Forgotten brew", "Wizard's mind bomb"),
    "Mining resources": ("Dwarven stout", "Spicy stew"),
    "Fishing supplies": ("Fishing potion", "Fishing mix", "Fish pie", "Admiral pie"),
    "Axes / woodcutting": ("Spicy stew",),
    "Farming tools": ("Garden pie", "Spicy stew"),
    "Herblore skill": ("Botanical pie", "Spicy stew"),
    "Hunting supplies": ("Hunter potion", "Spicy stew"),
    "Thieving gear": ("Spring sq'irkjuice", "Summer sq'irkjuice", "Spicy stew"),
    "Crafting supplies": ("Mushroom pie", "Spicy stew"),
    "Fletching supplies": ("Fletching potion", "Dragonfruit pie"),
    "Smithing tools": ("Dwarven stout", "Spicy stew"),
    "Construction supplies": ("Crystal saw", "Spicy stew"),
    "Runecrafting tools": ("Spicy stew",),
    "Agility gear": ("Agility potion", "Summer pie"),
    "Slayer gear": ("Wild pie",),
}

KNOWN_FALSE_POSITIVES = {
    "Farming tools": ("Air battlestaff", "Fire battlestaff"),
    "Combat potions": ("Dragon battleaxe", "Overload(4)"),
    "Magic boosts": ("Ancestral robe top", "Prayer potion(4)"),
    "Ranged boosts": ("Masori chaps", "Magic potion(4)"),
    "Prayer restores": ("Dragon bones", "Ranging potion(4)"),
    "Attack boosts": ("Dragon defender", "Strength potion(4)"),
    "Defence boosts": ("Justiciar chestguard", "Attack potion(4)"),
    "Attack skill": ("Saradomin brew(4)", "Magic potion(4)"),
    "Defence skill": ("Combat potion(4)",),
    "Giant seaweed runs": ("Giant seaweed", "Drift net"),
    "Cerberus preparation": ("Primordial crystal", "Pegasian crystal", "Eternal crystal"),
    "Wyrms task": ("Wyrm bones", "Dragon sword", "Antifire potion(4)", "Cannonball"),
    "Drakes task": ("Drake bones", "Drake's claw"),
    "Hydras task": ("Hydra bones", "Hydra's eye"),
    "Sulphur lizards task": ("Ice cooler", "Small lizard"),
    "Basilisks task": ("Basilisk jaw",),
    "Nightmare Zone kit": ("Overload(4)", "Absorption(4)"),
    "Fishing Trawler kit": ("Angler hat", "Angler top"),
    "Easy clue kit": ("Reward casket (easy)",),
    "Medium clue kit": ("Reward casket (medium)",),
    "Hard clue kit": ("Reward casket (hard)",),
    "Elite clue kit": ("Reward casket (elite)",),
    "Master clue kit": ("Reward casket (master)",),
}


def matches_stem(name, stem):
    # Only item families, not unrelated partial words.
    lower, prefix = name.casefold(), stem.casefold()
    if lower == prefix:
        return True
    if lower.startswith(prefix):
        tail = lower[len(prefix):]
        return not tail or tail[0] in " ("
    return False


def load_snapshot(url):
    req = Request(url, headers={"User-Agent": "SmartBankSearch-name-only-audit/1.0"})
    with urlopen(req, timeout=90) as response:
        data = json.load(response)
    if "records" not in data or not isinstance(data["records"], list):
        raise RuntimeError("Unexpected item snapshot schema, refusing fabricated counts")
    seen, items = set(), []
    for record in data["records"]:
        if record.get("is_placeholder"):
            continue
        name = record.get("exact_osrs_item_name", "").strip()
        if not name or name in seen:
            continue
        seen.add(name)
        items.append((record["exact_osrs_item_id"], name))
    return data["snapshot_date"], len(data["records"]), items


def run_exact_java_matcher(items, directory):
    src = ROOT / "src/main/java/com/smartbanksearch"
    runner = ROOT / "src/test/java/com/smartbanksearch/CategoryNameAudit.java"
    compilation = [str(src / x) for x in ("ItemFacts.java", "ActivityCategories.java",
                    "CategoryMatcher.java", "CategoryDirectoryModel.java")]
    classes = directory / "classes"
    classes.mkdir()
    subprocess.run(["javac", "--release", "11", "-d", str(classes),
                    *compilation, str(runner)], check=True)
    input_tsv, output_tsv = directory / "items.tsv", directory / "matched.tsv"
    with input_tsv.open("w", encoding="utf-8") as file:
        for item_id, name in items:
            file.write(f"{item_id}\t{name.replace(chr(9), ' ').replace(chr(10), ' ')}\n")
    result = subprocess.run(["java", "-Xmx768m", "-cp", str(classes),
                             "com.smartbanksearch.CategoryNameAudit",
                             str(input_tsv), str(output_tsv)],
                            check=True, capture_output=True, text=True)
    print(result.stdout.strip())
    by_category = defaultdict(set)
    with output_tsv.open(encoding="utf-8") as file:
        next(file)
        for line in file:
            category, item_id, name = line.rstrip("\n").split("\t", 2)
            by_category[category].add(name)
    return by_category


def report(snapshot_date, source_count, items, matched):
    names = {name for _, name in items}
    expected_categories = 219
    if len(matched) > expected_categories:
        raise RuntimeError("Unexpected category count; inspect source")
    counts = Counter({category: len(group) for category, group in matched.items()})
    missing_boosts = []
    verified_boosts = 0
    unavailable = []
    for category, stems in BOOST_FAMILIES.items():
        for stem in stems:
            matching = [n for n in names if matches_stem(n, stem)]
            if not matching:
                unavailable.append((category, stem))
                continue
            for name in matching:
                # compare against the same item name, not a fabricated test item
                if name not in matched.get(category, set()):
                    missing_boosts.append((category, name))
                else:
                    verified_boosts += 1

    false_positives = []
    verified_negatives = 0
    for category, blacklist in KNOWN_FALSE_POSITIVES.items():
        for bad in blacklist:
            if bad not in names:
                continue
            if bad in matched.get(category, set()):
                false_positives.append((category, bad))
            else:
                verified_negatives += 1

    lines = [
        "# OSRS real-item name audit",
        "",
        f"**Snapshot:** {snapshot_date} · [source]({SNAPSHOT})",
        f"**Source item IDs:** {source_count:,} · **Unique non-placeholder names evaluated:** {len(items):,}",
        f"**Categories in production:** {expected_categories} · **Categories with name-only matches:** {len(matched)}",
        "",
        "**Scope:** Every category was evaluated using the production Java name matching logic.",
        "Only names were supplied; item equipment slots and stats are not present.",
        "A category with 0 matches can still be valid when RuneLite provides stats in-game.",
        "This is a historical pinned snapshot, not guaranteed to cover items released after its date.",
        "",
        "## Skill-boost families",
        f"Verified item/category pairs: **{verified_boosts}**",
        f"Missing matches: **{len(missing_boosts)}**",
        f"Families absent from this source: **{len(unavailable)}**",
    ]
    for c, name in missing_boosts[:100]:
        lines.append(f"- MISSING: `{c}` should include `{name}`")
    if len(missing_boosts) > 100:
        lines.append(f"- ...and {len(missing_boosts)-100} additional missing pairs")
    lines += ["", "## Curated false-positive probes",
              f"Correctly excluded: **{verified_negatives}** · Still matching incorrectly: **{len(false_positives)}**"]
    for c, name in false_positives:
        lines.append(f"- FALSE POSITIVE: `{c}` returns `{name}`")
    lines += ["", "## Most and least populated categories (name-only)"]
    for category, count in counts.most_common(20):
        lines.append(f"- {category}: {count:,} matching names")
    lines += ["", "### Categories with few or no name-only hits"]
    for category, count in sorted(counts.items(), key=lambda p: p[1])[:20]:
        lines.append(f"- {category}: {count:,}")
    lines += ["", "## Next checks", "- Review reported misses; update only where relevant to banking.",
              "- Run with current RuneLite item metadata to validate stat-driven equipment categories.",
              "- Manually verify normal/exclusive bank searches and third-party bank plugin interactions.",
              ""]
    return "\n".join(lines), missing_boosts, false_positives


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--report", type=Path, default=Path("osrs-item-name-audit.md"))
    parser.add_argument("--strict", action="store_true",
                        help="Fail if any curated skill-boost or false-positive checks fail")
    args = parser.parse_args()
    snapshot_date, source_count, items = load_snapshot(SNAPSHOT)
    with tempfile.TemporaryDirectory() as tmp:
        matched = run_exact_java_matcher(items, Path(tmp))
        content, missing, bad = report(snapshot_date, source_count, items, matched)
        args.report.parent.mkdir(parents=True, exist_ok=True)
        args.report.write_text(content, encoding="utf-8")
        print("\n".join(content.splitlines()[:95]))
        print("Report:", args.report)
        if args.strict and (missing or bad):
            raise SystemExit(f"Audit failed: {len(missing)} missing boosts, {len(bad)} false positives")


if __name__ == "__main__":
    main()
