# composeApp

## Overview
Shared Kotlin Multiplatform library that contains all of BrainAGator's UI and logic, built with
Compose Multiplatform and Material3. The platform apps are thin hosts around its `App()`
composable. `composeApp/build.gradle.kts` configures these targets:

| Target | Output |
| ------ | ------ |
| `androidLibrary` | Android library with namespace `io.github.karczews.brainagator`, consumed by `androidApp` |
| `iosArm64`, `iosSimulatorArm64` | Static `ComposeApp` framework, consumed by `iosApp` |
| `jvm` | Desktop app, `mainClass = io.github.karczews.brainagator.MainKt`, packaged as DMG, MSI and DEB |
| `js`, `wasmJs` | Browser executables. The Wasm build is deployed to GitHub Pages |

Other build settings:

- Uses JVM toolchain 21, with Android bytecode targeting JVM 11.
- Passes `-Xexpect-actual-classes` to the compiler.
- Keeps the generated `Res` class non-public (`publicResClass = false`).
- Writes Roborazzi screenshot goldens to `src/jvmTest/screenshots`.
- Requires `entitlements.plist` at the repo root. Gradle configuration fails without it.

## Subdirectories & Boundaries
- src: [README](./src/README.md) - The source sets. `commonMain` holds the shared app, theme,
  text-to-speech abstraction, UI and games. `androidMain`, `iosMain`, `jvmMain`, `jsMain` and
  `wasmJsMain` hold the platform `actual`s and TTS backends, plus the iOS and desktop entry
  points. `webMain` holds the shared browser `main()`. `commonTest` and `jvmTest` are
  test-only.
- [AGENTS.md](./AGENTS.md) - Module conventions for agents: theming, strings, previews,
  concurrency rules and build commands.
- `build.gradle.kts` - The module build script (see Overview).
- `detekt-baseline.xml` - Detekt baseline for this module.
