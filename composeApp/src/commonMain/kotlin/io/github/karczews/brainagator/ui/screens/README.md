# Screens

## Overview
Top-level screens: the home game picker (a pager of large game cards with a speaking
gator), the win celebration, and the `GameInfo` catalog model that ties each game's
strings, icon, accent color and `GameType` together. Individual games and their shared
frame live in `games/`.

## Interface
- `data class GameInfo(titleRes, subtitleRes, descriptionRes: StringResource, icon: ImageVector, accentColor: Color, gameType: GameType, isPlayable: Boolean = true)` -
  catalog entry; `isPlayable = false` marks a coming-soon placeholder.
- `val games: List<GameInfo>` - ordered home catalog: ShapeMatch, NumberOrder,
  ColorMatch, SizeOrder, Pattern, OddOneOut, SpotDifference.
- `@Composable GameSelectionScreen(sharedTransitionScope: SharedTransitionScope, animatedContentScope: AnimatedContentScope, onGameSelected: (GameInfo) -> Unit = {}, onWelcomeSpoken: () -> Unit = {}, shouldSpeakWelcome: Boolean = true)` -
  home screen.
- `internal @Composable GamePager(state: PagerState, games: List<GameInfo>, onCardClick: (page: Int) -> Unit, modifier = Modifier)` -
  `HorizontalPager` of hero cards with neighbours peeking.
- `internal @Composable PagerControls(games, currentPage: Int, onPrevious, onNext, onDotClick: (page: Int) -> Unit, modifier = Modifier)` -
  previous/next arrows plus tappable page dots.
- `@Composable GameWonScreen(onPlayAgainClick: () -> Unit, onBackToMainClick: () -> Unit, showFireworks: Boolean = true)` -
  win celebration with icon-only Home and Replay buttons.
- Subpackage: [Games](./games/README.md) - game frame, layout, placeholder and the 7 game
  packages.

## Invariants
- Page index `i` maps to `games[i]`; `AppNavigation` looks up `GameInfo` by `GameType`
  from `games`, so every `GameType` needs exactly one entry here.
- Card tap: a playable game (or any game when `LocalIsDebugBuild` is true) calls
  `onGameSelected`; otherwise a peeking card scrolls into view, and the current card
  speaks the coming-soon line.
- `GamePager` reports the tapped card's own page index, so a peeking playable card starts
  its game on the first tap.
- Welcome speech: only on the first settled page and only if `shouldSpeakWelcome`; then
  `onWelcomeSpoken()` is called. The first settled page's intro is always queued with
  `speak`, even when the welcome is skipped. Later page changes speak that game's intro with
  `speakNow` (interrupting).
- Arrows wrap around (`(page +/- 1) % games.size`).
- `GameSelectionScreen` provides `LocalSharedTransitionContext` from its scope arguments
  so the gator shared element can animate.
- Non-playable cards use muted accent colors and a "coming soon" sticker.
- Landscape (`maxWidth > maxHeight`) switches both screens to side-by-side layouts.
- `showFireworks = false` disables `FireworksAnimation` (used by screenshot tests).
- User-facing text comes from string resources, except the build-info label
  (`Platform().name` plus "debug"/"release", shown on the home screen in every build) and the
  "Brainagator" logo spans.

## Side Effects
- Both screens obtain TTS with `rememberTextToSpeech()`.
- `GameSelectionScreen`: `LaunchedEffect(pagerState)` collects `settledPage` and speaks
  welcome/intros; gator tap calls `speakNow` on the current intro; arrow, dot and card taps
  launch `animateScrollToPage`; animated home accent color.
- `GameWonScreen`: `LaunchedEffect(Unit)` speaks `win_speech`, joins it, and on
  cancellation logs (`Logger.d`), cancels the job and rethrows; infinite bounce/wobble
  animations; buttons call `onPlayAgainClick` / `onBackToMainClick`.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache --tests "*.DesktopScreenshotTest"`
  (covers the `gameSelection`, `gameSelectionSmallPhone` and `gameWon` goldens)
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
