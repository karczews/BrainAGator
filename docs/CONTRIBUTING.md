# Contributing Guide

Thank you for your interest in contributing to BrainAGator! This guide will help you get started with the project setup.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Code Style](#code-style)
  - [Pre-commit Hook](#pre-commit-hook)
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

4. **Commit your changes** (the pre-commit hook will run automatically)

5. **Push to your fork** and create a Pull Request

## Questions?

If you have questions or need help, please open an issue on GitHub.
