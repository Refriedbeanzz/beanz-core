# AI Index (Start Here)

## Project Overview
Beanz Core is a Hytale mod focused on:
- skill-based progression
- custom abilities (starting with jump system)
- interaction-driven mechanics

---

## Current Focus
- Double jump system
- Input detection issues (airborne press not registering correctly)

---

## Read These First

### Core Systems
- /docs/hytale/interactions/interaction-reference.md

### Movement
- /docs/hytale/movement/double-jump.md

### Current Work
- /docs/dev/tasks.md
- /docs/dev/changes.md

### Known Issues
- /docs/dev/bugs.md

---

## Required Workflow

Before making changes:
1. Read relevant docs under /docs/hytale/
2. Review current state in /docs/dev/changes.md and /docs/dev/tasks.md
3. Propose a plan before implementing

After making changes:
1. Update documentation (see rules below)
2. Ensure documentation reflects the current system state

A task is NOT complete until documentation is updated.

---

## How to Work in This Repo

- Follow patterns defined in interaction docs
- Do not invent new systems unless necessary
- Keep logic aligned with interaction-based design

---

## Notes

- Double jump depends on detecting fresh input (not held input)
- Airborne state handling is currently unreliable

---

## Mandatory Documentation Rule

After any significant change, Codex MUST:

1. Update /docs/dev/changes.md

2. If a non-obvious behavior was discovered:
   - Update /docs/dev/discoveries.md

3. If system behavior or design changed:
   - Update the relevant file under /docs/hytale/

---

## Definition of "Significant Change"

- New feature implementation
- Bug fix or debugging attempt
- Change in system behavior
- Discovery of engine behavior

---

## Required Format for /docs/dev/changes.md

All entries MUST follow this structure:

## [DATE]

### Task
<what was attempted>

### Result
<what happened>

### Findings
<important insight>

### Suspected Issue
<if applicable>

### Next Step
<what should happen next>

---

## Enforcement Rule

Failure to update documentation means the task is incomplete.

Documentation must be:
- concise
- structured
- non-duplicative