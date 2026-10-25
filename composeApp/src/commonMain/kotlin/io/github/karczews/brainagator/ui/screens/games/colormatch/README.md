# Color Match Game

## Overview
Placeholder package for the Color Match game. It declares the game's catalog
metadata and a screen entry point that currently renders the shared
`GamePlaceholder` from the parent `ui/screens/games` package. There is no game
logic yet.

## Interface
- `ColorMatchGameInfo: GameInfo` - catalog metadata: `Res.string.game_color_match` /
  `subtitle_color_match` / `desc_color_match`, icon `Icons.Rounded.Palette`, accent
  `GameBerry`, `GameType.ColorMatch`, `isPlayable = false`.
- `@Composable ColorMatchGameScreen(gameInfo: GameInfo, onBackClick: () -> Unit, onGameWon: () -> Unit)` -
  screen entry point; forwards all three arguments unchanged to `GamePlaceholder`.

## Invariants
- `isPlayable` is hard-coded to `false`: the game is shown as "coming soon" until real
  logic replaces the placeholder.
- `gameType` is fixed to `GameType.ColorMatch`; accent is the theme constant `GameBerry`.
- Title, subtitle and description are `StringResource` IDs, never hardcoded text; this
  file does not call `stringResource`.
- The screen renders whatever `gameInfo` the caller passes; it does not read
  `ColorMatchGameInfo` itself (only the private `@Preview` does).
- Stateless: no `remember`, `Random`, or effects.

## Side Effects
- None directly. `onBackClick` and `onGameWon` are passed through to `GamePlaceholder`:
  its back button calls `onBackClick`, and `onGameWon` fires only from its debug-build
  test button (`LocalIsDebugBuild`). `GamePlaceholder` also speaks the "coming soon"
  text on entry via `GameScreenScaffold`.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache --tests "*.DesktopScreenshotTest"`
  (covers the `colorMatch` golden)
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
