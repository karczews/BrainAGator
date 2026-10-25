# androidMain

## Overview
Android source set of the `composeApp` Kotlin Multiplatform library (configured as
`androidLibrary`). It holds the Android `actual`s for the commonMain platform abstraction
(`Platform`, `isDebugBuild`) and the Android text-to-speech backend, plus the library manifest
and launcher resources. It has no activity; the app entry point is in the separate `androidApp/`
module, which also provides the real `LocalIsDebugBuild` value.

## Subdirectories & Boundaries
- kotlin/io/github/karczews/brainagator: [Android package](./kotlin/io/github/karczews/brainagator/README.md) - `actual class Platform` ("Android <SDK_INT>"), `isDebugBuild` hardcoded `true`, and `tts/` (`AndroidTextToSpeech(context)`; no-arg `createTextToSpeech()` throws, use `createTextToSpeech(context)` or `rememberTextToSpeech()`).
- AndroidManifest.xml - empty `<manifest/>` for the library (non-code).
- res/ - launcher icons (`mipmap-*`, `drawable*`) and `values/strings.xml` (non-code).
