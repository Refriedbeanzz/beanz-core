# Double Jump System

## Goal
Allow player to jump once normally, then perform a second jump while airborne.

---

## Requirements

- Player must be airborne
- Player must press jump again
- Only one extra jump allowed
- Reset when player lands

---

## Core Logic

1. Player presses jump
2. If grounded → normal jump
3. If airborne AND has not double jumped → perform second jump
4. Mark double jump as used
5. Reset when player touches ground

---

## Key Checks

- airborne = true
- doubleJumpUsed = false

---

## Future Implementation (Hytale)

Will likely use:

- Condition (airborne check)
- Interaction chain for second jump
- Cooldown or state tracking for usage

## Current Suspected Failure Point

The main problem is probably not jump force.  
The main problem is detecting a **new jump press while airborne**.

Observed behavior to watch for:
- first jump sometimes becomes double jump immediately
- airborne jump press sometimes is not detected
- system may be reading jump as held instead of pressed again

## What Must Be True

Double jump should only trigger when:
- first jump already happened
- player is airborne
- a new jump input is received
- doubleJumpUsed is still false

## Required State Tracking

We need to track per-player:

- hasJumped (bool)
- isAirborne (bool)
- doubleJumpUsed (bool)

---

## State Flow

On first jump:
- hasJumped = true
- isAirborne = true

While in air:
- listen for NEW jump input

On second jump:
- if isAirborne AND doubleJumpUsed = false
  → apply jump force
  → doubleJumpUsed = true

On landing:
- reset all:
  - hasJumped = false
  - isAirborne = false
  - doubleJumpUsed = false