# Contributing Guide

Thank you for your interest in contributing to BrainAGator! This guide will help you get started with the project setup.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Code Style](#code-style)
  - [Pre-commit Hook](#pre-commit-hook)
- [Development Workflow](#development-workflow)
  - [Project Structure](#project-structure)
  - [Building and Running](#building-and-running)
  - [Hot Reload](#hot-reload-desktop-development)
  - [Running Checks](#running-checks-before-committing)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Submitting Changes](#submitting-changes)

## Prerequisites

- [JDK 21](https://adoptium.net/) or later
- [Git](https://git-scm.com/)

## Project Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/karczews/BrainAGator.git
   cd BrainAGator
   ```

2. **Set up the pre-commit hook** (see below)

3. **Build the project**
   ```bash
   ./gradlew build
   ```

## Code Style

This project uses [ktlint](https://pinterest.github.io/ktlint/) to enforce consistent Kotlin code style. The configuration is defined in `.editorconfig` at the project root.

### Pre-commit Hook

We provide a pre-commit Git hook that automatically checks and formats your code before each commit. This ensures all contributions follow the project's code style.

#### Setup

The pre-commit hook must be manually installed in your local Git repository:

```bash
# From the project root
cp git-hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

**Location:** The hook file is stored in `git-hooks/pre-commit` (shared in the repo) and should be copied to `.git/hooks/pre-commit` (your local Git hooks directory).

**Note:** The `.git/hooks/` directory is not tracked by Git, so each contributor must set up the hook individually.

#### What the Hook Does

1. **Runs automatically** before each commit
2. **Executes ktlint format** on all Kotlin files
3. **Warns you** if any files were modified by the formatter
4. **Blocks the commit** if files were changed, requiring you to review and stage them manually

#### Example Workflow

```bash
# Make your changes
vim composeApp/src/.../MyFile.kt

# Stage your files
git add composeApp/src/.../MyFile.kt

# Attempt to commit
git commit -m "feat: add new feature"
# Hook runs ktlint format...
# If files were modified:
#   ❌ Commit blocked
#   ✅ Hook shows which files changed
#   ⚠️  You must review and stage manually

# Review the changes
git diff

# Stage the formatted files
git add composeApp/src/.../MyFile.kt

# Commit again
git commit -m "feat: add new feature"
# ✅ Commit succeeds
```

#### Bypassing the Hook (Not Recommended)

If you absolutely need to bypass the hook (e.g., for a work-in-progress commit):

```bash
git commit --no-verify -m "WIP: temporary commit"
```

**Warning:** Bypassing the hook may result in CI failures. Always run `./gradlew ktlintCheck` before pushing.

#### Troubleshooting

**Hook not running?**
- Ensure the file is executable: `chmod +x .git/hooks/pre-commit`
- Verify it's in the correct location: `.git/hooks/pre-commit`

**Hook blocking commits unexpectedly?**
- Check if ktlint modified any files: `git status`
- Review the changes: `git diff`
- Stage only the intended files: `git add <specific-files>`

**Want to disable the hook temporarily?**
```bash
mv .git/hooks/pre-commit .git/hooks/pre-commit.disabled
```

To re-enable:
```bash
mv .git/hooks/pre-commit.disabled .git/hooks/pre-commit
```

## Development Workflow

### Project Structure

Understanding the project structure helps you know where to make changes:

- **`/composeApp/src/commonMain`** - Code shared across all platforms (Android, iOS, Web, Desktop)
- **`/composeApp/src/{platform}Main`** - Platform-specific code (e.g., `androidMain`, `iosMain`, `jvmMain`, `jsMain`)
- **`/iosApp`** - iOS application entry point (contains Swift code for iOS-specific features)
- **`/androidApp`** - Android application entry point

### Building and Running

Before submitting changes, test your code by building and running the application:

#### Android Application

**macOS/Linux:**
```bash
./gradlew :androidApp:assembleDebug
```

**Windows:**
```shell
.\gradlew.bat :androidApp:assembleDebug
```

#### Desktop (JVM) Application

**macOS/Linux:**
```bash
./gradlew :composeApp:run
```

**Windows:**
```shell
.\gradlew.bat :composeApp:run
```

#### Web Application

**Wasm target** (faster, modern browsers) - **macOS/Linux:**
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

**Wasm target** (faster, modern browsers) - **Windows:**
```shell
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
```

**JS target** (slower, supports older browsers) - **macOS/Linux:**
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

**JS target** (slower, supports older browsers) - **Windows:**
```shell
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

#### iOS Application

Open the `/iosApp` directory in Xcode and run it from there, or use the run configuration in your IDE.

### Hot Reload (Desktop Development)

For rapid iteration during desktop development:

```bash
# Explicit reload
./gradlew reload

# Alternative
./gradlew :composeApp:hotReloadJvmMain

# Auto-reload on source changes
./gradlew :composeApp:hotRunJvm --auto
```

**Note:** Hot reload requires the JetBrains Runtime (JB runtime).

### Running Checks Before Committing

Always run these commands before pushing your changes:

```bash
# Check code style
./gradlew ktlintCheck

# Build the project
./gradlew build

# Run tests (if available)
./gradlew test
```

## Commit Message Guidelines

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification for commit messages. This format helps generate changelogs and makes the project history easier to understand.

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

The header is required. Scope is optional. Keep all lines under 100 characters.

### Commit Types

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat: Add offline map support` |
| `fix` | Bug fix | `fix: Resolve crash on null response` |
| `docs` | Documentation changes | `docs: Update API usage examples` |
| `style` | Code style changes (formatting, semicolons, etc.) | `style: Fix indentation in utils` |
| `refactor` | Code refactoring (no behavior change) | `refactor: Extract common validation logic` |
| `perf` | Performance improvements | `perf: Optimize image loading` |
| `test` | Adding or updating tests | `test: Add unit tests for parser` |
| `chore` | Build process or auxiliary tool changes | `chore: Update Gradle wrapper` |
| `ci` | CI/CD configuration changes | `ci: Add GitHub Actions workflow` |

### Subject Line Rules

- Use imperative, present tense: "Add feature" not "Added feature"
- Capitalize the first letter
- No period at the end
- Maximum 70 characters
- Be concise but descriptive

### Examples

**Simple feature:**
```
feat: Add user authentication

Implement JWT-based authentication for API endpoints.
Adds login, logout, and token refresh functionality.
```

**Bug fix with scope:**
```
fix(api): Handle null response in user endpoint

The user API could return null for deleted accounts,
causing a crash in the dashboard. Add null check
before accessing user properties.
```

**Breaking change:**
```
feat(api)!: Remove deprecated v1 endpoints

Remove all v1 API endpoints that were deprecated
in version 23.1. Clients should migrate to v2.

BREAKING CHANGE: v1 endpoints no longer available
```

**Reference issue:**
```
fix: Correct calculation in price formatter

The formatter was using wrong decimal places
for currencies with 3+ decimals.

Closes #123
```

### Tips

- **Single purpose**: Each commit should do one thing
- **Explain why**: Describe why the change was made, not just what
- **Reference issues**: Use "Fixes #123", "Closes #456", or "Relates to #789"
- **Breaking changes**: Mark with `!` after type/scope or use `BREAKING CHANGE:` footer

## Submitting Changes

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the code style guidelines

3. **Run checks locally**
   ```bash
   ./gradlew ktlintCheck
   ./gradlew build
   ```

4. **Commit your changes** using [Conventional Commits](https://www.conventionalcommits.org/) format (the pre-commit hook will run automatically)

5. **Push to your fork** and create a Pull Request

## Questions?

If you have questions or need help, please open an issue on GitHub.
