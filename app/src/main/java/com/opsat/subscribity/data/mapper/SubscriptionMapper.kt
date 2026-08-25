package com.opsat.subscribity.data.mapper

import com.opsat.subscribity.data.local.SubscriptionEntity
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.Subscription

private const val PERIOD_WEEKLY = "WEEKLY"
private const val PERIOD_MONTHLY = "MONTHLY"
private const val PERIOD_QUARTERLY = "QUARTERLY"
private const val PERIOD_YEARLY = "YEARLY"
private const val PERIOD_CUSTOM = "CUSTOM"

fun SubscriptionEntity.toDomain(): Subscription = Subscription(
    id = id,
    name = name,
    icon = icon,
    period = decodeBillingPeriod(periodType, periodCustomDays),
    price = price,
    currency = CurrencyCode(currencyCode),
    nextPaymentDate = nextPaymentDate,
    isTrial = isTrial,
    trialPeriod = trialPeriodType?.let { decodeBillingPeriod(it, trialPeriodCustomDays) },
    trialPrice = trialPrice,
    isSharedWithOthers = isSharedWithOthers,
    personsCount = personsCount,
)

fun Subscription.toEntity(): SubscriptionEntity {
    val (periodType, periodCustomDays) = encodeBillingPeriod(period)
    val (trialPeriodType, trialPeriodCustomDays) = trialPeriod?.let(::encodeBillingPeriod) ?: (null to null)
    return SubscriptionEntity(
        id = id,
        name = name,
        icon = icon,
        periodType = periodType,
        periodCustomDays = periodCustomDays,
        price = price,
        currencyCode = currency.code,
        nextPaymentDate = nextPaymentDate,
        isTrial = isTrial,
        trialPeriodType = trialPeriodType,
        trialPeriodCustomDays = trialPeriodCustomDays,
        trialPrice = trialPrice,
        isSharedWithOthers = isSharedWithOthers,
        personsCount = personsCount,
    )
}

private fun encodeBillingPeriod(period: BillingPeriod): Pair<String, Int?> = when (period) {
    BillingPeriod.Weekly -> PERIOD_WEEKLY to null
    BillingPeriod.Monthly -> PERIOD_MONTHLY to null
    BillingPeriod.Quarterly -> PERIOD_QUARTERLY to null
    BillingPeriod.Yearly -> PERIOD_YEARLY to null
    is BillingPeriod.Custom -> PERIOD_CUSTOM to period.days
}

private fun decodeBillingPeriod(type: String, customDays: Int?): BillingPeriod = when (type) {
    PERIOD_WEEKLY -> BillingPeriod.Weekly
    PERIOD_MONTHLY -> BillingPeriod.Monthly
    PERIOD_QUARTERLY -> BillingPeriod.Quarterly
    PERIOD_YEARLY -> BillingPeriod.Yearly
    PERIOD_CUSTOM -> BillingPeriod.Custom(requireNotNull(customDays) { "customDays required for CUSTOM period" })
    else -> error("Unknown period type: $type")
}
