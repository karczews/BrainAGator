# COMPOSEAPP MODULE

**Purpose**: Shared multiplatform library containing all common UI and business logic

## STRUCTURE
```
composeApp/
├── src/
│   ├── commonMain/kotlin/        # Shared code (all platforms)
│   ├── androidMain/kotlin/       # Android-specific
│   ├── iosMain/kotlin/           # iOS-specific
│   ├── jvmMain/kotlin/           # Desktop/JVM-specific
│   ├── webMain/kotlin/            # Web-specific
│   ├── jsMain/kotlin/             # JS-specific
│   ├── wasmJsMain/kotlin/         # Wasm-specific
│   └── commonTest/kotlin/         # Shared tests
```

## WHERE TO LOOK
| Task | Location |
|------|----------|
| Shared UI | commonMain/kotlin/com/example/brainagator/App.kt |
| Platform abstraction | commonMain/kotlin/.../Platform.kt (expect class) |
| Android impl | androidMain/kotlin/.../Platform.android.kt (actual class) |
| iOS impl | iosMain/kotlin/.../Platform.ios.kt (actual class) |
| JVM impl | jvmMain/kotlin/.../Platform.jvm.kt (actual class) |
| Web entry | webMain/kotlin/com/example/brainagator/main.kt |
| Desktop entry | jvmMain/kotlin/com/example/brainagator/main.kt |

## CONVENTIONS

### expect/actual Pattern
Use for platform-specific APIs:
- Define `expect class` in `commonMain` with the same name as the file (e.g., `Platform` in `Platform.kt`)
- Provide `actual class` implementations in each `{platform}Main` with matching filenames (e.g., `Platform.android.kt` contains `actual class Platform`)

**Enforced by detekt**: The `MatchingDeclarationName` rule is enabled with `multiplatformTargets` configured. This requires that platform-specific classes match their filenames to avoid naming violations. Using `expect class` with identical names across platforms satisfies this requirement and is the idiomatic KMP approach.

### Shared UI Rules
- All UI components in `commonMain` using Compose Multiplatform
- All platforms render `App()` composable from commonMain
- Use `Material3` for consistent design

### Resource Management
- Compose resources in `commonMain/composeResources/`
- Generated accessors in `brainagator.composeapp.generated.resources`

## ANTI-PATTERNS
- Don't put platform-specific code in `commonMain` - use expect/actual
- Don't create platform-specific UI components - keep in commonMain when possible

## NOTES
- Module configured as `androidLibrary` for Android target
- Framework output: `ComposeApp.framework` (iOS, static)
- Desktop executable configured for DMG/MSI/DEB formats
