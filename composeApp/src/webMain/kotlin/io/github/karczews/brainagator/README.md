# Web Entry Point (`webMain` package)

## Overview
Shared browser entry point for both the Kotlin/JS (`jsMain`) and Kotlin/Wasm (`wasmJsMain`)
targets. `webMain` is the source set common to those two web targets; their platform `actual`s
live in `composeApp/src/jsMain/` and `composeApp/src/wasmJsMain/`. All UI comes from the
commonMain root composable `App()`.

## Interface
- `fun main()` (`Main.kt`, `@OptIn(ExperimentalComposeUiApi::class)` for `ComposeViewport`):
  calls `initializeLogger()`, then `ComposeViewport { App() }`.

## Invariants
- `initializeLogger()` must run before `ComposeViewport` so logging is configured before the UI
  starts.
- `ComposeViewport` is called without a container argument, so it uses the library default host
  element; the host page (`composeApp/src/webMain/resources/index.html`) has no dedicated
  container, only a loading spinner and the `composeApp.js` script tag.
- This is the only `main()` for the web targets; `jsMain` and `wasmJsMain` declare none.

## Side Effects
- `initializeLogger()` replaces the global Kermit log writers with the platform writer (browser
  console).
- `ComposeViewport { App() }` starts the Compose UI and attaches its surface to the DOM.
- `main()` does not remove the loading spinner from `index.html`.

## Verification
- Run: `./gradlew :composeApp:wasmJsBrowserDevelopmentWebpack`
