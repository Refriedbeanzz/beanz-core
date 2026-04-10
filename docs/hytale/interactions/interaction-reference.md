# Hytale Interaction System Reference

> Source: Official Hytale Modding Docs  
> Cleaned for internal dev use

---

# Core Concept

All interactions inherit from:

`Interaction`

---

# Interaction System Fundamentals

- Hytale uses an **interaction-driven** system, not traditional event listeners
- Player input is resolved through **interaction evaluation**, not direct key events
- Input alone does not guarantee an action will execute

## Interaction Flow

`Input -> Condition Evaluation -> Action Execution`

- Inputs are interpreted through interaction definitions
- Conditions determine whether an interaction triggers
- Actions execute only when conditions pass

## Conditions

- Interactions rely on player state at evaluation time
- Relevant state includes:
  - grounded or airborne state
  - movement state
  - current interaction context
- Interaction success depends on state when evaluation happens, not just when input begins

## Input Handling Insight

- Input is not `press = action`
- Input must pass interaction conditions
- Held vs fresh input may resolve differently depending on the interaction system

## Conflict / Override Behavior

- Multiple interactions may compete
- Conditions and rules determine which interaction executes
- Some interactions block, interrupt, or override default behavior

---

# Base Interaction Fields

| Field | Type | Required | Notes |
|------|------|--------|------|
| ViewDistance | Double | No | Default: 96.0 |
| Effects | InteractionEffects | No | Sounds, particles, animations |
| HorizontalSpeedMultiplier | Float | No | Default: 1.0 |
| RunTime | Float | No | Minimum execution time |
| CancelOnItemChange | Boolean | No | Default: true |
| Rules | InteractionRules | Yes | Execution constraints |
| Settings | Map<GameMode, InteractionSettings> | No | Per gamemode settings |
| Camera | InteractionCameraSettings | No | Camera keyframes |

---

# Game Modes

- Creative  
- Adventure  

---

# InteractionEffects

## Fields

| Field | Type | Notes |
|------|------|------|
| Particles | ModelParticle[] | 3rd person |
| FirstPersonParticles | ModelParticle[] | 1st person |
| WorldSoundEventId | SoundEvent | World sound |
| LocalSoundEventId | SoundEvent | Player-only sound |
| Trails | ModelTrail[] | Trails |
| WaitForAnimationToFinish | Boolean | Default: false |
| ItemPlayerAnimationsId | Asset | Animation set |
| ItemAnimationId | String | Animation ID |
| ClearAnimationOnFinish | Boolean | Default: false |
| ClearSoundEventOnFinish | Boolean | Default: false |
| CameraEffect | Asset | Camera effect |
| MovementEffects | MovementEffects | Movement changes |
| StartDelay | Float | Delay before execution |

---

# InteractionRules

## Fields

| Field | Type | Notes |
|------|------|------|
| BlockedBy | InteractionType[] | Cannot run if active |
| Blocking | InteractionType[] | Prevents others |
| InterruptedBy | InteractionType[] | Cancels this |
| Interrupting | InteractionType[] | Cancels others |

## Notes

- Rule checks are part of interaction evaluation
- Competing interactions are resolved through conditions, blocking, and interruption rules
- Root interactions can prevent or cancel other chains before default behavior continues

---

# Interaction Types

Primary, Secondary, Ability1, Ability2, Ability3, Use, Pick, Pickup,
CollisionEnter, CollisionLeave, Collision, EntityStatEffect,
SwapTo, SwapFrom, Death, Wielding, ProjectileSpawn,
ProjectileHit, ProjectileMiss, ProjectileBounce,
Held, HeldOffhand, Equipped, Dodge, GameModeSwap

---

# SimpleInteraction

Used for chaining logic.

## Fields

| Field | Type | Notes |
|------|------|------|
| Next | Interaction | On success |
| Failed | Interaction | On failure |

---

# Flow Control Interactions

## Condition

Fails if conditions not met.

### Fields
- RequiredGameMode
- Jumping
- Swimming
- Crouching
- Running
- Flying

