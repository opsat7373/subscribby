package com.opsat.subscribity.domain.model

import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class SubscriptionTest {

    private fun subscription(
        isTrial: Boolean = false,
        trialPeriod: BillingPeriod? = null,
        trialPrice: BigDecimal? = null,
    ) = Subscription(
        name = "Test",
        icon = "test",
        period = BillingPeriod.Monthly,
        price = BigDecimal("9.99"),
        currency = CurrencyCode("USD"),
        nextPaymentDate = LocalDate.of(2026, 9, 1),
        isTrial = isTrial,
        trialPeriod = trialPeriod,
        trialPrice = trialPrice,
    )

    @Test
    fun `trial fields are optional when isTrial is false`() {
        subscription(isTrial = false, trialPeriod = null, trialPrice = null)
    }

    @Test
    fun `constructing with a non-null trialPeriod while isTrial is false throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            subscription(isTrial = false, trialPeriod = BillingPeriod.Weekly, trialPrice = null)
        }
    }

    @Test
    fun `constructing with a non-null trialPrice while isTrial is false throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            subscription(isTrial = false, trialPeriod = null, trialPrice = BigDecimal.ZERO)
        }
    }

    @Test
    fun `constructing with isTrial true and both trial fields set succeeds`() {
        subscription(isTrial = true, trialPeriod = BillingPeriod.Weekly, trialPrice = BigDecimal.ZERO)
    }

    @Test
    fun `constructing with isTrial true and a missing trialPeriod throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            subscription(isTrial = true, trialPeriod = null, trialPrice = BigDecimal.ZERO)
        }
    }

    @Test
    fun `constructing with isTrial true and a missing trialPrice throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            subscription(isTrial = true, trialPeriod = BillingPeriod.Weekly, trialPrice = null)
        }
    }
}
