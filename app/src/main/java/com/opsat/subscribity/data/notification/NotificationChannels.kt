package com.opsat.subscribity.data.notification

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {
    const val PAYMENT_REMINDERS_CHANNEL_ID = "payment_reminders"

    fun ensureChannels(context: Context) {
        val channel = NotificationChannelCompat.Builder(
            PAYMENT_REMINDERS_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
        )
            .setName("Payment reminders")
            .setDescription("Reminders before a subscription payment is charged")
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}
