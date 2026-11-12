# Desktop Entry Point and Platform Actuals (`jvmMain` package)

## Overview
Desktop (JVM) entry point, JVM `actual` declarations for the commonMain platform abstraction in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/Platform.kt`, and the desktop
text-to-speech backend in `tts/`. `composeApp/build.gradle.kts` sets
`mainClass = "io.github.karczews.brainagator.MainKt"`.

## Interface
- `fun main()` (`Main.kt`, class `MainKt`): calls `initializeLogger()`, then
  `application { Window(onCloseRequest = ::exitApplication, title = "brainagator") { App() } }`.
  No window size or state is passed, so Compose Desktop defaults apply.
- `actual class Platform actual constructor()` (`Platform.jvm.kt`):
  `actual val name = "Java ${System.getProperty("java.version")}"`.
- `actual val isDebugBuild: Boolean = true` (`Platform.jvm.kt`): hardcoded.
- [tts](./tts/README.md): `DesktopTextToSpeech` spawning OS speech processes;
  `createTextToSpeech()`; `rememberTextToSpeech()`.

## Invariants
- `initializeLogger()` must run before `application {}` so the first log call has a writer.
- The app has one window; closing it calls `exitApplication`, which ends the process.
- `isDebugBuild` is always `true` on desktop, including packaged DMG/MSI/DEB distributions.
- The `MainKt` class name is referenced from `build.gradle.kts`; moving or renaming `main()`
  requires updating `mainClass`.

## Side Effects
- `initializeLogger()` replaces the global Kermit log writers with the platform writer.
- `application {}` starts the Compose Desktop event loop and blocks `main()` until exit.
- Closing the window disposes the composition, which triggers `rememberTextToSpeech` cleanup.
- Constructing `Platform` reads the `java.version` system property.
- `tts/` spawns external speech processes (see [tts](./tts/README.md)).

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
- Desktop run: `./gradlew :composeApp:run`
