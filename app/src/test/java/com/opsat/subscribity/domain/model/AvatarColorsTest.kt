package com.opsat.subscribity.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AvatarColorsTest {

    @Test
    fun `palette has eight colors`() {
        assertEquals(8, AvatarColors.palette.size)
    }

    @Test
    fun `random always returns a palette member`() {
        repeat(50) {
            assertTrue(AvatarColors.random() in AvatarColors.palette)
        }
    }

    @Test
    fun `random is deterministic for a seeded Random`() {
        val first = AvatarColors.random(Random(42))
        val second = AvatarColors.random(Random(42))
        assertEquals(first, second)
    }
}
