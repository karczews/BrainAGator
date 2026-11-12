# iosMain

## Overview
iOS source set of the `composeApp` Kotlin Multiplatform library, compiled for `iosArm64` and
`iosSimulatorArm64` into the static `ComposeApp` framework. It holds the `MainViewController()`
entry point that the SwiftUI app in `iosApp/` hosts, the iOS `actual`s for the commonMain platform
abstraction, and the `AVSpeechSynthesizer` text-to-speech backend.

## Subdirectories & Boundaries
- kotlin/io/github/karczews/brainagator: [iOS package](./kotlin/io/github/karczews/brainagator/README.md) - `MainViewController()` (initializes logging, hosts `App()`), `actual class Platform` (UIDevice system name and version), `isDebugBuild` from `kotlin.native.Platform.isDebugBinary`, and `tts/` (`IosTextToSpeech`).
