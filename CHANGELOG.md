# CHANGELOG

User-facing summary of what's changed in each update. Polished, easy to read.

Format based on [Keep a Changelog](https://keepachangelog.com/), versioning follows [SemVer](https://semver.org/):
- **MAJOR** — breaking changes
- **MINOR** — new features, backwards compatible
- **PATCH** — bug fixes only

Pre-1.0 while the skill/ability framework is still evolving. `[Unreleased]` at the top collects work-in-progress until the next version is cut.

---

## [Unreleased]

---

## [0.1.0] — 2026-04-22

First tracked release. Establishes the skill + ability framework using Jump as the proving-ground system.

### Added
- **Jump skill** — XP-based progression from level 1 to 100. XP awarded on every jump.
  - Jump height scales with level, up to 1.5× at level 100.
  - Fall damage reduction scales with level — full immunity at level 100.
  - Stamina cost per jump decreases with level, reaching minimum cost at level 70.
  - Low stamina lowers jump force; stamina is consumed at the moment of jump.
  - Stamina exhaustion recovery — regen resumes automatically after full drain.
- **Sky Leap ability** — unlocks at jump level 60.
  - Mid-air boost on the Ability 3 key, one use per airtime, resets on landing.
  - Force scales with jump level and current stamina.
  - Draws from the shared jump stamina pool.
  - Bindable to a held item or helmet slot.
  - Input buffering — pressing Ability 3 a fraction early still triggers correctly.
  - HUD notification on successful use.
- **Level-up HUD notifications.**
- **Admin commands**
  - `/beanz` — base debug command and skill inspector.
  - `/beanzlevel <1-100>` — set your own jump level; syncs XP and ability unlocks.
  - `/beanzlevel <player> <skill> <level>` — set any online player's skill level.
- **Build & deploy** — `gradlew jar` and `gradlew deployToHytale` for one-step deploy to the Hytale Mods folder.

### Fixed
- Ground jump force is now primed before takeoff so the passive level-scaled boost applies on the first frame.
- Ground jump stamina preview computed while grounded; subtraction deferred to execution to prevent double-charge.
- Exhaustion recovery no longer stuck — replaced misused `SprintStaminaRegenDelay.hasDelay()` with a per-player exhaustion timer and one-time sprint delay reset.
- Sky Leap correctly routes through `AbilityManager` so Jump skill bonuses apply to the final force.
- Sky Leap triggers correctly when Ability 3 is pressed while still grounded (buffered until airborne).
