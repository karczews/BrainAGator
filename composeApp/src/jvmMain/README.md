# jvmMain

## Overview
Desktop (JVM) source set of the `composeApp` Kotlin Multiplatform library. It holds the desktop
`main()` entry point (`MainKt`, packaged as DMG/MSI/DEB), the JVM `actual`s for the commonMain
platform abstraction, and the text-to-speech backend that drives OS speech commands. Its tests
live in the separate `jvmTest` source set.

## Subdirectories & Boundaries
- kotlin/io/github/karczews/brainagator: [Desktop package](./kotlin/io/github/karczews/brainagator/README.md) - `main()` (initializes logging, opens one "brainagator" window hosting `App()`), `actual class Platform` ("Java <version>"), `isDebugBuild` hardcoded `true`, and `tts/` (`DesktopTextToSpeech` spawning `say`, PowerShell, or `espeak`).
