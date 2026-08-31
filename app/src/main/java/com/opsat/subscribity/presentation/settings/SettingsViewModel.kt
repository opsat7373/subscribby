package com.opsat.subscribity.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.subscribity.domain.usecase.ObserveThemeModeUseCase
import com.opsat.subscribity.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
) : ViewModel() {

    val state: StateFlow<SettingsState> = observeThemeMode()
        .map { mode -> SettingsState(themeMode = mode) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SelectThemeMode -> viewModelScope.launch {
                setThemeMode(intent.mode)
            }
        }
    }
}
