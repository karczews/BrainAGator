# PROJECT KNOWLEDGE BASE

**Generated:** 2026-01-26
**Commit:** 2a785c7

## OVERVIEW
Kotlin Multiplatform project targeting Android, iOS, Web, and Desktop (JVM). Uses Compose Multiplatform for shared UI code across all platforms.

## STRUCTURE
```
brainagator/
├── composeApp/    # Shared multiplatform library (main codebase)
├── androidApp/    # Android application entry point
└── iosApp/        # iOS application entry point
```

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| Shared UI/Logic | composeApp/src/commonMain/kotlin | Common code for all platforms |
| Platform-specific | composeApp/src/{platform}Main/kotlin | androidMain, iosMain, jvmMain, webMain, jsMain, wasmJsMain |
| Android entry | androidApp/src/main/java | MainActivity with Compose integration |
| iOS entry | iosApp/iosApp | SwiftUI app embedding Compose framework |
| Platform abstraction | composeApp/src/*/kotlin/Platform*.kt | expect/actual pattern for platform APIs |

## CONVENTIONS

### Multiplatform Architecture
- **commonMain**: All shared business logic and Compose UI components
- **Source sets**: Separate source sets per platform (androidMain, iosMain, jvmMain, etc.)
- **expect/actual**: Use for platform-specific APIs (see Platform.kt pattern)

### Entry Point Pattern
All platforms render the same `App()` composable from `composeApp/src/commonMain/kotlin/com/example/brainagator/App.kt`

### Build System
- **Gradle version catalog**: All dependencies defined in `gradle/libs.versions.toml`
- **Toolchains**: JVM 21 for Kotlin toolchain, JDK 17 for compilation
- **Memory**: High allocation configured (4GB Gradle, 3GB Kotlin daemon)

### Code Quality
- **Qodana**: Static analysis via `qodana_code_quality.yml` workflow
- **License checking**: Enforced via Qodana

### Communication Guidelines
- **Avoid assumptions**: When investigating issues, verify claims with evidence rather than making assumptions
- **Verify "known issues"**: Don't label issues as "known" or "tracked" without verifying ticket/issue tracker references
- **Be precise**: Distinguish between what is actually known vs. what is inferred from symptoms
- **Prefer new commits**: When making updates to existing changes (including during code review), create new commits instead of amending existing ones. This preserves the history and makes it easier to track what changed.

**Example of inferred vs. known:**
- ❌ *Inferred*: "This is a known issue with Kotlin 2.3.10" (without evidence)
- ✅ *Known*: "The stack trace shows the warning originates from Kotlin plugin's internal artifact creation code at `KotlinTargetArtifactKt.createPublishArtifact`"

## ANTI-PATTERNS (THIS PROJECT)
- Package name mismatch: `androidApp` uses `com.example.androidapp`, `composeApp` uses `io.github.karczews.brainagator`
- Dual Android modules: Both `androidApp` (app) and `composeApp` (library) are Android targets - potentially confusing

## UNIQUE STYLES
- Hot reload support via Compose Hot Reload plugin
- Configuration cache and build caching enabled by default
- Non-transitive R classes for Android
- Kotlin code style: official

## COMMANDS
```bash
# Android
./gradlew :composeApp:assembleDebug

# Desktop (JVM)
./gradlew :composeApp:run

# Web Wasm
./gradlew :composeApp:wasmJsBrowserDevelopmentRun  # Start dev server (local development)
./gradlew :composeApp:wasmJsBrowserDevelopmentWebpack  # Build WASM bundle (CI/artifacts)

# Web JS
./gradlew :composeApp:jsBrowserDevelopmentRun

# Hot Reload
./gradlew reload

# Tests
./gradlew testDebug
```

## NOTES
- Project has dual Android modules (androidApp + composeApp as androidLibrary) - document rationale if intentional
- Package names should align across modules for consistency
- Qodana token uses project-specific instance ID - may need updating if migrating Qodana instance