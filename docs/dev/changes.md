## [2026-04-10] Pivot from Jump Input to Ability System

### Summary
Abandoned unreliable jump-key double-jump detection in favor of building a proper ability system using Hytale’s supported interaction inputs.

### Details
- Identified that jump-key inference is unreliable due to input timing and engine handling
- Decided to move air abilities (e.g., Sky Leap) to Ability3 trigger
- Separated concerns:
  - Jump skill = progression, unlocks
  - Ability system = execution/trigger
- Began implementing Ability framework (AbilityType, AbilityManager, PlayerAbilityData)

---

## [2026-04-10] Ability3 Input Not Firing (Initial Failure)

### Summary
Ability3 input did not trigger any logs or behavior despite multiple implementations.

### Details
- Implemented inbound `SyncInteractionChains` PacketWatcher
- No packets or interaction logs were received during gameplay
- Confirmed:
  - Ability3 keybind works client-side
  - Server receives no interaction events
- Conclusion: input not reaching server → watcher approach insufficient alone

---

## [2026-04-10] Discovery of Interaction Injection Requirement

### Summary
Determined that Ability inputs depend on item interaction mappings, not global key events.

### Details
- Found evidence from working mod strings:
  - `injectAbility3`
  - `cachedPacket`
- Implemented asset injection:
  - Hooked `LoadedAssetsEvent`
  - Injected `InteractionType.Ability3` into item assets
  - Cleared cached packets to force rebuild
- Result:
  - Mod failed asset validation initially (bad injection path)
  - After fixes, mod loaded but Ability3 still did not trigger

---

## [2026-04-10] Jump System Blocking Without Ability Fallback

### Summary
Jump system was disabling air jump in favor of Ability3 before Ability3 was working.

### Details
- Logs show:
  - `blockedReason=air_ability_handled_by_ability3`
  - `actionChosen=NONE`
- No Ability3 logs present
- Result: player cannot double jump at all
- Decision:
  - Restore Jump fallback
  - Do not disable existing system until new path is proven

---

## [2026-04-10] Narrowing Ability Trigger Scope

### Summary
Shifted from global injection to controlled item-based testing.

### Details
- Determined:
  - Ability3 only triggers if bound to held item interaction
  - Dirt block test produced no results
- Decision:
  - Stop injecting into all assets
  - Target a single known item (e.g., sword/tool)
  - Build minimal test: Ability3 → log/chat message

---

## [2026-04-10] Reset to Minimal Ability Proof

### Summary
Restarted ability implementation with minimal test goal.

### Details
- Removed:
  - skill integration
  - stamina logic
  - airborne checks
  - cooldowns
- New goal:
  - Press Ability3 → log fires
- Focus:
  - prove interaction path works before building system
---

## [2026-04-10] Explicit Ability3 Test Item

### Summary
Replaced runtime interaction injection with one dedicated Beanz test item wired directly to Ability3.

### Details
- Removed the runtime item injection hook from plugin setup
- Added a dedicated item asset with `Interactions.Ability3 = Root_BeanzTestAbility`
- Added matching root interaction + interaction assets
- Kept the Java handler minimal:
  - server log
  - player notification
  - tiny upward bump for visible proof

---

## [2026-04-10] Ability3 Test Item -> SKY_LEAP Proof

### Summary
Converted the working dedicated Ability3 test item from a generic bump into a simple airborne-only SKY_LEAP proof.

### Details
- Kept the same item asset, root interaction, and interaction registration
- Updated the Java handler to:
  - block on ground
  - apply a jump-force-style upward boost while airborne
  - log SKY_LEAP use/block results
- Still intentionally excludes skill gating, stamina, cooldowns, and Jump integration

---

[2026-04-10]
Task
Ingested official Hytale interaction system reference

Result
Updated interaction-reference.md with core system behavior and input handling model

Findings
Hytale uses interaction-based input resolution rather than direct input events, making timing and state conditions critical

