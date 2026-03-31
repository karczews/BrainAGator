# BrainAGator — Agent Instructions

Kotlin Multiplatform (KMP) project targeting Android, iOS, Web (Wasm/JS), and Desktop (JVM). Shared UI via Compose Multiplatform + Material3.

## Project Structure

```text
composeApp/src/
  commonMain/kotlin/io/github/karczews/brainagator/  # Shared UI + logic (ALL platforms)
  androidMain/  iosMain/  jvmMain/  jsMain/  wasmJsMain/  # Platform-specific (expect/actual)
  commonTest/   jvmTest/   # Tests
androidApp/  # Android app entry point
iosApp/      # iOS app entry point (SwiftUI + Compose framework)
```

Base package: `io.github.karczews.brainagator`

## Build & Run Commands

```bash
./gradlew build                                    # Full build
./gradlew :androidApp:assembleDebug                # Android debug APK
./gradlew :composeApp:run                          # Desktop (JVM)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun  # Web dev server
./gradlew :composeApp:jsBrowserDevelopmentRun      # Web (JS) dev server
./gradlew reload                                   # Hot reload (desktop, needs JB runtime)
```

## Test Commands

```bash
./gradlew testDebug                                        # All tests (debug)
./gradlew :composeApp:jvmTest                              # JVM tests only
./gradlew :composeApp:testDebugUnitTest                    # Android unit tests
./gradlew :composeApp:jvmTest --tests "*.QueuedTextToSpeechTest"  # Single test class
./gradlew :composeApp:jvmTest --tests "*.speakExecutesPerformSpeak"      # Single test
```

Test framework: `kotlin.test` (`assertEquals`, `assertTrue`, `assertFalse`). Coroutine tests use `kotlinx-coroutines-test` with `runTest` + `StandardTestDispatcher`.

## Lint & Format Commands

```bash
./gradlew ktlintCheck       # Check formatting (CI runs this)
./gradlew ktlintFormat      # Auto-fix formatting
./gradlew detekt            # Static analysis (CI runs this)
./gradlew detektBaseline    # Generate detekt baseline
```

**Always run `ktlintCheck` and `detekt` before committing** — CI enforces both.

## Code Style

### Formatting (.editorconfig)
- Indent: 4 spaces, no tabs
- Max line length: 140 chars (ktlint), 180 chars (detekt)
- Charset: UTF-8, LF line endings, final newline required
- `ktlint_function_naming_ignore_when_annotated_with=Composable`

### License Header
Every `.kt` file starts with the Apache 2.0 copyright header (enforced by CI):
```kotlin
/*
 * Copyright <YEAR> Krzysztof Karczewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * ...
 */
```

### Imports
- No wildcard imports (detekt `WildcardImport` active, except `java.util.*`)
- Order: standard ktlint import ordering
- Import `Logger` as `io.github.karczews.brainagator.Logger` — **never** import Kermit directly

### Naming
- Classes: `PascalCase` — detekt enforces `[A-Z][a-zA-Z0-9]*`
- Functions: `camelCase` — `@Composable` functions exempted from naming rules
- Variables/params: `camelCase`
- Constants: `SCREAMING_SNAKE_CASE` or `camelCase` for top-level properties
- Enum entries: `PascalCase` (e.g., `ShapeMatch`, `NumberOrder`)

### Types & Nullability
- Use Kotlin non-null types preferentially; avoid `!!` (detekt `UnsafeCallOnNullableType`)
- Use safe calls `?.`, elvis `?:`, and safe casts `as?`
- No referential equality on strings (`===`) — use structural `==` (detekt `AvoidReferentialEquality`)

### Error Handling
- Use `check`, `require`, `error` for programming errors (detekt `UseCheckOrError`, `UseRequire`)
- No swallowed exceptions — at minimum log them
- No `printStackTrace()` — use Logger instead
- No generic `Exception` catches in production code (detekt `TooGenericExceptionCaught`)
- `CancellationException` pattern: catch, perform cleanup (stop/release resources), rethrow

### Logger Usage
```kotlin
import io.github.karczews.brainagator.Logger

Logger.i { "Started" }
Logger.e(e) { "Failed to do X" }  // Exception as first param, no ${e.message} in string
```

### Detekt Key Limits
- `LongMethod`: 60 lines
- `LargeClass`: 600 lines
- `LongParameterList`: 6 function params, 7 constructor params (data classes exempt)
- `NestedBlockDepth`: 4 levels
- `ReturnCount`: max 2 returns per function
- `CyclomaticComplexMethod`: complexity ≤ 15
- `TooManyFunctions`: 11 per file/class/interface/object (tests exempt)
- `NewLineAtEndOfFile`: files must end with a newline
- No `FIXME:`, `STOPSHIP:`, `TODO:` comments (detekt `ForbiddenComment`)

## Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):
```text
<type>(scope): <lowercase imperative description>
<type>: <lowercase imperative description>

Types: feat fix docs style refactor perf test chore ci
Breaking: <type>!: description or BREAKING CHANGE: footer
```
CI validates commit message format via `commisery`.

## CI Checks (all must pass)
- `ktlintCheck` — formatting
- `detekt` — static analysis
- `testDebug` — unit tests
- `check-copyrights` — license headers
- `conventional-commits` — commit message format
- `android-instrumentation-tests`, `ios-build`, `wasm-build` — platform builds

## Key Dependencies
- Compose Multiplatform 1.10.3, Material3
- Navigation 3 (navigation3-ui)
- Kotlin 2.3.20, JVM toolchain 21
- kotlinx-serialization-json, kotlinx-atomicfu
- Kermit (via Logger typealias)
- Kotlin test + kotlinx-coroutines-test for testing
