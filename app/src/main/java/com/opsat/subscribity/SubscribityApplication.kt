package com.opsat.subscribity

import android.app.Application
import com.opsat.subscribity.data.notification.NotificationChannels
import com.opsat.subscribity.di.ApplicationScope
import com.opsat.subscribity.domain.notification.ReminderScheduleCoordinator
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltAndroidApp
class SubscribityApplication : Application() {

    @Inject
    lateinit var reminderScheduleCoordinator: ReminderScheduleCoordinator

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensureChannels(this)
        reminderScheduleCoordinator.start(applicationScope)
    }
}
