# Beanz Core — Roadmap

> Last updated: April 2026

This document tracks what has been built and what's planned for the server's skill and ability systems.

---

## Design Philosophy

- **Use-based progression** — skills level up by doing, not from quests or menus
- **No hard classes** — players build flexible characters by choosing which skills they invest in
- **Natural improvement** — leveling feels like genuine mastery, not arbitrary numbers
- **Abilities unlock through mastery** — not handed out, earned by reaching thresholds
- **Jump is the testbed** — proves every system before the pattern gets applied to other skills

---

## System Infrastructure

### Core Skill Framework
- ✅ `PlayerSkillsComponent` — persistent per-player skill data (level + XP) via entity component system
- ✅ `SkillLevelTable` — XP curve defining XP required per level (1–100)
- ✅ `SkillService` — get/create player skill data from world store
- ✅ `SkillType` enum — extensible type registry (currently: `JUMP`)
- ✅ `SkillSnapshot` — lightweight read-only skill state record
- ✅ `SkillRewardService` — central location for all skill-scaling formulas

### Core Ability Framework
- ✅ `AbilityType` enum — extensible type registry; each entry carries its own skill + level unlock requirement
- ✅ `AbilityManager` — handles unlock, cooldown, execution, and force calculation for all abilities
- ✅ `PlayerAbilityData` — persistent per-player ability state (unlocked abilities, cooldowns)
- ✅ `LevelUpNotificationService` — HUD notification on level-up

### Build & Deployment
- ✅ Gradle build with `deployToHytale` task
- ✅ Deploys to `%APPDATA%/Hytale/UserData/Mods`
- ✅ `hytaleHome` configurable via `gradle.properties`
- ✅ Plugin version injected into `manifest.json` / `plugin.json` at build time

---

## Movement Skills

### Jump ✅ (First system — fully built)

The foundation skill. Proves the entire progression pipeline and gates the first set of air abilities.

**Ground Jump**
- ✅ Jump height scales with level — up to 1.5× base at level 100
- ✅ Jump force is primed on the ground tick so it applies on the first frame of takeoff
- ✅ Stamina cost per jump — decreases as level increases, reaching minimum cost at level 70
- ✅ Low stamina means a lower jump — stamina scales jump force
- ✅ Stamina is consumed at the moment of jump, not on key press
- ✅ Stamina exhaustion recovery — regen resumes automatically after full drain
- ✅ Ground jump input detection distinguishes a new press from a held key

**Sky Leap** *(unlocks at level 60)*
- ✅ Mid-air boost — activate with the Ability 3 key while airborne
- ✅ One use per airtime — resets on landing
- ✅ Respects cooldown between uses
- ✅ Force scales with jump level and current stamina
- ✅ Draws from the same stamina pool as ground jump
- ✅ Bound to held item or wearable (helmet slot)
- ✅ HUD notification on successful use
- ✅ Input buffering — pressing Ability 3 a fraction of a second early still triggers correctly

**Passive Scaling**
- ✅ Jump height increases with level
- ✅ Fall damage reduction scales with level — 0% at level 1, full immunity at level 100

**XP Gain**
- ✅ XP awarded on each jump
- [ ] Bonus XP on landing from height (scales with fall distance survived)

**Ability Unlocks by Level**

| Level | Ability | Status |
|---|---|---|
| 60 | Sky Leap — mid-air Ability 3 boost | ✅ Built |
| ~75 | Wall Jump — jump off a wall surface | Planned |
| ~90 | TBD — high-mastery movement ability | Planned |

---

### Sprint / Endurance *(planned)*

Affects the stamina pool and regeneration rate. Since stamina already gates jump effectiveness, Sprint and Jump are naturally coupled — a high Sprint player jumps better for longer.

- [ ] XP gain from sprinting
- [ ] Stamina pool size scales with level
- [ ] Stamina regen rate scales with level
- [ ] Sprint duration scales with level
- [ ] Passive: reduces stamina cost of jump at high level

**Ability Unlocks by Level**

| Level | Ability |
|---|---|
| ~50 | Dash — short burst of speed on Ability input |
| ~80 | Endurance Surge — temporary stamina regen boost |

---

### Parkour / Agility *(planned — extends Jump + Sprint)*

Combines jump, wall interactions, and fluid movement into a mastery track. Rather than pure stat boosts, Agility unlocks movement abilities.

