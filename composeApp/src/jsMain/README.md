# jsMain

## Overview
Kotlin/JS (browser) source set of the `composeApp` Kotlin Multiplatform library. It holds only
the JS `actual`s for the commonMain platform abstraction and the Web Speech API text-to-speech
backend. The web entry point `main()` and host page are shared with wasmJs in
`composeApp/src/webMain/`.

## Subdirectories & Boundaries
- kotlin/io/github/karczews/brainagator: [JS package](./kotlin/io/github/karczews/brainagator/README.md) - `actual class Platform` ("Web with Kotlin/JS"), `isDebugBuild` hardcoded `false`, and `tts/` (`JsTextToSpeech` over Web Speech `external` declarations).
