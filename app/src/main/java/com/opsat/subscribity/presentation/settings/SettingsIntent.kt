package com.opsat.subscribity.presentation.settings

import com.opsat.subscribity.domain.model.ThemeMode

sealed interface SettingsIntent {
    data class SelectThemeMode(val mode: ThemeMode) : SettingsIntent
}
