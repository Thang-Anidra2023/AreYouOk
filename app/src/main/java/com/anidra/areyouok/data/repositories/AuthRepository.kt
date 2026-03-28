package com.anidra.areyouok.data.repositories

import android.content.Context
import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.manager.AppDatabase
import com.anidra.areyouok.data.network.AuthApi
import com.anidra.areyouok.data.network.dto.LoginRequest
import com.anidra.areyouok.data.network.dto.RegisterRequest
import com.anidra.areyouok.data.network.dto.UserResponse
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
    suspend fun register(
        email: String,
        password: String,
        registrationType: String? = null,
        firstName: String? = null,
        middleName: String? = null,
        lastName: String? = null,
        country: String? = null,
        state: String? = null,
        mobileNumber: String? = null,
        addressLine1: String? = null,
        addressLine2: String? = null
    ): UserResponse {
        return api.register(
            RegisterRequest(
                email = email,
                password = password,
                registrationType = registrationType,
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                country = country,
                state = state,
                mobileNumber = mobileNumber,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2
            )
        )
    }

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
        CheckInWorkScheduler.cancelAll(context)
        CheckInReminderWorkScheduler.cancel(context)
        EmergencyContactsWorkScheduler.cancelAll(context)

        if (clearLocalData) {
            db.clearAllTables()
        }

        prefs.clearSession()
    }
}