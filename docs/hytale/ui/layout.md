# Hytale UI Layout

> Source: Official Hytale Documentation  
> Cleaned for internal dev use

---

# Layout Fundamentals

The layout system controls how UI elements are sized and positioned.

Every element has:

- a container rectangle from its parent
- an anchor that positions or sizes it
- padding that affects child layout
- a `LayoutMode` if it is arranging children

Core flow:

`Parent container -> Anchor -> Padding -> Child content/layout`

---

# Anchor

`Anchor` controls how an element fits inside its parent container.

## Fixed Size

```ui
Button {
    Anchor: (Width: 200, Height: 40);
}
```

Creates a fixed `200 x 40` element.

## Positioning

```ui
Label {
    Anchor: (Top: 10, Left: 20, Width: 100, Height: 30);
}
```

Uses edge offsets plus explicit size.

## Edge Anchoring

```ui
Button {
    Anchor: (Bottom: 10, Right: 10, Width: 100, Height: 30);
}
```

Pins the element to container edges.

## Stretching

```ui
Group {
    Anchor: (Top: 0, Bottom: 0, Left: 0, Right: 0);
}
```

Shorthand:

```ui
Group {
    Anchor: (Full: 0);
}
```

## Mixed Anchoring

```ui
Panel {
    Anchor: (Top: 10, Bottom: 10, Left: 20, Width: 300);
}
```

Supports fixed size on one axis and stretch on the other.

---

# Padding

`Padding` adds inner spacing and changes where children can render.

## Uniform Padding

```ui
Group {
    Padding: (Full: 20);
}
```

## Directional Padding

```ui
Group {
    Padding: (Top: 10, Bottom: 20, Left: 15, Right: 15);
}
```

## Shorthand

```ui
Group {
    Padding: (Horizontal: 20, Vertical: 10);
}
```

## Effect On Children

If a child fills the parent, padding still reduces the actual content area available to that child.

---

# LayoutMode

`LayoutMode` determines how a container arranges its children.

## Stack Modes

- `Top`
  - vertical stack from top
- `Bottom`
  - vertical stack aligned to bottom
- `Left`
  - horizontal stack from left
- `Right`
  - horizontal stack aligned to right

Spacing comes from child anchor offsets such as `Bottom` or `Right`.

## Centering Modes

- `Center`
  - centers children horizontally
- `Middle`
  - centers children vertically
- `CenterMiddle`
  - horizontal stack centered both ways
- `MiddleCenter`
  - vertical stack centered both ways

## Absolute Layout

- `Full`
  - children use their own anchors directly
  - useful for absolute positioning

## Scrolling Layouts

- `TopScrolling`
- `BottomScrolling`
- `LeftScrolling`
- `RightScrolling`

These behave like their base layout modes but add scrollbars when content exceeds bounds.

## Wrapping Layout

- `LeftCenterWrap`
  - flows left to right
  - wraps to new rows when space runs out
  - centers each row horizontally

---

# FlexWeight

`FlexWeight` distributes remaining space between children.

```ui
Group {
    LayoutMode: Left;
    Anchor: (Width: 600);
    Group { FlexWeight: 1; }
    Group { FlexWeight: 2; }
    Group { FlexWeight: 1; }
}
```

In that case the remaining width is split proportionally `1 : 2 : 1`.

Use this when part of a row or column should expand while others remain fixed.

---

# Visibility

```ui
Button #HiddenButton {
    Visible: false;
}
```

When hidden:

- the element is not displayed
- its children are not displayed
- it does not take up layout space

---

# Practical Use

- Use `Anchor` for element placement and sizing rules
- Use `Padding` to control inner spacing cleanly
- Use `LayoutMode` to choose stack, center, scroll, or wrap behavior
- Use `FlexWeight` to fill remaining space without hardcoding every width

---

# Beanz Core Relevance

- Use stack and centering modes for `/beanz` menu composition
- Use `Full` only when exact manual positioning is necessary
- Prefer padding and layout modes over fragile absolute offsets for scalable UI screens
