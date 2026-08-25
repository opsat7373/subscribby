package com.opsat.subscribity.presentation.subscriptionlist

import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.Subscription
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

fun Subscription.toUiModel(): SubscriptionListItemUiModel = SubscriptionListItemUiModel(
    id = id,
    name = name,
    iconKey = icon,
    nextPaymentDateLabel = nextPaymentDate.format(dateFormatter),
    priceLabel = formatPrice(price, currency),
    periodLabel = period.toLabel(),
)

private fun formatPrice(price: BigDecimal, currency: CurrencyCode): String {
    // Locale.US is used deliberately so the symbol (e.g. "$") doesn't vary with the device locale.
    val symbol = runCatching { Currency.getInstance(currency.code).getSymbol(Locale.US) }.getOrDefault(currency.code)
    return "$symbol ${price.setScale(2, RoundingMode.HALF_UP)}"
}

private fun BillingPeriod.toLabel(): String = when (this) {
    BillingPeriod.Weekly -> "Weekly"
    BillingPeriod.Monthly -> "Monthly"
    BillingPeriod.Quarterly -> "Quarterly"
    BillingPeriod.Yearly -> "Yearly"
    is BillingPeriod.Custom -> "Every $days days"
}
