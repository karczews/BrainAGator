# Android Entry Point (`io.github.karczews.brainagator`)

## Overview

This package holds the Android launcher activity. It is a thin host. It sets up logging,
turns on edge-to-edge drawing, and puts the shared Compose UI on screen. The UI is `App()`
from `composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/App.kt`. All screens,
theming and navigation live in `composeApp`. This package defines none of its own.

## Interface

- `MainActivity : ComponentActivity` (`MainActivity.kt`): the only class in this package.
  `androidApp/src/main/AndroidManifest.xml` registers it as the exported `MAIN`/`LAUNCHER`
  activity.
  - `onCreate(savedInstanceState: Bundle?)` makes these calls, in order:
    1. `super.onCreate`
    2. `initializeLogger()`
    3. `enableEdgeToEdge()`
    4. `setContent { CompositionLocalProvider(LocalIsDebugBuild provides BuildConfig.DEBUG) { App() } }`
- External symbols it uses:
  - `App()`, `initializeLogger()` and `LocalIsDebugBuild` from `composeApp`, defined in
    `composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/App.kt`, `Logger.kt` and
    `Platform.kt`.
  - `BuildConfig` from `io.github.karczews.brainagator.app`. This is the app module's
    `namespace`, which differs from the `applicationId` `io.github.karczews.brainagator`.

## Invariants

- `initializeLogger()` must run before `setContent`. `App()` logs `"App composable started"`
  from a `LaunchedEffect`, and the Kermit writers must be set up before that.
- `App()` must stay wrapped in `CompositionLocalProvider(LocalIsDebugBuild provides BuildConfig.DEBUG)`.
  `composeApp` is a KMP library with no build variants, and its Android `isDebugBuild` is
  hard-coded to `true`. Without this override, release builds would show debug-only UI.
- `BuildConfig` is generated only because `androidApp/build.gradle.kts` sets
  `buildFeatures.buildConfig = true`. The import must match the module `namespace`.
- Do not add UI, theme or navigation here. Put shared UI in `composeApp`.
- There is no `Application` subclass. Startup setup runs in `onCreate`, so it runs again
  whenever the activity is recreated. The manifest declares no `configChanges`. Re-running is
  safe today because `initializeLogger()` replaces the writer list instead of adding to it.

## Side Effects

- Replaces the process-wide Kermit log writers: `Logger.setLogWriters(platformLogWriter())`.
- Sets the activity window to draw edge-to-edge (`enableEdgeToEdge()`).
- Installs the Compose content tree, and sets `LocalIsDebugBuild` to `BuildConfig.DEBUG` for the
  whole `App()` tree.
- Does no disk, network or persistence work itself. `savedInstanceState` is only passed to
  `super.onCreate`.

## Verification

- Run: `./gradlew :androidApp:assembleDebug`
- Run: `./gradlew connectedAndroidTest` (instrumentation tests; needs a connected device or
  emulator)
