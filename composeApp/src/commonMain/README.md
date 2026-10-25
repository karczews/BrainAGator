# commonMain

## Overview
Shared Kotlin Multiplatform source set of `composeApp`, compiled for every target
(Android, iOS, Desktop/JVM, JS, Wasm). It holds all common UI and logic under the
`io.github.karczews.brainagator` package, plus the Compose Multiplatform resources.
Platform-specific `actual` implementations live in the sibling source sets
(`composeApp/src/androidMain`, `iosMain`, `jvmMain`, `jsMain`, `wasmJsMain`); tests live in
`composeApp/src/commonTest` and `composeApp/src/jvmTest`.

## Subdirectories & Boundaries
- `io.github.karczews.brainagator`: [Shared app package](./kotlin/io/github/karczews/brainagator/README.md) -
  app root (`App`, `AppNavigation` over a Navigation 3 `Route` back stack), `Logger`
  facade, `Platform` / `isDebugBuild` `expect`s and `LocalIsDebugBuild`; subpackages
  `theme` (light-only Material3 theme and design tokens), `tts` (queued text-to-speech
  abstraction with platform `actual`s) and `ui` (navigation keys, tactile elements, home
  pager, win screen, and 7 game packages, 2 of them playable).
- `composeResources/` - Compose Multiplatform resources: drawables (`drawable/` vector
  XML and density-specific PNGs such as the gator and star art), the Lexend font
  (`font/`), and strings in `values/` (default) and `values-pl/` (Polish). Accessed through
  the generated `brainagator.composeapp.generated.resources.Res`.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache` (runs `commonTest` and `jvmTest` on the JVM)
