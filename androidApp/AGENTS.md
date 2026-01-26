# ANDROID APP MODULE

**Purpose**: Android application entry point that consumes composeApp library

## STRUCTURE
```
androidApp/
├── src/main/java/com/example/androidapp/
│   ├── MainActivity.kt      # Entry point
│   └── ui/theme/          # Android-specific theme
├── src/test/               # Unit tests (JVM)
└── src/androidTest/         # Instrumented tests (device)
```

## WHERE TO LOOK
| Task | Location |
|------|----------|
| App entry | MainActivity.kt (imports and renders composeApp.App) |
| Theme | ui/theme/{Color,Theme,Type}.kt |
| Unit tests | src/test/java/... |
| Instrumented tests | src/androidTest/java/... |

## CONVENTIONS
- **Activity**: Single MainActivity extends ComponentActivity
- **Composition**: Calls `App()` from composeApp module
- **Theme**: Uses Compose Material3 theme in ui/theme/
- **Edge-to-edge**: `enableEdgeToEdge()` called in onCreate

## ANTI-PATTERNS
- Don't implement UI directly - use shared composeApp components
- Package name mismatch: uses `com.example.androidapp` while composeApp uses `com.example.brainagator`

## NOTES
- Depends on composeApp as project dependency: `implementation(projects.composeApp)`
- Uses Compose BOM for version management
- Configured as standard Android app with minSdk 28, targetSdk 36
