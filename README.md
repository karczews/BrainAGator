# BrainAGator Blueprint

[![Website](https://img.shields.io/badge/website-live-6C63FF?logo=github)](https://karczews.github.io/BrainAGator/)
[![Privacy Policy](https://img.shields.io/badge/privacy%20policy-read-43C59E?logo=github)](https://karczews.github.io/BrainAGator/privacy-policy.html)

## System Overview

This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that's common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple's CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you're sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

BrainAGator is a set of educational games for young children, built with Compose
Multiplatform and Material3. All UI and logic live in the shared `composeApp` library; the code
is in its `commonMain` source set. The platform apps are thin hosts. Each has one entry point
that calls `initializeLogger()` and then shows the shared `App()` composable:

| Platform | Entry point |
| -------- | ----------- |
| Android | `MainActivity` in `androidApp` |
| iOS | `MainViewController()` in `composeApp/src/iosMain`, hosted by the SwiftUI shell in `iosApp` |
| Desktop | `main()` in `composeApp/src/jvmMain` |
| Web (JS and Wasm) | `main()` in `composeApp/src/webMain` |

`App()` runs a Navigation 3 back stack with three kinds of screen: a home pager of games, a
game screen, and a win screen. There are 7 games. Shape Match and Number Order are playable;
the other 5 are "coming soon" placeholders. Spoken instructions and feedback come from a
queued text-to-speech abstraction in `commonMain`. Each platform implements it on its native
speech engine.

## Local Interface & Invariants

The root directory holds the build and tooling configuration that applies to every module:

- `settings.gradle.kts`: root project `brainagator`. Includes `:composeApp` and `:androidApp`,
  and enables `TYPESAFE_PROJECT_ACCESSORS`, which provides `projects.composeApp`.
- `build.gradle.kts`:
  - Applies ktlint to all projects, excluding `build/` and `generated/` paths.
  - Applies detekt to each subproject with these settings:
    - Config: `config/detekt/detekt.yml`, with `buildUponDefaultConfig = true`.
    - Baseline: each module's own `detekt-baseline.xml`.
    - `jvmTarget = "21"`.
    - Reports: HTML, SARIF and Checkstyle, written to `build/reports/`.
- `gradle.properties`:
  - Sets `app.versionCode` and `app.versionName`. The release workflow rewrites both.
  - Turns on configuration cache and build cache.
- `gradle/libs.versions.toml`: the version catalog, including the Android SDK levels.
- `commitlint.config.mjs`: local commit-message linting through husky. Extends
  `@commitlint/config-conventional` and limits the commit types to `feat`, `fix`, `docs`,
  `style`, `refactor`, `perf`, `test`, `chore`, `ci`, `build` and `revert`.
- `package.json`: dev-only Node tooling (husky, commitlint). It has no `test` script.
- `entitlements.plist`: macOS entitlements for the desktop package. The `composeApp` build
  fails during configuration if this file is missing.
- `.licenserc.yaml`: every `**/*.kt` outside `build/`, `.gradle/`, `.idea/` and `iosApp/` must
  carry the Apache-2.0 header for Krzysztof Karczewski.
- [AGENTS.md](./AGENTS.md): code style, detekt limits and commit conventions for agents.

## Subdirectories & System Boundaries

- composeApp: [README](./composeApp/README.md) - The shared KMP library. It contains:
  - `App()` and its navigation.
  - Theme and design tokens.
  - The queued `TextToSpeech` abstraction, with one backend per platform.
  - Tactile UI elements, the screens and the games.
  - The iOS, desktop and web entry points.

  It is built as an Android library, the static `ComposeApp` iOS framework, a desktop app
  (DMG, MSI, DEB), and JS and Wasm browser executables.
- androidApp: [README](./androidApp/README.md) - The Android application module. It is a thin
  host with one launcher activity, `MainActivity`, which provides `LocalIsDebugBuild` from
  `BuildConfig.DEBUG` and calls `App()`. Release builds are signed using environment
  variables and published to the Play `alpha` track.
- iosApp: [README](./iosApp/README.md) - The Xcode project and SwiftUI shell. It hosts
  `MainViewControllerKt.MainViewController()` full-screen. Every Xcode build runs
  `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` to produce the framework.
- scripts: [README](./scripts/README.md) - Scripts you run by hand.
  `generate_density_images.sh` uses `ffmpeg` to resize a PNG into the mdpi to xxhdpi
  `drawable-*` folders under `composeApp/src/commonMain/composeResources/`.
- docs: [README](./docs/README.md) - Human documentation: the
  [Contributing Guide](./docs/CONTRIBUTING.md) and the [design system](./docs/DESIGN.md).
  `docs/webpages/` holds the GitHub Pages site and the privacy policy. Not an EGM contract.
- `config/` - `detekt/detekt.yml`, the shared detekt rules.
- `gradle/` - The version catalog and the Gradle wrapper.
- `designs/` - Image prototypes of the mascot and assets, stored in Git LFS. No code.
- `git-hooks/` - The `pre-commit` hook template.

## Global Invariants

- Shared UI and logic go in `composeApp/src/commonMain`. Platform APIs are reached only
  through `expect`/`actual`. The app modules (`androidApp`, `iosApp`) contain no UI, theme or
  navigation.
- Every entry point calls `initializeLogger()` before `App()` logs anything. Code imports the
  project `io.github.karczews.brainagator.Logger` typealias, never Kermit directly.
- Shared UI reads `LocalIsDebugBuild.current`, not `isDebugBuild`. `isDebugBuild` is wrong on
  some platforms, so only `LocalIsDebugBuild` is reliable:

  | Platform | `isDebugBuild` | Correct for release builds? |
  | -------- | -------------- | --------------------------- |
  | Android (`composeApp`) | hard-coded `true` | Yes, because `androidApp` overrides `LocalIsDebugBuild` with `BuildConfig.DEBUG` |
  | Desktop | hard-coded `true` | No |
  | JS, Wasm | hard-coded `false` | No |
  | iOS | Kotlin/Native `isDebugBinary` | Yes |

- Cross-module names that must stay stable:
  - The `ComposeApp` framework `baseName` and `MainViewControllerKt.MainViewController()`,
    which the Swift code uses.
  - `mainClass = io.github.karczews.brainagator.MainKt` for desktop.
  - The fully qualified `io.github.karczews.brainagator.MainActivity` in the Android manifest.

  All Kotlin code uses the package `io.github.karczews.brainagator`. The `androidApp` Gradle
  namespace is `io.github.karczews.brainagator.app`.
- Every platform TTS backend extends `QueuedTextToSpeech`. These rules apply:
  - Utterances play one at a time, in FIFO order.
  - `performSpeak` suspends until speech ends.
  - Only the non-suspending engine start call goes inside `withSpeakStartLock`.
- User-facing strings come from `stringResource(Res.string.*)`, in English and Polish. Colors
  come from `MaterialTheme.colorScheme` or `AppTheme.extendedColors`, and sizes from `Dimens`.
  The theme is light-only. Known exceptions: the home screen's build-info label and logo text
  are hardcoded, and the fireworks, rainbow and Shape Match tile palettes are hardcoded colors.
- CI enforces:
  - `ktlintCheck`.
  - `detekt`.
  - `testDebug` and the Roborazzi screenshot check.
  - Apache-2.0 headers on `.kt` files.
  - Conventional Commits, checked by `commisery` in CI and `commitlint` locally.
  - Android instrumentation, iOS and Wasm builds.

## Global Side Effects

- **Audio.** Text-to-speech plays through each platform's engine:
  - Android: the `android.speech.tts` engine.
  - iOS: `AVSpeechSynthesizer`.
  - Desktop: `say`, PowerShell `System.Speech` or `espeak`, run as child processes.
  - Browsers: the Web Speech API.
- **Logging.** Start-up replaces Kermit's global log writers with the platform writer
  (Logcat, the console, or stdout).
- **No network or storage.** The app code makes no network calls and keeps no persistent
  storage. The only saved state is the Navigation `rememberSaveable` welcome flag, plus
  Android `allowBackup="true"`.
- **CI and release (`.github/workflows/`):**
  - Every push to `main` runs the release workflow (`release.yml`). It computes the next
    version from Conventional Commits. Only when that produces a version bump does it publish
    the Android bundle to Google Play (`alpha`) and create a GitHub release.
  - Pushes to `main` that touch app, build or `docs/webpages/` paths trigger `pages.yml`. It
    builds the Wasm distribution and deploys it to GitHub Pages under `app/`, together with
    `docs/webpages/`.
  - `opencode-review.yml` runs an opencode AI review on each non-bot PR, and `opencode.yml`
    responds to `/oc` or `/opencode` comments. `android-release-build.yml` is a manual signed
    APK/AAB build.
- **Build-time.** Xcode builds invoke Gradle. `scripts/generate_density_images.sh` overwrites
  images in `composeResources/drawable-*`.

## System Verification

- Run: `./gradlew testDebug` (the CI unit-test task; it currently runs only `:androidApp:testDebugUnitTest`)
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache` (JVM and coroutine tests)
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache` (screenshot goldens)
- Run: `./gradlew ktlintCheck` and `./gradlew detekt`
- Run: `./gradlew :composeApp:wasmJsBrowserDevelopmentWebpack` (Wasm build)
- Run: `./gradlew connectedAndroidTest` (instrumentation tests; needs a device or emulator)
- Run from `iosApp/`: `xcodebuild build -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17' -derivedDataPath DerivedData CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO`

## Build and Run

Quick commands to build and run the application:

```bash
# Android
./gradlew :androidApp:assembleDebug

# Desktop (JVM)
./gradlew :composeApp:run

# Web (Wasm)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# iOS - Open in Xcode
open iosApp
```

For detailed development workflow including hot reload, see the [Contributing Guide](docs/CONTRIBUTING.md).

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).

---

## Contributing

We welcome contributions! Please see our [Contributing Guide](docs/CONTRIBUTING.md) to get started.
