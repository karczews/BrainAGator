# webMain

## Overview
Source set shared by the Kotlin/JS and Kotlin/Wasm browser targets of the `composeApp` Kotlin
Multiplatform library. It holds the single web `main()` entry point and the static host page.
Target-specific `actual`s and text-to-speech live in `composeApp/src/jsMain/` and
`composeApp/src/wasmJsMain/`.

## Subdirectories & Boundaries
- kotlin/io/github/karczews/brainagator: [Web package](./kotlin/io/github/karczews/brainagator/README.md) - `main()` initializes logging and starts `ComposeViewport { App() }`.
- resources/ - `index.html` (title, loading spinner, `composeApp.js` script tag) and `styles.css` (full-size, no-overflow `html`/`body`) (non-code).
