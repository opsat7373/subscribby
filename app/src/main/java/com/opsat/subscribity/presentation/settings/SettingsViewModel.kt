package com.opsat.subscribity.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.subscribity.domain.usecase.ObserveNotificationSettingsUseCase
import com.opsat.subscribity.domain.usecase.ObserveThemeModeUseCase
import com.opsat.subscribity.domain.usecase.SetNotificationsEnabledUseCase
import com.opsat.subscribity.domain.usecase.SetReminderDaysBeforeUseCase
import com.opsat.subscribity.domain.usecase.SetReminderTimeUseCase
import com.opsat.subscribity.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    observeNotificationSettings: ObserveNotificationSettingsUseCase,
    private val setNotificationsEnabled: SetNotificationsEnabledUseCase,
    private val setReminderDaysBefore: SetReminderDaysBeforeUseCase,
    private val setReminderTime: SetReminderTimeUseCase,
) : ViewModel() {

    private val isTimePickerVisible = MutableStateFlow(false)

    val state: StateFlow<SettingsState> = combine(
        observeThemeMode(),
        observeNotificationSettings(),
        isTimePickerVisible,
    ) { mode, settings, timePickerVisible ->
        SettingsState(
            themeMode = mode,
            notificationsEnabled = settings.enabled,
            reminderDaysBeforeText = settings.daysBefore.toString(),
            reminderHour = settings.hour,
            reminderMinute = settings.minute,
            isTimePickerVisible = timePickerVisible,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SelectThemeMode -> viewModelScope.launch {
                setThemeMode(intent.mode)
            }

            is SettingsIntent.NotificationsEnabledToggled -> viewModelScope.launch {
                setNotificationsEnabled(intent.enabled)
            }

            is SettingsIntent.ReminderDaysBeforeChanged -> {
                val days = intent.value.filter(Char::isDigit).toIntOrNull()
                if (days != null && days in 0..30) {
                    viewModelScope.launch { setReminderDaysBefore(days) }
                }
            }

            is SettingsIntent.ReminderTimeSelected -> viewModelScope.launch {
                setReminderTime(intent.hour, intent.minute)
                isTimePickerVisible.value = false
            }

            is SettingsIntent.TimePickerVisibilityChanged -> {
                isTimePickerVisible.value = intent.visible
            }
        }
    }
}
