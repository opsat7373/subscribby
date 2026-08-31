package com.opsat.subscribity.domain.model

import kotlin.random.Random

/** Fixed, theme-independent avatar background colors (ARGB Int) for [SubscriptionIconType.LETTER]. */
object AvatarColors {
    val palette: List<Int> = listOf(
        0xFFE57373.toInt(), // red
        0xFFFFB74D.toInt(), // orange
        0xFFFFD54F.toInt(), // amber
        0xFF81C784.toInt(), // green
        0xFF4FC3F7.toInt(), // light blue
        0xFF7986CB.toInt(), // indigo
        0xFFBA68C8.toInt(), // purple
        0xFFF06292.toInt(), // pink
    )

    fun random(random: Random = Random.Default): Int = palette[random.nextInt(palette.size)]
}
