package com.opsat.subscribity.testing

import com.opsat.subscribity.domain.model.NotificationSettings
import com.opsat.subscribity.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNotificationPreferencesRepository : NotificationPreferencesRepository {
    val settingsFlow = MutableStateFlow(NotificationSettings())

    override val notificationSettings: Flow<NotificationSettings> = settingsFlow

    override suspend fun setEnabled(enabled: Boolean) {
        settingsFlow.value = settingsFlow.value.copy(enabled = enabled)
    }

    override suspend fun setDaysBefore(days: Int) {
        settingsFlow.value = settingsFlow.value.copy(daysBefore = days)
    }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        settingsFlow.value = settingsFlow.value.copy(hour = hour, minute = minute)
    }
}
