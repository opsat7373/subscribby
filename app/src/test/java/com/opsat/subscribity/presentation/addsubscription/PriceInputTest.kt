package com.opsat.subscribity.presentation.addsubscription

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceInputTest {

    @Test
    fun `keeps plain digits`() {
        assertEquals("1299", sanitizePriceInput("1299"))
    }

    @Test
    fun `keeps a single decimal point`() {
        assertEquals("12.9", sanitizePriceInput("12.9"))
    }

    @Test
    fun `truncates to two decimal digits`() {
        assertEquals("12.99", sanitizePriceInput("12.999"))
    }

    @Test
    fun `strips non-numeric characters`() {
        assertEquals("12.5", sanitizePriceInput("abc12.5x"))
    }

    @Test
    fun `drops a second decimal point`() {
        assertEquals("12.56", sanitizePriceInput("12.5.6"))
    }

    @Test
    fun `empty input stays empty`() {
        assertEquals("", sanitizePriceInput(""))
    }
}
