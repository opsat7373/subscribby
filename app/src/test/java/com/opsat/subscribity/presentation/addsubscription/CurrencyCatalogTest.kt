package com.opsat.subscribity.presentation.addsubscription

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency

class CurrencyCatalogTest {

    @Test
    fun `used codes come first, deduplicated, in order`() {
        val options = buildCurrencyOptions(listOf("UAH", "USD", "UAH"))

        assertEquals("UAH", options[0].code)
        assertEquals("USD", options[1].code)
        assertEquals(1, options.count { it.code == "UAH" })
    }

    @Test
    fun `covers every available ISO currency exactly once`() {
        val options = buildCurrencyOptions(listOf("EUR"))

        assertEquals(Currency.getAvailableCurrencies().size, options.size)
        assertEquals(options.size, options.map { it.code }.distinct().size)
    }

    @Test
    fun `filters by code case-insensitively`() {
        val options = buildCurrencyOptions(emptyList())
        val filtered = filterCurrencyOptions(options, "usd")
        assertTrue(filtered.any { it.code == "USD" })
        assertTrue(filtered.all { it.code.contains("USD", ignoreCase = true) || it.displayName.contains("usd", ignoreCase = true) })
    }

    @Test
    fun `filters by display name case-insensitively`() {
        val options = buildCurrencyOptions(emptyList())
        val filtered = filterCurrencyOptions(options, "dollar")
        assertTrue(filtered.any { it.code == "USD" })
    }

    @Test
    fun `blank query returns the full list`() {
        val options = buildCurrencyOptions(listOf("EUR"))
        assertEquals(options, filterCurrencyOptions(options, ""))
    }
}
