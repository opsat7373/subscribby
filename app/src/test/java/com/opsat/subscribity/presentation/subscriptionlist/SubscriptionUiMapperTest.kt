package com.opsat.subscribity.presentation.subscriptionlist

import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.Subscription
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SubscriptionUiMapperTest {

    private fun subscription(
        period: BillingPeriod = BillingPeriod.Monthly,
        price: BigDecimal = BigDecimal("9.99"),
        currency: CurrencyCode = CurrencyCode("USD"),
    ) = Subscription(
        name = "Test",
        icon = "test",
        period = period,
        price = price,
        currency = currency,
        nextPaymentDate = LocalDate.of(2026, 9, 1),
    )

    @Test
    fun `maps basic fields`() {
        val uiModel = subscription().toUiModel()

        val expectedDate = LocalDate.of(2026, 9, 1)
            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        assertEquals("Test", uiModel.name)
        assertEquals("test", uiModel.iconKey)
        assertEquals(expectedDate, uiModel.nextPaymentDateLabel)
    }

    @Test
    fun `formats known currency with symbol and a space before the amount`() {
        val uiModel = subscription(currency = CurrencyCode("USD"), price = BigDecimal("15.99")).toUiModel()
        assertEquals("$ 15.99", uiModel.priceLabel)
    }

    @Test
    fun `falls back to the raw code for an unrecognized currency`() {
        val uiModel = subscription(currency = CurrencyCode("ZZZ"), price = BigDecimal("5.00")).toUiModel()
        assertEquals("ZZZ 5.00", uiModel.priceLabel)
    }

    @Test
    fun `formats period labels with full names for each variant`() {
        assertEquals("Weekly", subscription(period = BillingPeriod.Weekly).toUiModel().periodLabel)
        assertEquals("Monthly", subscription(period = BillingPeriod.Monthly).toUiModel().periodLabel)
        assertEquals("Quarterly", subscription(period = BillingPeriod.Quarterly).toUiModel().periodLabel)
        assertEquals("Yearly", subscription(period = BillingPeriod.Yearly).toUiModel().periodLabel)
        assertEquals("Every 45 days", subscription(period = BillingPeriod.Custom(45)).toUiModel().periodLabel)
    }
}
