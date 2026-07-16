# Hytale UI Documentation

> Source: Official Hytale Documentation by Hypixel Studios Canada Inc.  
> https://github.com/HytaleModding/site/tree/main/content/docs/en/official-documentation/custom-ui

---

## Files

| File | Contents |
|---|---|
| [custom-ui.md](custom-ui.md) | What Custom UI is, architecture overview, key principles (declarative, asset-driven, event-driven, selector-based) |
| [markup.md](markup.md) | `.ui` file syntax — elements, named expressions, spread operator, document references, templates, property types, colors, fonts, paths |
| [layout.md](layout.md) | Anchor, Padding, LayoutMode (all modes), FlexWeight, Visibility |
| [common-styling.md](common-styling.md) | `Common.ui` shared style library, how to import it, path rules |
| [type-documentation.md](type-documentation.md) | Full list of available UI elements, property types, and enums — with Beanz Core usage notes |

## Placeholder Files

The following files were created as stubs — the official docs do not have content for them yet:

- `menu-reference.md`
- `ui-loading.md`
- `ui-paths.md`
- `ui-patterns.md`

## Quick Reference

**Discover available styles:**  
Run `/ui-gallery` in-game to see live examples of every `Common.ui` component.

**VS Code extension for `.ui` syntax highlighting:**  
https://marketplace.visualstudio.com/items?itemName=HypixelStudiosCanadaInc.vscode-hytaleui

**Key Java classes:**
- `UICommandBuilder` — sends `append()`, `set()`, `clear()` commands to the client
- `BasicCustomUIPage` / `InteractiveCustomUIPage` — base classes for custom pages
- `handleDataEvent()` — receives events from user interaction
