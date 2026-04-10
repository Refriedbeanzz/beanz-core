## Documentation Update + Git Push Rules

After every meaningful development change, you MUST:

1. Update documentation:

Append to:
- /docs/dev/changes.md
- /docs/dev/discoveries.md (ONLY for non-obvious findings)

2. Use this format:

### changes.md entry format
## [YYYY-MM-DD] <Short Title>

### Summary
<1-2 sentence summary>

### Details
- bullet points of what was done
- include decisions, debugging steps, or feature work

---

### discoveries.md entry format
## [YYYY-MM-DD] <Short Title>

### Finding
<clear statement of discovery>

### Details
- bullet points explaining evidence or behavior
- only include non-obvious findings

---

3. Do NOT:
- rewrite or remove existing entries
- include repetitive or low-value steps
- include explanations outside the format

4. After updating docs, you MUST commit and push:

git add docs/dev/changes.md docs/dev/discoveries.md
git commit -m "docs: update changes and discoveries"
git push

5. This is REQUIRED after:
- implementing features
- debugging sessions
- architectural decisions
- confirming engine behavior

6. If no meaningful change occurred:
- do NOT update docs