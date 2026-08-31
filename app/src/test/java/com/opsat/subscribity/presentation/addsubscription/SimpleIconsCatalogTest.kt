package com.opsat.subscribity.presentation.addsubscription

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SimpleIconsCatalogTest {

    @Test
    fun `blank query returns the full catalog`() {
        assertEquals(SimpleIconsCatalog.allIcons, SimpleIconsCatalog.filterIconOptions(""))
    }

    @Test
    fun `filter matches case-insensitively by title`() {
        val result = SimpleIconsCatalog.filterIconOptions("netflix")
        assertTrue(result.any { it.slug == "netflix" })
    }

    @Test
    fun `filter matches case-insensitively by slug`() {
        val result = SimpleIconsCatalog.filterIconOptions("SPOTIFY")
        assertTrue(result.any { it.slug == "spotify" })
    }

    @Test
    fun `filter excludes non-matching entries`() {
        val result = SimpleIconsCatalog.filterIconOptions("netflix")
        assertTrue(result.none { it.slug == "spotify" })
    }

    @Test
    fun `exactMatchOrNull finds a full case-insensitive title match`() {
        val match = SimpleIconsCatalog.exactMatchOrNull("netflix")
        assertEquals("netflix", match?.slug)
    }

    @Test
    fun `exactMatchOrNull returns null for a partial match`() {
        assertNull(SimpleIconsCatalog.exactMatchOrNull("Net"))
    }

    @Test
    fun `drawableResFor resolves a known slug and returns null for an unknown one`() {
        assertEquals(SimpleIconsCatalog.allIcons.first { it.slug == "netflix" }.drawableResId, SimpleIconsCatalog.drawableResFor("netflix"))
        assertNull(SimpleIconsCatalog.drawableResFor("not-a-real-slug"))
    }
}
