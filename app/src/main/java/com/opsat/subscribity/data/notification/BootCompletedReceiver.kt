package com.opsat.subscribity.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.opsat.subscribity.domain.notification.ReminderScheduleCoordinator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var coordinator: ReminderScheduleCoordinator

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                coordinator.rescheduleOnce()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
