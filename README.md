# Handoff: Ink Ledger theme for Subscribity (Android / Compose)

## Overview

Re-theme the existing Subscribity Android app (`com.opsat.subscribity`, Jetpack Compose + Material 3)
to the **Ink Ledger** visual system: paper stock, black rules, one ink-red accent, no rounded corners,
no filled cards, money set as a ledger column in tabular figures.

This is a **theme + component-shell change only**. No navigation, domain, data or ViewModel changes.
Screen structure (list + spending summary, single-screen add/edit form, settings, 3-item bottom bar)
stays exactly as it is today.

## About the design files

The files in this bundle are **design references written in HTML** — prototypes showing the intended
look and behaviour. They are not production code and must not be embedded in the app (no WebView).
The task is to reproduce them in the app's existing Compose environment, using its established
patterns: `MaterialTheme`, `presentation/theme/`, the MVI-ish `*State`/`*Intent`/`*Effect` screens.

## Fidelity

**High fidelity.** Colors, type sizes, letter-spacing, border widths and paddings below are final —
match them. Where a value is not stated, keep what the app does today.

---

## Design tokens

### Color — light (default)

| Role | Hex | M3 slot |
| --- | --- | --- |
| Paper (ground) | `#F5F4F0` | `background`, `surface` |
| Ink (text, plates, hard rules) | `#14161A` | `onBackground`, `onSurface`, `outline` |
| Ink 50% | `#8014161A` | `onSurfaceVariant` |
| Ink 18% (hairline) | `2E14161A` → `Color(0x2E14161A)` | `outlineVariant` |
| Accent (ink red) | `#C2452F` | `primary` |
| Accent bright (large marks, dark ground) | `#E0644D` | `tertiary` / display accent |
| On accent | `#F5F4F0` | `onPrimary` |
| Ink 5% (softest fill) | `Color(0x0D14161A)` | `surfaceVariant` |

### Color — dark (exact inversion)

| Role | Hex |
| --- | --- |
| Ground | `#14161A` |
| Text / plates | `#F5F4F0` |
| Text 50% | `Color(0x80F5F4F0)` |
| Hairline (20%) | `Color(0x33F5F4F0)` |
| Accent | `#E0644D` |
| On accent | `#14161A` |
| Softest fill (8%) | `Color(0x14F5F4F0)` |

Rules: **two ground colors only** (paper and ink). The accent is the single saturated element — used
for the add button, the "due soon" caption, the active reminder switch and the section tick marks.
No other hues, no gradients, no tonal elevation, no shadows.

### Shape

All corners square:

```kotlin
val LedgerShapes = Shapes(
    extraSmall = RectangleShape, small = RectangleShape, medium = RectangleShape,
    large = RectangleShape, extraLarge = RectangleShape,
)
```

Containers are drawn, not filled: `Modifier.border(1.dp, outlineVariant)` on a transparent
background. Replace `Card`/`ElevatedCard`/`tonalElevation` usages with borders.
Two rule weights only:
- **hairline** — 1.dp, `outlineVariant` (row separators, field separators)
- **hard rule** — 2.dp, `Ink`/`onBackground` (table head underline, ledger total, bottom-bar top edge)

### Typography

Family: **Archivo** for everything (Google Fonts, downloadable). Figures use the same family with
`fontFeatureSettings = "tnum"` so columns align. Optional: **Space Mono** for dates in dense lists.

| Style | Spec | Used for |
| --- | --- | --- |
| Plate label | 13sp / w800 / +0.20em / UPPERCASE | plate headers ("SUBSCRIPTIONS", "NEW ENTRY") |
| Micro label | 9.5–10sp / w400 / +0.20em / UPPERCASE / text 50% | field labels, column heads |
| Figure XL | 44sp / w800 / −0.03em / tnum | monthly total |
| Figure M | 26sp / w800 / tnum | secondary currency total |
| Row name | 19sp / w600 / −0.01em | subscription name |
| Row amount | 21sp / w600 / tnum | price |
| Row caption | 11sp / w400 / +0.12em / UPPERCASE / text 50% | "Sep 5 · monthly" |
| Field value | 20–22sp / w600 | name, currency, price, date in the form |
| Control label | 11–13sp / w600 / +0.10–0.16em / UPPERCASE | segmented cells, buttons, tab labels |
| Body row | 17sp / w500 | settings row titles |

