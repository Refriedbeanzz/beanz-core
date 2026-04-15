# Hytale Custom UI

> Source: Official Hytale Documentation  
> Cleaned for internal dev use

---

# What Custom UI Is

Custom UI is Hytale's server-controlled framework for building custom user interfaces.

- Built through Java plugins and asset packs
- Separate from built-in client UI
- Supports:
  - interactive pages
  - HUD overlays
  - server-driven updates

Built-in client UI is not moddable.

---

# UI Categories

## Client UI (Not Moddable)

Built into the C# game client.

- main menu
- settings
- built-in HUD
- inventory and crafting
- development tools

These cannot be modified by server mods.

## In-Game UI (Moddable via Server)

Server-controlled UI that can be created and updated by plugins.

### Custom Pages

Full-screen interactive overlays used during gameplay.

- can be dismissed with `ESC`
- capture keyboard and mouse input
- support loading states
- useful for:
  - shops
  - dialogs
  - menus
  - configuration screens

### Custom HUDs

Persistent display-only overlays drawn over gameplay.

- no user interaction
- always visible during gameplay
- lightweight and non-intrusive
- useful for:
  - quest trackers
  - status displays
  - custom server info

---

# Architecture Overview

Custom UI uses a command-based server/client architecture.

Flow:

1. Java code builds UI commands with `UICommandBuilder`
2. Commands are sent to the client
3. Client parses `.ui` assets and builds the element tree
4. User interacts with the UI
5. Events are sent back to Java
6. Server code processes the event and sends updates back

Key server-side pieces:

- `InteractiveCustomUIPage`
- `UICommandBuilder`
- `handleDataEvent()`

---

# Key Principles

## Declarative

UI is described through commands, not direct object creation.

Examples:

- append an element
- set a property
- clear children

## Asset-Driven

UI structure lives in `.ui` markup assets instead of hardcoded Java layout.

Benefits:

- reusable templates
- designer-friendly iteration
- consistent visual structure

## Event-Driven

User input returns to server code through registered events.

- button clicks
- form submission
- other UI interactions

## Selector-Based

Specific UI elements are targeted by selectors.

Examples:

- `#MyButton`
- `#List[0]`
- `#List[0] #Title`
- `#Label.TextColor`

---

# Practical Modding Use

Custom UI is suited for:

- shop interfaces
- quest dialogs
- admin panels
- settings menus
- quest HUDs
- status indicators

Localization is supported through the game's translation system.

---

# Beanz Core Relevance

- Use custom pages for interactive server menus
- Use custom HUDs for lightweight jump/skill/status overlays
- Keep layout in `.ui` assets and use Java only for data binding and event handling
