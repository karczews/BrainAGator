# src

## Overview
Kotlin Multiplatform source sets of the `composeApp` library. `commonMain` holds all shared UI
and logic, including the `expect` declarations (`Platform`, `isDebugBuild`,
`createTextToSpeech`, `rememberTextToSpeech`). Each platform source set supplies the matching
`actual`s, a text-to-speech backend built on the common `QueuedTextToSpeech` base, and, where
the platform needs one, the entry point that calls `initializeLogger()` and then hosts `App()`.
`webMain` is the intermediate source set shared by the `js` and `wasmJs` targets, and holds
their single `main()`. Android has no entry point here; the launcher activity is in the
separate `androidApp` module.

## Subdirectories & Boundaries
- commonMain: [README](./commonMain/README.md) - Shared code for every target: `App()` and
  Navigation 3 routing, theme and design tokens, the queued `TextToSpeech` abstraction, tactile
  UI elements, the home pager, the win screen and 7 game packages (ShapeMatch and NumberOrder
  playable, 5 placeholders). Also holds `composeResources/` (drawables, Lexend font, English
  and Polish strings).
- androidMain: [README](./androidMain/README.md) - Android `actual`s (`Platform` from the SDK
  level, `isDebugBuild` hardcoded `true`) and `AndroidTextToSpeech`. The no-arg
  `createTextToSpeech()` throws; use `createTextToSpeech(context)` or `rememberTextToSpeech()`.
- iosMain: [README](./iosMain/README.md) - `MainViewController()`, the Swift-facing entry
  point hosted by `iosApp/`, iOS `actual`s (`isDebugBuild` from `isDebugBinary`), and
  `IosTextToSpeech` over `AVSpeechSynthesizer`.
- jvmMain: [README](./jvmMain/README.md) - Desktop `main()` (`MainKt`, one "brainagator"
  window), JVM `actual`s (`isDebugBuild` hardcoded `true`), and `DesktopTextToSpeech`, which
  spawns `say`, PowerShell or `espeak`.
- jsMain: [README](./jsMain/README.md) - Kotlin/JS `actual`s (`isDebugBuild` hardcoded
  `false`) and `JsTextToSpeech` over Web Speech `external` declarations.
- wasmJsMain: [README](./wasmJsMain/README.md) - Kotlin/Wasm `actual`s (`isDebugBuild`
  hardcoded `false`) and `WasmTextToSpeech` over `@JsFun` Web Speech interop.
- webMain: [README](./webMain/README.md) - The shared browser `main()`
  (`initializeLogger()`, then `ComposeViewport { App() }`) and the host page in `resources/`.
- `commonTest/` - Shared unit tests (`kotlin-test` only). Test-only, not documented.
- `jvmTest/` - JVM tests with `kotlinx-coroutines-test` and Roborazzi screenshot tests. The
  golden images live in `jvmTest/screenshots/`. Test-only, not documented.
