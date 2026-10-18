# Smart Bank Search v1.5 — Activity Review Guide

**Draft:** [Source PR #2](https://github.com/steeltype-ux/smart-bank-search/pull/2). **Do not merge until gameplay review.** The current official RuneLite Plugin Hub version remains unchanged.

All 67 new searches return **candidate items already in the player's bank**. A search is not an authoritative checklist or optimal loadout. The lists below show the leading **curated matching name terms** (examples, not exact item IDs), plus shared supplies used by a kit. The implementation may match more item names than shown.

### How to review

1. Check that each search returns relevant items for the activity and does not overwhelm the bank with unrelated matches.
2. Try charged, uncharged, dose and item-name variants where applicable.
3. Confirm the **existing** `@herb run` works unchanged, and `@herbrun` returns identical results.
4. Check regular native bank searches and the existing `@toa`, `@cox`, `@tob`, `@gwd`, `@clues` filters.
5. Record missing items, false positives and suggested aliases in [GitHub Issues](https://github.com/steeltype-ux/smart-bank-search/issues) or as comments on PR #2.

## Bosses and raids (15)

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@zulrah` | anti venom, antivenom, ring of suffering, ring of recoil, zul andra teleport, zulrah's scales, toxic blowpipe | high-healing food, prayer-restoring supplies, brews, ranged boosts, magic boosts |
| `@vorkath` | dragon hunter crossbow, dragon hunter lance, dragonfire ward, dragonfire shield, anti dragon shield, anti-dragon shield, antifire | high-healing food, prayer-restoring supplies, brews, melee boosts, ranged boosts |
| `@muspah` | sapphire dragon bolts, sapphire bolts, crossbow, bow of faerdhinen, crystal armour, crystal armor, masori | high-healing food, prayer-restoring supplies, brews, ranged boosts, magic boosts, stamina/energy |
| `@jad` | toxic blowpipe, dragon dart, rune dart, amethyst dart, rune arrow, dragon arrow, crystal bow | high-healing food, prayer-restoring supplies, brews, ranged boosts |
| `@inferno` | twisted bow, bow of faerdhinen, crystal armour, crystal armor, ancestral, masori, justiciar | high-healing food, prayer-restoring supplies, brews, ranged boosts, magic boosts |
| `@nex` | zaryte crossbow, armadyl crossbow, dragon crossbow, ruby dragon bolts, diamond dragon bolts, torva, masori | high-healing food, prayer-restoring supplies, brews, ranged boosts, melee boosts, stamina/energy |
| `@nightmare boss` | inquisitor, bludgeon, scythe of vitur, sanguinesti staff, harmonised nightmare staff, harmonized nightmare staff, trident | high-healing food, prayer-restoring supplies, brews, melee boosts, stamina/energy |
| `@cerberus` | primordial crystal, pegasian crystal, pegasian crystal, eternal crystal, spectral spirit shield, arclight, emberlight | high-healing food, prayer-restoring supplies, brews, melee boosts |
| `@hydra boss` | dragon hunter crossbow, dragon hunter lance, twisted bow, bow of faerdhinen, toxic blowpipe, ranging potion, antipoison | high-healing food, prayer-restoring supplies, brews, ranged boosts |
| `@scurrius` | bone mace, bone shortbow, bone staff, rat bone, ratbone, rune pouch, scurrius spine | high-healing food, prayer-restoring supplies, melee boosts, ranged boosts |
| `@cox prep` | dragon hunter lance, dragon hunter crossbow, twisted bow, bow of faerdhinen, scythe of vitur, ancestral, masori | high-healing food, prayer-restoring supplies, brews, melee boosts, ranged boosts, magic boosts |
| `@tob prep` | scythe of vitur, sanguinesti staff, toxic blowpipe, bandos godsword, dragon warhammer, dragon claws, ghrazi rapier | high-healing food, prayer-restoring supplies, brews, melee boosts, ranged boosts |
| `@toa prep` | osmumten's fang, keris partisan, tumeken's shadow, bow of faerdhinen, toxic blowpipe, lightbearer, masori | high-healing food, prayer-restoring supplies, brews, melee boosts, ranged boosts, magic boosts |
| `@barrows prep` | barrows teleport, drakan's medallion, morytania legs, spade, strange old lockpick, trident of the seas, trident of the swamp | high-healing food, prayer-restoring supplies, stamina/energy |
| `@gwd prep` | ecumenical key, frozen key, trollheim teleport, stony basalt, icy basalt, rope, bandos | high-healing food, prayer-restoring supplies, brews, stamina/energy, melee boosts, ranged boosts |

## Slayer tasks (12)

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@gargoyles` | rock hammer, rock thrownhammer, granite hammer, granite maul, slayer ring, guthan, abyssal whip | Slayer equipment, high-healing food, prayer-restoring supplies, melee boosts |
| `@dust devils` | facemask, masked earmuffs, slayer helmet, black mask, ancient staff, ancient sceptre, kodai wand | prayer-restoring supplies, magic boosts |
| `@nechryaels` | slayer helmet, black mask, ancient staff, ancient sceptre, kodai wand, blood rune, death rune | prayer-restoring supplies, magic boosts |
| `@aberrant spectres` | nose peg, slayer helmet, black mask, salve graveyard teleport, slayer ring, herb sack, ranging potion | high-healing food, prayer-restoring supplies, ranged boosts |
| `@basilisks` | mirror shield, v's shield, slayer helmet, black mask, slayer ring, rune pouch, basilisk jaw | Slayer equipment, high-healing food, prayer-restoring supplies, melee boosts |
| `@kurasks` | leaf bladed sword, leaf bladed axe, leaf bladed spear, leaf-bladed sword, leaf-bladed axe, broad bolts, broad arrows | Slayer equipment, high-healing food, prayer-restoring supplies |
| `@skeletal wyverns` | elemental shield, mind shield, ancient wyvern shield, dragonfire shield, dragonfire ward, slayer helmet, black mask | high-healing food, prayer-restoring supplies, ranged boosts |
| `@abyssal demons` | arclight, emberlight, abyssal whip, abyssal dagger, slayer helmet, black mask, scythe of vitur | high-healing food, prayer-restoring supplies, melee boosts |
| `@demonic gorillas` | arclight, emberlight, demonbane, toxic blowpipe, bow of faerdhinen, crossbow, rune pouch | high-healing food, prayer-restoring supplies, melee boosts, ranged boosts |
| `@barrage tasks` | ancient staff, ancient sceptre, kodai wand, master wand, blood rune, death rune, water rune | prayer-restoring supplies, magic boosts |
| `@cannon tasks` | cannonball, dwarf multicannon, cannon base, cannon stand, cannon barrels, cannon furnace, slayer helmet | high-healing food, prayer-restoring supplies |
| `@kraken boss` | trident of the seas, trident of the swamp, sanguinesti staff, occult necklace, ancestral, ahrim, tome of fire | high-healing food, prayer-restoring supplies, magic boosts |

## Minigames (16)

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@wintertodt` | tinderbox, bruma torch, pyromancer, clue hunter, warm gloves, firemaking cape, firemaking hood | high-healing food |
| `@tempoross` | harpoon, hammer, rope, bucket, bucket of water, angler, spirit angler | stamina/energy |
| `@gotr` | pickaxe, chisel, essence pouch, colossal pouch, raiments of the eye, abyssal lantern, abyssal needle | stamina/energy |
| `@giants foundry` | bronze bar, iron bar, steel bar, mithril bar, adamantite bar, rune bar, steel platebody | None |
| `@mahogany homes` | plank, oak plank, teak plank, mahogany plank, steel bar, saw, amy's saw | stamina/energy |
| `@hallowed sepulchre` | hallowed grapple, hallowed hammer, hallowed focus, hallowed ring, hallowed symbol, strange old lockpick, graceful | None |
| `@pyramid plunder` | pharaoh's sceptre, lockpick, antipoison, antidote, serpentine helm, stamina potion, energy potion | high-healing food |
| `@tithe farm` | gricoller's can, watering can, seed dibber, spade, farmer's outfit, farmer's strawhat, farming cape | None |
| `@pest control` | void knight, elite void, dragon scimitar, abyssal whip, dragon defender, chinchompa, toxic blowpipe | high-healing food |
| `@barbarian assault` | fighter torso, granite body, penance, barbarian assault, horn of glory, dragon scimitar, abyssal whip | high-healing food, melee boosts, ranged boosts |
| `@soul wars` | soul cape, soul wars, barrage, ancient staff, ancient sceptre, blood rune, death rune | high-healing food, prayer-restoring supplies |
| `@nightmare zone` | dwarven rock cake, locator orb, absorption, overload, berserker ring, black mask, abyssal whip | high-healing food |
| `@shades of mortton` | olive oil, sacred oil, pyre log, pyre logs, shade remains, serum 207, silver key | None |
| `@fishing trawler` | swamp paste, rope, bailing bucket, bucket, angler outfit, angler hat, angler top | None |
| `@volcanic mine` | pickaxe, dragon pickaxe, crystal pickaxe, rune pickaxe, numulite, mining cape, mining hood | high-healing food |
| `@blast furnace` | iron ore, gold ore, coal, mithril ore, adamantite ore, runite ore, coal bag | None |

## Farming and skilling (5)

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@farming contracts` | spade, rake, seed dibber, magic secateurs, ultracompost, bottomless compost bucket, seed box | None |
| `@giant seaweed` | seaweed spore, giant seaweed, diving apparatus, fishbowl helmet, merfolk trident, brine sabre, flippers | None |
| `@motherlode mine` | pickaxe, prospector, coal bag, gem bag, mining gloves, expert mining gloves, celestial ring | None |
| `@bird snaring` | bird snare, hunter cape, hunter hood, stamina potion, spotted cape, spottier cape, ring of endurance | None |
| `@chinchompa hunting` | box trap, chinchompa, red chinchompa, black chinchompa, hunter cape, hunter hood, stamina potion | None |

## Wilderness and PvP (9)

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@revenants` | bracelet of ethereum, revenant ether, webweaver bow, craw's bow, viggora's chainmace, ursine chainmace, thammaron's sceptre | Wilderness travel/utility, high-healing food, prayer-restoring supplies |
| `@calvarion` | ursine chainmace, viggora's chainmace, viggoras chainmace, salve amulet, dragon mace, inquisitor's mace, blighted | Wilderness travel/utility, high-healing food, prayer-restoring supplies, melee boosts |
| `@spindel` | ursine chainmace, viggora's chainmace, dragon mace, inquisitor's mace, webweaver bow, blighted, antipoison | Wilderness travel/utility, high-healing food, prayer-restoring supplies, melee boosts |
| `@artio` | webweaver bow, craw's bow, bow of faerdhinen, crossbow, entangle, ice barrage, blood rune | Wilderness travel/utility, high-healing food, prayer-restoring supplies, ranged boosts |
| `@chaos elemental` | webweaver bow, craw's bow, bow of faerdhinen, black d'hide, looting bag, blighted, royal seed pod | Wilderness travel/utility, high-healing food, prayer-restoring supplies, ranged boosts |
| `@chaos altar` | dragon bones, superior dragon bones, wyvern bones, hydra bones, dagannoth bones, burning amulet, wilderness sword | Wilderness travel/utility, high-healing food |
| `@wildy bosses` | webweaver bow, craw's bow, ursine chainmace, viggora's chainmace, accursed sceptre, thammaron's sceptre, salve amulet | Wilderness travel/utility, high-healing food, prayer-restoring supplies |
| `@wildy escape` | royal seed pod, amulet of glory, ring of wealth, ring of dueling, burning amulet, wilderness sword, entangle | Wilderness travel/utility, high-healing food, prayer-restoring supplies |
| `@anti pk` | dragon claws, voidwaker, abyssal dagger, dragon dagger, granite maul, ags, armadyl godsword | Wilderness travel/utility, high-healing food, prayer-restoring supplies |

## Clues and quests (10)

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@clue kit` | spade, clue scroll, strange device, light box, puzzle box, challenge scroll, rope | None |
| `@easy clue kit` | clue scroll (easy), reward casket (easy), spade, teleport to house, stamina potion, games necklace, amulet of glory | None |
| `@medium clue kit` | clue scroll (medium), reward casket (medium), spade, teleport to house, stamina potion, games necklace, skills necklace | None |
| `@hard clue kit` | clue scroll (hard), reward casket (hard), spade, stamina potion, royal seed pod, burning amulet, amulet of glory | high-healing food, prayer-restoring supplies |
| `@elite clue kit` | clue scroll (elite), reward casket (elite), spade, stamina potion, rune pouch, strange device, royal seed pod | high-healing food, prayer-restoring supplies |
| `@master clue kit` | clue scroll (master), reward casket (master), spade, stamina potion, rune pouch, strange device, light box | high-healing food, prayer-restoring supplies |
| `@quest prep` | rope, spade, knife, chisel, tinderbox, hammer, bucket of water | high-healing food, prayer-restoring supplies |
| `@quest travel` | teleport to house, varrock teleport, falador teleport, ardougne teleport, camelot teleport, lumbridge teleport, ectophial | stamina/energy |
| `@quest combat` | rune pouch, anti dragon shield, dragonfire shield, antifire, antipoison, antivenom, sanfew serum | high-healing food, brews |
| `@fairy rings` | dramen staff, lunar staff, quest point cape, fairy enchantment, ardougne cloak, explorer's ring, teleport to house | None |

## Release safeguards

- This v1.5 branch preserves all 141 existing categories and adds 67, for 208 total.
- New `herbrun` and `herbruns` aliases point to the original `herb run` matcher, not a rewritten one.
- Existing raids/God Wars/Barrows searches remain available. New `... prep` aliases add broader loadout candidates without overriding old aliases.
- All categories are local-only filters; no withdrawals, equipment changes, external calls, or account-state assumptions.
- GitHub CI is a required build gate, but passing it does not substitute for in-game verification.

**After review:** Merge the source PR, then open a separate Plugin Hub manifest-update PR pinned to the tested release commit.