Next Step
Align double jump implementation with interaction condition timing and input evaluation model

---

[2026-04-10]
Task
Ingested argument types documentation

Result
Added argument-types.md and documented command parsing system

Findings
Command inputs are strongly typed and validated before execution

Next Step
Refactor /beanz command to use typed arguments instead of manual parsing

---

[2026-04-10]
Task
Recreated wearable Ability3 proof using built-in armor inheritance only

Result
Added a new wearable test item that inherits from built-in cloth head armor, plus a small equip/unequip logging system

Findings
The previous wearable failure came from missing custom icon/model assets, so the new test avoids custom art references entirely

Next Step
Verify in game whether the wearable loads cleanly and whether wearing it alone can expose Ability3

---

[2026-04-10]
Task
Prepared the dedicated Ability3 test item for offhand proof

Result
Updated the existing held-item Ability3 test item to be a single-stack utility-compatible item without changing its interaction path

Findings
The only concrete offhand-related asset clue exposed by the server item config is Utility.Compatible on utility items; no standalone AllowOffhand field was found

Next Step
Test whether the same Ability3 item can be placed in offhand and still fire there

---

## [2026-04-11] Restore Jump Skill Bonus In Ability3 Force Path

### Summary
Reconnected the main-hand/off-hand Ability3 trigger to the shared `AbilityManager` SKY_LEAP path so Jump skill bonuses apply again at the final velocity write.

### Details
- Confirmed the held-item Ability3 asset was still routed through `TestAbility3Interaction`
- Confirmed that legacy interaction applied its own hardcoded upward force and bypassed `AbilityManager.useSkyLeap(...)`
- Updated `TestAbility3Interaction` to delegate into `AbilityManager.useSkyLeap(...)` instead of writing velocity directly
- Updated the final SKY_LEAP force calculation to log and apply:
  - `finalJumpForce = baseJumpForce + skillBonus + abilityBonus`
- Added final application debug logging for:
  - `jumpLevel`
  - `skillBonus`
  - `abilityBonus`
  - `finalJumpForce`

---

## [2026-04-11] Fix SKY_LEAP Holder Resolution For Interaction Path

### Summary
Patched the Ability3 interaction path to pass the live player entity context directly into `AbilityManager` instead of relying on `playerRef.getHolder()`, which was null during the interaction callback.

### Details
- Confirmed `AbilityManager.useSkyLeap(PlayerRef)` was blocking at `playerRef.getHolder()`
- Updated `TestAbility3Interaction` to pass the already-resolved player entity ref and runtime components into a new `AbilityManager.useSkyLeap(...)` overload
- Kept the existing `playerRef.getHolder()` path for non-interaction callers
- Added one concise debug log immediately before the existing holder-missing block showing:
  - player
  - entity ref
  - holder result
  - lookup path attempted

---

## [2026-04-11] Restore Passive Ground Jump Boost And SKY_LEAP Popup

### Summary
Restored the passive Jump skill boost on the normal first jump by priming the runtime ground jump force before takeoff, and restored the successful SKY_LEAP popup notification.

### Details
- Confirmed normal first jump still depends on `MovementManager.settings.jumpForce`
- Confirmed the system was only changing that runtime jump force after jump input was already being processed
- Updated `JumpSkillSystem` to prime the scaled ground jump force while the player is still on the ground
- Restored the SKY_LEAP success notification in `AbilityManager` after successful ability use

---

## [2026-04-12] Temporarily Disable SKY_LEAP And Move Ground Jump Prep Earlier

### Summary
Temporarily disabled SKY_LEAP execution without removing any code or assets, and moved the passive ground-jump force prep ahead of the no-input return so the normal first jump can be tested in isolation.

