# Smart Bank Search v1.5: one-shot optimization audit

> **Draft only.** Source: [PR #2](https://github.com/steeltype-ux/smart-bank-search/pull/2). No Plugin Hub release has been requested. This audit covers all **51** additional activity kits beyond the **16 minigame kits** optimized previously.

## Design principles

1. A kit searches **items available in the bank** for a typical or alternative setup. It is **not** a dynamic equipment recommendation, a packing list, an inventory withdrawer, or a promise that all selected items belong in one trip.
2. Explicitly include critical protection items, useful weapons or tools, runes, and teleports. Keep optional playstyles visible but avoid generic substring terms that select unrelated bank objects.
3. Differentiate **boss drops**, **one-time rewards**, **unbankable instance items**, and **spell names** from items a player would bring.
4. Existing long activity aliases, short aliases, and normal/exclusive search behaviour must remain intact. Validating against an actual bank and RuneLite item IDs is still required before publication.

## Bosses and raids (15)

| Activity | Practical preparation changes |
|---|---|
| `@zulrah` | Add serpentine helm, Ava's device, trident alternatives and Magic/Ranged boosts; retain anti-venom and recoil/suffering protection |
| `@vorkath` | Add Salve amulet variants, bolts, actual Crumble Undead spell **runes** and Fremennik/POH travel; avoid matching the unbankable spell name itself |
| `@muspah` | Sapphire enchanted bolts, Forgotten brew, Greater Corruption rune alternatives, relevant ranged/Magic switches and Weiss teleport |
| `@jad` | Add Ava alternatives, crossbows, heal/sustain tools and additional ammo alongside Blowpipe and prayer restores |
| `@inferno` | Add actual dart variants, Mage/Ancients switches, Virtus pieces and optional stamina; prior important ranged/prayer gear retained |
| `@nex` | Add Zaryte vambraces and ranged upgrades; retain Frozen Key / ancient ceremonial, entry keys, crossbows and protection |
| `@nightmare boss` | Specific crush options (Inquisitor's mace, bludgeon, Soulreaper axe); remove generic `mace` / `crush weapon` labels that match too much |
| `@cerb` | Surface usable Spectral shield, demonbane weapons, gear and prayer support; deliberately remove **Primordial, Pegasian and Eternal crystals**, which are loot/upgrade components |
| `@hydra` | Add **boots of stone/brimstone**, a safety-critical Mount Karuulm requirement; remove broad `konar` and `kourend` keywords |
| `@scurrius` | Add prayer and combat alternatives to bone weapons; remove rat-bone and Scurrius-spine **drops** |
| `@cox prep` | Improve mixed-style raids equipment, useful skilling tools, spec weapons and rune-pouch alternatives |
| `@tob prep` | Add special-attack, swap and rune gear; remove `Dawnbringer` (acquired within the raid) |
| `@toa prep` | Add elidinis' wards, rune pouches and combat switches; remove `Thread of elidinis` reward component |
| `@barrows prep` | Add Ghommal's hilts, better runes, mage/ranged options and restoration/teleport choices |
| `@gwd prep` | Add Bones to Peaches, useful switches, spec options and sustain gear; four factions remain combined in one broad GWD kit |

## Slayer and combat trips (12)

| Activity | Practical preparation changes |
|---|---|
| `@gargs` | Add herb/gem collection, High Alchemy runes, defensive weapons and bracelets; keep rock hammer |
| `@dustdevils` | Add mage-damage gear, smoke/fire runes and ashes handling; remove nonexistent `ice rune` and irrelevant bonecrusher |
| `@nechs` | Add Magic boosts, herb/seed collection, bracelets and ashes handling; remove nonexistent `ice rune` |
| `@spectres` | Add herb/seed collection, magic-defence clothing and task bracelets; keep mandatory nose peg or Slayer helmet |
| `@basilisks` | Add Ranged choices and Prayer supplies; remove `Basilisk jaw` loot |
| `@kurasks` | Explicitly cover real **Leaf-bladed battleaxe**, other leaf-bladed weapons, Slayer's staff and broad ammo; add herb collection |
| `@skeletal wyverns` | Shields that protect from icy breath, suitable dragonbane/ranged gear and potions; remove unrelated anti-venom |
| `@abbys` | Demonbane and mage/ranged alternatives, bracelets and ash collection |
| `@demonics` | Melee/Ranged switches, demonbane gear, prayer, antipoison and royal seed pod; no multicannon (gorillas are immune) |
| `@barrage` / `@burst` | Magic sets, runes, Ancient sceptres and looting utility; remove nonexistent `ice rune` |
| `@cannon` | Preserve cannon components, add granite cannonballs and offensive boosts; legacy narrow `@cannon supplies` still supported |
| `@kraken` | Add relevant Trident alternatives, Magic boosts, shields and Slayer task bracelets |

## Farming, Hunter and mining (5)

| Activity | Practical preparation changes |
|---|---|
| `@contracts` | Replace catch-all `seed` with common contract-specific seeds/crops and saplings; include farming tools |
| `@giant seaweed` | Surface *spores*, diving gear, compost, spade and Digsite pendant; remove harvested Giant seaweed and unrelated drift net |
| `@mlm` | Add Varrock armour 4, celestial jewellery and mining alternatives; remove soft-clay packs from preparation |
| `@bird snaring` | Add Hunter boosts, appropriate camo/clothing and transport alternatives |
| `@chinchompa` | Add **box traps** plus black-chin Wilderness defence/escape gear, spare traps and optional tick-manipulation tools; `@chins` still means thrown weapons |

## Wilderness and PvP (9)

| Activity | Practical preparation changes |
|---|---|
| `@revs` | Bracelet of ethereum, ether, Revenant teleports, Salve variants, weapon choices and emergency escape items |
| `@calv` | Crush weapons, Salve amulet variants, low-risk boosts and Wilderness travel/escape |
| `@spindel` | Crush/Ranged choices, serpentine/anti-venom protection, mobility and escape |
| `@artio` | Range/Magic switches, **actual entangle/freezing runes or blighted sacks**, rather than matching spell names as bank items |
| `@chaos elemental` | Ranged setups, healing and optional full-inventory filler/escape items |
| `@chaos altar` | Additional bankable bones and ashes, prayer-running travel and low-risk escape |
| `@wildy bosses` | General Wilderness weapons, salve alternatives, runes, healing and escaping |
| `@wildy escape` | Teleports, anti-freeze/poison, tank gear and binding **runes** rather than spell-name text |
| `@anti pk` | Special attack weapons, Magic gear, consumables and Wilderness travel without ambiguous `ags`/spell-name matches |

## Clues and quests (10)

| Activity | Practical preparation changes |
|---|---|
| `@cluekit` | Add clue box, Scroll sack, visibility/ghostspeak items, Wildy protection and broad teleports |
| `@easyclue` | Add low-risk travel items for easy clues; does not turn on Wilderness/combat consumables |
| `@mediumclue` | Add real teleport jewellery, light/puzzle boxes and useful scroll tools; remove scenery-only `fairy ring` |
| `@hardclue` | Add clue box, Wildy safety and rune/travel options alongside combat/food candidates |
| `@eliteclue` | Add special-step and puzzle equipment, Wilderness protection and useful travel |
| `@masterclue` | Add ghost/visibility equipment, clue box, puzzle tools, ring teleports and Quest/Diary cape |
| `@questprep` | Add common quest utility materials, runes, lanterns, antipoison and fairy-ring staves |
| `@questtravel` | Expand actual consumable/wearable teleports and Diary items; remove scenery-only `fairy ring` |
| `@questcombat` | Add gear choices, defensive shields, spell runes, emergency jewellery and consumables |
| `@fairyrings` | Add real **Dramen/Lunar staff** and nearby teleport items (Slayer ring, POH, Quest cape); account Diary unlocks can eliminate the staff requirement |

## Independent references

- [OSRS Wiki: Slayer equipment](https://oldschool.runescape.wiki/w/Slayer_equipment)
- [OSRS Wiki: Alchemical Hydra](https://oldschool.runescape.wiki/w/Alchemical_Hydra)
- [OSRS Wiki: Demonic gorillas](https://oldschool.runescape.wiki/w/Demonic_gorilla)
- [OSRS Wiki: Kurasks](https://oldschool.runescape.wiki/w/Kurask)
- [OSRS Wiki: Farming contracts](https://oldschool.runescape.wiki/w/Farming_contract)
- [OSRS Wiki: Black chinchompas](https://oldschool.runescape.wiki/w/Hunting_black_chinchompa)
- [OSRS Wiki: Revenant Caves](https://oldschool.runescape.wiki/w/Revenant_Caves)
- [OSRS Wiki: Clue scroll](https://oldschool.runescape.wiki/w/Clue_scroll)
- [OSRS Wiki: Fairy rings](https://oldschool.runescape.wiki/w/Fairy_rings)
- [OSRS Wiki: Nightmare Zone](https://oldschool.runescape.wiki/w/Nightmare_Zone)

## Required validation before v1.5 ships

1. Automated GitHub build and JUnit/category regression suite must pass at the **final** head commit.
2. In RuneLite, test **exclusive** `@category` and **ordinary** `category` search behaviour. Validate basic item names, variant names and charged versions, including multiple bank tabs.
3. Confirm relevant items in a real player's bank for **all 67 activity kits**, plus existing category searches. The tests here are string fixtures, **not** a substitute for item metadata tests or client interaction.
4. Deliberately test at least two false positives per activity, especially generic magic runes, teleports, potions and Wilderness gear. A single broad activity kit includes alternatives and never infers the user's equipment/quest/skill levels.
5. Confirm the GitHub PR remains a **draft** until the maintainer reviews and explicitly chooses to merge/publish. No changes to the Plugin Hub manifest yet.
