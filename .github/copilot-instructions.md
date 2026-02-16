# Copilot Instructions for BrainAGator

## Before Making Any Changes

**Always read the contributing guide first:**
- Read `docs/CONTRIBUTING.md` completely before starting any work
- Follow all code style guidelines (ktlint, conventional commits)
- Understand the project structure and where to make changes

## Key Guidelines from CONTRIBUTING.md

### 1. Code Style
- Use ktlint for formatting
- Follow `.editorconfig` settings
- Pre-commit hook will run automatically

### 2. Conventional Commits
Follow the commit message format:
```
<type>(<scope>): <subject>

<body>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `ci`

### 3. Logger Usage
- Use the Logger typealias: `import io.github.karczews.brainagator.Logger`
- **Never** import Kermit directly
- Pass exception as first parameter: `Logger.e(e) { "Error message" }`
- Don't include `${e.message}` in log strings (redundant)

### 4. Project Structure
- `/composeApp/src/commonMain` - Shared code across all platforms
- `/composeApp/src/{platform}Main` - Platform-specific code
- `/iosApp` - iOS entry point
- `/androidApp` - Android entry point

### 5. Before Committing
Always run:
```bash
./gradlew ktlintCheck
./gradlew build
```

### 6. Testing
- Build and run on target platforms before submitting
- Test Android: `./gradlew :androidApp:assembleDebug`
- Test Desktop: `./gradlew :composeApp:run`
- Test Web: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`

## PR Requirements
- All CI checks must pass
- Follow conventional commit format
- Include appropriate scope in commits
- Update documentation if needed

## Questions?
When in doubt, refer to `docs/CONTRIBUTING.md` for detailed guidelines.
