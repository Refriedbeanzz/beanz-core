# Hytale Common UI Styling

> Source: Official Hytale Documentation  
> Cleaned for internal dev use

---

# Common UI Styling Library

Hytale provides a shared styling and component library through `Common.ui`.

- Intended for reuse in custom UI
- Helps custom screens match the core game's visual language
- Includes shared styles and reusable UI components

The in-game `/ui-gallery` command shows live examples of the available styles.

---

# Importing Common Styles

To use the shared library in a `.ui` file:

```ui
$Common = "Common.ui";
```

Then reference shared styles or components:

```ui
$Common.@TextButton { @Text = "My Button"; }
$Common.@Container { ... }
```

---

# Path Rules

`Common.ui` lives at:

`Common/UI/Custom/Common.ui`

If your custom UI file is inside a subfolder of `Common/UI/Custom/`, use relative traversal:

```ui
$Common = "../Common.ui";
```

Use the correct relative path based on the `.ui` file's folder location.

---

# Practical Use

- Prefer shared Common styles for buttons, containers, and repeated UI patterns
- Use them to keep custom pages visually aligned with the base game
- Avoid rebuilding standard styling from scratch when the shared library already provides it

---

# Beanz Core Relevance

- Use `Common.ui` as the default base for `/beanz` page styling
- Keep Beanz-specific layout and data binding separate from shared visual primitives
