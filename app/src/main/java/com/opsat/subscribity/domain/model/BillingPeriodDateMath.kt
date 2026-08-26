package com.opsat.subscribity.domain.model

import java.time.LocalDate

fun LocalDate.plus(period: BillingPeriod): LocalDate = when (period) {
    BillingPeriod.Weekly -> plusWeeks(1)
    BillingPeriod.Monthly -> plusMonths(1)
    BillingPeriod.Quarterly -> plusMonths(3)
    BillingPeriod.Yearly -> plusYears(1)
    is BillingPeriod.Custom -> when (period.unit) {
        CustomPeriodUnit.DAYS -> plusDays(period.count.toLong())
        CustomPeriodUnit.WEEKS -> plusWeeks(period.count.toLong())
        CustomPeriodUnit.MONTHS -> plusMonths(period.count.toLong())
    }
}

val BillingPeriod.approximateDays: Int
    get() = when (this) {
        BillingPeriod.Weekly -> 7
        BillingPeriod.Monthly -> 30
        BillingPeriod.Quarterly -> 90
        BillingPeriod.Yearly -> 365
        is BillingPeriod.Custom -> when (unit) {
            CustomPeriodUnit.DAYS -> count
            CustomPeriodUnit.WEEKS -> count * 7
            CustomPeriodUnit.MONTHS -> count * 30
        }
    }
