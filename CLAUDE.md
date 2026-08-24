# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Subscribity is a newly scaffolded, single-module Android application (`com.opsat.subscribity`). At present the project contains only the default Android Studio template skeleton: `app/src/main/AndroidManifest.xml` declares no activity, and `app/src/main/java/com/opsat/subscribity/` is empty. There is no launcher Activity, no layouts beyond generated resources, and no application architecture yet — do not assume any exists. When implementing the first features, you are establishing the architecture, not extending an existing one.

## Build system

- Gradle Kotlin DSL, single `:app` module (see `settings.gradle.kts`).
- Dependency versions are centralized in `gradle/libs.versions.toml` (the version catalog) and referenced via `libs.*` in `app/build.gradle.kts` — add new dependencies there, not as inline coordinate strings.
- `compileSdk` = 36, `minSdk` = 26, `targetSdk` = 36, Java 11 source/target compatibility.

## Common commands

Run from the repository root using the Gradle wrapper (`gradlew.bat` on Windows / PowerShell, `./gradlew` under Bash).

```
gradlew.bat assembleDebug              # build the debug APK
gradlew.bat build                      # full build (compiles, lints, runs unit tests)
gradlew.bat test                       # run JVM unit tests (app/src/test)
gradlew.bat testDebugUnitTest --tests "com.opsat.subscribity.ExampleUnitTest" # run a single unit test class
gradlew.bat connectedAndroidTest       # run instrumented tests on a connected device/emulator (app/src/androidTest)
gradlew.bat lint                       # run Android Lint
```

## Structure

- `app/src/main/java/com/opsat/subscribity/` — application code (currently empty).
- `app/src/main/res/` — Android resources.
- `app/src/test/` — local JVM unit tests (JUnit 4).
- `app/src/androidTest/` — instrumented tests (AndroidX Test + Espresso).

## Claude Code tooling

- `android-gradle` project skill (`.claude/skills/`) documents the Gradle wrapper commands for this repo (build/test/lint/install).
- `.claude/settings.json` hooks: `Edit`/`Write` under `app/build/` or `.gradle/` is denied (generated artifacts), and editing a `.kt`/`.kts` file prints a reminder to run `gradlew.bat lint`.
- Installed plugins (user scope, from `claude-plugins-official`): `kotlin-lsp` (Kotlin language intelligence — requires the `kotlin-lsp` binary installed separately, no Windows package yet, see plugin README), `code-review`, `hookify`.
