package com.opsat.subscribity.data.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.opsat.subscribity.R
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.NotificationPreferencesRepository
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import com.opsat.subscribity.presentation.common.currencySymbol
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var subscriptionRepository: SubscriptionRepository

    @Inject
    lateinit var notificationPreferencesRepository: NotificationPreferencesRepository

    override fun onReceive(context: Context, intent: Intent) {
        val subscriptionId = intent.getLongExtra(EXTRA_SUBSCRIPTION_ID, -1L)
        if (subscriptionId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = notificationPreferencesRepository.notificationSettings.first()
                val subscription = subscriptionRepository.getSubscription(subscriptionId)
                if (settings.enabled && subscription != null && subscription.notificationsEnabled) {
                    showNotification(context, subscription)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, subscription: Subscription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val amount = "${currencySymbol(subscription.currency)}${subscription.price}"
        val notification = NotificationCompat.Builder(context, NotificationChannels.PAYMENT_REMINDERS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Upcoming payment: ${subscription.name}")
            .setContentText("Charges on ${subscription.nextPaymentDate}: $amount")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(subscription.id.toInt(), notification)
    }

    companion object {
        const val EXTRA_SUBSCRIPTION_ID = "extra_subscription_id"
    }
}
