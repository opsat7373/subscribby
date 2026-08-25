package com.opsat.subscribity.domain.model

enum class CustomPeriodUnit { DAYS, WEEKS, MONTHS }

sealed class BillingPeriod {
    data object Weekly : BillingPeriod()
    data object Monthly : BillingPeriod()
    data object Quarterly : BillingPeriod()
    data object Yearly : BillingPeriod()
    data class Custom(val count: Int, val unit: CustomPeriodUnit) : BillingPeriod() {
        init {
            require(count > 0) { "count must be positive" }
        }
    }
}
