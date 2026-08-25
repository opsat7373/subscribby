package com.opsat.subscribity.presentation.addsubscription

import java.util.Currency
import java.util.Locale

/** The device's currency, if it can be determined; `null` otherwise (caller decides the fallback). */
fun systemCurrencyCodeOrNull(): String? =
    runCatching { Currency.getInstance(Locale.getDefault()).currencyCode }.getOrNull()

// Currency.getDisplayName() does a locale-data lookup per call; computed once and reused for the
// process lifetime instead of once per buildCurrencyOptions() call (this function runs twice per
// AddSubscriptionViewModel instantiation) to avoid janking the screen transition.
private val currencyOptionsByCode: Map<String, CurrencyOption> by lazy {
    Currency.getAvailableCurrencies().associate { currency ->
        currency.currencyCode to CurrencyOption(currency.currencyCode, currency.getDisplayName(Locale.US))
    }
}

/**
 * All ISO 4217 currencies, with [usedCodes] (deduplicated, in the given order) listed first so
 * currencies already present in the database surface before the rest of the catalog.
 */
fun buildCurrencyOptions(usedCodes: List<String>): List<CurrencyOption> {
    val orderedUsedCodes = usedCodes.distinct().filter { it in currencyOptionsByCode }
    val usedSet = orderedUsedCodes.toSet()
    val remaining = currencyOptionsByCode.keys.filter { it !in usedSet }.sorted()
    return (orderedUsedCodes + remaining).map { code -> currencyOptionsByCode.getValue(code) }
}

fun filterCurrencyOptions(options: List<CurrencyOption>, query: String): List<CurrencyOption> {
    if (query.isBlank()) return options
    return options.filter {
        it.code.contains(query, ignoreCase = true) || it.displayName.contains(query, ignoreCase = true)
    }
}
