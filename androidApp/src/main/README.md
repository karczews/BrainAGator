# main

## Overview

This is the production source set of the Android app module. It holds three things:

- The manifest. It declares a single exported `MAIN`/`LAUNCHER` activity,
  `io.github.karczews.brainagator.MainActivity`, and requests no permissions.
- The Kotlin entry-point package that defines that activity.
- The Android resources the manifest references.

The `<application>` element sets no `android:name`, so the app has no custom `Application`
class. It sets `allowBackup="true"` and `supportsRtl="true"`. Its theme is
`@style/Theme.Brainagator`, a plain `android:Theme.Material.Light.NoActionBar`. All visible UI
comes from the shared Compose `App()` in `composeApp`.

The manifest gives the activity name in fully qualified form because the class's package,
`io.github.karczews.brainagator`, differs from the Gradle `namespace`,
`io.github.karczews.brainagator.app`.

## Subdirectories & Boundaries

- kotlin/io/github/karczews/brainagator: [README](./kotlin/io/github/karczews/brainagator/README.md) -
  `MainActivity`, the launcher activity. It sets up Kermit logging, enables edge-to-edge
  drawing, and calls `App()`. It provides `LocalIsDebugBuild` from the app's
  `BuildConfig.DEBUG`.
- `AndroidManifest.xml` - The application and activity declarations, and the launcher intent
  filter. It declares no permissions, services, receivers or providers.
- `res/` - The launcher label `app_name` (currently `"androidApp"`), the theme
  `Theme.Brainagator`, adaptive launcher icons (`mipmap-anydpi`, plus `.webp` fallbacks for
  mdpi to xxxhdpi), and their `drawable/` layers.
