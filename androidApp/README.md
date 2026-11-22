# androidApp

## Overview

This is the Android application module, a thin host for the shared Compose UI. It depends on
the shared module through `implementation(projects.composeApp)` and renders `App()` from
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/App.kt`. The module itself
contains no shared UI, theme or navigation code.

Build configuration (`build.gradle.kts`):

- Plugins: `com.android.application`, the Kotlin Compose compiler plugin, and Gradle Play
  Publisher (`com.github.triplet.play`).
- Identity:

  | Setting | Value |
  | ------- | ----- |
  | `namespace` | `io.github.karczews.brainagator.app` (where `BuildConfig` is generated) |
  | `applicationId` | `io.github.karczews.brainagator` |
  | `compileSdk` | 37 |
  | `targetSdk` | 36 |
  | `minSdk` | 28 |

  The SDK levels come from `gradle/libs.versions.toml`. `versionCode` and `versionName` are
  read from `app.versionCode` and `app.versionName` in the root `gradle.properties`.
- `buildFeatures`: `compose = true`, `buildConfig = true`. `buildConfig` must stay on because
  `MainActivity` reads `BuildConfig.DEBUG`.
- Java and Kotlin both target JVM 11.
- `release` build type:
  - Not minified.
  - Signed with `android-release.jks` from this directory. The file is gitignored. The
    passwords and alias come from the environment variables `KEYSTORE_PASSWORD`, `KEY_ALIAS`
    and `KEY_PASSWORD`.
- `play {}`: publishes app bundles to the `alpha` track with `ResolutionStrategy.AUTO`.
  Credentials come from the `ANDROID_PUBLISHER_CREDENTIALS` environment variable. Uploads
  happen only when Play Publisher tasks run. `AUTO` version-code resolution also queries Play
  during release assemble and bundle builds, so those builds need the credentials too (see
  `.github/actions/android-release/action.yml`).

## Subdirectories & Boundaries

- src: [README](./src/README.md) - The source sets. `main` holds the manifest (one exported
  launcher activity, no permissions, `allowBackup="true"`), the `MainActivity` entry point
  and the resources. `MainActivity` sets up logging, enables edge-to-edge drawing, and hosts
  `App()` with `LocalIsDebugBuild` taken from `BuildConfig.DEBUG`. `test` and `androidTest`
  contain only tests.
- `build.gradle.kts` - The module build script (see Overview).
- `proguard-rules.pro` - The stock template with every rule commented out. It has no effect
  while release minification is off.
- `.gitignore` - Ignores `/build`, `android-release.jks` and `play-service-account.json`.
