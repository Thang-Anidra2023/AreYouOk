package com.anidra.areyouok.data.repositories

import com.anidra.areyouok.data.manager.SessionManager
import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.data.network.UserApiService
import com.anidra.areyouok.data.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val api: UserApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getAccountProfile(): AccountProfile {
        return sessionManager.withAuthRetry { auth ->
            api.getMe(auth).toDomain()
        }
    }
}