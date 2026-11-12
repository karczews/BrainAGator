# Kotlin/Wasm Platform Actuals (`wasmJsMain` package)

## Overview
Kotlin/Wasm `actual` declarations for the commonMain platform abstraction in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/Platform.kt`, plus the Wasm
text-to-speech backend in `tts/`. This source set has no entry point: `main()` for both the js
and wasmJs targets lives in `webMain`
(`composeApp/src/webMain/kotlin/io/github/karczews/brainagator/Main.kt`).

## Interface
- `actual class Platform actual constructor()` (`Platform.wasmJs.kt`):
  `actual val name = "Web with Kotlin/Wasm"` (constant).
- `actual val isDebugBuild: Boolean = false` (`Platform.wasmJs.kt`): hardcoded.
- [tts](./tts/README.md): `WasmTextToSpeech` over the Web Speech API via `@JsFun` interop;
  `createTextToSpeech()`; `rememberTextToSpeech()`.

## Invariants
- `isDebugBuild` is always `false` on Kotlin/Wasm. The inline comment says compiler configuration
  sets it to `true` in debug builds, but no build file does this.
- `Platform.name` is a constant; nothing is read from the browser.

## Side Effects
- `Platform.wasmJs.kt` has none (constant values only).
- `tts/` calls the browser Web Speech API (see [tts](./tts/README.md)).

## Verification
- Run: `./gradlew :composeApp:wasmJsBrowserDevelopmentWebpack`
