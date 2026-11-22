# Spot the Difference Game

## Overview
Placeholder package for the Spot the Difference game. It declares the game's
catalog metadata and a screen entry point that currently renders the shared
`GamePlaceholder` from the parent `ui/screens/games` package. There is no game
logic yet.

## Interface
- `SpotDifferenceGameInfo: GameInfo` - catalog metadata: `Res.string.game_spot_difference` /
  `subtitle_spot_difference` / `desc_spot_difference`, icon `Icons.Rounded.Search`, accent
  `GamePlum`, `GameType.SpotDifference`, `isPlayable = false`.
- `@Composable SpotDifferenceGameScreen(gameInfo: GameInfo, onBackClick: () -> Unit, onGameWon: () -> Unit)` -
  screen entry point; forwards all three arguments unchanged to `GamePlaceholder`.

## Invariants
- `isPlayable` is hard-coded to `false`: the game is shown as "coming soon" until real
  logic replaces the placeholder.
- `gameType` is fixed to `GameType.SpotDifference`; accent is the theme constant `GamePlum`.
- Title, subtitle and description are `StringResource` IDs, never hardcoded text; this
  file does not call `stringResource`.
- The screen renders whatever `gameInfo` the caller passes; it does not read
  `SpotDifferenceGameInfo` itself.
- Stateless: no `remember`, `Random`, or effects.

## Side Effects
- None directly. `onBackClick` and `onGameWon` are passed through to `GamePlaceholder`:
  its back button calls `onBackClick`, and `onGameWon` fires only from its debug-build
  test button (`LocalIsDebugBuild`). `GamePlaceholder` also speaks the "coming soon"
  text on entry via `GameScreenScaffold`.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
