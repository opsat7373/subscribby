package com.opsat.subscribity.domain.notification

import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.usecase.ObserveNotificationSettingsUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import com.opsat.subscribity.testing.FakeNotificationPreferencesRepository
import com.opsat.subscribity.testing.FakeNotificationScheduler
import com.opsat.subscribity.testing.FakeSubscriptionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderScheduleCoordinatorTest {

    private val subscriptionRepository = FakeSubscriptionRepository()
    private val notificationPreferencesRepository = FakeNotificationPreferencesRepository()
    private val scheduler = FakeNotificationScheduler()

    private val fixedNow = LocalDateTime.of(2026, 9, 1, 0, 0)
    private val clock = Clock.fixed(fixedNow.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())

    private val coordinator = ReminderScheduleCoordinator(
        ObserveSubscriptionsUseCase(subscriptionRepository),
        ObserveNotificationSettingsUseCase(notificationPreferencesRepository),
        scheduler,
        clock,
    )

    private fun subscription(
        id: Long,
        nextPaymentDate: LocalDate,
        notificationsEnabled: Boolean = true,
    ) = Subscription(
        id = id,
        name = "Test",
        icon = "test",
        period = BillingPeriod.Monthly,
        price = BigDecimal("9.99"),
        currency = CurrencyCode("USD"),
        nextPaymentDate = nextPaymentDate,
        notificationsEnabled = notificationsEnabled,
    )

    @Test
    fun `schedules an enabled subscription with a future reminder`() = runTest {
        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 10)),
        )

        coordinator.rescheduleOnce()

        assertTrue(scheduler.scheduled.containsKey(1L))
    }

    @Test
    fun `does not schedule a subscription with notifications disabled`() = runTest {
        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 10), notificationsEnabled = false),
        )

        coordinator.rescheduleOnce()

        assertFalse(scheduler.scheduled.containsKey(1L))
    }

    @Test
    fun `does not schedule a subscription whose reminder time already passed`() = runTest {
        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 2)),
        )

        coordinator.rescheduleOnce()

        assertFalse(scheduler.scheduled.containsKey(1L))
    }

    @Test
    fun `does not schedule anything when notifications are globally disabled`() = runTest {
        notificationPreferencesRepository.setEnabled(false)
        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 10)),
        )

        coordinator.rescheduleOnce()

        assertTrue(scheduler.scheduled.isEmpty())
    }

    @Test
    fun `cancels a previously scheduled alarm when the subscription disappears`() = runTest {
        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 10)),
        )
        coordinator.rescheduleOnce()
        assertTrue(scheduler.scheduled.containsKey(1L))

        subscriptionRepository.subscriptionsFlow.value = emptyList()
        coordinator.rescheduleOnce()

        assertFalse(scheduler.scheduled.containsKey(1L))
        assertEquals(listOf(1L), scheduler.cancelled)
    }

    @Test
    fun `cancels a previously scheduled alarm when the subscription's toggle is turned off`() = runTest {
        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 10)),
        )
        coordinator.rescheduleOnce()
        assertTrue(scheduler.scheduled.containsKey(1L))

        subscriptionRepository.subscriptionsFlow.value = listOf(
            subscription(id = 1L, nextPaymentDate = LocalDate.of(2026, 9, 10), notificationsEnabled = false),
        )
        coordinator.rescheduleOnce()

        assertFalse(scheduler.scheduled.containsKey(1L))
    }
}
