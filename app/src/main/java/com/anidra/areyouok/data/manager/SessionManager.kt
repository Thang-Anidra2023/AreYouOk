package com.anidra.areyouok.data.manager

import com.anidra.areyouok.data.datastore.UserPrefs
import com.anidra.areyouok.data.network.AuthApi
import com.anidra.areyouok.data.network.dto.RefreshTokenRequest
import com.anidra.areyouok.data.session.SessionExpiredException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val prefs: UserPrefs,
    private val authApi: AuthApi
) {
    private val refreshMutex = Mutex()

    suspend fun clearSession() {
        prefs.clearSession()
    }

    suspend fun getAccessToken(): String? = prefs.authToken.first()

    suspend fun refreshAccessToken(failedAccessToken: String? = null): String? {
        return refreshMutex.withLock {
            val latestAccessToken = prefs.authToken.first()

            if (
                !failedAccessToken.isNullOrBlank() &&
                !latestAccessToken.isNullOrBlank() &&
                latestAccessToken != failedAccessToken
            ) {
                return latestAccessToken
            }

            val refreshToken = prefs.refreshToken.first()
            if (refreshToken.isNullOrBlank()) {
                prefs.clearSession()
                return null
            }

            try {
                val response = authApi.refresh(
                    RefreshTokenRequest(refreshToken = refreshToken)
                )

                val newAccessToken = response.accessToken
                val newRefreshToken = response.refreshToken ?: refreshToken

                prefs.updateTokens(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken
                )

                return newAccessToken
            } catch (e: IOException) {
                throw e
            } catch (e: HttpException) {
                if (e.code() == 400 || e.code() == 401 || e.code() == 403) {
                    prefs.clearSession()
                    return null
                }
                throw e
            }
        }
    }

    suspend fun <T> withAuthRetry(
        block: suspend (authorization: String) -> T
    ): T {
        var accessToken = prefs.authToken.first()

        if (accessToken.isNullOrBlank()) {
            accessToken = refreshAccessToken()
            if (accessToken.isNullOrBlank()) {
                throw SessionExpiredException()
            }
        }

        try {
            return block("Bearer $accessToken")
        } catch (e: HttpException) {
            if (e.code() != 401 && e.code() != 403) {
                throw e
            }
        }

        val refreshedToken = refreshAccessToken(failedAccessToken = accessToken)
        if (refreshedToken.isNullOrBlank()) {
            throw SessionExpiredException()
        }

        return block("Bearer $refreshedToken")
    }
}