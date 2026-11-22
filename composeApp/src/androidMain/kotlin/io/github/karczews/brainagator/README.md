# Android Platform Actuals (`androidMain` package)

## Overview
Android `actual` declarations for the commonMain platform abstraction in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/Platform.kt`, plus the Android
text-to-speech backend in `tts/`. This source set has no app entry point: the Android activity
lives in the separate `androidApp/` module
(`androidApp/src/main/kotlin/io/github/karczews/brainagator/MainActivity.kt`).

## Interface
- `actual class Platform actual constructor()` (`Platform.android.kt`):
  `actual val name = "Android ${Build.VERSION.SDK_INT}"`, for example "Android 34".
- `actual val isDebugBuild: Boolean = true` (`Platform.android.kt`): a hardcoded default. KMP
  library modules have no `BuildConfig` or build variants.
- [tts](./tts/README.md): `AndroidTextToSpeech(context)` over the Android TTS engine;
  `createTextToSpeech(context)`; `rememberTextToSpeech()`. The no-arg `createTextToSpeech()`
  throws `IllegalStateException` on Android.

## Invariants
- `isDebugBuild` is `true` in both debug and release Android builds. The real variant comes from
  `LocalIsDebugBuild`, which `androidApp`'s `MainActivity` provides as `BuildConfig.DEBUG`. Shared
  UI must read `LocalIsDebugBuild.current`, not `isDebugBuild`.
- `Platform.name` is computed once per instance from the SDK level.
- Android TTS requires a `Context`: obtain it through `rememberTextToSpeech()` or
  `createTextToSpeech(context)`, and call `shutdown()` when not using the composable.

## Side Effects
- `Platform` reads `Build.VERSION.SDK_INT`; no I/O or logging.
- `tts/` binds the Android TTS engine and plays audio (see [tts](./tts/README.md)).

## Verification
- Run: `./gradlew :composeApp:compileAndroidMain` (compile check; this source set has no unit tests)
