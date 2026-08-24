---
name: android-gradle
description: Build, test, and lint the Subscribity Android app via the Gradle wrapper. Use when asked to build, compile, run unit/instrumented tests, run a single test, lint, or install the app for this project.
tools: Bash
---

# Android Gradle (Subscribity)

Run all commands from the repository root using the Gradle wrapper (`gradlew.bat` on Windows PowerShell, `./gradlew` under Git Bash). Module is `:app`, package is `com.opsat.subscribity`.

## Commands

| Task | Command |
|---|---|
| Build debug APK | `gradlew.bat assembleDebug` |
| Full build (compile + lint + unit tests) | `gradlew.bat build` |
| Run JVM unit tests (`app/src/test`) | `gradlew.bat test` |
| Run a single unit test class | `gradlew.bat testDebugUnitTest --tests "com.opsat.subscribity.ExampleUnitTest"` |
| Run a single unit test method | `gradlew.bat testDebugUnitTest --tests "com.opsat.subscribity.ExampleUnitTest.methodName"` |
| Run instrumented tests (needs a connected device/emulator, `app/src/androidTest`) | `gradlew.bat connectedAndroidTest` |
| Android Lint | `gradlew.bat lint` |
| Install debug build on a connected device | `gradlew.bat installDebug` |
| Clean build outputs | `gradlew.bat clean` |

## Notes

- Never hand-edit anything under `app/build/` or `.gradle/` — those are generated/cache directories (a project hook already blocks edits there).
- After changing dependency versions, edit `gradle/libs.versions.toml` (the version catalog), not inline coordinate strings in `app/build.gradle.kts`.
- A first build after a clean checkout or a wrapper/AGP change can take a while (dependency resolution) — don't assume a hang.
