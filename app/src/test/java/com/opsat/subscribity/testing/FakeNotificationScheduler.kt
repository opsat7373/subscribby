package com.opsat.subscribity.testing

import com.opsat.subscribity.domain.notification.NotificationScheduler

class FakeNotificationScheduler : NotificationScheduler {
    val scheduled = mutableMapOf<Long, Long>()
    val cancelled = mutableListOf<Long>()

    override fun schedule(subscriptionId: Long, triggerAtMillis: Long) {
        scheduled[subscriptionId] = triggerAtMillis
    }

    override fun cancel(subscriptionId: Long) {
        scheduled.remove(subscriptionId)
        cancelled += subscriptionId
    }
}
