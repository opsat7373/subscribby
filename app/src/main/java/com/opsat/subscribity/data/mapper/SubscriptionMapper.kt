package com.opsat.subscribity.data.mapper

import com.opsat.subscribity.data.local.SubscriptionEntity
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.model.Subscription

private const val PERIOD_WEEKLY = "WEEKLY"
private const val PERIOD_MONTHLY = "MONTHLY"
private const val PERIOD_QUARTERLY = "QUARTERLY"
private const val PERIOD_YEARLY = "YEARLY"
private const val PERIOD_CUSTOM = "CUSTOM"

private data class PeriodColumns(val type: String, val customCount: Int?, val customUnit: String?)

fun SubscriptionEntity.toDomain(): Subscription = Subscription(
    id = id,
    name = name,
    icon = icon,
    period = decodeBillingPeriod(periodType, periodCustomCount, periodCustomUnit),
    price = price,
    currency = CurrencyCode(currencyCode),
    nextPaymentDate = nextPaymentDate,
    isTrial = isTrial,
    trialPeriod = trialPeriodType?.let { decodeBillingPeriod(it, trialPeriodCustomCount, trialPeriodCustomUnit) },
    trialPrice = trialPrice,
    isSharedWithOthers = isSharedWithOthers,
    personsCount = personsCount,
    notificationsEnabled = notificationsEnabled,
)

fun Subscription.toEntity(): SubscriptionEntity {
    val periodColumns = encodeBillingPeriod(period)
    val trialColumns = trialPeriod?.let(::encodeBillingPeriod)
    return SubscriptionEntity(
        id = id,
        name = name,
        icon = icon,
        periodType = periodColumns.type,
        periodCustomCount = periodColumns.customCount,
        periodCustomUnit = periodColumns.customUnit,
        price = price,
        currencyCode = currency.code,
        nextPaymentDate = nextPaymentDate,
        isTrial = isTrial,
        trialPeriodType = trialColumns?.type,
        trialPeriodCustomCount = trialColumns?.customCount,
        trialPeriodCustomUnit = trialColumns?.customUnit,
        trialPrice = trialPrice,
        isSharedWithOthers = isSharedWithOthers,
        personsCount = personsCount,
        notificationsEnabled = notificationsEnabled,
    )
}

private fun encodeBillingPeriod(period: BillingPeriod): PeriodColumns = when (period) {
    BillingPeriod.Weekly -> PeriodColumns(PERIOD_WEEKLY, null, null)
    BillingPeriod.Monthly -> PeriodColumns(PERIOD_MONTHLY, null, null)
    BillingPeriod.Quarterly -> PeriodColumns(PERIOD_QUARTERLY, null, null)
    BillingPeriod.Yearly -> PeriodColumns(PERIOD_YEARLY, null, null)
    is BillingPeriod.Custom -> PeriodColumns(PERIOD_CUSTOM, period.count, period.unit.name)
}

private fun decodeBillingPeriod(type: String, customCount: Int?, customUnit: String?): BillingPeriod = when (type) {
    PERIOD_WEEKLY -> BillingPeriod.Weekly
    PERIOD_MONTHLY -> BillingPeriod.Monthly
    PERIOD_QUARTERLY -> BillingPeriod.Quarterly
    PERIOD_YEARLY -> BillingPeriod.Yearly
    PERIOD_CUSTOM -> BillingPeriod.Custom(
        count = requireNotNull(customCount) { "customCount required for CUSTOM period" },
        unit = CustomPeriodUnit.valueOf(requireNotNull(customUnit) { "customUnit required for CUSTOM period" }),
    )
    else -> error("Unknown period type: $type")
}
