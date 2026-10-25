# Kotlin/JS Platform Actuals (`jsMain` package)

## Overview
Kotlin/JS `actual` declarations for the commonMain platform abstraction in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/Platform.kt`, plus the JS
text-to-speech backend in `tts/`. This source set has no entry point: `main()` for both the js
and wasmJs targets lives in `webMain`
(`composeApp/src/webMain/kotlin/io/github/karczews/brainagator/Main.kt`).

## Interface
- `actual class Platform actual constructor()` (`Platform.js.kt`):
  `actual val name = "Web with Kotlin/JS"` (constant).
- `actual val isDebugBuild: Boolean = false` (`Platform.js.kt`): hardcoded; Kotlin/JS has no
  build-variant signal.
- [tts](./tts/README.md): `JsTextToSpeech` over the Web Speech API; `createTextToSpeech()`;
  `rememberTextToSpeech()`; Web Speech `external` declarations.

## Invariants
- `isDebugBuild` is always `false` on Kotlin/JS, in dev and production webpack builds alike, so
  debug-only UI never shows on this target unless `LocalIsDebugBuild` is provided.
- `Platform.name` is a constant; nothing is read from the browser.

## Side Effects
- `Platform.js.kt` has none (constant values only).
- `tts/` calls the browser Web Speech API (see [tts](./tts/README.md)).

## Verification
- Manual smoke check only (no automated verification task exists for the JS target):
  `./gradlew :composeApp:jsBrowserDevelopmentRun`
