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
