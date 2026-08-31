package com.opsat.subscribity.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.opsat.subscribity.domain.notification.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmManagerNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationScheduler {
    private val alarmManager: AlarmManager = context.getSystemService()!!

    override fun schedule(subscriptionId: Long, triggerAtMillis: Long) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntentFor(subscriptionId))
    }

    override fun cancel(subscriptionId: Long) {
        alarmManager.cancel(pendingIntentFor(subscriptionId))
    }

    private fun pendingIntentFor(subscriptionId: Long): PendingIntent {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            .putExtra(ReminderBroadcastReceiver.EXTRA_SUBSCRIPTION_ID, subscriptionId)
        return PendingIntent.getBroadcast(
            context,
            subscriptionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
