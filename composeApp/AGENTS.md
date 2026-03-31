# COMPOSEAPP MODULE

Shared multiplatform library containing all common UI and business logic.

## Structure

```
composeApp/src/
  commonMain/kotlin/io/github/karczews/brainagator/  # Shared code (all platforms)
  androidMain/kotlin/   # Android-specific
  iosMain/kotlin/       # iOS-specific
  jvmMain/kotlin/       # Desktop/JVM-specific
  jsMain/kotlin/        # JS-specific
  wasmJsMain/kotlin/    # Wasm-specific
  commonTest/kotlin/    # Shared tests
  jvmTest/kotlin/       # JVM tests (coroutine tests go here)
```

## Where to Look

| Task | Location |
|------|----------|
| Shared UI | `commonMain/.../App.kt`, `ui/screens/` |
| Navigation | `commonMain/.../ui/navigation/Routes.kt` |
| Theme | `commonMain/.../theme/` (Color.kt, Typography.kt, Theme.kt) |
| TTS abstraction | `commonMain/.../tts/QueuedTextToSpeech.kt` (expect) |
| TTS JVM impl | `jvmMain/.../tts/DesktopTextToSpeech.kt` |
| TTS Wasm impl | `wasmJsMain/.../tts/WasmTextToSpeech.kt` |
| Platform abstraction | `commonMain/.../Platform.kt` (expect class) |
| Logger | `commonMain/.../Logger.kt` (typealias facade) |
| Resources | `commonMain/composeResources/` |

## Compose UI Conventions

### Theming & Colors
- All UI in `commonMain` using Compose Multiplatform + Material3
- Use `MaterialTheme.colorScheme.*` tokens, not hardcoded `Color(0x...)` or raw `Color.White`
- Named color constants (e.g., `MutedPurple`) are acceptable in `theme/Color.kt` but should ultimately flow through the theme system — prefer `MaterialTheme.colorScheme` for anything user-visible
- When adding custom colors, register them in the theme so they adapt to light/dark mode

### Strings & Resources
- All user-facing strings: `stringResource(Res.string.*)` — no hardcoded strings in UI code
- Add translations in `composeResources/values-{locale}/strings.xml`
- Compose resources in `commonMain/composeResources/`
- Generated accessors in `brainagator.composeapp.generated.resources`

### Previews
- Use `@Preview` annotation in `commonMain` (not `androidMain`) — supported since Compose Multiplatform 1.10
- Use `androidx.compose.ui.tooling.preview.Preview`

### Shared Element Transitions
- Use `SharedTransitionLayout` + `NavDisplay` for navigation animations
- Provide `SharedTransitionContext` via `CompositionLocalProvider` so screens can access scopes without mandatory parameters
- `LaunchedEffect` key changes: setting a value to X then back to X in the same synchronous block does not trigger recomposition — use a counter or separate trigger instead

### Code Quality
- Extract repetitive `TextStyle` / style blocks into helper functions (detekt `LongMethod` = 60 lines)
- Remove unused parameters, imports, and variables immediately — detekt flags `UnusedParameter`, `UnusedPrivateProperty`, `UnusedVariable`
- Never leave commented-out code in production files — remove it or track intent in issues

## Concurrency (KMP-specific)

### Atomic State
- **Never** `@Volatile` in `commonMain` — it's JVM-only (`kotlin.jvm.Volatile`), breaks Wasm/JS builds. Use `kotlinx.atomicfu` `atomic()` instead
- For shared mutable state, use atomic operations (`getAndSet`, `compareAndSet`) rather than separate read-then-write — the two-step pattern creates race conditions
- **Don't** use `MutableStateFlow` as an atomic variable — triggers recomposition and carries collection overhead
- `kotlinx.atomicfu` works across all KMP targets: JVM (volatile fields), Native (atomic intrinsics), Wasm/JS (single-threaded passthrough)

### Coroutines
- Use `kotlinx.coroutines` for async work
- Test with `runTest` + `StandardTestDispatcher` (see `jvmTest/` for patterns)
- Inject dispatchers, don't hardcode `Dispatchers.IO` (detekt `InjectDispatcher`)
- Cleanup event listeners / callbacks in `finally` blocks after `await()` to avoid leaks

### External Processes (JVM)
- Always `destroy()` processes in `finally` blocks to prevent orphaned subprocesses
- Use atomic swap (`getAndSet(null)`) rather than separate read + null-assign for process cleanup
- Use `runInterruptible` for blocking `waitFor()` calls so cancellation works correctly

## expect/actual Pattern

- `expect` declarations in `commonMain`, `actual` in each `{platform}Main`
- File names must match class names (detekt `MatchingDeclarationName` with `multiplatformTargets`)
- Keep platform-specific code out of `commonMain` — use expect/actual

## Module Build Commands

```bash
./gradlew :composeApp:detekt       # Static analysis
./gradlew :composeApp:ktlintCheck  # Formatting check
./gradlew :composeApp:ktlintFormat # Auto-fix formatting
./gradlew :composeApp:jvmTest      # Run JVM tests
```

## Notes
- Module configured as `androidLibrary` for Android target
- Framework output: `ComposeApp.framework` (iOS, static)
- Desktop executable configured for DMG/MSI/DEB formats
- `jvmTest` has `kotlinx-coroutines-test` dependency; `commonTest` has only `kotlin-test`
