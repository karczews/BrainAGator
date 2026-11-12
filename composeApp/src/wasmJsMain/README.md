# wasmJsMain

## Overview
Kotlin/Wasm (browser) source set of the `composeApp` Kotlin Multiplatform library. It holds only
the Wasm `actual`s for the commonMain platform abstraction and the Web Speech API text-to-speech
backend. The web entry point `main()` and host page are shared with js in
`composeApp/src/webMain/`.

## Subdirectories & Boundaries
- kotlin/io/github/karczews/brainagator: [Wasm package](./kotlin/io/github/karczews/brainagator/README.md) - `actual class Platform` ("Web with Kotlin/Wasm"), `isDebugBuild` hardcoded `false`, and `tts/` (`WasmTextToSpeech` over `@JsFun` Web Speech interop).
