# Games

## Overview
Shared game frame plus one subpackage per game. The files here give every game the
same screen chrome (background, back button, entry speech, TTS instance), a responsive
board layout, and a "coming soon" placeholder for games that are not playable yet. Each
game package exposes a `<Name>GameInfo` catalog entry and a `<Name>GameScreen`
composable that `AppNavigation` (in `App.kt`) selects by `GameType`.

## Interface
- `@Composable GameScreenScaffold(gameInfo: GameInfo, onBackClick: () -> Unit, entrySpeech: String? = null, content: @Composable (PaddingValues, TextToSpeech) -> Unit)` -
  game frame: `PlayfulBackground` tinted by `gameInfo.accentColor`, back-button top bar,
  spoken entry text; hands content the scaffold padding and a remembered TTS.
- `@Composable GameBoardLayout(instruction: @Composable () -> Unit, progress: @Composable () -> Unit, modifier: Modifier = Modifier, boardMinWidth: Dp = Dimens.boardMinWidth, board: @Composable (isLandscape: Boolean, boardModifier: Modifier) -> Unit)` -
  side panel (instruction + progress) beside the board, or board stacked above them.
- `@Composable GamePlaceholder(gameInfo: GameInfo, onBackClick: () -> Unit, onGameWon: (() -> Unit)? = null)` -
  "coming soon" screen built on `GameScreenScaffold`.
- `val rainbowColors: List<Color>` and `val rainbowBrush: Brush` (vertical gradient) -
  decorative palette, also used by `ui/elements/basic`.
- Game packages:
  - [Shape Match](./shapematch/README.md) - playable; 5 rounds of "tap the <color> <shape>".
  - [Number Order](./numberorder/README.md) - playable; tap 5 numbers in ascending order.
  - [Color Match](./colormatch/README.md) - placeholder.
  - [Size Order](./sizeorder/README.md) - placeholder.
  - [Pattern](./pattern/README.md) - placeholder.
  - [Odd One Out](./oddoneout/README.md) - placeholder.
  - [Spot the Difference](./spotdifference/README.md) - placeholder.

## Invariants
- `GameScreenScaffold` obtains TTS once via `rememberTextToSpeech()` and passes that same
  instance to `content`; game code should use it rather than creating its own.
- `content` receives the Material3 `Scaffold` inner padding and must apply it itself.
- Entry speech is `entrySpeech` if given, else `stringResource(gameInfo.descriptionRes)`;
  it is spoken once per entry (`LaunchedEffect(Unit)`), not on recomposition.
- The scaffold does not shut down TTS; release is left to the platform `actual` of
  `rememberTextToSpeech`.
- `GameBoardLayout` uses the side panel only when `maxWidth > maxHeight` and the panel
  (`min(40% width, width - spaceMd - boardMinWidth)`) is at least `Dimens.sidePanelMinWidth`;
  `isLandscape` is true only in that mode, and `board` must apply `boardModifier`.
- `GamePlaceholder` speaks `Res.string.game_coming_soon` on entry and calls `onGameWon`
  only from a test button shown when `LocalIsDebugBuild.current` and `onGameWon != null`.
- A new game needs a `GameType` entry, a package here with `<Name>GameInfo` and
  `<Name>GameScreen(gameInfo, onBackClick, onGameWon)`, an entry in the `games` list
  (`ui/screens/GameSelectionScreen.kt`), and wiring in `AppNavigation`.
- All user-facing strings come from Compose resources (`go_back`, `game_coming_soon`,
  `test_trigger_win`, game title/description resources).

## Side Effects
- `GameScreenScaffold`: creates a platform TTS instance; `LaunchedEffect(Unit)` speaks the
  entry text and joins the job; on cancellation it logs (`Logger.d`), cancels the speech
  job and rethrows.
- Back button calls `onBackClick`.
- `GamePlaceholder`: gator tap calls `tts.speakNow(...)`; debug test button calls `onGameWon`.
- `GameBoardLayout` and `Brushes.kt` have no side effects.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
