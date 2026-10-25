# Shape Match Game

## Overview
Playable "tap the <color> <shape>" game. Each round deals a grid of unique
shape/color tiles and speaks a prompt naming one of them; five correct picks win the
game. The package also owns the custom Compose `Shape`s and the shape/color tables
the game draws from.

## Interface
- `ShapeMatchGameInfo: GameInfo` - catalog metadata: icon `Icons.Rounded.Category`,
  accent `GameGrape`, `GameType.ShapeMatch` (playable).
- `@Composable ShapeMatchGameScreen(gameInfo: GameInfo, onBackClick: () -> Unit, onGameWon: () -> Unit, maxShapes: Int = 5, maxColors: Int = 5, numbersOfItemsToSelect: Int = 6, random: Random = Random.Default)` -
  game screen built on `GameScreenScaffold`.
- `class TriangleShape`, `StarShape`, `DiamondShape`, `HeartShape`, `PentagonShape : Shape` -
  path-based outlines sized to the layout bounds.
- `@Composable ShapesPreview()` - public `@Preview` of every shape with a `rainbowBrush` border.
- `internal data class GameShape(shape: Shape, nameRes: StringResource)` and
  `internal val gameShapes` - 7 entries: circle, square, triangle, star, diamond, heart, pentagon.
- `internal data class GameColor(color: Color, nameRes: StringResource)` and
  `internal val gameColors` - 8 entries: red, blue, yellow, green, orange, purple, pink, turquoise.

## Invariants
- `require(numbersOfItemsToSelect >= 1)`; `maxShapes` / `maxColors` are clamped to
  `1..gameShapes.size` / `1..gameColors.size`.
- Tiles are drawn from the shuffled cartesian product of shapes and colors, so no two
  tiles in a round share both shape and color; tile count is
  `min(numbersOfItemsToSelect, shapes * colors)`.
- All randomness (shape/color pick, pair shuffle, target pick) uses the injected `random`;
  a seeded `Random` makes rounds deterministic (the screenshot test uses `Random(0)`).
- A tile is correct when its pair equals the current target (data-class equality; shapes
  compare by the single instances in `gameShapes`).
- 5 rounds (`totalIterations = 5`); after the fifth correct pick `onGameWon()` is called
  and the pairs are not regenerated.
- `key(correctCount)` around the grid resets per-tile shake state every round.
- Prompt and tile labels come from `stringResource` (`shape_match_select_instruction`,
  shape and color name resources); tile `contentDescription` is "<color> <SHAPE>".
- Grid uses `ceil(n / 2)` columns when `GameBoardLayout` reports `isLandscape` (side-panel
  mode) and 2 columns otherwise, including landscape screens too narrow for the side panel.
  Tile height never drops below `Dimens.answerTileMinHeight` (the column scrolls instead).

## Side Effects
- `LaunchedEffect(instructionText, correctCount)` speaks each round's prompt via the
  scaffold's `tts`, skipped once the game is won.
- Gator avatar click calls `tts.speakNow(instructionText)`.
- Correct pick increments `correctCount` and either calls `onGameWon()` or regenerates
  the round; wrong pick launches `shakeState.shake()` with wrong colors.
- `onBackClick` is forwarded to `GameScreenScaffold`.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache --tests "*.DesktopScreenshotTest"`
  (covers the `shapeMatch` golden)
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
