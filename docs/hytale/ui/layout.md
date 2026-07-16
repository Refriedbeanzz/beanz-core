# Layout

> Source: Official Hytale Documentation — Hypixel Studios Canada Inc.  
> https://github.com/HytaleModding/site/tree/main/content/docs/en/official-documentation/custom-ui

---

## Layout Fundamentals

The layout system determines how UI elements are positioned and sized on screen.

Every UI element has:

- **Container Rectangle** — the space allocated by the parent
- **Anchor** — how the element positions/sizes itself within the container
- **Padding** — inner spacing that affects child layout
- **LayoutMode** — how the element arranges its children (if it's a container)

```
┌────────────────────────────────────────────────┐
│  Container Rectangle (from parent)             │
│                                                │
│  ┌──────────────────────────────┐              │
│  │ Anchored Rectangle           │              │
│  │                              │              │
│  │ ┌────────────────────────┐   │              │
│  │ │ Padding                │   │              │
│  │ │ ┌────────────────────┐ │   │              │
│  │ │ │ Content Area       │ │   │              │
│  │ │ └────────────────────┘ │   │              │
│  │ └────────────────────────┘   │              │
│  └──────────────────────────────┘              │
└────────────────────────────────────────────────┘
```

---

## Anchor

`Anchor` controls how an element positions and sizes itself within its container rectangle.

### Fixed Size

```ui
Button {
    Anchor: (Width: 200, Height: 40);
}
```

Creates a 200×40 pixel button.

### Positioning

```ui
Label {
    Anchor: (Top: 10, Left: 20, Width: 100, Height: 30);
}
```

- **Top**: 10 pixels from container's top edge
- **Left**: 20 pixels from container's left edge
- **Width**: 100 pixels wide
- **Height**: 30 pixels tall

### Anchoring to Edges

```ui
Button {
    Anchor: (Bottom: 10, Right: 10, Width: 100, Height: 30);
}
```

Anchors the button to the bottom-right corner, 10 pixels from each edge.

### Stretching

```ui
Group {
    Anchor: (Top: 0, Bottom: 0, Left: 0, Right: 0);
}

// Shorthand:
Group {
    Anchor: (Full: 0);
}
```

Stretches to fill the entire container.

### Mixed Anchoring

```ui
Panel {
    Anchor: (Top: 10, Bottom: 10, Left: 20, Width: 300);
}
```

- Fixed width of 300 pixels
- Stretches vertically between top and bottom edges
- 10 pixels from top and bottom
- 20 pixels from left

---

## Padding

`Padding` creates inner spacing, affecting where children are positioned.

### Uniform Padding

```ui
Group {
    Padding: (Full: 20);
}
```

### Directional Padding

```ui
Group {
    Padding: (Top: 10, Bottom: 20, Left: 15, Right: 15);
}
```

### Shorthand

```ui
Group {
    Padding: (Horizontal: 20, Vertical: 10);
}
// Equivalent to: Top: 10, Bottom: 10, Left: 20, Right: 20
```

### Effect on Children

```ui
Group {
    Anchor: (Width: 200, Height: 100);
    Padding: (Full: 10);

    Label {
        Anchor: (Full: 0);
    }
}
```

The label fills the group, but there's a 10-pixel gap on all sides due to padding.

---

## LayoutMode

`LayoutMode` determines how a container arranges its children.

### Top (Vertical Stack)

```ui
Group {
    LayoutMode: Top;

    Button { Anchor: (Height: 30); }
    Button { Anchor: (Height: 30); }
    Button { Anchor: (Height: 30); }
}
```

Children stack vertically top-to-bottom. Use a child's `Anchor.Bottom` to add spacing after it.

### Bottom (Vertical Stack, Bottom-Aligned)

Same as `Top` but aligned to the bottom edge of the parent.

### Left (Horizontal Stack)

```ui
Group {
    LayoutMode: Left;

    Button { Anchor: (Width: 80); }
    Button { Anchor: (Width: 80); }
    Button { Anchor: (Width: 80); }
}
```

Children arrange horizontally left-to-right. Use `Anchor.Right` for spacing between elements.

### Right (Horizontal Stack, Right-Aligned)

Same as `Left` but aligned to the right side of the parent.

### Center

Centers children horizontally within the parent.

### Middle

Centers children vertically within the parent.

### CenterMiddle (Horizontal Stack, Fully Centered)

```ui
Group {
    LayoutMode: CenterMiddle;

    Button { Anchor: (Width: 80); }
    Button { Anchor: (Width: 80); }
    Button { Anchor: (Width: 80); }
}
```

Children stack horizontally, centered both horizontally and vertically:

```
┌──────────────────────────────────────────┐
│                                          │
│     ┌──────────┬──────────┬──────────┐   │
│     │ B1       │ B2       │ B3       │   │
│     └──────────┴──────────┴──────────┘   │
│                                          │
└──────────────────────────────────────────┘
```

### MiddleCenter (Vertical Stack, Fully Centered)

```ui
Group {
    LayoutMode: MiddleCenter;

    Button { Anchor: (Height: 30); }
    Button { Anchor: (Height: 30); }
    Button { Anchor: (Height: 30); }
}
```

Children stack vertically, centered both horizontally and vertically.

### Full (Absolute Positioning)

```ui
Group {
    LayoutMode: Full;

    Label {
        Anchor: (Top: 20, Left: 20, Width: 100, Height: 30);
    }
}
```

Children use absolute positioning via their own `Anchor` properties.

### TopScrolling / BottomScrolling

Like `Top`/`Bottom`, but adds a scrollbar if content exceeds the container height:

```ui
Group {
    LayoutMode: TopScrolling;
    ScrollbarStyle: $Common.@DefaultScrollbar;

    // ... many children
}
```

### LeftScrolling / RightScrolling

Like `Left`/`Right`, but adds a horizontal scrollbar.

### LeftCenterWrap (Wrapping Horizontal Stack)

```ui
Group {
    LayoutMode: LeftCenterWrap;

    Button { Anchor: (Width: 80, Height: 30); }
    Button { Anchor: (Width: 80, Height: 30); }
    // ...
}
```

Children flow left-to-right. When there's no more horizontal space, they wrap to the next row. Each row is horizontally centered:

```
┌──────────────────────────────────────────┐
│      ┌──────────┬──────────┬──────────┐  │
│      │ B1       │ B2       │ B3       │  │
│      └──────────┴──────────┴──────────┘  │
│           ┌──────────┬──────────┐        │
│           │ B4       │ B5       │        │
│           └──────────┴──────────┘        │
└──────────────────────────────────────────┘
```

---

## FlexWeight

`FlexWeight` distributes remaining space among children proportionally.

```ui
Group {
    LayoutMode: Left;
    Anchor: (Width: 400);

    Button { Anchor: (Width: 100); }
    Group  { FlexWeight: 1; }      // Takes all remaining space (200px)
    Button { Anchor: (Width: 100); }
}
```

### Multiple FlexWeights

```ui
Group {
    LayoutMode: Left;
    Anchor: (Width: 600);

    Group { FlexWeight: 1; }  // 150px  (1/4)
    Group { FlexWeight: 2; }  // 300px  (2/4)
    Group { FlexWeight: 1; }  // 150px  (1/4)
}
```

---

## Visibility

```ui
Button #HiddenButton {
    Visible: false;
}
```

When hidden:

- Element and its children are not displayed
- Element **does not take up layout space**
