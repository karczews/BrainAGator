# src

## Overview

These are the Gradle source sets of the Android app module. `main` is the only production
source set. It holds the manifest, the launcher activity `MainActivity` and the app resources.
`test` and `androidTest` contain only tests. All three source sets use the Kotlin package
`io.github.karczews.brainagator`. The module's Gradle `namespace` is different:
`io.github.karczews.brainagator.app`.

## Subdirectories & Boundaries

- main: [README](./main/README.md) - The production source set. It holds:
  - `AndroidManifest.xml`, which declares one exported `MAIN`/`LAUNCHER` activity. There is
    no custom `Application` class and no permissions.
  - `MainActivity`, which sets up logging, enables edge-to-edge drawing, and hosts the shared
    `App()` with `LocalIsDebugBuild` taken from `BuildConfig.DEBUG`.
  - `res/`, which holds the app name, theme and launcher icons.
- `test/` - JVM unit tests (JUnit4, `ExampleUnitTest.kt`). Test-only, not documented.
- `androidTest/` - Instrumented device tests (`AndroidJUnit4`, `ExampleInstrumentedTest.kt`),
  run with `./gradlew connectedAndroidTest`. Test-only, not documented.
