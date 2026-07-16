# Common Styling

> Source: Official Hytale Documentation — Hypixel Studios Canada Inc.  
> https://github.com/HytaleModding/site/tree/main/content/docs/en/official-documentation/custom-ui

---

## Common UI Styling Library

Hytale provides a shared UI component and style library through `Common.ui`. It's intended to give custom UIs a visual language consistent with the core game.

You can see live examples of all available styles and components by running `/ui-gallery` in-game.

---

## Importing

To use these styles in your `.ui` file:

```ui
$Common = "Common.ui";

// Then reference styles and components:
$Common.@VTextButton { @Text = "My Button"; }
$Common.@Container { ... }
```

---

## Path Rules

`Common.ui` lives at:

```
Common/UI/Custom/Common.ui
```

If your custom UI document is inside a subfolder of `Common/UI/Custom/`, reference it via relative path traversal:

```ui
$Common = "../Common.ui";
```

Adjust the number of `../` hops based on how deeply nested your `.ui` file is. See [markup.md](markup.md#path) for full path rules.
