package com.opsat.subscribity.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    background = PaperLight,
    onBackground = InkLight,
    surface = PaperLight,
    onSurface = InkLight,
    outline = InkLight,
    onSurfaceVariant = InkLight50,
    outlineVariant = InkLight18,
    primary = AccentInkRed,
    onPrimary = OnAccentLight,
    tertiary = AccentBrightLight,
    surfaceVariant = InkLight5,
)

private val DarkColors = darkColorScheme(
    background = GroundDark,
    onBackground = TextDark,
    surface = GroundDark,
    onSurface = TextDark,
    outline = TextDark,
    onSurfaceVariant = TextDark50,
    outlineVariant = HairlineDark20,
    primary = AccentDark,
    onPrimary = OnAccentDark,
    tertiary = AccentDark,
    surfaceVariant = SoftFillDark8,
)

@Composable
fun SubscribityTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, shapes = LedgerShapes, typography = LedgerTypography, content = content)
}
