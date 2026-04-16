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

**Planned Jump Unlocks**
- [ ] XP gain on each jump (small amount, scales with jump height or distance)
- [ ] XP gain on landing (scales with fall distance survived)
- [ ] Double jump — second mid-air jump at ~level 40, reduced force *(next up)*
- [ ] Wall jump — jump off a wall surface at ~level 75
- [ ] Jump force tuning at level 100 (current value is too high)

---

### Sprint / Endurance *(planned)*

Affects the stamina pool and regeneration rate. Since stamina already gates jump effectiveness, Sprint and Jump are naturally coupled — a high Sprint player jumps better for longer.

- [ ] `SPRINT` skill type
- [ ] XP gain from sprinting
- [ ] Stamina pool size scales with Sprint level
- [ ] Stamina regen rate scales with Sprint level
- [ ] Sprint duration scales with level
- [ ] Passive: reduces stamina cost of jump at high Sprint level

---

### Parkour / Agility *(planned — extends Jump + Sprint)*

Combines jump, wall interactions, and fluid movement into a mastery track. Rather than pure stat boosts, Agility unlocks movement abilities.

- [ ] `AGILITY` skill type
- [ ] XP gain from chaining movement actions (wall jumps, long jumps, landing from height)
- [ ] Mantling / ledge grab ability
- [ ] Reduced fall damage at high level
- [ ] Movement ability unlocks (speed burst, slide, etc.)

---

## Combat Skills *(planned framework)*

Standard RPG combat structure, each skill uses the same XP + unlock pattern proven by Jump.

### Melee
- [ ] `MELEE` skill type
- [ ] XP on hit, bonus XP on kill
- [ ] Damage bonus scales with level
- [ ] Ability unlocks: heavy strike, parry, counter

### Ranged
- [ ] `RANGED` skill type
- [ ] XP on hit, bonus XP on kill
- [ ] Accuracy / damage scales with level
- [ ] Ability unlocks: charged shot, multi-shot

### Magic
- [ ] `MAGIC` skill type
- [ ] XP on ability use
- [ ] Damage / effect scales with level
- [ ] Ability unlocks: AOE, lingering effects

---

## Gathering Skills *(planned)*

Core survival loop. XP awarded on resource actions.

- [ ] `MINING` — XP from breaking ore/stone nodes; faster break speed, better yield at high level
- [ ] `WOODCUTTING` — XP from felling trees; faster chop, bonus wood drops at high level
- [ ] `FORAGING` — XP from gathering plants/mushrooms/etc.; expanded loot table at high level

---

## Crafting Skills *(planned)*

- [ ] `CRAFTING` — XP from crafting actions; unlock advanced recipes at high level
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
