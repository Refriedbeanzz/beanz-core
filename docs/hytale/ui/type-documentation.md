# Type Documentation

> Source: Official Hytale Documentation — Hypixel Studios Canada Inc.  
> https://github.com/HytaleModding/site/tree/main/content/docs/en/official-documentation/custom-ui/type-documentation

This is a generated list of UI elements accessible by markup. Individual element/type pages live under the `type-documentation/` path on the HytaleModding site — links below point there.

---

## Elements

- ActionButton
- AssetImage
- BackButton
- BlockSelector
- Button
- CharacterPreviewComponent
- CheckBox
- CheckBoxContainer
- CircularProgressBar
- CodeEditor
- ColorOptionGrid
- ColorPicker
- ColorPickerDropdownBox
- CompactTextField
- DropdownBox
- DropdownEntry
- DynamicPane
- DynamicPaneContainer
- FloatSlider
- FloatSliderNumberField
- **Group** — generic container, the most common layout element
- HotkeyLabel
- ItemGrid
- ItemIcon
- ItemPreviewComponent
- ItemSlot
- ItemSlotButton
- **Label** — text display element
- LabeledCheckBox
- MenuItem
- MultilineTextField
- NumberField
- Panel
- **ProgressBar** — visual progress indicator
- ReorderableList
- ReorderableListGrip
- SceneBlur
- Slider
- SliderNumberField
- Sprite
- TabButton
- TabNavigation
- **TextButton** — standard clickable button with text
- TextField
- TimerLabel
- ToggleButton

---

## Property Types

- Anchor
- BlockSelectorStyle
- ButtonSounds
- ButtonStyle / ButtonStyleState
- CheckBoxStyle / CheckBoxStyleState
- ClientItemStack
- ColorOptionGridStyle
- ColorPickerDropdownBoxStyle
- ColorPickerStyle
- DropdownBoxStyle / DropdownBoxSounds
- InputFieldStyle / InputFieldDecorationStyle / InputFieldButtonStyle / InputFieldIcon
- ItemGridStyle / ItemGridSlot
- LabeledCheckBoxStyle / LabeledCheckBoxStyleState
- LabelSpan
- **LabelStyle** — font size, color, alignment, wrapping
- NumberFieldFormat
- **Padding** — inner spacing object
- PatchStyle
- PopupStyle
- **ScrollbarStyle** — used with scrolling LayoutModes
- SliderStyle
- SoundStyle
- SpriteFrame
- SubMenuItemStyle / SubMenuItemStyleState
- Tab / TabStyle / TabStyleState / TabNavigationStyle
- TextButtonStyle / TextButtonStyleState
- TextTooltipStyle
- ToggleButtonStyle / ToggleButtonStyleState

---

## Enums

- ActionButtonAlignment
- CodeEditorLanguage
- ColorFormat
- DropdownBoxAlign
- InputFieldButtonSide / InputFieldIconSide
- ItemGridInfoDisplayMode
- **LabelAlignment** — horizontal text alignment options
- **LayoutMode** — Full, Top, Bottom, Left, Right, Center, Middle, CenterMiddle, MiddleCenter, TopScrolling, BottomScrolling, LeftScrolling, RightScrolling, LeftCenterWrap
- MouseWheelScrollBehaviourType
- ProgressBarAlignment / ProgressBarDirection
- ResizeType
- TimerDirection
- TooltipAlignment

---

## Notes for Beanz Core

The most commonly used elements in our UI so far:

| Element | Used for |
|---|---|
| `Group` | Panels, cards, frames, progress bar containers |
| `Label` | Titles, body text, XP numbers, progress text |
| `TextButton` | Buttons (not yet used but available via `$Common`) |
| `ProgressBar` | Could replace manual `Group`/`Fill` approach in future |

The `ProgressBar` element may be worth switching to instead of the manual `Group #Fill` width trick we're currently using — check `/ui-gallery` in-game to see how it renders.
