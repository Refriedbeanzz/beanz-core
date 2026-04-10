# Hytale Command Argument Types

> Source: Official Hytale Modding Docs  
> Cleaned for internal dev use

---

# Argument Types Overview

- Hytale commands use **typed arguments**
- Inputs are parsed and validated before execution
- Invalid inputs prevent command execution entirely

---

# Core Behavior

- Arguments are not raw strings
- Each parameter has a defined type
- The command system handles:
  - parsing
  - validation
  - suggestions and auto-complete

---

# Built-in Types

Common built-in argument types include:

- player / entity
- integer
- string
- boolean
- position / vector

These types let commands receive already-validated values instead of manually parsing text.

---

# Validation Model

- Commands only run if all arguments are valid
- Invalid input is rejected before command execution
- This removes the need for manual parsing in command handlers
- It encourages strict command structure

---

# Suggestions / Auto-complete

- Argument types define valid inputs
- The command system uses those definitions for suggestions and tab completion
- This improves UX and reduces user error

---

# Custom Argument Types

- Developers can define custom argument types
- This is useful for structured Beanz systems such as:
  - skills
  - abilities
  - enums

---

# Usage in Beanz Core

- `/beanz` should use typed arguments
- Avoid string parsing entirely
- Prefer command structure like:
  - `/beanz jump setlevel <player> <level>`
  - `/beanz jump addxp <player> <amount>`

## Recommended Direction

- Use player-typed arguments for player targets
- Use integer-typed arguments for level and XP values
- Add custom argument types later for skill ids, ability ids, or constrained enums

---

# Key Takeaway

- Hytale command input is strongly typed, validated first, and structured for auto-complete
- Clean command design should lean on argument types instead of manual string splitting