- [ ] XP gain from chaining movement actions (wall jumps, long jumps, landing from height)
- [ ] Mantling / ledge grab at mid level
- [ ] Reduced fall damage at high level (supplements Jump's fall damage reduction)
- [ ] Movement ability unlocks (speed burst, slide, etc.)

**Ability Unlocks by Level**

| Level | Ability |
|---|---|
| ~30 | Mantle — grab and pull up onto ledges |
| ~60 | Slide — quick low slide under obstacles |
| ~80 | Blink — short-range forward dash through space |

---

## Combat Skills *(planned)*

Standard RPG combat structure. Each skill uses the same XP + unlock pattern proven by Jump.

### Melee
- [ ] XP on hit, bonus XP on kill
- [ ] Damage bonus scales with level

| Level | Ability |
|---|---|
| ~40 | Heavy Strike — charged slow hit with knockback |
| ~60 | Parry — brief block window on Ability input |
| ~80 | Counter — damage boost after successful parry |

### Ranged
- [ ] XP on hit, bonus XP on kill
- [ ] Accuracy / damage scales with level

| Level | Ability |
|---|---|
| ~40 | Charged Shot — hold to charge for increased damage/range |
| ~70 | Multi-shot — fire multiple projectiles in a spread |

### Magic
- [ ] XP on ability use
- [ ] Damage / effect power scales with level

| Level | Ability |
|---|---|
| ~40 | AOE Blast — area-of-effect burst |
| ~70 | Lingering Effect — leave a damage/effect zone |
| ~90 | TBD — high-mastery spell |

---

## Gathering Skills *(planned)*

Core survival loop. XP awarded on resource actions. All gathering skills follow the same pattern: faster action and better yield as level increases, with ability unlocks at mid and high thresholds.

- [ ] **Mining** — XP from breaking ore/stone; faster break speed, better yield at high level
  - ~60: Vein Mine — break connected ore nodes in one action
  - ~85: Prospector's Eye — nearby ore nodes highlighted briefly
- [ ] **Woodcutting** — XP from felling trees; faster chop, bonus wood drops at high level
  - ~60: Fell — chop an entire tree at once
  - ~80: Splitter — bonus chance for planks on each cut
- [ ] **Foraging** — XP from gathering plants/mushrooms; expanded loot table at high level
  - ~50: Keen Eye — rare plants appear in your vision briefly
  - ~75: Abundance — chance to gather double the yield

---

## Crafting Skills *(planned)*

- [ ] **Crafting** — XP from crafting; unlock advanced recipes at high level
  - ~50: Bulk Craft — craft multiple items in one action
  - ~80: Inspired — chance to craft a superior-quality item
- [ ] **Smithing** — XP from forging gear; improved output quality/durability at high level
- [ ] **Building** — XP from placing blocks in structures; unlock build-mode abilities

---

## Admin & Tooling

- ✅ `/beanz` — base debug command and skill inspector
- ✅ `/beanzlevel <1-100>` — set your own jump level; syncs XP and ability unlocks
- ✅ `/beanzlevel <player> <skill> <level>` — set any online player's skill level; syncs their ability unlocks
- [ ] Skill inspection command — view your own or another player's current skill levels
- [ ] XP override command — add/remove XP from a specific skill

---

## Progression Path

The intended order of implementation:

```
Jump (done)
  └─ Bonus XP on landing from height

Sprint / Endurance
  └─ Stamina pool scaling
  └─ Regen rate scaling

Parkour / Agility
  └─ Chained movement XP
  └─ Mantle / ledge grab
  └─ Slide ability

Combat (Melee → Ranged → Magic)

Gathering (Mining → Woodcutting → Foraging)

Crafting (Crafting → Smithing → Building)
```

---

## Future Modules

These will be separate mods that depend on Beanz Core as their framework.

| Module | Description |
|---|---|
| `beanz-skills` | Houses additional skill trees once core framework is proven |
| `beanz-factions` | Faction creation, territory claiming, faction-based perks |
| `beanz-raids` | Raid event framework, boss encounters, loot tables |
| `beanz-taming` | Creature taming, bonding mechanics, companion abilities |
| `beanz-world` | World-stage progression, region unlocking, environmental scaling |

---

## Architecture Notes

- All per-player state lives in entity components and is persisted automatically by the Hytale entity store
- Systems tick every server frame via `getEntityStoreRegistry().registerSystem(...)`
- Skill scaling formulas live in `SkillRewardService` — add new skills there first
- Ability execution is gated through `AbilityManager` — add new `AbilityType` values and handle them there
- Each `AbilityType` carries its own skill + level unlock requirement — `syncAbilityUnlocks` automatically locks/unlocks all abilities when a player's level changes
- `SkillType` and `AbilityType` enums are intentionally extensible — adding a skill is an enum value + a system
