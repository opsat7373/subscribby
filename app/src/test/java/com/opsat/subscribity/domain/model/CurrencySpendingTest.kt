package com.opsat.subscribity.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class CurrencySpendingTest {

    private fun subscription(
        price: String,
        currency: String,
        period: BillingPeriod,
    ) = Subscription(
        name = "Test",
        icon = "test",
        period = period,
        price = BigDecimal(price),
        currency = CurrencyCode(currency),
        nextPaymentDate = LocalDate.of(2026, 9, 1),
    )

    @Test
    fun `monthly subscription contributes its exact price`() {
        val result = listOf(subscription("9.99", "USD", BillingPeriod.Monthly)).monthlySpendingByCurrency()

        assertEquals(listOf(CurrencySpending(CurrencyCode("USD"), BigDecimal("9.99"))), result)
    }

    @Test
    fun `weekly subscription is approximated to a 30-day average`() {
        val result = listOf(subscription("7.00", "USD", BillingPeriod.Weekly)).monthlySpendingByCurrency()

        assertEquals(BigDecimal("30.00"), result.single().monthlyTotal)
    }

    @Test
    fun `yearly subscription is approximated to a 30-day average`() {
        val result = listOf(subscription("365.00", "USD", BillingPeriod.Yearly)).monthlySpendingByCurrency()

        assertEquals(BigDecimal("30.00"), result.single().monthlyTotal)
    }

    @Test
    fun `custom period is approximated using its unit`() {
        val result = listOf(
            subscription("45.00", "USD", BillingPeriod.Custom(count = 45, unit = CustomPeriodUnit.DAYS)),
        ).monthlySpendingByCurrency()

        assertEquals(BigDecimal("30.00"), result.single().monthlyTotal)
    }

    @Test
    fun `different currencies are grouped separately and never summed together`() {
        val result = listOf(
            subscription("10.00", "USD", BillingPeriod.Monthly),
            subscription("5.00", "USD", BillingPeriod.Monthly),
            subscription("100.00", "UAH", BillingPeriod.Monthly),
        ).monthlySpendingByCurrency()

        assertEquals(2, result.size)
        assertEquals(BigDecimal("15.00"), result.single { it.currency == CurrencyCode("USD") }.monthlyTotal)
        assertEquals(BigDecimal("100.00"), result.single { it.currency == CurrencyCode("UAH") }.monthlyTotal)
    }

    @Test
    fun `results are sorted by monthly total descending`() {
        val result = listOf(
            subscription("5.00", "USD", BillingPeriod.Monthly),
            subscription("100.00", "UAH", BillingPeriod.Monthly),
        ).monthlySpendingByCurrency()

        assertEquals(listOf(CurrencyCode("UAH"), CurrencyCode("USD")), result.map { it.currency })
    }
}
