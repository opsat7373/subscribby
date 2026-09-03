# Prompt for Claude Code

Run Claude Code in the `subscribby` repo root, drop the whole `design_handoff_ledger_theme/` folder
inside it (or point at it), then paste this:

---

Read `design_handoff_ledger_theme/README.md` in full. It is a design handoff for re-theming this app
to the "Ink Ledger" visual system. The HTML files in that folder are design references only — do not
copy code from them and do not use a WebView; reproduce the design in our existing Jetpack Compose
setup.

Scope: theming and presentation only. Do not change navigation, domain models, use cases, Room,
DataStore or any ViewModel logic. Do not change the screen inventory or the field order of the
add/edit form.

Do this in order, and stop after each step so I can build and look:

1. `presentation/theme/` — rewrite `Color.kt` with the light and dark token tables from the README,
   wire both schemes in `Theme.kt`, set `Shapes` to all-`RectangleShape`, and add `Type.kt` with the
   Archivo downloadable-font family and the named text styles from the README's typography table
   (add the `androidx.compose.ui:ui-text-google-fonts` dependency to `gradle/libs.versions.toml`,
   not as an inline coordinate). Keep `ThemeMode` handling in `MainActivity` as is.

2. `presentation/common/` — add the four component shells the README specifies: `Plate`,
   `SegmentedRow`, `LedgerRow`, `SquareSwitch`. Each with a `@Preview` in light and dark.

3. Rebuild `SpendingSummaryCard.kt` as a `Plate` and `SubscriptionListScreen.kt` with `LedgerRow` +
   the column head and ledger-total rows, per README section "Subscription list".

4. `AddSubscriptionScreen.kt` — restyle to README section "Add / edit subscription": ink monogram
   square + name, the currency/price two-cell block, `SegmentedRow` for the billing period, and
   `SquareSwitch` for every toggle. Replace all M3 `Switch` and `Card` usages here.

5. `SettingsScreen.kt` and `SubscribityBottomBar.kt` per the README (square centre button, 2.dp top
   rule, outline icons, segmented theme picker, square steppers).

Constraints: no hard-coded colors, sizes or font names in screen files — everything through
`MaterialTheme.colorScheme` / our named text styles / a `Dimens` object. No shadows, no
`tonalElevation`, no rounded corners anywhere. Minimum touch target 48.dp even where the drawn box is
smaller. Keep every existing unit and instrumented test green; update `AddSubscriptionScreenTest`
and `SubscriptionListScreenTest` node matchers if the restyle changes semantics, but do not weaken
what they assert.

Finish with `gradlew.bat lint` and `gradlew.bat test`, then show me a summary of the files you
touched.
