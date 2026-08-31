package com.opsat.subscribity.domain.repository

import com.opsat.subscribity.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    val notificationSettings: Flow<NotificationSettings>

    suspend fun setEnabled(enabled: Boolean)

    suspend fun setDaysBefore(days: Int)

    suspend fun setReminderTime(hour: Int, minute: Int)
}
