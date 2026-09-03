package com.opsat.subscribity.presentation.subscriptionlist

import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.CurrencySpending
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.presentation.common.currencySymbol
import com.opsat.subscribity.presentation.common.customPeriodText
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

/** Presentation-only "due soon" heuristic for the list's accent highlight — unrelated to the real reminder setting. */
private const val DueSoonWindowDays = 3L

fun Subscription.toUiModel(): SubscriptionListItemUiModel = SubscriptionListItemUiModel(
    id = id,
    name = name,
    iconType = iconType,
    iconValue = iconValue,
    iconColor = iconColor,
    nextPaymentDateLabel = nextPaymentDate.format(dateFormatter),
    priceLabel = formatPrice(price, currency),
    periodLabel = period.toLabel(),
    isDueSoon = !nextPaymentDate.isAfter(LocalDate.now().plusDays(DueSoonWindowDays)),
    isCustomCycle = period is BillingPeriod.Custom,
)

fun CurrencySpending.toUiModel(): SpendingSummaryItemUiModel =
    SpendingSummaryItemUiModel(amount = monthlyTotal.toPlainString(), currencyCode = currencySymbol(currency))

private fun formatPrice(price: BigDecimal, currency: CurrencyCode): String =
    "${currencySymbol(currency)} ${price.setScale(2, RoundingMode.HALF_UP)}"

private fun BillingPeriod.toLabel(): String = when (this) {
    BillingPeriod.Weekly -> "Weekly"
    BillingPeriod.Monthly -> "Monthly"
    BillingPeriod.Quarterly -> "Quarterly"
    BillingPeriod.Yearly -> "Yearly"
    is BillingPeriod.Custom -> customPeriodText(count, unit)
}
