package com.opsat.subscribity.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.opsat.subscribity.domain.model.NotificationSettings
import com.opsat.subscribity.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
private val DAYS_BEFORE_KEY = intPreferencesKey("reminder_days_before")
private val HOUR_KEY = intPreferencesKey("reminder_hour")
private val MINUTE_KEY = intPreferencesKey("reminder_minute")

class NotificationPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : NotificationPreferencesRepository {
    override val notificationSettings: Flow<NotificationSettings> = dataStore.data.map { preferences ->
        NotificationSettings(
            enabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true,
            daysBefore = preferences[DAYS_BEFORE_KEY] ?: 3,
            hour = preferences[HOUR_KEY] ?: 10,
            minute = preferences[MINUTE_KEY] ?: 0,
        )
    }

    override suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED_KEY] = enabled }
    }

    override suspend fun setDaysBefore(days: Int) {
        dataStore.edit { it[DAYS_BEFORE_KEY] = days }
    }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[HOUR_KEY] = hour
            it[MINUTE_KEY] = minute
        }
    }
}
