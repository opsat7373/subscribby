package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.model.ThemeMode
import com.opsat.subscribity.domain.repository.ThemePreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThemeModeUseCase @Inject constructor(
    private val repository: ThemePreferencesRepository,
) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode
}
