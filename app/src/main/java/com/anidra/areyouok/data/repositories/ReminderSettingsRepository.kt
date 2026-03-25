package com.anidra.areyouok.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

@Singleton
class ReminderSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        val CHECK_IN_REMINDERS_ENABLED =
            booleanPreferencesKey("check_in_reminders_enabled")
    }

    val checkInRemindersEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { prefs ->
            prefs[CHECK_IN_REMINDERS_ENABLED] ?: true
        }

    suspend fun setCheckInRemindersEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[CHECK_IN_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun isCheckInRemindersEnabled(): Boolean {
        return checkInRemindersEnabled.first()
    }
}