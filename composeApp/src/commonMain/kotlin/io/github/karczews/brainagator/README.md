# BrainAGator Shared App (`io.github.karczews.brainagator`)

## Overview
Root package of the shared Kotlin Multiplatform app code used by Android, iOS, Desktop
(JVM) and Web (JS/Wasm). Files at this level define the app entry composable, the
Navigation 3 wiring from routes to screens, the shared-transition context, the logger
facade, and platform `expect` declarations. Theme, text-to-speech and UI live in
subpackages.

Platform `actual`s for `Platform` and `isDebugBuild` (outside this partition):
`composeApp/src/androidMain/kotlin/io/github/karczews/brainagator/Platform.android.kt`,
`composeApp/src/iosMain/kotlin/io/github/karczews/brainagator/Platform.ios.kt`,
`composeApp/src/jvmMain/kotlin/io/github/karczews/brainagator/Platform.jvm.kt`,
`composeApp/src/jsMain/kotlin/io/github/karczews/brainagator/Platform.js.kt`,
`composeApp/src/wasmJsMain/kotlin/io/github/karczews/brainagator/Platform.wasmJs.kt`.

## Interface
- `@Composable App()` - app root: logs start-up and wraps `AppNavigation` in `AppTheme`.
- `@Composable AppNavigation(modifier: Modifier = Modifier)` - Navigation 3 `NavDisplay`
  over a `Route` back stack inside a `SharedTransitionLayout`.
- `data class SharedTransitionContext(sharedTransitionScope: SharedTransitionScope, animatedContentScope: AnimatedContentScope)`
  and `val LocalSharedTransitionContext` (`compositionLocalOf`, default `null`).
- `typealias Logger = co.touchlab.kermit.Logger` and `fun initializeLogger()` - sets the
  platform log writer.
- `expect class Platform() { val name: String }`.
- `expect val isDebugBuild: Boolean` and `val LocalIsDebugBuild` (`compositionLocalOf`,
  default `isDebugBuild`).
- `class Greeting { fun greet(): String }` - "Hello, <platform name>!" template helper.
- Subpackages:
  - [Theme](./theme/README.md) - `AppTheme`, color palette, `ExtendedColors`, `Dimens`,
    Lexend typography.
  - [Text-to-speech](./tts/README.md) - `TextToSpeech`, `QueuedTextToSpeech`,
    `rememberTextToSpeech`, `speakNow`.
  - [UI](./ui/README.md) - navigation keys, tactile elements, screens and games.

## Invariants
- The back stack is a `remember`ed (not saveable) `mutableStateListOf` starting as
  `[Route.GameSelection]`. Selecting a game pushes `Route.Game`; back pops
  (`removeLastOrNull`); winning pushes `Route.GameWon` above the game entry. Push and pop
  both use a fade transition.
- From the win screen, "home" clears the stack to `[GameSelection]`, and "play again"
  resets to `[GameSelection, Game(gameType)]`, so the finished game's state is dropped and
  back still leads home.
- `Route.Game` resolves `GameInfo` from `games` by `GameType` and fails with `error(...)`
  if a type has no entry; the `when` over `GameType` must name every game screen.
- The home welcome message is spoken only once per `AppNavigation`: `hasSpokenWelcome` is
  `rememberSaveable`, passed as `shouldSpeakWelcome = !hasSpokenWelcome`, and set by
  `onWelcomeSpoken`, so returning home does not repeat it.
- `LocalSharedTransitionContext` is provided for game routes (and by
  `GameSelectionScreen`); consumers must tolerate `null` (previews, tests).
- Always import `io.github.karczews.brainagator.Logger`, never Kermit directly;
  `initializeLogger()` must be called once at start-up before logging (platform entry
  points do this, e.g. `androidApp/src/main/kotlin/io/github/karczews/brainagator/MainActivity.kt`).
- `LocalIsDebugBuild` defaults to the platform `isDebugBuild`; the Android app module
  overrides it at its entry point because KMP library modules have no build variants.
- Platform-specific code stays behind `expect`/`actual` (no platform APIs in
  `commonMain`); `Platform` actuals are named `Platform.<platform>.kt`, while TTS actuals
  live in per-platform classes such as `DesktopTextToSpeech.kt` (see the tts README).

## Side Effects
- `App` logs "App composable started" (`Logger.i`) once on first composition.
- `AppNavigation` mutates the back stack in response to screen callbacks, writes the
  saveable `hasSpokenWelcome` flag, and provides `LocalSharedTransitionContext` to game
  screens.
- A `Route.Game` whose `GameType` has no `GameInfo` throws `IllegalStateException`.
- `initializeLogger()` replaces Kermit's global log writers.
- Constructing `Platform` / `Greeting` calls into platform code.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