### Details
- Added a temporary execution gate in `AbilityManager` so SKY_LEAP interactions stay wired but do not execute
- Updated `JumpSkillSystem` to treat SKY_LEAP as inactive while that gate is off
- Confirmed the passive jump-force prep was still below the `!jumpInputDetected` early return
- Moved the passive `MovementManager.settings.jumpForce` prep above that return so the scaled ground jump force is ready before takeoff

---

## [2026-04-12] Restore Stamina Scaling On Normal Ground Jump

### Summary
Reintroduced stamina-based jump strength for the regular ground jump by previewing the grounded jump ratio before takeoff and using that same ratio when the jump executes.

### Details
- Moved grounded stamina calculations ahead of the no-input return so the runtime ground jump force can be primed from current stamina
- Updated grounded jump force to scale directly with `jumpRatio`
- Kept stamina consumption on jump execution
- Added a concise grounded-jump debug log with:
  - `jumpLevel`
  - `staminaBefore`
  - `staminaCost`
  - `jumpRatio`
  - `finalJumpForce`

---

## [2026-04-12] Consume Stamina On Successful Ground Jump

### Summary
Moved normal-jump stamina consumption to the actual grounded jump execution point so the jump is charged once, only when it really happens.

### Details
- Kept the grounded stamina preview path for jump-force priming
- Deferred normal-jump stamina subtraction until `abilityType == GROUND`
- Avoided double-charging by skipping the earlier shared subtraction block for grounded jumps
- Added a concise grounded-jump stamina log with:
  - `staminaBefore`
  - `staminaSpent`
  - `staminaAfter`
  - `finalJumpForce`

---

## [2026-04-12] Fix Exhaustion Recovery Reset For Ground Jump Stamina

### Summary
Replaced the stuck recovery-flag check with a small per-player exhaustion timer and a one-time sprint delay reset so stamina can regenerate again after exhaustion.

### Details
- Confirmed `SprintStaminaRegenDelay.hasDelay()` was being treated like a live recovery flag inside `JumpSkillSystem`
- Added per-player fields to `JumpAbilityStateComponent` to track:
  - exhaustion start time
  - cached stamina delay stat index/value
- After the recovery wait window, clear the active sprint delay resource once so stamina regen can resume
- Added a concise recovery tick debug log with:
  - `staminaCurrent`
  - `staminaDelayActive`
  - `exhaustedRecoveryActive`
  - `timeSinceExhaustion`
  - `recoveryReady`

---

## [2026-04-12] Retag Active Stamina Recovery Log Path

### Summary
Confirmed stamina recovery already runs inside `JumpSkillSystem.tick(...)`, then retagged the active recovery log from `JumpDebug` to `StaminaRecovery` so live log searches match the real execution path.

### Details
- Verified `JumpSkillSystem` is registered in plugin setup and runs as an entity ticking system
- Verified the exhaustion/recovery evaluation and debug log sit in the active tick path before the no-input early return
- Verified the previously built jar still contained:
  - `[BeanzCore][JumpDebug] Stamina recovery tick: ...`
- Updated only the active recovery log line to:
  - `[BeanzCore][StaminaRecovery] staminaCurrent=...`
- Rebuilt the local jar after the tag change

---

[2026-04-15]
Task
Ingested official Hytale Custom UI documentation

Result
Added `docs/hytale/ui/custom-ui.md`, indexed it in the UI README, and documented the server-controlled page/HUD model

Findings
Hytale Custom UI is a server-driven, command-based system where Java sends UI commands, `.ui` assets define layout, and interaction events flow back to the server

Next Step
Use the custom page + selector model when expanding `/beanz` UI and future HUD overlays

---

[2026-04-15]
Task
Ingested official Hytale Common Styling documentation

Result
Added `docs/hytale/ui/common-styling.md`, indexed it in the UI README, and documented how to import and reuse `Common.ui`

Findings
Hytale exposes a shared `Common.ui` style/component library that can be imported into custom UI files to keep server-built pages visually consistent with the base game

Next Step
Base future `/beanz` page styling on `Common.ui` primitives before adding custom visual overrides

---
