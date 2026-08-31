package com.opsat.subscribity.presentation.settings

import com.opsat.subscribity.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val reminderDaysBeforeText: String = "3",
    val reminderHour: Int = 10,
    val reminderMinute: Int = 0,
    val isTimePickerVisible: Boolean = false,
)
