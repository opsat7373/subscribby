package com.opsat.subscribity.domain.notification

interface NotificationScheduler {
    fun schedule(subscriptionId: Long, triggerAtMillis: Long)

    fun cancel(subscriptionId: Long)
}