```kotlin
val provider = GoogleFont.Provider(
    "com.google.android.gms.fonts", "com.google.android.gms",
    R.array.com_google_android_gms_fonts_certs)
private val archivo = GoogleFont("Archivo")
val Archivo = FontFamily(
    Font(archivo, provider, FontWeight.Normal),
    Font(archivo, provider, FontWeight.Medium),
    Font(archivo, provider, FontWeight.SemiBold),
    Font(archivo, provider, FontWeight.ExtraBold),
)
```

Minimum touch target stays 48.dp even where the drawn box is smaller.

### Spacing

Screen gutter **18.dp**. Row vertical padding **17.dp**. Field block padding **16.dp / 18.dp**.
Plate padding **18.dp**. Section gap **20–26.dp**. Icon size 22.dp, stroke 1.8–2.dp.

---

## Component shells to add (`presentation/theme/` or `presentation/common/`)

Four small composables carry the whole look; screens then compose from them unchanged.

1. **`Plate(content)`** — inverted block: fills with `onBackground`, content in `background`.
   Full-bleed, 18.dp padding, square. Used as the list header (with the totals) and as the form header.

2. **`SegmentedRow(options, selectedIndex, onSelect)`** — equal-width cells inside a 1.dp `Ink`
   border, 44.dp tall, cell dividers 1.dp `Ink`; selected cell fills `Ink` with `Paper` label.
   Used for the billing period (`Wk / Mo / Qtr / Yr / Cust`) and the theme choice in settings.

3. **`LedgerRow(name, caption, amount, onClick)`** — `Row` with the name/caption block weighted 1f
   and the amount right-aligned in tnum; 1.dp hairline bottom border; 17.dp vertical padding.
   Captions turn `accent` when the charge is due soon or the cycle is custom.

4. **`SquareSwitch(checked, onCheckedChange)`** — 58×30.dp, 1.dp `Ink` border, 3.dp inset,
   22×22.dp square knob. Off: knob `Ink` 35% on transparent, knob left. On: track `accent`,
   knob `Paper`, knob right. Replaces M3 `Switch` everywhere.

Plus two edits to existing files:
- `SubscribityBottomBar.kt`: centre add button `RoundedCornerShape(13.5.dp)` → `RectangleShape`,
  56.dp square, `accent` fill; bar top edge 2.dp `Ink`; height 72.dp; icons → outline strokes
  (list = three equal lines, settings = two slider lines with square handles), labels 10sp/w600/+0.18em/UPPERCASE.
- `SpendingSummaryCard.kt`: becomes a `Plate` — "PER MONTH" micro label, 44sp total, currency code
  in accent underneath, secondary currency in a right-hand column separated by a 1.dp vertical rule,
  and a row of three 26×3.dp tick marks (first `accent`, rest 30% ink).

---

## Screens

### 1. Subscription list (`presentation/subscriptionlist/`)

- **Plate header**: "SUBSCRIPTIONS" plate label; below it, left column "PER MONTH" + total (44sp) +
  currency code in accent; right column, separated by a 1.dp vertical rule with 18.dp left padding:
  "ALSO" + second-currency total (26sp) + its code at 50%. Then the three tick marks.
- **Column head**: "SERVICE · NEXT CHARGE" left, "AMOUNT" right; micro label; underlined by the
  2.dp hard rule.
