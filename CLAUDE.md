# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Subscribity is a single-module Android app (`com.opsat.subscribity`) for tracking recurring subscriptions: adding/editing/deleting subscriptions, a monthly spending summary, dark/light/system theming, and a settings screen. The architecture described below is established and should be followed for new features rather than reinvented.

## Build system

- Gradle Kotlin DSL, single `:app` module (see `settings.gradle.kts`).
- Dependency versions are centralized in `gradle/libs.versions.toml` (the version catalog) and referenced via `libs.*` in `app/build.gradle.kts` — add new dependencies there, not as inline coordinate strings.
- `compileSdk` = 37, `minSdk` = 26, `targetSdk` = 36, Java 11 source/target compatibility.
- Key stack: Jetpack Compose (Material 3), Hilt (DI), Room (persistence), DataStore Preferences (settings), Navigation Compose, KSP for annotation processing.

## Common commands

Run from the repository root using the Gradle wrapper (`gradlew.bat` on Windows PowerShell, `./gradlew` under Bash).

```
gradlew.bat assembleDebug              # build the debug APK
gradlew.bat build                      # full build (compiles, lints, runs unit tests)
gradlew.bat test                       # run JVM unit tests (app/src/test)
gradlew.bat testDebugUnitTest --tests "com.opsat.subscribity.ExampleUnitTest"            # run a single unit test class
gradlew.bat testDebugUnitTest --tests "com.opsat.subscribity.ExampleUnitTest.methodName" # run a single unit test method
gradlew.bat connectedAndroidTest       # run instrumented tests on a connected device/emulator (app/src/androidTest)
gradlew.bat lint                       # run Android Lint
gradlew.bat installDebug               # install debug build on a connected device
```

## Architecture

Clean-architecture-style layering under `app/src/main/java/com/opsat/subscribity/`:

- **`domain/`** — pure Kotlin, no Android/framework dependencies.
  - `model/` — `Subscription` (validates its own invariants in an `init` block, e.g. trial fields must be null unless `isTrial`, `personsCount` rules for `isSharedWithOthers`), `BillingPeriod` (sealed: `Weekly`/`Monthly`/`Quarterly`/`Yearly`/`Custom(count, unit)`), `CurrencyCode`, `CurrencySpending`, `ThemeMode`. `BillingPeriodDateMath.kt` and the `monthlySpendingByCurrency()` extension hold the date/spending math as top-level functions rather than methods on the models.
  - `repository/` — interfaces only (`SubscriptionRepository`, `ThemePreferencesRepository`); implementations live in `data/`.
  - `usecase/` — one class per action (`AddSubscriptionUseCase`, `EditSubscriptionUseCase`, `DeleteSubscriptionUseCase`, `ObserveSubscriptionsUseCase`, `ObserveThemeModeUseCase`, `SetThemeModeUseCase`). ViewModels depend on use cases, not repositories directly.

- **`data/`**
  - `local/` — Room: `SubscribityDatabase` (single `subscriptions` table via `SubscriptionEntity`), `SubscriptionDao`, `Converters` (type converters for `BigDecimal`/`LocalDate`/enums), `Migrations.kt` (hand-written `MIGRATION_N_N+1` `Migration` objects — the schema has already changed once, from a fixed `periodCustomDays` column to a `periodCustomCount`/`periodCustomUnit` pair; follow this pattern, including a `MigrationTest` in `androidTest`, for future schema changes rather than destructive migration).
  - `mapper/` — `SubscriptionMapper.kt`: `Entity <-> Domain` conversions (`toDomain()`, `toEntity()`).
  - `repository/` — `SubscriptionRepositoryImpl`, `ThemePreferencesRepositoryImpl` (backed by DataStore Preferences).
  - `seed/` — `SubscriptionSeedData` + `SubscriptionSeeder`, invoked from `DatabaseModule`'s Room `onCreate` callback only when `BuildConfig.DEBUG` is true — debug builds start with sample data, release builds don't.

- **`presentation/`** — one package per screen, each following the same MVI-ish shape: `*State` (immutable data class held in a `MutableStateFlow`), `*Intent` (sealed class of user actions, dispatched via a single `onIntent(intent)` entry point on the ViewModel), `*Effect` (sealed class of one-shot events like navigation, sent through a `Channel`/`receiveAsFlow()` and collected in the Composable). Screens: `subscriptionlist/` (list + `SpendingSummaryCard`), `addsubscription/` (shared create/edit form — mode is `AddSubscriptionMode.Create` vs `Edit(id, originalName)` derived from a nullable `SUBSCRIPTION_ID_ARG` nav argument), `settings/`. `common/` holds cross-screen formatting helpers (`CurrencyFormatting`, `PeriodFormatting`); `theme/` holds the Compose `MaterialTheme` setup (`Color.kt`, `Theme.kt`) driven by `ThemeMode` (`LIGHT`/`DARK`/`SYSTEM`).
  - `navigation/SubscribityNavHost.kt` defines all routes/transitions in one place; the add/edit form is reached via the same route with an optional `subscriptionId` argument (`0L` sentinel = create mode).

- **`di/`** — Hilt modules: `RepositoryModule` (`@Binds` interface -> impl), `DatabaseModule` (provides `SubscribityDatabase`/`SubscriptionDao`, registers migrations and the debug seed callback), `DataStoreModule` (provides the `Preferences` DataStore singleton), `CoroutineScopeModule` (provides the app-level `@ApplicationScope` `CoroutineScope` used for the seed callback).

- `MainActivity` observes `ThemeMode` via `ObserveThemeModeUseCase` and resolves it to a boolean `darkTheme` (falling back to `isSystemInDarkTheme()` for `SYSTEM`) before rendering `SubscribityNavHost` inside `SubscribityTheme`. `SubscribityApplication` is the `@HiltAndroidApp` entry point.

## Testing conventions

- Unit tests (`app/src/test`) cover domain logic (`BillingPeriodDateMathTest`, `SubscriptionTest`, `CurrencySpendingTest`), ViewModels (`SubscriptionListViewModelTest`, `AddSubscriptionViewModelTest`), and presentation helpers (`CurrencyCatalogTest`, `PriceInputTest`, `SubscriptionUiMapperTest`). ViewModel tests use `testing/FakeSubscriptionRepository.kt` rather than a mocking framework.
- Instrumented tests (`app/src/androidTest`) cover Room (`SubscriptionDaoTest`, `MigrationTest`) and full-screen Compose UI (`AddSubscriptionScreenTest`, `SubscriptionListScreenTest`).

## Claude Code tooling

- `android-gradle` project skill (`.claude/skills/`) documents the Gradle wrapper commands for this repo (build/test/lint/install).
- `.claude/settings.json` hooks: a `PreToolUse` hook (`.claude/hooks/block-generated.ps1`) denies `Edit`/`Write` under `app/build/` or `.gradle/` (generated artifacts), and a `PostToolUse` hook (`.claude/hooks/kt-lint-reminder.ps1`) prints a reminder to run `gradlew.bat lint` after editing a `.kt`/`.kts` file.
- Installed plugins (user scope, from `claude-plugins-official`): `kotlin-lsp` (Kotlin language intelligence — requires the `kotlin-lsp` binary installed separately, no Windows package yet, see plugin README), `code-review`, `hookify`.
