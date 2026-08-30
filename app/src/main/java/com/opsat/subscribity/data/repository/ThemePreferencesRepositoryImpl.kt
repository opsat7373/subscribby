package com.opsat.subscribity.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.opsat.subscribity.domain.model.ThemeMode
import com.opsat.subscribity.domain.repository.ThemePreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

class ThemePreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : ThemePreferencesRepository {
    override val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY]?.let { name ->
            runCatching { ThemeMode.valueOf(name) }.getOrNull()
        } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE_KEY] = mode.name }
    }
}
