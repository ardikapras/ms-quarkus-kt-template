# Git Hooks

This directory contains git hooks that automatically run checks before certain git operations.

## Available Hooks

### pre-push

Runs before every `git push` to ensure code quality:

1. **Kotlin Formatting Check** - Ensures all code is properly formatted
2. **Tests** - Runs all tests to prevent broken code from being pushed

## Installation

Hooks are **automatically installed** when you run:
```bash
./gradlew build
```

Or manually install with:
```bash
./gradlew installGitHooks
```

## What happens during push

```bash
$ git push
Checking Kotlin code formatting...
✅ No formatting issues were found
Running tests...
✅ All tests passed
✅ Pre-push checks passed successfully!
```

## If checks fail

### Formatting issues:
```bash
❌ There are code formatting issues
Run './gradlew formatKotlin' to fix them automatically
```

### Test failures:
```bash
❌ Some tests failed
```
Fix the failing tests before pushing.

## Skipping hooks (NOT recommended)

In emergency situations only:
```bash
git push --no-verify
```

**Warning:** This bypasses all quality checks!

## Adding new hooks

1. Create the hook file in this directory (e.g., `pre-commit`)
2. Make it executable: `chmod +x hooks/pre-commit`
3. Run `./gradlew installGitHooks` to install
4. The hook will be automatically installed for all team members on their next build
