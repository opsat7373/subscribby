package com.opsat.subscribity.data.seed

import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.model.Subscription
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Sample subscriptions used both to pre-populate the database on first launch and as fixtures
 * for the ViewModel/Compose tests, so the tests exercise the exact data the app ships with.
 * Explicit, stable ids (rather than the domain default `0L` "not yet persisted") since these
 * represent already-identified seed rows, and duplicate `0L` ids would collide as LazyColumn keys.
 */
object SubscriptionSeedData {
    val subscriptions: List<Subscription> = listOf(
        Subscription(
            id = 1L,
            name = "Netflix",
            icon = "netflix",
            period = BillingPeriod.Monthly,
            price = BigDecimal("15.99"),
            currency = CurrencyCode("USD"),
            nextPaymentDate = LocalDate.now().plusDays(5),
            isSharedWithOthers = true,
            personsCount = 3,
        ),
        Subscription(
            id = 2L,
            name = "Spotify",
            icon = "spotify",
            period = BillingPeriod.Monthly,
            price = BigDecimal("9.99"),
            currency = CurrencyCode("EUR"),
            nextPaymentDate = LocalDate.now().plusDays(12),
            isTrial = true,
            trialPeriod = BillingPeriod.Weekly,
            trialPrice = BigDecimal.ZERO,
        ),
        Subscription(
            id = 3L,
            name = "YouTube Premium",
            icon = "youtube",
            period = BillingPeriod.Yearly,
            price = BigDecimal("139.99"),
            currency = CurrencyCode("GBP"),
            nextPaymentDate = LocalDate.now().plusMonths(4),
        ),
        Subscription(
            id = 4L,
            name = "Спортзал",
            icon = "gym",
            period = BillingPeriod.Custom(count = 45, unit = CustomPeriodUnit.DAYS),
            price = BigDecimal("1200"),
            currency = CurrencyCode("UAH"),
            nextPaymentDate = LocalDate.now().plusDays(20),
        ),
        Subscription(
            id = 5L,
            name = "Amazon Prime",
            icon = "amazon",
            period = BillingPeriod.Monthly,
            price = BigDecimal("500"),
            currency = CurrencyCode("JPY"),
            nextPaymentDate = LocalDate.now().plusDays(10),
        ),
    )
}
