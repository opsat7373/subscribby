package com.opsat.subscribity.presentation.common

import com.opsat.subscribity.domain.model.CurrencyCode
import java.util.Currency
import java.util.Locale

// Locale.US is used deliberately so the symbol (e.g. "$") doesn't vary with the device locale.
fun currencySymbol(currency: CurrencyCode): String =
    runCatching { Currency.getInstance(currency.code).getSymbol(Locale.US) }.getOrDefault(currency.code)
