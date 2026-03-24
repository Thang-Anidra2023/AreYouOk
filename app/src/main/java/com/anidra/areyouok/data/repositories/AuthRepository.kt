package com.anidra.areyouok.data.repositories

import android.content.Context
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.manager.AppDatabase
import com.anidra.areyouok.data.network.AuthApi
import com.anidra.areyouok.data.network.dto.LoginRequest
import com.anidra.areyouok.data.work.CheckInReminderWorkScheduler
import com.anidra.areyouok.data.work.CheckInWorkScheduler
import com.anidra.areyouok.data.work.EmergencyContactsWorkScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val prefs: UserPrefs,
    private val db: AppDatabase,
    @ApplicationContext private val context: Context
) {
    suspend fun login(email: String, password: String) {
        val res = api.login(LoginRequest(email = email, password = password))

        val userId = res.user?.id ?: ""
        prefs.setSession(
            userId = userId,
            accessToken = res.accessToken,
            refreshToken = res.refreshToken
        )
    }

    suspend fun logout(clearLocalData: Boolean = true) = withContext(Dispatchers.IO) {
        // Stop app background work first
        CheckInWorkScheduler.cancelAll(context)
        CheckInReminderWorkScheduler.cancelAll(context)
        EmergencyContactsWorkScheduler.cancelAll(context)

        // Remove local user data so another user won't see old data
        if (clearLocalData) {
            db.clearAllTables()
        }

        // Clear auth state last so navigation only changes after cleanup finishes
        prefs.clearSession()
    }
}