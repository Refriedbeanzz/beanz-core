# Markup

> Source: Official Hytale Documentation — Hypixel Studios Canada Inc.  
> https://github.com/HytaleModding/site/tree/main/content/docs/en/official-documentation/custom-ui

---

## Markup

An element is the basic building block of a user interface. There are various types of elements with different properties.

```ui
// Basic declaration of an element
// attached on all sides of its parent with 10 pixels of margin
Group { Anchor: (Left: 10, Top: 10, Right: 10, Bottom: 10); }

Group { Anchor: (Full: 10); } // More concise version

// Declaration of a Label with a name
// (can be used to access the element from game code)
Label #MyLabel {
  Style: LabelStyle(FontSize: 16); // or just Style: (FontSize: 16), type can be inferred
  Text: "Hi! I am text.";
}

// Declaration of a Group containing 2 children
// FlexWeight distributes leftover space after explicit widths/heights
Group {
  LayoutMode: Left;
  Label { Text: "Child 1"; FlexWeight: 2; }
  Label { Text: "Child 2"; FlexWeight: 1; }
}
```

---

## Documents

A UI document (`.ui` file) contains trees of elements. There can be multiple root elements.

---

## Named Expressions

Named expressions can be declared at any level of a document (including at the root) and are scoped to that subtree. They must be declared at the top of the block.

```ui
@Title = "Hytale";
@ExtraSpacing = 5;

Label {
  Text: @Title;
  Style: (LetterSpacing: 2 + @ExtraSpacing);
}
```

### Spread Operator

Use `...` to reuse a named expression while overriding some of its fields:

```ui
@MyBaseStyle = LabelStyle(FontSize: 24, LetterSpacing: 2);

Label {
  Style: (...@MyBaseStyle, FontSize: 36);
}
```

Multiple named expressions can be layered:

```ui
@TitleStyle = LabelStyle(FontSize: 24, HorizontalAlignment: Center);
@SpacedTextStyle = LabelStyle(LetterSpacing: 2);

Label {
  Style: (...@TitleStyle, ...@SpacedTextStyle);
}
```

### Document References

A document can reference another document and access its named expressions:

```ui
// Document references are defined with $ prefix
$Common = "../Common.ui";

TextButton {
  Style: $Common.@DefaultButtonStyle;
}
```

---

## Templates

Declare a reusable UI fragment as a named expression and instantiate it multiple times with customization:

```ui
// Template declaration
@Row = Group {
  Anchor: (Height: 50);
  Label #Label { Anchor: (Left: 0, Width: 100); Text: @LabelText; }
  Group #Content { Anchor: (Left: 100); }
};

// Using the template twice
Group #Rows {
  LayoutMode: TopScrolling;

  @Row #MyFirstRow {
    @LabelText = "First row";
    #Content { TextInput {} }
  }

  @Row #MySecondRow {
    @LabelText = "Second row";
  }
}
```

You can override local named expressions and insert additional children at any point in the template tree. Local named expressions must be defined at the very top of the block, before properties and child elements.

---

## Property Types

### Basic Types

- **Boolean** — `Visible: false;`
- **Int** — `Height: 20;`
- **Float, Double, Decimal** — `Min: 0.2;`
- **String** — `Text: "Hi!";`
- **Char** — `PasswordChar: "*";` *(cannot contain more than one character)*
- **Color** — `Background: #ffffff;`
- **Objects** — `Style: (Background: #ffffff)`
- **Array** — `TextSpans: [(Text: "Hi", IsBold: true)]`

### Translations

Translation keys can be referenced anywhere a string is accepted:

```ui
Label {
  Text: %ui.general.cancel;
}
```

### Colors

- `#rrggbb` — 6-digit hex, fully opaque
- `#rrggbb(a.a)` — 6-digit hex with alpha between 0 and 1 *(preferred for readability)*
- `#rrggbbaa` — 8-digit hex with alpha

```ui
Group {
  Background: #000000(0.3);
}
```

### Font Name

Font names are referenced by their string ID. Strings are automatically converted to `UIFontName`:

```ui
Label {
  Text: "Hi";
  Style: (FontName: "Secondary");
}
```

**Available Font Names:**

| Name | Use |
|---|---|
| `Default` | Standard text, always used unless overridden |
| `Secondary` | Headlines or standout elements |
| `Mono` | Development only (profiling, error overlays) |

### Path

Paths reference other UI assets. They are always relative to the file where they are declared. Strings are automatically converted to `UIPath`.

```ui
Path: "Test.png"
```

Examples:

| UIPath | .ui File | Resulting Path |
|---|---|---|
| `MyButton.png` | `Menu/MyAwesomeMenu.ui` | `Menu/MyButton.png` |
| `../MyButton.png` | `Menu/MyAwesomeMenu.ui` | `MyButton.png` |
| `../../MyButton.png` | `Menu/Popup/Templates/MyAwesomeMenu.ui` | `Menu/MyButton.png` |

### Objects

Objects contain a set of properties:

```ui
Group {
  Anchor: (
    Height: 10,
    Width: 20
  );
}
```

---

## Visual Studio Code Extension

There is an official extension for Visual Studio Code which adds syntax highlighting for `.ui` files:  
https://marketplace.visualstudio.com/items?itemName=HypixelStudiosCanadaInc.vscode-hytaleui
