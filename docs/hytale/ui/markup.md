# Hytale UI Markup

> Source: Official Hytale Documentation  
> Cleaned for internal dev use

---

# Markup Fundamentals

UI markup is built from element trees inside `.ui` documents.

- an element is the basic building block
- documents can contain multiple root elements
- properties define behavior, appearance, and layout
- child elements create the UI tree

Basic example:

```ui
Group {
    Anchor: (Full: 10);
}
```

Named element example:

```ui
Label #MyLabel {
  Style: (FontSize: 16);
  Text: "Hi! I am text.";
}
```

Named elements can be targeted from game code and selectors.

---

# Documents

A `.ui` file contains one or more trees of elements.

- multiple root elements are allowed
- each subtree can contain its own scoped named expressions
- referenced assets and relative paths resolve from the file where they are declared

---

# Named Expressions

Named expressions are declared with `@` and are scoped to their subtree.

They must appear at the top of the block where they are declared.

```ui
@Title = "Hytale";
@ExtraSpacing = 5;
Label {
  Text: @Title;
  Style: (LetterSpacing: 2 + @ExtraSpacing);
}
```

Use them for:

- reusable values
- reusable styles
- reusable template fragments

## Spread Operator

`...` reuses a named object while overriding fields.

```ui
@MyBaseStyle = LabelStyle(FontSize: 24, LetterSpacing: 2);
Label {
  Style: (...@MyBaseStyle, FontSize: 36);
}
```

Multiple named expressions can be layered together.

---

# Document References

Other UI documents can be referenced with `$`.

```ui
$Common = "../Common.ui";
TextButton {
  Style: $Common.@DefaultButtonStyle;
}
```

This is how shared documents like `Common.ui` expose reusable styles and components.

---

# Templates

Reusable UI fragments can be declared as named expressions and instantiated multiple times.

```ui
@Row = Group {
  Anchor: (Height: 50);
  Label #Label { Anchor: (Left: 0, Width: 100); Text: @LabelText; }
  Group #Content { Anchor: (Left: 100); }
};
```

When instantiating a template, you can:

- override local named expressions
- inject additional children into template nodes

Rule:

- local named expressions must be declared at the top of the block before properties or children

---

# Property Types

Common markup value types include:

- `Boolean`
- `Int`
- `Float`, `Double`, `Decimal`
- `String`
- `Char`
- `Color`
- objects
- arrays

Translations can be referenced anywhere a string is accepted:

```ui
Label {
  Text: %ui.general.cancel;
}
```

---

# Colors

Supported color literals:

- `#rrggbb`
- `#rrggbb(a.a)`
- `#rrggbbaa`

Preferred format:

- `#rrggbb(a.a)` for readability

Example:

```ui
Group {
  Background: #000000(0.3);
}
```

---

# Fonts

Font names can be referenced directly by string ID.

```ui
Label {
  Text: "Hi";
  Style: (FontName: "Secondary");
}
```

Available documented names:

- `Default`
- `Secondary`
- `Mono`

---

# Paths

UI asset paths are relative to the file where they are declared.

Examples:

- `"MyButton.png"`
- `"../MyButton.png"`
- `"../../MyButton.png"`

Use relative traversal when referencing assets from nested UI folders.

---

# Objects

Object properties use grouped field syntax.

```ui
Group {
  Anchor: (
    Height: 10,
    Width: 20
  );
}
```

This is used throughout markup for style objects, anchors, and other compound values.

---

# Tooling Note

There is an official Visual Studio Code extension for `.ui` syntax highlighting.

---

# Beanz Core Relevance

- Use named expressions and templates to keep `/beanz` UI reusable
- Use `$Common` references for shared styles instead of duplicating visual config
- Keep UI assets modular so Java code only binds data and events