### Notes
- Conditions are checked during interaction evaluation
- State changes between input and evaluation can cause the interaction to fail

---

## FirstClick

Splits logic between tap vs hold.

| Field | Notes |
|------|------|
| Click | Quick press |
| Hold | Long press |

### Notes
- Fresh input and held input can route to different branches
- This behavior is part of interaction evaluation, not a separate raw key listener

---

## Interrupt

Cancels running interactions.

| Field | Notes |
|------|------|
| Entity | User / Owner / Target |
| InterruptTypes | Types to cancel |

---

## Parallel

Runs multiple interactions at once.

| Field | Notes |
|------|------|
| Interactions | RootInteractions[] |

---

## Repeat

Loops interaction.

| Field | Notes |
|------|------|
| ForkInteractions | RootInteraction |
| Repeat | Count (-1 = infinite) |

---

## Selector

Finds entities/blocks and forks interactions.

| Field | Notes |
|------|------|
| Selector | Target finder |
| HitEntity | Interaction |
| HitBlock | Interaction |
| FailOn | Entity / Block / Both |

---

## Serial

Runs interactions in order.

| Field | Notes |
|------|------|
| Interactions | Interaction[] |

---

# Cooldowns

## Key Concepts

- Cooldowns tied to **RootInteraction**
- Default: **0.35s on input**
- Can use:
  - Shared cooldown IDs
  - Charges
  - Custom timing

---

## Cooldown Interactions

### CooldownCondition
- Fails if cooldown active

### IncrementCooldown
- Modify cooldown

### ResetCooldown
- Reset + refill charges

### TriggerCooldown
- Consume charge

---

# Combo System (Chaining)

## Chaining

Cycles through actions.

| Field | Notes |
|------|------|
| ChainId | Shared combo |
| Next | Interaction[] |
| Flags | Conditional overrides |

---

## CancelChain
- Reset combo

## ChainFlag
- Set combo state

---

# Charging System

## Charging

Hold input to charge.

| Field | Notes |
|------|------|
| AllowIndefiniteHold | Hold forever |
| Next | Map<time, interaction> |
| CancelOnOtherClick | Cancel condition |
| FailOnDamage | Cancel on hit |

---

# Wielding (Blocking System)

Specialized Charging interaction.

## Features
- Damage reduction
- Knockback modification
- Angle-based blocking

---

# Block Interactions

## Examples

- BlockCondition
- BreakBlock
- ChangeBlock
- ChangeState
- PlaceBlock
- DestroyBlock

---

# Item Interactions

## Examples

- AddItem *(buggy)*
- ModifyInventory *(recommended)*
- EquipItem
- PickupItem

---

# Entity Interactions

## DamageEntity

Core combat interaction.

### Features
- Directional damage
- Body part targeting
- Effects + knockback
- Failure/blocked handling

---

# Stats System

## Interactions

- ChangeStat
- ChangeStatWithModifier
- StatsCondition

---

# Entity Effects

## Interactions

- ApplyEffect
- ClearEntityEffect
- EffectCondition

---

# Farming

## Interactions

- ChangeFarmingStage
- FertilizeSoil
- HarvestCrop
- UseWateringCan

---

# Key Takeaways (IMPORTANT)

- Everything is **interaction chains**
- Flow control = your "programming language"
- Charging + Chaining = advanced combat systems
- Cooldowns are **shared state**
- Selector = AOE / targeting system
- Input resolution depends on interaction definitions, conditions, and timing

---

# Dev Notes

- `AddItem` is buggy -> use `ModifyInventory`
- Charging can break cooldown logic if not handled
- Interaction chains can fork heavily -> watch performance

# Example: Simple Interaction Chain

```json
{
  "Type": "Simple",
  "Next": "MyNextInteraction",
  "Failed": "MyFailInteraction"
}
```

---

## Ability Execution Context (Important)

Ability triggers are NOT global.

They depend on the item context:

- Main hand abilities override offhand abilities
- Offhand abilities only execute when not overridden

This aligns with how the interaction system resolves actions based on the held item context.

Design systems must account for:
-> single active ability source at a time
