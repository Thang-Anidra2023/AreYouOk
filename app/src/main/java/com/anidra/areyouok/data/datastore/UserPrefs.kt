package com.anidra.areyouok.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val KEY_USER_ID = stringPreferencesKey("user_id")

    val authToken: Flow<String?> = context.dataStore.data.map { it[KEY_AUTH_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }
    val userId: Flow<String?> = context.dataStore.data.map { it[KEY_USER_ID] }

    suspend fun setSession(userId: String, accessToken: String, refreshToken: String?) {
        context.dataStore.edit {
            it[KEY_USER_ID] = userId
            it[KEY_AUTH_TOKEN] = accessToken
            if (refreshToken.isNullOrBlank()) {
                it.remove(KEY_REFRESH_TOKEN)
            } else {
                it[KEY_REFRESH_TOKEN] = refreshToken
            }
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String?) {
        context.dataStore.edit {
            it[KEY_AUTH_TOKEN] = accessToken
            if (refreshToken.isNullOrBlank()) {
                it.remove(KEY_REFRESH_TOKEN)
            } else {
                it[KEY_REFRESH_TOKEN] = refreshToken
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(KEY_USER_ID)
            it.remove(KEY_AUTH_TOKEN)
            it.remove(KEY_REFRESH_TOKEN)
        }
    }
}