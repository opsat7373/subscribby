package com.opsat.subscribity.domain.repository

import com.opsat.subscribity.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemePreferencesRepository {
    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
