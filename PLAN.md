# PLAN

Current state and next steps. See ROADMAP.md for broader goals.

---

## Current State

**Shipped and working:**
- Jump skill end-to-end: XP curve, level scaling, fall damage reduction, stamina scaling, exhaustion recovery.
- Sky Leap ability: unlocks at jump level 60, input buffering, held-item + helmet-slot binding, routed through `AbilityManager` so skill bonuses apply.
- Admin commands: `/beanz`, `/beanzlevel` (self + other-player variants).
- Core frameworks in place: `SkillType`, `AbilityType`, `SkillService`, `AbilityManager`, `PlayerSkillsComponent`, `PlayerAbilityData`, `SkillRewardService`, `LevelUpNotificationService`.
- Build pipeline: `gradlew jar` + `gradlew deployToHytale` → `%APPDATA%/Hytale/UserData/Mods`.

**Recent polish (Apr 2026):**
- Sky Leap force scale tuned to 0.6, separated from double jump.
- Sky Leap buffering when Ability3 pressed while still grounded.
- Ground jump prep moved above `!jumpInputDetected` early return.
- Stamina recovery fix using per-player exhaustion timer.

---

## Next Up

1. **Bonus XP on landing from height** — scales with fall distance survived. Only remaining checkbox on the Jump skill.
2. **Sprint / Endurance skill** — next skill in the progression path.
   - Stamina pool size scales with level.
   - Stamina regen rate scales with level.
   - XP gained from sprinting.
   - Naturally couples with Jump since stamina already gates jump effectiveness.

After Sprint lands, the pattern proves out on a second skill and Parkour/Agility becomes the next target (mantle, slide, blink).

---

## Open Questions

- None tracked yet.

---

## Notes
- `SkillType` and `AbilityType` enums are extensible — adding a new skill = enum value + tick system + entries in `SkillRewardService`.
- Per-player state lives in entity components, persisted automatically by the Hytale entity store.
- Engine reference material under `docs/hytale/` — interactions, UI, movement, abilities, commands.
