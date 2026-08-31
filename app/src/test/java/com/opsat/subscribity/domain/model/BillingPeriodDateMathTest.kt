package com.opsat.subscribity.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class BillingPeriodDateMathTest {

    private val start = LocalDate.of(2026, 1, 15)

    @Test
    fun `weekly adds one week`() {
        assertEquals(LocalDate.of(2026, 1, 22), start.plus(BillingPeriod.Weekly))
    }

    @Test
    fun `monthly adds one month`() {
        assertEquals(LocalDate.of(2026, 2, 15), start.plus(BillingPeriod.Monthly))
    }

    @Test
    fun `quarterly adds three months`() {
        assertEquals(LocalDate.of(2026, 4, 15), start.plus(BillingPeriod.Quarterly))
    }

    @Test
    fun `yearly adds one year`() {
        assertEquals(LocalDate.of(2027, 1, 15), start.plus(BillingPeriod.Yearly))
    }

    @Test
    fun `custom days adds the exact number of days`() {
        assertEquals(
            LocalDate.of(2026, 1, 29),
            start.plus(BillingPeriod.Custom(count = 14, unit = CustomPeriodUnit.DAYS)),
        )
    }

    @Test
    fun `custom weeks adds count times seven days`() {
        assertEquals(
            LocalDate.of(2026, 1, 29),
            start.plus(BillingPeriod.Custom(count = 2, unit = CustomPeriodUnit.WEEKS)),
        )
    }

    @Test
    fun `custom months adds count months`() {
        assertEquals(
            LocalDate.of(2026, 4, 15),
            start.plus(BillingPeriod.Custom(count = 3, unit = CustomPeriodUnit.MONTHS)),
        )
    }

    @Test
    fun `month-end date clamps to the shorter month, matching java-time defaults`() {
        val endOfJanuary = LocalDate.of(2026, 1, 31)
        assertEquals(LocalDate.of(2026, 2, 28), endOfJanuary.plus(BillingPeriod.Monthly))
    }

    private fun subscriptionWithNextPaymentDate(nextPaymentDate: LocalDate) = Subscription(
        name = "Test",
        period = BillingPeriod.Monthly,
        price = BigDecimal("9.99"),
        currency = CurrencyCode("USD"),
        nextPaymentDate = nextPaymentDate,
    )

    @Test
    fun `reminderDateTime subtracts daysBefore and applies the configured time`() {
        val subscription = subscriptionWithNextPaymentDate(LocalDate.of(2026, 9, 10))
        val settings = NotificationSettings(daysBefore = 3, hour = 10, minute = 0)
        assertEquals(LocalDateTime.of(2026, 9, 7, 10, 0), subscription.reminderDateTime(settings))
    }

    @Test
    fun `reminderDateTime crosses a month-year boundary`() {
        val subscription = subscriptionWithNextPaymentDate(LocalDate.of(2026, 1, 1))
        val settings = NotificationSettings(daysBefore = 3, hour = 9, minute = 30)
        assertEquals(LocalDateTime.of(2025, 12, 29, 9, 30), subscription.reminderDateTime(settings))
    }

    @Test
    fun `reminderDateTime with zero daysBefore falls on the payment day itself`() {
        val subscription = subscriptionWithNextPaymentDate(LocalDate.of(2026, 9, 10))
        val settings = NotificationSettings(daysBefore = 0, hour = 10, minute = 0)
        assertEquals(LocalDateTime.of(2026, 9, 10, 10, 0), subscription.reminderDateTime(settings))
    }
}
