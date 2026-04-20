# DEVLOG

Raw session notes — decisions, dead ends, context. Newest entries on top.

---

## [2026-04-20] New docs structure
- Adopted root-level CLAUDE.md / DEVLOG.md / PLAN.md / CHANGELOG.md / ROADMAP.md pattern.
- Seeded from `docs/dev/changes.md` + `discoveries.md` + git log.
- Old `docs/hytale/` engine reference kept intact (still useful). `docs/dev/` retained but superseded by these root files going forward.

---

## [2026-04-12 to 2026-04-15] Sky Leap timing, stamina recovery, UI docs
**Sky Leap timing fix + jump polish (shipped)**
- Lowered SkyLeap force scale to 0.6, separated from double jump.
- Buffered SkyLeap when still on ground at Ability3 press time (input buffering).
- Restored original SkyLeap force formula after experimenting.

**Ground jump stamina (shipped)**
- Ground jump force must be primed before takeoff — moved `settings.jumpForce` prep above the `!jumpInputDetected` early return so the scaled passive boost is available on first frame.
- Stamina preview for force priming happens while grounded; actual stamina subtraction deferred to grounded execution to avoid double-charge.
- Fixed exhaustion recovery: `SprintStaminaRegenDelay.hasDelay()` is not a live recovery timer — added per-player exhaustion timer in `JumpAbilityStateComponent` with one-time sprint delay reset when recovery becomes ready.
- Detect ground jump using pre-update `hasLeftGroundSinceInitialJump`; mark it `true` when ground jump starts.

**UI reference docs ingested**
- Added `docs/hytale/ui/{custom-ui, common-styling, layout, markup}.md` from official Hytale UI docs.
- Key takeaway: Custom UI is server-driven, command-based. `.ui` assets define layout, interaction events flow back to server. Base styling on `Common.ui` primitives.

---

## [2026-04-11] Sky Leap / Jump integration
- Problem: main-hand/off-hand Ability3 worked but Jump skill level bonuses and level-100 force bonus were bypassed — `TestAbility3Interaction` was writing `defaultSettings.jumpForce * 1.15` directly instead of calling `AbilityManager.useSkyLeap(...)`.
- Fix: route `TestAbility3Interaction` through `AbilityManager.useSkyLeap(...)`. Formula: `finalJumpForce = baseJumpForce + skillBonus + abilityBonus`.
- Dead end: `AbilityManager.useSkyLeap(PlayerRef)` blocked at `playerRef.getHolder() == null` inside the interaction callback even though `context.getEntity()` resolved fine. Fix: new overload that accepts the already-resolved entity ref + runtime components from the callback; kept `PlayerRef` path for non-interaction callers.
- Restored SKY_LEAP popup notification on success.

---

## [2026-04-10] Ability3 input — interaction-based, not global
**Big discovery: Hytale does NOT treat Ability1/2/3 as global key events.** Inputs resolve through item interaction mappings. No interaction on held item → no packet → no server event. PacketWatchers alone cannot detect Ability3.

**Path taken:**
1. Tried `SyncInteractionChains` PacketWatcher — nothing received (correct: no interaction = no packet).
2. Tried global injection into all loaded item assets via `LoadedAssetsEvent` + cached packet clear — broke asset validation.
3. Pivoted to dedicated custom item with explicit `Interactions.Ability3 = Root_BeanzTestAbility` mapping. Worked.
4. Trimmed handler to minimum (log + chat + small bump) to prove the path, then layered in airborne check → SKY_LEAP proof.
5. Added wearable test via built-in cloth head armor inheritance (no custom art refs — prior wearable failed from missing icon/model assets).
6. Ability3 item converted to utility-compatible (`ItemUtility.Compatible` flag) for offhand test. No explicit `AllowOffhand` field exists in item config.

**Also pivoted:** Abandoned jump-key inference approach for double jump — unreliable due to timing. Moved air abilities to Ability3 entirely. Cleanly separated: Jump skill = progression/unlocks, Ability system = execution.

Also added `/beanzlevel` command using typed argument system (not raw string parsing).
