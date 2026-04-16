# Beanz Core — Roadmap

> Last updated: April 2026
> Branch: `claude/jump-and-skyleap`

This document tracks what has been built, what is currently in progress, and what is planned for the future.

---

## Jump Skill System

The first skill module built into Beanz Core. Implements a full XP-based progression system (levels 1–100) that passively improves jump height and gates access to the SKY_LEAP air ability.

### Core Skill Infrastructure
- [x] `PlayerSkillsComponent` — persistent per-player skill data (level + XP) stored via entity component system
- [x] `SkillLevelTable` — XP curve defining XP required per level (1–100)
- [x] `SkillService` — get/create player skill data from world store
- [x] `SkillType` enum — extensible type registry (currently: `JUMP`)
- [x] `SkillSnapshot` — lightweight read-only skill state record
- [x] `SkillRewardService` — central location for all skill-scaling formulas

### Jump Skill — Ground Jump
- [x] Passive jump force boost — scales with jump level using `getJumpMultiplier()`
- [x] Jump force is primed on the ground tick before takeoff so it's applied on the first frame of the jump
- [x] Stamina cost per jump — scales inversely with level (`max(2.0, 5.5 - 0.05 * level)`, min cost reached at level 70)
- [x] Stamina scales the jump force — low stamina = reduced jump height
- [x] Stamina consumption executes at the moment of jump (not on press)
- [x] Stamina exhaustion recovery — after full drain, a recovery timer resets the sprint delay stat so stamina regenerates again
- [x] Ground jump detection correctly identifies new jump presses vs. held input

### Jump Skill — SKY_LEAP Ability
- [x] `AbilityType` enum — extensible type registry (currently: `SKY_LEAP`)
- [x] `AbilityManager` — handles unlock, cooldown, execution, and force calculation for all abilities
- [x] `PlayerAbilityData` — persistent per-player ability state (unlocked abilities, cooldowns)
- [x] SKY_LEAP unlocks at **level 60**
- [x] SKY_LEAP is airborne-only — blocked if player is on the ground
- [x] SKY_LEAP is one-per-airtime — resets on landing
- [x] SKY_LEAP respects cooldown
- [x] SKY_LEAP stamina cost — uses same stamina pool as ground jump
- [x] SKY_LEAP force calculation: `baseForce + skillBonus + abilityBonus * jumpRatio`
- [x] SKY_LEAP activates via **Ability3 input** bound to a held or offhand item interaction
- [x] SKY_LEAP sends a HUD notification on successful use
- [x] `JumpAbilityStateComponent` — per-player frame state tracking (airtime, double-jump flags, wall contact, stamina recovery state, etc.)

### Jump Skill — Ability Binding
- [x] Dedicated test item wired to Ability3 interaction
- [x] Wearable item support (helmet slot) triggers Ability3 via `WearableAbilityTestSystem`
- [x] Interaction asset registered through `getCodecRegistry(Interaction.CODEC)`
- [x] `SKY_LEAP_TEMPORARILY_DISABLED` flag — can gate ability execution without removing any wiring

### Jump Skill — Level-Up Feedback
- [x] `LevelUpNotificationService` — sends a HUD notification when the player gains a level
- [x] Level-up logged server-side with before/after level info

### Fall Damage
- [x] `JumpFallDamageSystem` — calculates and applies fall damage based on velocity

### Admin Commands
- [x] `/beanz` — base admin command (debug/test entry point)
- [x] `/beanzlevel <level>` — sets your own jump level (1–100), syncs XP and ability unlocks
- [ ] `/beanzsetlevel <player> <skill> <level>` — admin form targeting any online player by username *(in progress)*

---

## Build & Deployment
- [x] Gradle build with `deployToHytale` task
- [x] Deploys to `%APPDATA%/Hytale/UserData/Mods`
- [x] `hytaleHome` configurable via `gradle.properties`
- [x] Plugin version injected into `manifest.json` / `plugin.json` at build time

---

## In Progress

| Feature | Notes |
|---|---|
| `/beanzsetlevel <player> <skill> <level>` | Admin command to set any online player's skill level — needs `RequiredArg` API fix and new command class |
| `/beanzlevel` arg parsing fix | Hytale `RequiredArg` constructor requires `(AbstractCommand, name, desc, ArgType)` — fix in progress |
| Jump force tuning at level 100 | Current force is too high; needs scaling adjustment |

---

## Planned — Near Term

| Feature | Notes |
|---|---|
| Double jump | Air ability via Ability3; second jump with reduced force |
| Wall jump | Detect wall contact, allow jump off surface |
| XP gain on jump | Award small XP per successful jump; more at higher height/fall distance |
| XP gain on landing | Award XP based on fall distance survived |
| Skill level cap messaging | Notify player when they reach max level |
| Persistent ability cooldown display | Show cooldown state in HUD |
| Per-skill unlock table | Generalized unlock system driven by level thresholds |

---

## Planned — Future Modules

These will be separate mods that depend on Beanz Core.

| Module | Description |
|---|---|
| `beanz-skills` | Additional skill trees beyond jump (mining, combat, swimming, etc.) |
| `beanz-factions` | Faction creation, territory claiming, and faction-based perks |
| `beanz-raids` | Raid event framework, boss encounters, loot tables |
| `beanz-taming` | Creature taming, bonding mechanics, companion abilities |
| `beanz-world` | World-stage progression, region unlocking, environmental scaling |

---

## Architecture Notes

- All per-player state lives in entity components (`PlayerSkillsComponent`, `PlayerAbilityData`, `JumpAbilityStateComponent`) and is persisted automatically by the Hytale entity store
- Systems tick every server frame and are registered via `getEntityStoreRegistry().registerSystem(...)`
- Skill scaling formulas are centralized in `SkillRewardService` — add new skills there first
- Ability execution is gated through `AbilityManager` — add new ability types to `AbilityType` and handle them in `AbilityManager`
- All `SkillType` and `AbilityType` enums are intentionally extensible — adding a new skill means adding an enum value and wiring up a system
