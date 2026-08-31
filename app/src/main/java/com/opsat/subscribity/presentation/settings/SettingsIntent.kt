package com.opsat.subscribity.presentation.settings

import com.opsat.subscribity.domain.model.ThemeMode

sealed interface SettingsIntent {
    data class SelectThemeMode(val mode: ThemeMode) : SettingsIntent
    data class NotificationsEnabledToggled(val enabled: Boolean) : SettingsIntent
    data class ReminderDaysBeforeChanged(val value: String) : SettingsIntent
    data class ReminderTimeSelected(val hour: Int, val minute: Int) : SettingsIntent
    data class TimePickerVisibilityChanged(val visible: Boolean) : SettingsIntent
}
