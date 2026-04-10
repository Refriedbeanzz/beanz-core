# Ability System Behavior (Observed)

## Core Finding

Hytale ability execution is context-sensitive based on held items.

---

## Hand Priority Behavior

### Main Hand
- Takes priority for ability execution
- If main-hand item has abilities, they override offhand abilities

### Offhand
- Can execute abilities ONLY when main hand does not override
- Requires item to be:
  - "Utility.Usable": true
  - "Utility.Compatible": true

---

## Confirmed Behavior

### Case 1: Offhand Only
- Offhand item present
- Main hand empty or non-ability item
- Result: Ability3 executes successfully

### Case 2: Main Hand + Offhand (Both Have Abilities)
- Main hand item has abilities
- Offhand item has abilities
- Result: Main hand overrides -> offhand ability does NOT fire

---

## Design Implication

Abilities are NOT merged across hands.

System behaves as:
- Active ability source = dominant hand context (main hand)

---

## Current Working Model

- Main Hand -> combat abilities (weapons)
- Offhand -> utility/mobility abilities

---

## Open Questions

- Can offhand be forced to override main hand?
- Can abilities be merged manually via code?
- Is there a hidden priority flag beyond Utility.Compatible?

---

## Status

- Offhand abilities functional
- Priority behavior confirmed
