# Smart Bank Search v1.5 — Activity Review Guide

**Draft:** [Source PR #2](https://github.com/steeltype-ux/smart-bank-search/pull/2). **Do not merge until gameplay review.** The current official RuneLite Plugin Hub version remains unchanged.

**Completed first-pass optimization of all 67 activities:** the 16 minigames are reviewed in detail below; all 51 other categories are covered in the [one-shot activity audit](ONE_SHOT_ACTIVITY_AUDIT.md). This is not a claim of gameplay validation.

All 67 new searches return **candidate items already in the player's bank**. A search is not an authoritative checklist or optimal loadout. The lists below show the leading **curated matching name terms** (examples, not exact item IDs), plus shared supplies used by a kit. The implementation may match more item names than shown.

### Easier search names

You can type these **without** `@` for additive native bank search results, or with `@` for category-only results. The original longer activity names still work.

| Short search | What it prepares | Full activity |
|---|---|---|
| `cannon` | Multicannon, ammunition, Slayer task equipment, food and prayer supplies | `cannon tasks` |
| `chinchompa` | Box traps, chinchompas and Hunter preparation | `chinchompa hunting` |
| `birdhouse` | Birdhouse-run tools and supplies | `birdhouse run` |
| `treerun` | Tree-farming run items | `tree run` |
| `cerb`, `hydra` | Boss preparation | `cerberus`, `hydra boss` |
| `gargs`, `nech`, `basilisk`, `kurask`, `abbys`, `gorillas` | Slayer tasks | Their full monster names |
| `burst` | Bursting/barraging Slayer supplies | `barrage tasks` |
| `wt`, `temp` | Wintertodt, Tempoross | `wintertodt`, `tempoross` |
| `mh`, `plunder`, `tithe`, `pest`, `ba` | Other minigame kits | Full minigame names |
| `shades`, `vm`, `bf` | Shades of Mort'ton, Volcanic Mine, Blast Furnace | Full minigame names |
| `contracts`, `revs`, `calv` | Farming contracts, Revenants, Calvar'ion | Full activity names |

**Legacy compatibility:** `cannon` is intentionally now the **Cannon Slayer trip kit**, but `cannon supplies`, `cannonballs`, `cannons`, and `multicannon` still return the original narrow cannon-items category. Similarly, `chinchompa` is now the hunting kit while `chins` and `chinchompas` remain in the original thrown-weapon category. Ambiguous established shortcuts like `clues`, `slayer`, `hunter`, and `barrows` are **not** repurposed.

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
| `@gargoyles` | rock hammer, rock thrownhammer, granite hammer, granite maul, dual macuahuitl, slayer ring, guthan, abyssal whip | Slayer equipment, high-healing food, prayer-restoring supplies, melee boosts |
| `@dust devils` | facemask, masked earmuffs, slayer helmet, black mask, ancient staff, ancient sceptre, kodai wand | prayer-restoring supplies, magic boosts |
| `@nechryaels` | slayer helmet, black mask, ancient staff, ancient sceptre, kodai wand, blood rune, death rune | prayer-restoring supplies, magic boosts |
| `@aberrant spectres` | nose peg, slayer helmet, black mask, salve graveyard teleport, slayer ring, herb sack, ranging potion | high-healing food, prayer-restoring supplies, ranged boosts |
| `@basilisks` | mirror shield, v's shield, slayer helmet, black mask, slayer ring, rune pouch, basilisk jaw | Slayer equipment, high-healing food, prayer-restoring supplies, melee boosts |
| `@kurasks` | leaf bladed sword, leaf bladed axe, leaf bladed spear, leaf-bladed sword, leaf-bladed axe, broad bolts, broad arrows | Slayer equipment, high-healing food, prayer-restoring supplies |
| `@skeletal wyverns` | elemental shield, mind shield, ancient wyvern shield, dragonfire shield, dragonfire ward, slayer helmet, black mask | high-healing food, prayer-restoring supplies, ranged boosts |
| `@abyssal demons` | arclight, emberlight, abyssal whip, abyssal dagger, slayer helmet, black mask, scythe of vitur | high-healing food, prayer-restoring supplies, melee boosts |
| `@demonic gorillas` | arclight, emberlight, demonbane, toxic blowpipe, bow of faerdhinen, crossbow, rune pouch | high-healing food, prayer-restoring supplies, melee boosts, ranged boosts |
| `@barrage tasks` / `@barrage` | ancient staff, ancient sceptre, kodai wand, master wand, blood rune, death rune, water rune | prayer-restoring supplies, magic boosts |
| `@cannon tasks` | cannonball, dwarf multicannon, cannon base, cannon stand, cannon barrels, cannon furnace, slayer helmet | high-healing food, prayer-restoring supplies |
| `@kraken boss` | trident of the seas, trident of the swamp, sanguinesti staff, occult necklace, ancestral, ahrim, tome of fire | high-healing food, prayer-restoring supplies, magic boosts |

## Minigames (16)

**Shades of Mort'ton — four interconnected activities in one bank search:**
- **Temple repair / Sacred oil:** Bring a **Flamtaer hammer** (far better for solo repair), **Flamtaer bracelet**, **Flamtaer bag**, swamp paste, timber beams and limestone bricks. An Imcando/ordinary hammer is an alternative. **Serum 207 / 208**, olive oil and sacred oil are useful when sanctifying oil or curing locals. Teleport scrolls and Morytania legs help with access.
- **Pyre cremation:** Match **all six named shade remains** (Loar, Phrin, Riyl, Asyn, Fiyr, Urium) and all relevant **pyre-log tiers**, together with logs, tinderbox and suitable axes for self-processing. Pick log tier based on the shade type; low-tier logs do not cremate high-tier remains.
- **Catacombs:** The actual keys are named by **metal and colour**, e.g., `Silver key purple`, `Gold key brown`. Restrict names to valid five metal / five colour combinations instead of all `silver key` or `gold key` items (which would match keys from unrelated quests). Existing appropriately tiered keys let players pass barriers without re-earning them.
- **Scope:** This search is a superset of temple-repair supplies, remains/pyre materials and keys. Most players doing an individual stage need only some of these, plus optional food.

Sources: [Shades of Mort'ton minigame](https://oldschool.runescape.wiki/w/Mort%27ton_shade), [Shades of Mort'ton quest](https://oldschool.runescape.wiki/w/Shades_of_morton_quest).

**Fishing Trawler — optimised for actual contribution:**
- **Mass-world Angler outfit farming:** Use **swamp paste** to patch ten leaks, reaching the **50 contribution points** required for the chance at an outfit piece. Once at 50 points, participation can stop; more contribution improves fish/heron odds, not outfit odds. A player can also repair nets with **rope**, bail with a **bailing bucket**, hammer railings or cut Kraken tentacles with an **axe**.
- **Bring only what is relevant:** Swamp paste, rope, **bailing bucket** (distinct from ordinary bucket), hammer/Imcando hammer, and explicit hatchet tiers. Basic gear can be obtained on the ship or at the nearby store, so it is convenient, not a bank-prep requirement.
- **Do not match:** Angler hat/top/waders/boots (these are rewards, not necessary equipment), fishing capes, generic fishing rods, fish barrel, ordinary bucket, stamina/food.

Sources: [Fishing Trawler strategies](https://osrsindex.com/wiki/fishing-trawler-strategies?site=osrs_wiki), [Fishing Trawler mechanics](https://danielpgleason.com/osrs/reference/fishing-trawler-fd824660/).

**Volcanic Mine — food and sustain are part of the setup:**
- **Core:** Best available pickaxe, **numulite** for entry where payment is needed, **Prospector** outfit for Mining XP, **celestial ring / signet**, optional **Varrock armour 4 as a Prospector jacket substitute** (its ordinary double-ore effect does NOT apply here), and Volcanic Mine teleports / Digsite pendant.
- **Survival:** Healing food, prayer potions or super restores, Regen bracelet and Hitpoints cape; useful Prayer bonus from god blessings, dragonbone/bonecrusher necklace, Proselyte, Ardougne cloak, Falador shield, or ring of the gods. Desert amulet or POH teleport restores between rounds.
- **Options:** Stamina potion / ring of endurance / Graceful are more important for solo capping routes than typical teams; no generic energy potions. **Volcanic ore is not a normal bank resource** and should not be returned.

Sources: [Volcanic Mine guide / inventory](https://oldschoolrunescape.fandom.com/wiki/Volcanic_Mine), [Pay-to-play Mining (Varrock armour exclusion)](https://osrsindex.com/wiki/pay-to-play-mining-training?site=osrs_wiki).

**Blast Furnace — distinguish gold training from coal-based bars:**
- **Gold XP method:** **Gold ore + goldsmith gauntlets**, switching to **ice gloves** to remove heated bars. **Smiths gloves (i)** combine Smiths' gloves and ice gloves; a **bucket of water** works when neither is available.
- **Coal-based bars / profit:** Select appropriate copper + tin, iron, silver, gold, mithril, adamantite or runite ore and **coal** as required. **Coal bag** is highly recommended for steel/mithril/adamant/runite production, but not required for gold or iron.
- **Other useful equipment:** **Coins** to pay the operating fee, stamina potions, graceful/other lightweight clothing, and optionally ring of endurance, explorer's ring or **ring of charos (a)** for fee reduction where applicable. Avoid standard Smiths' tunic/trousers/boots: the uniform speeds *anvil actions*, not Blast Furnace ore smelting. Smithing catalysts can work with coal-based metals but are deliberately omitted: nonstackable and rarely worthwhile.
- **Precision:** Match only the real input ores/coal, not bar outputs or arbitrary ore-named gear.

Sources: [Blast Furnace strategies](https://oldschool.runescape.wiki/w/Blast_Furnace%2FStrategies), [Smiths' uniform](https://oldschool.runescape.wiki/w/Smithing_armor).

**Pest Control — combat gear over consumables:**
- The game refreshes stats and prayer between rounds. It rewards fast portal damage (and dealing with Spinners) more than stocking food and prayer potions. Gear options include **Void/Elite Void**, melee DPS weapons (whip, scimitar, defender, rapier, fang), ranged weapons (blowpipe, bowfa, crystal bow), and special weapons (BGS, claws, dagger, Voidwaker). Optional **chinchompas or Ancient Magicks** help with clustered pests but cost resources; include rune pouch, blood/death/water runes for that specialized setup.
- **Optional:** ranging or super combat potion for short-round damage boosts, and **Pest control teleport** for access. Exclude generic food, brews, prayer/restore potions and unrelated run energy supplies.

**Barbarian Assault — role-specific equipment, not external supplies:**
- **Attacker:** Bring melee or ranged DPS weapons (dragon scimitar, whip, MSB, crystal bow, bowfa, Venator bow, blowpipe), damage gear and optional attack/strength/ranging potions. Attackers draw the correct arrows/runes from **attacker dispensers** and must respond to attack-style calls.
- **Defender:** **Imcando hammer** avoids picking up the spawned hammer to repair traps; defensive gear (Dinh's bulwark, Dragonfire shield, Zamorakian hasta, Excalibur) helps while running bait.
- **Healer/Collector:** Prioritize **high defence and free inventory slots**. The machines provide poisoned food/vials and egg bags; do not load bank food or role items before entering. Penance armour is fine aesthetically but does not provide a minigame advantage.
- **Restrictions:** **Prayers are disabled**. External runes/ammunition and rune pouches are **not permitted**. Remove prayer potions, rune pouch and general food from the filter. **Games necklace** teleports to Barbarian Outpost. Do not show `Horn of glory` as a bank-preparation item.

Sources: [OSRS Wiki: Barbarian Assault](https://oldschool.runescape.wiki/w/Barbarian_Assault), [OSRS Wiki: strategy guide](https://oldschool.runescape.wiki/w/Barb_assault_strats).

**Soul Wars — bring switches and runes, collect supplies on-site:**
- **Magic-defence gear:** Black/blessed dragonhide, Karil's and similar equipment, to resist frequent Ice Barrage attacks. **Attackers / PvP:** Ancient staff, Ancient sceptre or Kodai wand with **blood, death, water and soul runes** and a rune pouch; spec weapons such as claws, DDS, godswords, Voidwaker and ranged options such as blowpipe, bowfa or crossbows.
- **Supply stations:** **Bandages, combat potions, barricades and explosives** are collected *after entering* and expire when the game ends. The plugin's bank search should **not** suggest food, prayer/restore potions, stat potions or these on-site items. Players should enter with inventory room for bandages.
- **Teleport:** Amulet of glory or combat bracelet can get players toward the Edgeville entrance if they are not using the minigame teleport.

Source: [OSRS Soul Wars practical strategy](https://www.osrsguide.com/soul-wars-guide-osrs/), [OSRS Wiki: Explosive potion (Soul Wars)](https://oldschool.runescape.wiki/w/Explosive_potion_%28Soul_Wars%29).

**Nightmare Zone — bank gear vs point-purchased doses:**
- **Absorption / Overload** cannot be stored in a normal bank and are withdrawn from Dominic's barrels outside the dream. Players should still prepare those *in the NMZ lobby* after retrieving relevant bank gear.
- **Dharok training:** Full Dharok's (helm, platebody, platelegs, greataxe), **dwarven rock cake or locator orb** to reach low HP, and point-bought absorption/overload doses from barrels.
- **AFK melee:** Obsidian helmet/platebody/platelegs, **Berserker necklace**, **Toktz-xil-ak**, or an alternate whip/defender and strength-focused outfit. **Prayer setup:** Proselyte/Initiate equipment, prayer potion, holy wrench and special weapons (Saradomin godsword or Ancient mace); ordinary super combat or ranging potions are especially relevant to early points-building.
- **Precision:** No high-healing food, black mask for generic dreams, or unbankable absorption/overload doses. The player needs **coins in Dominic's coffer** to start games.

Sources: [OSRS Wiki: Nightmare Zone](https://oldschool.runescape.wiki/w/Nmz), [OSRS Wiki: pay-to-play melee training](https://oldschool.runescape.wiki/w/Pay_to_pay_melee), [OSRS Wiki: Nightmare Zone strategies](https://oldschoolrunescape.fandom.com/wiki/Nightmare_Zone/Strategies).

**Tempoross kit — reviewed and optimized:** Start with a harpoon and a rope, hammer and buckets as needed (basic supplies are available on-site). The **full four-piece Spirit angler outfit** acts as a rope, freeing an inventory space; the regular Angler outfit also grants Fishing XP but does **not** replace the rope. An **Imcando hammer** is equippable for repair work. Harpoon upgrades can improve fishing performance, while Lightbearer, Elven signet, Humidify runes/tomes and fishing boosts are for specialized approaches. Stamina/energy potions and a Ring of endurance are intentionally excluded from routine prep. **Fish barrel cannot store harpoonfish**, and tackle box is storage rather than in-encounter equipment. This is a single candidate-supplies filter, not an optimal or account-specific loadout.

Sources: [OSRS Wiki strategy overview](https://oldschool.runescape.wiki/w/Tempoross/Strategies), [Spirit angler top](https://oldschool.runescape.wiki/w/Spirit_angler_top), [Tempoross Cove](https://oldschool.runescape.wiki/w/Tempoross_Cove).

**Guardians of the Rift — researched preparation kit:**
- **Normal runs:** Bring a pickaxe (rune/dragon/crystal commonly used) and the essence pouches permitted by your Runecraft level: *small, medium, large, giant, colossal*. The colossal pouch replaces the four smaller pouches when crafted; degraded versions are included for repair decisions. A **chisel** is used to create guardian helpers but is optional in busy masses. The **Raiments of the Eye** is four separately named items: *Hat of the eye, Robe top of the eye, Robe bottoms of the eye, Boots of the eye*. Each is covered, including recolours.
- **Useful mining gear:** The **Varrock armour** and charged **celestial ring or signet** can give extra guardian fragments/essence. A mining cape, prospector/graceful pieces, penance gloves or spottier cape are alternatives. **Ghommal's lucky penny** supports celestial mining jewellery; Lightbearer can help if relying on pickaxe special attacks.
- **Pouch upkeep / lantern:** The **Abyssal lantern** grants different bonuses depending on fuel: yew for extra points, redwood for protecting pouches from degradation and a blend of other bonuses, or magic for more runes. A **Runecraft/max cape** prevents degradation. Alternatively, repair between games with **Astral Contact** (formerly NPC Contact; renamed August 2026), or use Apprentice Cordelia's pearl service once unlocked. Lantern logs are selected *before* running games and are not normal carried inventory.
- **Combination runes — optional advanced setup:** **Binding necklace** (also carry spares), **rune pouch/divine rune pouch**, and a selection of **air, water, earth, fire, steam, smoke, astral and cosmic runes**. Use the Lunar spellbook's **Magic Imbue** (82 Magic) to avoid needing opposing talismans; **Astral Contact** (67 Magic) repairs pouches via the Dark mage. Tomes of fire/water and the appropriate staff are alternate rune-saving options. These are optional matches in `@gotr`, **not** essentials for relaxed runs.
- **Deliberate exclusions:** Routine games do not require extra **elemental/catalytic talismans** to use portal guardians, an **abyssal needle** after pouch construction, banked pure/daeyalt essence or generic stamina/energy potions. The **ring of the elements** teleports to rune altars, not the minigame; the **amulet of the eye** and **necklace of passage** are useful entry teleports.
- **Practical note:** A single `@gotr` bank search shows candidates from both normal and combination-rune setups; players choose based on their Runecraft level and strategy. It cannot prioritize or hide optional equipment dynamically.

Sources: [OSRS Wiki — GotR strategies (mirror)](https://osrsindex.com/wiki/guardians-of-the-rift-strategies?site=osrs_wiki), [OSRS Wiki — GotR](https://osrsindex.com/wiki/guardians-of-the-rift?site=osrs_wiki), [OSRS Wiki — essence pouch (mirror)](https://osrsindex.com/wiki/essence-pouch?site=osrs_wiki), [Jagex — August 2026 update](https://steamcommunity.com/app/1343370/announcements/).

**Mahogany Homes — contracts and route optimization:**
- **Real supplies:** Regular, oak, teak, or mahogany planks **according to contract level**, a few **steel bars**, and a saw + hammer. **Amy's saw** or an off-hand **Imcando hammer** can free inventory space. **Plank sack** carries up to 28 planks. Individual **Carpenter's helmet, shirt, trousers and boots** give bonus Construction XP.
- **Four cities:** Varrock, East Ardougne, Falador and Hosidius. Include Varrock/Ardougne/Falador teleports, Xeric's talisman, ring of wealth, skills necklace, POH teleport, Ardougne cloak, Kharedst's memoirs/book of the dead and achievement diary cape.
- **Efficient optional setup:** Rune pouch with **air + astral + cosmic** for **Astral Contact** (previously NPC Contact), saving trips back to Amy; graceful is optional since tea restores run energy after contracts. Divine rune pouch, law/dust/steam runes, air/dust/steam battlestaves and **Plank Make** supplies (nature runes, log basket, sawmill vouchers) are advanced alternatives. **No nails**, unlike Sepulchre.
- **Precision:** No generic stamina/energy category, noncontract bars, logs or arbitrary plank-named objects.

Sources: [OSRS Wiki — Mahogany Homes](https://oldschool.runescape.wiki/w/Mahogany_Homes), [OSRS Index — current recommended setups](https://osrsindex.com/wiki/mahogany-homes?site=osrs_wiki).

**Hallowed Sepulchre — separate Agility XP runs from coffin-looting runs:**
- **Fast laps:** Graceful/weight reduction and a **Saradomin item** (for obelisk run-energy restore), optionally **Hallowed ring** to reduce trap costs; food/poison protection while learning and Drakan's medallion for travel. Ring of endurance or stamina potions are optional, not essential with obelisks.
- **Full-loot additions:** **Strange old lockpick** (or ordinary lockpick), **Mith grapple** + light **Dorgeshuun crossbow** if Hallowed grapple not owned, **vampyre dust** for braziers, and construction supplies (**planks + matching nails + saw + hammer**). **Hallowed hammer** prevents broken nails; Hallowed focus, grapple and symbol are upgrades.
- **Magic/defense:** Rune pouch, cosmic/fire/nature runes for enchanting/alching; **holy symbol or holy blessing** for Saradomin affiliation, prayer book + symbol for curing poison, or antipoison/serpentine helm. Hallowed symbol also counts as Saradomin gear. Food remains optional for learners.
- **Precision:** Do not include arbitrary logs, planked furniture or generic melee equipment.

Sources: [Sepulchre strategies](https://osrsindex.com/wiki/hallowed-sepulchre-strategies?site=osrs_wiki), [Sepulchre mechanics](https://osrsindex.com/wiki/hallowed-sepulchre?site=osrs_wiki).

**Pyramid Plunder — survivability and access:**
- **Core:** **Pharaoh's sceptre** to teleport directly to the minigame, anti-poison/antidote or **charged serpentine helm**, food, prayer potions for Protect from Melee, and weight-reducing outfit.
- **Choice-based:** **Snake charm** is useful at low Thieving (under ~51); **lockpick** can open doors more easily but gives less door XP and is often omitted by experienced players. **Prayer book + holy symbol** cures poison as an alternative; **regen bracelet + Hitpoints cape**, desert amulet, POH restoration, ring of dueling and stamina potion are convenience options.
- **Precision:** Generic energy potions and irrelevant snake drops excluded. This does **not** automatically select a single optimum depending on Thieving level.

Source: [Pyramid Plunder — recommended setup](https://osrsindex.com/wiki/pyramid-plunder?site=osrs_wiki).

**Tithe Farm — correct tools and Humidify option:**
- **Core:** **Spade**, **seed dibber** unless bare-handed planting is unlocked, and around **6–8 filled watering cans**, or **Gricoller's can** once purchased. **Gricoller's fertiliser and Golovanova/Bologano/Logavano seeds are supplied inside**, not bank-preparation items. Players select seeds according to Farming level (34/54/74).
- **Bonus and advanced setup:** Every **Farmer's outfit** item is explicitly matched: strawhat, jacket/shirt (body-type variants), boro trousers and boots. Weight-reducing graceful is optional; Humidify with **astral, fire and water/steam runes** (and optionally a rune pouch) can refill cans. Stamina or super energy and Explorer's ring can help high-intensity runs, with suitable Hosidius travel teleports if available.
- **Precision:** Regular Farming seeds/compost, seed box, herb sack, Farming cape and non-Tithe gear are excluded.

Sources: [Tithe Farm/Strategies](https://oldschool.runescape.wiki/w/Tithe_farm_guide), [Tithe Farm overview](https://oldschool.runescape.wiki/w/Tithe_Farm), [Farmer outfit variants](https://oldschool.runescape.wiki/w/Auto_weeder).

**Giant's Foundry kit — optimized based on OSRS Wiki strategy:**
- **Materials:** The crucible takes 28 bars' worth of bronze, iron, steel, mithril, adamantite or runite metal, either as bars or eligible smithable equipment.
- **Recycling:** Platebodies count for **4 units**; platelegs, plateskirts, chainbodies, kiteshields, battleaxes, warhammers and 2h swords count for **2**; full helms, square shields, scimitars, longswords and claws count for **1**. These metal items count as one fewer bar than they originally cost to smith. Decorated, ornamental and unprocessable equipment is intentionally excluded.
- **Recommended alloys:** Iron/steel for early access, steel/mithril from 50 Smithing, mithril/adamant from 70, and adamant/rune from 85 when affordable. **14/14** is a simple balanced start; a lower-tier-heavy mix such as **19/9** may improve cost or reduce refinement stages for certain tiers. Ratios are strategic choices, not automatic bank filtering.
- **What to prioritize:** Use bars directly if already owned. Do **not** smith bars into items just to recycle them. If buying materials, compare the price of eligible secondhand equipment (notably platebodies and warhammers) to purchasing bars.
- **Useful equipment:** Smiths' tunic/trousers/gloves/boots improve refining progress; ice gloves or smiths gloves (i) conveniently handle hot preforms. A **bucket of water**, supplied at the Foundry, is the alternative. **Ring of dueling** and **amulet of glory** are optional travel conveniences. Ordinary hammers/Imcando hammer are not needed for the Foundry stations.

Sources: [OSRS Wiki: Giant's Foundry](https://oldschool.runescape.wiki/w/Giants%27_Foundry), [Strategies](https://oldschool.runescape.wiki/w/Giants_foundry/strategies), [Smiths' uniform](https://oldschool.runescape.wiki/w/Smiths%27_Uniform).

**Wintertodt warmth mechanics:** A food bite that normally heals at least 4 HP restores 35% warmth. Cake has three bites (up to 105% total theoretical warmth restoration per inventory slot); suitable pizzas and pies offer two (up to 70%). Regen bracelet enhances warmth regeneration and is already part of this kit. Food items like cake tins, pizza bases, uncooked pizzas and burnt pies are intentionally excluded. The filter matches curated names, not actual HP/heal values, and warmth caps at 100%.

| Search | Curated examples | Shared candidate supplies |
|---|---|---|
| `@wintertodt` | tinderbox, bruma torch, pyromancer, warm clothing, **regen bracelet**, Hitpoints cape, cakes, chocolate cakes, pizzas, pies | multi-bite food, cooked hunter meat, single-bite high-healing food, rejuvenation potions |
| `@tempoross` / `@temp` | Basic, dragon, crystal, infernal and barb-tail harpoons; all Angler and Spirit angler outfit pieces; rope, hammer, bucket, Imcando hammer | Optional Lightbearer, Elven signet, Humidify rune pouch/runes and fishing boosts; no generic energy, fish barrel or tackle box |
| `@gotr` | Any pickaxe; all five essence pouch sizes (including degraded); each Raiments of the Eye piece; abyssal lantern; chisel; Varrock armour, celestial mining bonuses, Runecraft cape | Optional binding necklaces, Lunar rune pouch/runes for combinations and Astral Contact; graceful/prospector/transport; excludes generic stamina and unneeded talismans/needle |
| `@giants foundry` / `@foundry` | Bronze, iron, steel, mithril, adamantite and **runite** bars; eligible smithable metal weapons/armour; Smiths' outfit, ice gloves, bucket, transport jewellery | No gold/silver bars, ores, ordinary smithing tools or ornamental gear |
| `@mahogany homes` / `@mh` | Contract-tier planks, steel bars, hammer/saw, Amy's saw, Imcando hammer, plank sack, four individual Carpenter's outfit pieces | Astral Contact runes/pouches, Varrock/Ardougne/Falador/Hosidius teleports, optional Plank Make; no generic stamina or raw logs |
| `@hallowed sepulchre` / `@sepulchre` | Hallowed upgrades, lockpick, Saradomin items, graceful and run-energy protection | Coffin-looting setup: grapple/crossbow, planks/nails/saw/hammer, dust, enchantment runes, poison protection and food; no unrelated logs or generic Smithing |
| `@pyramid plunder` / `@plunder` | Pharaoh's sceptre, lockpick, snake charm, antidotes/serpentine helm, prayer protection, food, graceful | Prayer book + holy symbol, regen bracelet + HP cape, desert amulet/teleports, optional stamina; no generic energy |
| `@tithe farm` / `@tithe` | Watering cans / Gricoller's can, spade, seed dibber, all five named Farmer's outfit variants | Humidify spell runes/rune pouch, graceful, optional run-energy/travel aids; on-site seeds/fertiliser, no unrelated farming compost/seeds |
| `@pest control` / `@pest` | Void/Elite Void, melee/ranged/magic weapons, chinchompas, special-attack gear and Pest Control teleport | Optional combat boosts and Ancient Magicks runes; **no** routine food or prayer potions; stats reset after each round |
| `@barbarian assault` / `@ba` | Attacker weapons, armour for non-attacker roles, Imcando hammer for Defender, games necklace and offensive combat boosts | **No** food, prayer pots, rune pouches, external runes or ammo, or Horn of Glory (not bankable) |
| `@soul wars` / `@soulwars` | Magic-defence armour, Ancient Magicks staffs and runes, rune pouch, melee/ranged alternatives, spec weapons, Soul cape | **No** food, bandages, barricades or combat potions in bank search; provided inside match |
| `@nightmare zone` / `@nmz` | Dwarven rock cake, locator orb, Dharok/Obsidian gear, berserker necklace/ring, AFK weapons, prayer equipment | Optional ordinary combat/prayer potions and coins; **no** overload/absorption (minigame barrels), food or unrelated Slayer mask |
| `@shades of mortton` / `@shades` | Flamtaer hammer/bracelet/bag, temple supplies, serums and oils, pyre logs, six named shade remains, accurately coloured catacomb keys, tinderbox | Optional logs/axe, food, Mort'ton travel; rejects unrelated generic keys and Smithing gear |
| `@fishing trawler` / `@trawler` | Swamp paste (most common 50-point method), rope, bailing bucket, hammer/Imcando hammer and axe alternatives | No ordinary bucket, Angler outfit (a reward), Fishing cape or unrelated fishing supplies |
| `@volcanic mine` / `@vm` | Pickaxes, numulite, Volcanic Mine teleports, Prospector/Celestial gear, HP sustain and prayer equipment | Healing food, prayer restores; optional solo stamina, run-energy jewellery and return teleports; no general energy potions or mining ore |
| `@blast furnace` / `@bf` | Copper/tin/iron/silver/gold/mithril/adamantite/runite ores and coal; coal bag, goldsmith gauntlets and ice gloves/Smiths gloves (i) | Coins, bucket of water, stamina/weight reduction, optional ring of charos; no finished bars, Smiths' outfit or unrelated Smithing tools |

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
