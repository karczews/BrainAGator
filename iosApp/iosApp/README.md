# iOS App Shell (`iosApp/iosApp`)

## Overview

This directory is the SwiftUI app shell for iOS. It declares the `@main` app and puts one
UIKit view controller on screen, full-screen. That controller comes from the Kotlin/Native
`ComposeApp` framework built from the `composeApp` module, and it hosts the shared Compose UI
(`App()`). All screens, logic and platform services live in `composeApp`. The Swift code here
only bridges into it.

## Interface

- `iOSApp: App` (`iOSApp.swift`, `@main`): the process entry point. Its `body` is a single
  `WindowGroup { ContentView() }`. It has no state, no app delegate adaptor and no scene-phase
  handling.
- `ContentView: View` (`ContentView.swift`): the root view. Its `body` is
  `ComposeView().ignoresSafeArea()`.
- `ComposeView: UIViewControllerRepresentable` (`ContentView.swift`):
  - `makeUIViewController(context:)` returns `MainViewControllerKt.MainViewController()`.
  - `updateUIViewController(_:context:)` is empty.
- External dependencies:
  - The Swift module `ComposeApp`, imported with `import ComposeApp`. It is a static framework
    with `baseName = "ComposeApp"`, built for `iosArm64`/`iosSimulatorArm64` in
    `composeApp/build.gradle.kts`.
  - The Kotlin function `MainViewController()` in
    `composeApp/src/iosMain/kotlin/io/github/karczews/brainagator/MainViewController.kt`. It
    returns `ComposeUIViewController { initializeLogger(); App() }`.
- Non-code files:
  - `Info.plist`. Its only key is `CADisableMinimumFrameDurationOnPhone = true`.
  - `Assets.xcassets/` and `Preview Content/`.

## Invariants

- Exactly one `@main` type, `iOSApp`, may exist in the iOS target.
- Swift calls Kotlin through two exported names. If either is renamed, the Swift build breaks.
  - `MainViewControllerKt.MainViewController()`: the top-level function `MainViewController`
    in `MainViewController.kt`.
  - `import ComposeApp`: the framework `baseName`.
- `makeUIViewController` must return a new controller from `MainViewController()` on each call.
  Do not cache or share one Compose controller between representables.
- No data flows from SwiftUI into Compose. `updateUIViewController` is empty, and all UI state
  lives in `App()`. A new SwiftUI input needs an explicit bridge.
- `.ignoresSafeArea()` makes Compose draw edge-to-edge, so the shared UI must handle insets
  itself.
- On iOS, logging is set up only by `initializeLogger()` inside the Compose content lambda. The
  Swift layer must not log before that runs.
- Keep `CADisableMinimumFrameDurationOnPhone = true` in `Info.plist`.

## Side Effects

- At launch, the app creates a `WindowGroup` scene and, through `MainViewController()`, a
  `ComposeUIViewController` that hosts the Compose runtime.
- When the content composes, `initializeLogger()` replaces the global Kermit log writers with
  the platform writer. The call is not wrapped in `remember` or `LaunchedEffect`, so it runs
  again whenever the root lambda runs again.
- `Info.plist` allows high refresh rates on ProMotion iPhones.
- The Swift code does no disk, network, `UserDefaults` or notification work of its own. Other
  platform effects, such as text-to-speech, come from `composeApp`.

## Verification

- Run from `iosApp/` (the parent directory, not this one):
  `xcodebuild build -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17' -derivedDataPath DerivedData CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO`
