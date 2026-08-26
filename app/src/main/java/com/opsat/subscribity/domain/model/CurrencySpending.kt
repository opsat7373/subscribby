package com.opsat.subscribity.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

data class CurrencySpending(val currency: CurrencyCode, val monthlyTotal: BigDecimal)

fun List<Subscription>.monthlySpendingByCurrency(): List<CurrencySpending> =
    groupBy { it.currency }
        .map { (currency, subscriptions) ->
            val total = subscriptions.sumOf { it.price.toDouble() / it.period.approximateDays * 30 }
            CurrencySpending(currency, BigDecimal(total).setScale(2, RoundingMode.HALF_UP))
        }
        .sortedByDescending { it.monthlyTotal }
