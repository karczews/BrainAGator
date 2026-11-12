# iosApp

## Overview

This directory is the iOS application host: an Xcode project with a single target, `iosApp`,
which produces `brainagator.app`. The app is a thin SwiftUI shell around the shared Compose
UI. The Kotlin side ships as the static `ComposeApp` framework, built from `composeApp`
(`iosArm64`, `iosSimulatorArm64`).

The target's first build phase, `Compile Kotlin Framework`, runs on every build. It changes
to the repo root (`$SRCROOT/..`) and runs `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`,
which provides the framework before Swift compiles. If the environment sets
`OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED=YES`, the phase is skipped. The project has no
`FRAMEWORK_SEARCH_PATHS` and no Swift Package dependencies. It needs
`ENABLE_USER_SCRIPT_SANDBOXING = NO` so the Gradle script can run.

Key build settings:

- Deployment target iOS 18.2.
- `ARCHS = arm64` only.
- iPhone and iPad.
- Automatic signing with `DEVELOPMENT_TEAM = ${TEAM_ID}`.
- `Info.plist` is generated and merged with `iosApp/Info.plist`.

## Subdirectories & Boundaries

- iosApp: [README](./iosApp/README.md) - The SwiftUI shell. The `@main` type `iOSApp` shows
  `ContentView`, which wraps `ComposeView`. `ComposeView` is a
  `UIViewControllerRepresentable` that returns `MainViewControllerKt.MainViewController()`
  (from `composeApp/src/iosMain/kotlin/io/github/karczews/brainagator/MainViewController.kt`),
  shown edge-to-edge. It also holds `Info.plist` and the asset catalogs.
- `Configuration/` - `Config.xcconfig`, the project-level base configuration for Debug and
  Release. It holds the only definitions of these settings:

  | Setting | Value |
  | ------- | ----- |
  | `TEAM_ID` | empty |
  | `PRODUCT_NAME` | `brainagator` |
  | `PRODUCT_BUNDLE_IDENTIFIER` | `io.github.karczews.brainagator.brainagator$(TEAM_ID)` |
  | `MARKETING_VERSION` | `1.0` |
  | `CURRENT_PROJECT_VERSION` | `1` |

  Setting `TEAM_ID` also changes the bundle ID suffix.
- `iosApp.xcodeproj/` - The Xcode project. It has the single `iosApp` target and the
  `Compile Kotlin Framework` script phase, and it uses Xcode 16 synchronized folder groups.
  No shared scheme is committed.
