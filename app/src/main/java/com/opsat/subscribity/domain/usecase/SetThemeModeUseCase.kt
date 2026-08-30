package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.model.ThemeMode
import com.opsat.subscribity.domain.repository.ThemePreferencesRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: ThemePreferencesRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}
