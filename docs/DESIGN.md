# Design System: Gator Jungle

## 1. North Star: "Toys You Can Press"

Brainagator is for young children, many of whom cannot read yet. Every screen should feel like a box of
friendly, chunky toys: bright, rounded, obvious to tap, and impossible to get lost in. The pixel-art gator
mascot is the guide. It appears on the home screen, "speaks" every instruction, and celebrates wins.

Four rules drive every decision:

1. **If it has a lip, you can press it.** Tappable things are raised blocks with a darker "lip" underneath that
   sinks when pressed. Things you only look at (instructions, progress) are flat cards with no lip.
2. **One color per game.** Each game owns an accent color. Its tile on the home screen, its top bar, its
   background wash and its highlights all use that color, so children recognise a game by color alone.
3. **Show, don't write.** Use icons, stars, shapes and the mascot before text. Speech (TTS) carries the
   instructions; on-screen text is a backup for grown-ups.
4. **Mistakes are gentle.** A wrong answer wiggles and turns soft coral. Never use alarming red, error sounds
   or anything that feels like punishment.

## 2. Color

Defined in `theme/Color.kt` and exposed through `MaterialTheme.colorScheme` plus
`AppTheme.extendedColors` (see `theme/ExtendedColors.kt`). Screens must read colors from the theme; only
`theme/Color.kt` may contain raw hex values.

| Role | Token | Value | Use |
|------|-------|-------|-----|
| Primary | `primary` | Gator Green `#1F7E37` | Main call to action, logo |
| Secondary | `secondary` | Sunny Yellow `#FFC233` | Rewards, stars, tagline pill (dark text only) |
| Tertiary | `tertiary` | Sky Blue `#1770BF` | Supporting accents |
| Error | `error` / `errorContainer` | Coral `#E04A3C` / `#FFDDD8` | Wrong answers (soft, never scary) |
| Background | `background` | Cream `#FFF8EC` | Screen canvas |
| Surface | `surface` | White | Tiles, cards |
| Text | `onBackground` / `onSurface` | Plum Ink `#2B2541` | All body text (softer than black) |
| Success | `AppTheme.extendedColors.success` | Gator Green | Correct answers |
| Reward | `AppTheme.extendedColors.reward` / `onReward` | Sunny Yellow / Honey Brown | Earned stars (always outlined in `onReward`), win screen |

**Game accents** (`GameInfo.accentColor`): Grape `#7A52E8`, Ocean `#1770BF`, Berry `#CF296C`, Tangerine
`#B74F0C`, Teal `#0C7C75`, Indigo `#4C5FD7`, Plum `#A23CB4`. Every accent (and the primary green) has at least
5:1 contrast with white, which keeps 4.5:1 under the tactile highlight, so white labels of any size stay
readable. Pick new accents with the same constraint, and keep them clear of the feedback colors (coral, green,
yellow) so right/wrong/reward cues never blend into a game's own color.

Helpers: `Color.darken(fraction)` makes lips and readable accent-colored text; `Color.lighten(fraction)` makes tints.

## 3. Typography

**Lexend** only (a variable font, so all weights render natively). It was designed for reading fluency,
which suits early readers. The scale in `theme/Typography.kt` is a step larger than Material defaults:
body text starts at 16sp and headings are ExtraBold. Keep copy short: one instruction, one line where possible.

## 4. Shape & Depth

- **Rounded everywhere.** `MaterialTheme.shapes`: 8 / 12 / 20 / 28 / 36dp. Buttons are pills or circles.
- **Depth comes from the lip, not shadows.** A tactile block is a face on top of a darker lip
  (`Dimens.lipHeight` = 6dp). On press, the face sinks into the lip with a bouncy spring.
- **Flat cards** (`PlayCard`) use a white fill with a 2dp `outlineVariant` border and no lip.
- **Backgrounds** (`PlayfulBackground`) are a soft vertical wash of the screen's accent over cream, with a
  few large, low-contrast bubbles. Keep decoration away from the top bar and interactive content.

## 5. Sizing

Tokens live in `theme/Dimens.kt`: spacing, touch targets, and the sizes of game tiles, answer tiles, icon
badges, illustrations and width caps. Screens use these tokens rather than raw `dp` values.

- Minimum touch target: **64dp** (`Dimens.touchTarget`), larger than the 48dp adult guideline.
- Round icon buttons: 64dp face plus lip.
- Spacing scale: 4 / 8 / 16 / 24 / 32dp; screen edge padding 20dp.
- Game boards must fit on screen without scrolling where possible: children do not know to scroll to
  find a hidden answer. Size tiles from the available space (see `ShapeGrid` and `NumberBoxesRow`).

## 6. Components

All shared building blocks live in `composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/ui/elements/`.
Build screens from these instead of styling Material components by hand.

| Component | Purpose |
|-----------|---------|
| `TactileSurface` | Core pressable block with a lip. Colors come from `TactileDefaults` (`colors(accent)`, `surfaceColors()`, `primaryColors()`, `successColors()`, `wrongColors()`). |
| `TactileButton` | Pill button with an optional leading icon. Use for main actions. |
| `TactileIconButton` | Round icon button (back, help). |
| `PlayCard` | Flat, non-interactive card. |
| `InstructionCard` | `PlayCard` in which the gator "says" the instruction, with an optional label. |
| `StarProgress` | Progress as stars that pop in when earned. |
| `PlayfulBackground` | Accent-tinted background with bubbles; every screen uses it. |
| `ShakeState` / `Modifier.shake` | Shared wrong-answer wiggle; pair with `TactileDefaults.wrongColors()` while `isShaking`. |

Game-level scaffolding lives in `ui/screens/games/`:

| Component | Purpose |
|-----------|---------|
| `GameScreenScaffold` | Background in the game's accent, top bar (back, title, help/replay), spoken instructions. |
| `GameBoardLayout` | Instruction on top, board in the middle, progress at the bottom; side column in landscape. |
| `GamePlaceholder` | "Coming soon" screen for games not built yet. |

### Building a new game screen

1. Add a `GameInfo` with a new accent color from `theme/Color.kt` and a `Icons.Rounded.*` icon.
2. Wrap the screen in `GameScreenScaffold`.
3. Use `GameBoardLayout` with an `InstructionCard`, your board and `StarProgress` in a `PlayCard`.
4. Make answers `TactileSurface`s: `surfaceColors(content = accent)` while open, `successColors()` when
   right, and a `ShakeState` + `wrongColors()` when wrong.

## 7. Do's and Don'ts

### Do
- Do use the game's accent for its identity and the theme's success/reward colors for feedback.
- Do keep one clear task per screen with large targets and lots of breathing room.
- Do let the mascot carry instructions and celebrations.
- Do check new screens at 360dp wide and in landscape.

### Don't
- Don't add a lip (or a shadow that looks like one) to anything that cannot be tapped.
- Don't use sharp corners, thin 1px lines or pure black text.
- Don't hardcode colors or dimensions in screens. Add tokens to `theme/` instead.
- Don't put essential information in text alone. Pair it with an icon, color or speech.
