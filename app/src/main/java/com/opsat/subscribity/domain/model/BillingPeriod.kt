package com.opsat.subscribity.domain.model

sealed class BillingPeriod {
    data object Weekly : BillingPeriod()
    data object Monthly : BillingPeriod()
    data object Quarterly : BillingPeriod()
    data object Yearly : BillingPeriod()
    data class Custom(val days: Int) : BillingPeriod() {
        init {
            require(days > 0) { "days must be positive" }
        }
    }
}
