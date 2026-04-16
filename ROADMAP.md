# Beanz Core — Roadmap

> Last updated: April 2026

This document tracks what has been built, what is currently in progress, and the full planned vision for the server's skill system.

---

## Design Philosophy

- **Use-based progression** — skills level up by doing, not from quests or menus
- **No hard classes** — players build flexible characters by choosing which skills they invest in
- **Natural improvement** — leveling feels like genuine mastery, not arbitrary numbers
- **Abilities unlock through mastery** — not handed out, earned through reaching thresholds
- **Jump is the testbed** — it proves every system (input detection, XP tracking, stat scaling, ability triggering, player feedback) before the pattern gets applied to every other skill

---

## System Infrastructure

### Core Skill Framework
- [x] `PlayerSkillsComponent` — persistent per-player skill data (level + XP) via entity component system
- [x] `SkillLevelTable` — XP curve defining XP required per level (1–100)
- [x] `SkillService` — get/create player skill data from world store
- [x] `SkillType` enum — extensible type registry (currently: `JUMP`)
- [x] `SkillSnapshot` — lightweight read-only skill state record
- [x] `SkillRewardService` — central location for all skill-scaling formulas

### Core Ability Framework
- [x] `AbilityType` enum — extensible type registry (currently: `SKY_LEAP`)
- [x] `AbilityManager` — handles unlock, cooldown, execution, and force calculation for all abilities
- [x] `PlayerAbilityData` — persistent per-player ability state (unlocked abilities, cooldowns)
- [x] `LevelUpNotificationService` — HUD notification on level-up

### Build & Deployment
- [x] Gradle build with `deployToHytale` task
- [x] Deploys to `%APPDATA%/Hytale/UserData/Mods`
- [x] `hytaleHome` configurable via `gradle.properties`
- [x] Plugin version injected into `manifest.json` / `plugin.json` at build time

---

## Movement Skills

### Jump ✅ (First system — fully built)

The foundation skill. Proves the entire progression pipeline and gates the first set of air abilities.

**Ground Jump**
- [x] Passive jump force boost — scales with level via `getJumpMultiplier()`
- [x] Jump force primed on ground tick so it applies on the first frame of takeoff
- [x] Stamina cost per jump — decreases as level increases (`max(2.0, 5.5 - 0.05 * level)`, min cost at level 70)
- [x] Stamina scales jump force — low stamina = lower jump
- [x] Stamina consumed at the moment of jump, not on key press
- [x] Stamina exhaustion recovery — timer resets sprint delay stat so regen resumes after full drain
- [x] Ground jump detection distinguishes new press from held input
- [x] Fall damage reduction via `JumpFallDamageSystem`

**SKY_LEAP Ability** *(unlocks at level 60)*
- [x] Airborne-only mid-air boost via Ability3 input
- [x] One use per airtime — resets on landing
- [x] Respects cooldown
- [x] Force calculation: `baseForce + skillBonus + abilityBonus * staminaRatio`
- [x] Draws from same stamina pool as ground jump
- [x] Bound to held item or wearable (helmet slot)
- [x] HUD notification on successful use
- [x] `JumpAbilityStateComponent` — per-player frame state (airtime, ability flags, wall contact, stamina recovery)

**Passive Scaling**
- [x] Jump height increases with level
- [x] Fall damage reduced by `JumpFallDamageSystem` (system built; scaling curve TBD)
- [ ] Fall damage reduction scales explicitly with level (e.g. 0% at level 1 → full immunity at level 100)

**XP Gain** *(use-based — not yet wired)*
- [ ] XP on each jump (small amount, scales with jump height or distance)
- [ ] Bonus XP on landing from height (scales with fall distance survived)

**Ability Unlocks by Level**

| Level | Unlock |
|---|---|
| 60 | ✅ SKY_LEAP — mid-air Ability3 boost |
| ~40 | Double jump — second jump in air, reduced force |
| ~75 | Wall jump — jump off a wall surface |
| ~90 | TBD — high-mastery movement ability |

**Tuning**
- [ ] Jump force at level 100 is currently too high — needs scaling pass

---

### Sprint / Endurance *(planned)*

Affects the stamina pool and regeneration rate. Since stamina already gates jump effectiveness, Sprint and Jump are naturally coupled — a high Sprint player jumps better for longer.

- [ ] `SPRINT` skill type
- [ ] XP gain from sprinting
- [ ] Stamina pool size scales with Sprint level
- [ ] Stamina regen rate scales with Sprint level
- [ ] Sprint duration scales with level
- [ ] Passive: reduces stamina cost of jump at high Sprint level

**Ability Unlocks by Level**

| Level | Unlock |
|---|---|
| ~50 | Dash — short burst of speed on Ability input |
| ~80 | Endurance Surge — temporary stamina regen boost |

