# CHANGELOG

Polished, user-facing entries. Only updated when something ships or a version changes.

Format: Added / Fixed / Changed / Breaking.

---

## Unreleased

### Added
- Jump skill with XP-based progression (levels 1–100), XP awarded on every jump.
- Jump height scales with level (up to 1.5× at level 100).
- Fall damage reduction scales with level — full immunity at level 100.
- Stamina cost per jump decreases as level increases (min cost reached at level 70).
- Low stamina lowers jump force; stamina is consumed at the moment of jump.
- Stamina exhaustion recovery — regen resumes automatically after full drain.
- Sky Leap ability (unlocks at jump level 60) — mid-air boost on Ability 3 key, one use per airtime, resets on landing.
- Sky Leap input buffering — pressing Ability 3 a fraction early still triggers correctly.
- Sky Leap force scales with jump level and current stamina; draws from shared stamina pool.
- Sky Leap bindable via held item or helmet slot; HUD notification on successful use.
- Level-up HUD notifications.
- `/beanz` — base debug command and skill inspector.
- `/beanzlevel <1-100>` — set your own jump level; syncs XP and ability unlocks.
- `/beanzlevel <player> <skill> <level>` — set any online player's skill level.
- Gradle `deployToHytale` task — one-step deploy to Hytale Mods folder.

### Fixed
- Ground jump force now primed before takeoff so passive level-scaled boost applies on the first frame.
- Ground jump stamina preview computed while grounded; subtraction deferred to execution to prevent double-charge.
- Exhaustion recovery no longer stuck — replaced misused `SprintStaminaRegenDelay.hasDelay()` with a per-player exhaustion timer and one-time sprint delay reset.
- Sky Leap correctly routes through `AbilityManager` so Jump skill bonuses apply to final force.
- Sky Leap triggers correctly when Ability 3 is pressed while still grounded (buffered until airborne).
