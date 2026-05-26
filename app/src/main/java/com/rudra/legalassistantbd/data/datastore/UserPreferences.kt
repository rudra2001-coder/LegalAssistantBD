package com.rudra.legalassistantbd.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

data class UserPreferencesData(
    val isDarkMode: Boolean = true,
    val isFirstLaunch: Boolean = true,
    val language: String = "bn",
    val databaseLoaded: Boolean = false
)

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val LANGUAGE = stringPreferencesKey("language")
        val DATABASE_LOADED = booleanPreferencesKey("database_loaded")
    }

    val preferences: Flow<UserPreferencesData> = context.dataStore.data.map { prefs ->
        UserPreferencesData(
            isDarkMode = prefs[Keys.IS_DARK_MODE] ?: true,
            isFirstLaunch = prefs[Keys.IS_FIRST_LAUNCH] ?: true,
            language = prefs[Keys.LANGUAGE] ?: "bn",
            databaseLoaded = prefs[Keys.DATABASE_LOADED] ?: false
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_DARK_MODE] = enabled }
    }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { it[Keys.IS_FIRST_LAUNCH] = false }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    }

    suspend fun setDatabaseLoaded(loaded: Boolean) {
        context.dataStore.edit { it[Keys.DATABASE_LOADED] = loaded }
    }
}
