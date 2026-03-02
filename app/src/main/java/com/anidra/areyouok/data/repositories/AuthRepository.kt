package com.anidra.areyouok.data.repositories

import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.network.AuthApi
import com.anidra.areyouok.data.network.dto.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val prefs: UserPrefs
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

    suspend fun logout() {
        prefs.clearSession()
    }
}