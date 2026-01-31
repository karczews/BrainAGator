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
| Platform abstraction | commonMain/kotlin/com/example/brainagator/Platform.kt (expect) |
| Android impl | androidMain/kotlin/com/example/brainagator/Platform.android.kt (actual) |
| iOS impl | iosMain/kotlin/com/example/brainagator/Platform.ios.kt (actual) |
| JVM impl | jvmMain/kotlin/com/example/brainagator/Platform.jvm.kt (actual) |
| Web entry | webMain/kotlin/com/example/brainagator/main.kt |
| Desktop entry | jvmMain/kotlin/com/example/brainagator/main.kt |

## CONVENTIONS

### expect/actual Pattern
Use for platform-specific APIs:
- Define interface/function in `commonMain` with `expect`
- Provide implementations in each `{platform}Main` with `actual`

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