- **Rows**: `LedgerRow` per subscription — name 19sp/w600, caption "Sep 5 · monthly" 11sp UPPERCASE
  50% (accent when custom cycle or due within the reminder window), amount 21sp/w600 tnum with the
  currency symbol attached (`$15.99`, `¥500`, `₴1200`).
- **Footer row**: "5 ACTIVE · LEDGER TOTAL" micro label + total in w800, closed by the 2.dp hard rule.
- No icon tiles, no avatars, no chevrons.

### 2. Add / edit subscription (`presentation/addsubscription/`)

One long scroll, field order unchanged from `AddSubscriptionState`:

1. **Icon + name** — a 96×96.dp `Ink` square holding the monogram (34sp/w800, `Paper`) on the left;
   the name field fills the rest, separated by a 1.dp vertical hairline, "NAME" micro label above the
   value. Brand icons from `SimpleIconsCatalog` render inside the same square, tinted `Paper`.
2. **Currency | Price** — two equal cells side by side, divided by a 1.dp vertical hairline;
   micro label above, value 20sp/w600; currency cell carries a 15.dp chevron at its right edge.
3. **Billing period** — `SegmentedRow` with `Wk / Mo / Qtr / Yr / Cust`.
4. **Next payment** — micro label; value 20sp/w600 with a relative caption ("IN 3 DAYS") in accent
   beside it. Custom-cycle count/unit and the trial length/price appear as the same two-cell block
   directly beneath when enabled.
5. **Toggles** — "Free trial", "Notify before payment", "Shared with others" as full-width rows with
   `SquareSwitch` at the right, hairline separators.
6. **Actions** — one bordered block split `1fr / 1.4fr`, 58.dp tall: "CANCEL" outlined, "SAVE ENTRY"
   filled `Ink` with `Paper` label. In edit mode a third full-width row "DELETE ENTRY" in accent text.

Validation: an empty name blocks save — label and rule turn `accent`, message 11sp in accent under
the field. Keep the existing `AddSubscriptionViewModel` rules.

### 3. Settings (`presentation/settings/`)

Plate header "SETTINGS". Theme choice as a three-cell `SegmentedRow` (`LIGHT / DARK / SYSTEM`).
Reminder rows: "Notify before payment" with `SquareSwitch`; "Days before" and "Time of day" as
hairline rows with a right-aligned square stepper (46×58.dp `−` / value / `+` cells divided by
1.dp hairlines). Footer: app version as a micro label at 50%.

---

## Interactions & behaviour

Unchanged from the current app. Motion is minimal and mechanical: 120–160ms fades and 8dp upward
slide for sheets/toasts, no spring, no scale. Pressed state = `Ink` 5% overlay (`Paper` 8% on dark);
selection is a fill flip, never a tint. Focused field: its baseline rule goes 2.dp `Ink`.

## State management

No new state. The only addition is `ThemeMode` already present in `SettingsState`.

## Assets

None new. Brand icons keep coming from `res/drawable/ic_brand_*.xml` via `SimpleIconsCatalog`;
they now render as `Paper`-tinted marks inside the ink monogram square. Interface icons are
hand-drawn strokes (lines, rectangles) — no icon font, no new dependency.

## Files in this bundle

- `Subscribity Screens.dc.html` — the design reference. Option **3a** ("Ink ledger") is the target:
  light list + light add form, then the dark pair. Later options in the same file are interactive
  variants of the same system (date picker, delete flow, states).
- `Subscribity.dc.html` — earlier alternative direction (steel-blue "Industry" palette, Barlow).
  Reference only; do not implement unless asked.
- `android-frame.jsx`, `support.js` — harness needed to open the HTML files in a browser.

## Alternative palette (only if the team prefers steel over ink)

`background #F2F2F3`, `onBackground #1D1F20`, `primary #5980A6`, soft fill `#EEF6FF`,
hairline `Color(0x291D1F20)`; dark: `#16191C / #E7E9EA / #94BCE3 / #1D2D3D`. Fonts Barlow +
Barlow Condensed. Same shapes, rules and components as above.
