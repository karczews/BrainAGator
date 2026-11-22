# iOS Entry Point and Platform Actuals (`iosMain` package)

## Overview
iOS entry point exported through the static `ComposeApp` framework, iOS `actual` declarations
for the commonMain platform abstraction in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/Platform.kt`, and the iOS
text-to-speech backend in `tts/`. The SwiftUI host calls `MainViewControllerKt.MainViewController()`
from `iosApp/iosApp/ContentView.swift`.

## Interface
- `fun MainViewController()` (`MainViewController.kt`): returns a `ComposeUIViewController` whose
  content calls `initializeLogger()` and then hosts the commonMain root composable `App()`.
  Takes no parameters.
- `actual class Platform actual constructor()` (`Platform.ios.kt`):
  `actual val name = UIDevice.currentDevice.systemName() + " " + systemVersion`.
- `actual val isDebugBuild: Boolean = kotlin.native.Platform.isDebugBinary` (`Platform.ios.kt`,
  `@OptIn(ExperimentalNativeApi::class)`).
- [tts](./tts/README.md): `IosTextToSpeech` over `AVSpeechSynthesizer`; `createTextToSpeech()`;
  `rememberTextToSpeech()`.

## Invariants
- The function name `MainViewController` is part of the Swift contract; renaming it breaks
  `iosApp`.
- `isDebugBuild` follows whether the Kotlin/Native binary was compiled as debug. The KDoc above it
  mentions a `-Dkotlin.native.isDebugBuild` flag, but the code reads only `isDebugBinary`.
- `MainViewController` does not provide `LocalIsDebugBuild`, so on iOS it defaults to
  `isDebugBuild`.
- `initializeLogger()` runs inside the composable content lambda, before `App()`, so it runs
  before any app logging.
- `Platform.name` reads UIKit `UIDevice` on the constructing thread, with no thread guard.

## Side Effects
- Each `MainViewController()` call creates a new Compose `UIViewController`.
- `initializeLogger()` replaces the global Kermit log writers with the platform writer.
- Constructing `Platform` queries `UIDevice.currentDevice`.
- `tts/` drives `AVSpeechSynthesizer` audio output (see [tts](./tts/README.md)).

## Verification
- Run from `iosApp/`: `xcodebuild build -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17' -derivedDataPath DerivedData CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO`
