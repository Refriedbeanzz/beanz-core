## Project Context + Working Preferences

### Repository
Main project repository:
https://github.com/Refriedbeanzz/beanz-core

Docs directory:
https://github.com/Refriedbeanzz/beanz-core/tree/main/docs

---

### Conversation Preferences (CRITICAL)

These rules define how responses should be structured for this project.

- Keep responses **short and to the point**
- Avoid long explanations unless explicitly requested
- No unnecessary fluff or filler

---

### Troubleshooting Workflow

- Provide **ONLY ONE step at a time**
- Wait for user to test and respond before continuing
- Do NOT give multiple possible fixes at once
- Each step must be:
  - Clear
  - Actionable
  - Minimal

---

### Iteration Style

- User will paste logs/results after each step
- Assistant should:
  - Analyze result
  - Provide next single step
- Continue iteratively until resolved

---

### Development Philosophy

- Prefer **working proof over perfect system**
- Build **small → test → expand**
- Do NOT over-engineer early systems
- Separate systems cleanly:
  - Skills = progression
  - Abilities = execution

---

## Current Focus
- Double jump system
- Ability system behavior confirmed (main-hand priority / offhand utility model)
- Designing mobility ability framework (Sky Leap)

---

### Current Critical Problem Area

- Ability input (Ability1/2/3) is not triggering reliably
- Hytale uses:
  - interaction system (item-based)
  - NOT global key events
- Input must be tied to valid interaction mapping

---

### Key Known Constraints (IMPORTANT)

- Server does NOT receive raw key input
- Ability inputs only fire if:
  - a valid interaction exists
  - the held item supports it
- Packet watchers do NOT work without interaction existing first

---

### Preferred Debugging Approach

- Always isolate the smallest test case
- Example:
  - Ability3 → simple log (no systems attached)
- Only expand once proof is confirmed

---

### Documentation System Rules

- changes.md = development steps, decisions, debugging
- discoveries.md = confirmed non-obvious engine behavior
- Never rewrite history, only append
- Keep entries concise and structured

---

### Codex Integration

- Codex updates docs after meaningful changes
- Codex commits and pushes:
  - changes.md
  - discoveries.md

---

### Important Note for Future Sessions

Do NOT assume previous context.

Always:
1. Review:
   - /docs/AI-INDEX.md
   - /docs/dev/changes.md
   - /docs/dev/discoveries.md
2. Then continue development

This file is the source of truth for:
- workflow
- constraints
- system design direction