---

### Parkour / Agility *(planned — extends Jump + Sprint)*

Combines jump, wall interactions, and fluid movement into a mastery track. Rather than pure stat boosts, Agility unlocks movement abilities.

- [ ] `AGILITY` skill type
- [ ] XP gain from chaining movement actions (wall jumps, long jumps, landing from height)
- [ ] Mantling / ledge grab at mid level
- [ ] Reduced fall damage at high level (supplements Jump's fall damage reduction)
- [ ] Movement ability unlocks (speed burst, slide, etc.)

**Ability Unlocks by Level**

| Level | Unlock |
|---|---|
| ~30 | Mantle — grab and pull up onto ledges |
| ~60 | Slide — quick low slide under obstacles |
| ~80 | Blink — short-range forward dash through space |

---

## Combat Skills *(planned framework)*

Standard RPG combat structure, each skill uses the same XP + unlock pattern proven by Jump.

### Melee
- [ ] `MELEE` skill type
- [ ] XP on hit, bonus XP on kill
- [ ] Damage bonus scales with level

**Ability Unlocks by Level**

| Level | Unlock |
|---|---|
| ~40 | Heavy Strike — charged slow hit with knockback |
| ~60 | Parry — brief block window on Ability input |
| ~80 | Counter — damage boost after successful parry |

### Ranged
- [ ] `RANGED` skill type
- [ ] XP on hit, bonus XP on kill
- [ ] Accuracy / damage scales with level

**Ability Unlocks by Level**

| Level | Unlock |
|---|---|
| ~40 | Charged Shot — hold to charge for increased damage/range |
| ~70 | Multi-shot — fire multiple projectiles in a spread |

### Magic
- [ ] `MAGIC` skill type
- [ ] XP on ability use
- [ ] Damage / effect power scales with level

**Ability Unlocks by Level**

| Level | Unlock |
|---|---|
| ~40 | AOE Blast — area-of-effect burst |
| ~70 | Lingering Effect — leave a damage/effect zone |
| ~90 | TBD — high-mastery spell |

---

## Gathering Skills *(planned)*

Core survival loop. XP awarded on resource actions. All gathering skills follow the same pattern: faster action + better yield as level increases, with an ability unlock at a mid and high threshold.

- [ ] `MINING` — XP from breaking ore/stone nodes; faster break speed, better yield at high level
  - Unlocks ~60: Vein Mine — break connected ore nodes in one action
  - Unlocks ~85: Prospector's Eye — nearby ore nodes are highlighted briefly
- [ ] `WOODCUTTING` — XP from felling trees; faster chop, bonus wood drops at high level
  - Unlocks ~60: Fell — chop an entire tree at once
  - Unlocks ~80: Splitter — bonus chance for planks/logs on each cut
- [ ] `FORAGING` — XP from gathering plants/mushrooms/etc.; expanded loot table at high level
  - Unlocks ~50: Keen Eye — rare plants appear in your vision briefly
  - Unlocks ~75: Abundance — chance to gather double the yield

---

## Crafting Skills *(planned)*

Each crafting skill uses the same unlock pattern — passive quality/speed improvements at low level, ability-style unlocks at mid and high thresholds.

- [ ] `CRAFTING` — XP from crafting actions; unlock advanced recipes at high level
  - Unlocks ~50: Bulk Craft — craft multiple items in one action
  - Unlocks ~80: Inspired — chance to craft a superior-quality item
- [ ] `SMITHING` — XP from forging gear; improved output quality/durability at high level
- [ ] `BUILDING` — XP from placing blocks in structures; unlock build-mode abilities

---

## Admin & Tooling

- [x] `/beanz` — base debug command
- [x] `/beanzlevel <level>` — set your own jump level (1–100), syncs XP and ability unlocks
- [ ] `/beanzsetlevel <player> <skill> <level>` — set any online player's skill level *(in progress)*
- [ ] Skill inspection command — view your own or another player's current skill levels
- [ ] XP override command — add/remove XP from a specific skill

---

## In Progress

| Feature | Notes |
|---|---|
| `/beanzlevel` arg parsing | Hytale `RequiredArg` constructor is `(AbstractCommand, name, desc, ArgType)` — fix in progress |
| `/beanzsetlevel <player> <skill> <level>` | New admin command class; depends on arg fix |
| Jump force tuning at level 100 | Current force too high — needs scaling pass |
| XP gain on jump | First real use-based progression loop for the Jump skill |

---

## Clean Progression Path

The intended order of implementation once Jump is fully polished:

```
Jump (done)
  └─ XP on jump/landing
  └─ Double jump unlock
  └─ Wall jump unlock

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
- `SkillType` and `AbilityType` enums are intentionally extensible — adding a skill is an enum value + a system
