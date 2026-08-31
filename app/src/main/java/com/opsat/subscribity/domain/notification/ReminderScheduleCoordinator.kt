package com.opsat.subscribity.domain.notification

import com.opsat.subscribity.domain.model.NotificationSettings
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.model.reminderDateTime
import com.opsat.subscribity.domain.usecase.ObserveNotificationSettingsUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class ReminderScheduleCoordinator @Inject constructor(
    private val observeSubscriptions: ObserveSubscriptionsUseCase,
    private val observeNotificationSettings: ObserveNotificationSettingsUseCase,
    private val scheduler: NotificationScheduler,
    private val clock: Clock,
) {
    private var previousScheduledIds: Set<Long> = emptySet()

    fun start(scope: CoroutineScope) {
        scope.launch {
            combine(observeSubscriptions(), observeNotificationSettings()) { subscriptions, settings ->
                subscriptions to settings
            }.collect { (subscriptions, settings) -> reschedule(subscriptions, settings) }
        }
    }

    suspend fun rescheduleOnce() {
        val subscriptions = observeSubscriptions().first()
        val settings = observeNotificationSettings().first()
        reschedule(subscriptions, settings)
    }

    private fun reschedule(subscriptions: List<Subscription>, settings: NotificationSettings) {
        previousScheduledIds.forEach(scheduler::cancel)
        previousScheduledIds = emptySet()

        if (!settings.enabled) return

        val now = LocalDateTime.now(clock)
        val newIds = mutableSetOf<Long>()
        subscriptions
            .filter { it.notificationsEnabled && it.id != 0L }
            .forEach { subscription ->
                val triggerAt = subscription.reminderDateTime(settings)
                if (triggerAt.isAfter(now)) {
                    val triggerAtMillis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    scheduler.schedule(subscription.id, triggerAtMillis)
                    newIds += subscription.id
                }
            }
        previousScheduledIds = newIds
    }
}
