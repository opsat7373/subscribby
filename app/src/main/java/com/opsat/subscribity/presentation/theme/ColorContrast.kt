package com.opsat.subscribity.presentation.theme

import androidx.compose.ui.graphics.Color

/** Simple perceived-luminance heuristic: black text on light backgrounds, white on dark ones. */
fun contrastingTextColor(backgroundArgb: Int): Color {
    val r = (backgroundArgb shr 16 and 0xFF) / 255.0
    val g = (backgroundArgb shr 8 and 0xFF) / 255.0
    val b = (backgroundArgb and 0xFF) / 255.0
    val luminance = 0.299 * r + 0.587 * g + 0.114 * b
    return if (luminance > 0.5) Color.Black else Color.White
}
