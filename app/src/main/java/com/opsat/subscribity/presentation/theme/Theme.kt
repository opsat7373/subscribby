package com.opsat.subscribity.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * A fixed light purple-pink "PC RGB lighting" neon look, regardless of system light/dark setting.
 */
private val NeonColorScheme = lightColorScheme(
    primary = NeonPink,
    onPrimary = NeonSurface,
    primaryContainer = NeonPinkContainer,
    onPrimaryContainer = OnNeonPinkContainer,
    secondary = NeonPurple,
    onSecondary = NeonSurface,
    secondaryContainer = NeonPurpleContainer,
    onSecondaryContainer = OnNeonPurpleContainer,
    tertiary = NeonPurple,
    background = NeonBackground,
    onBackground = NeonOnColor,
    surface = NeonSurface,
    onSurface = NeonOnColor,
    surfaceVariant = NeonSurfaceVariant,
    onSurfaceVariant = NeonOnSurfaceVariant,
)

@Composable
fun SubscribityTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = NeonColorScheme, content = content)
}
