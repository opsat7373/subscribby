package com.opsat.subscribity.data.seed

import com.opsat.subscribity.domain.model.AvatarColors
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.model.SubscriptionIconType
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
            period = BillingPeriod.Monthly,
            price = BigDecimal("15.99"),
            currency = CurrencyCode("USD"),
            nextPaymentDate = LocalDate.now().plusDays(5),
            isSharedWithOthers = true,
            personsCount = 3,
            iconType = SubscriptionIconType.BRAND,
            iconValue = "netflix",
        ),
        Subscription(
            id = 2L,
            name = "Spotify",
            period = BillingPeriod.Monthly,
            price = BigDecimal("9.99"),
            currency = CurrencyCode("EUR"),
            nextPaymentDate = LocalDate.now().plusDays(12),
            isTrial = true,
            trialPeriod = BillingPeriod.Weekly,
            trialPrice = BigDecimal.ZERO,
            iconType = SubscriptionIconType.BRAND,
            iconValue = "spotify",
        ),
        Subscription(
            id = 3L,
            name = "YouTube Premium",
            period = BillingPeriod.Yearly,
            price = BigDecimal("139.99"),
            currency = CurrencyCode("GBP"),
            nextPaymentDate = LocalDate.now().plusMonths(4),
            iconType = SubscriptionIconType.BRAND,
            iconValue = "youtube",
        ),
        Subscription(
            id = 4L,
            name = "Спортзал",
            period = BillingPeriod.Custom(count = 45, unit = CustomPeriodUnit.DAYS),
            price = BigDecimal("1200"),
            currency = CurrencyCode("UAH"),
            nextPaymentDate = LocalDate.now().plusDays(20),
            iconType = SubscriptionIconType.LETTER,
            iconColor = AvatarColors.palette[2],
        ),
        Subscription(
            id = 5L,
            name = "Amazon Prime",
            period = BillingPeriod.Monthly,
            price = BigDecimal("500"),
            currency = CurrencyCode("JPY"),
            nextPaymentDate = LocalDate.now().plusDays(10),
            // Amazon isn't in the curated Simple Icons subset (Amazon requested removal from the
            // library), so this falls back to LETTER like any other unmatched name would.
            iconType = SubscriptionIconType.LETTER,
            iconColor = AvatarColors.palette[4],
        ),
    )
}
