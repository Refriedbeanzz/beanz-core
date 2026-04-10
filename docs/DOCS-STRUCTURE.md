# Docs Structure

## Purpose
Defines how documentation is organized in this repository.

---

## Folder Layout

/docs
  /hytale
    /interactions
    /ui
    /skills
    /systems
  /dev
  /handoffs
  /templates

---

## Folder Rules

### /docs/hytale
Reference material about Hytale systems, engine behavior, modding discoveries, and external docs rewritten for internal use.

### /docs/hytale/interactions
Interaction-system reference docs, patterns, and examples.

### /docs/hytale/ui
UI paths, UI loading rules, document structure, and menu behavior.

### /docs/hytale/skills
Skill-specific design notes and implementation references.

### /docs/hytale/systems
Higher-level gameplay systems built on top of Hytale primitives.

### /docs/dev
Project-specific notes for active development.

Files may include:
- discoveries.md
- bugs.md
- systems.md
- tasks.md

### /docs/handoffs
Conversation summaries and current-state handoff documents.

### /docs/templates
Reusable markdown templates for documenting systems, bugs, and discoveries.

---

## Naming Conventions

- Use lowercase
- Use hyphen-separated file names
- Prefer descriptive names
- Example: `interaction-reference.md`

---

## File Creation Rules

- Create a new file when the topic is large, reusable, or likely to grow
- Append to an existing file when adding another note of the same type
- Do not create one-off files for tiny notes

---

## Priority

When deciding where to place a doc:
1. Put Hytale engine knowledge under `/docs/hytale/...`
2. Put Beanz Core implementation knowledge under `/docs/dev/...`
3. Put reusable templates under `/docs/templates/...`
4. Put transfer summaries under `/docs/handoffs/...`