package com.anidra.areyouok.data.repositories

import com.anidra.areyouok.data.manager.SessionManager
import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.data.model.AlertChannelPreference
import com.anidra.areyouok.data.network.UserApiService
import com.anidra.areyouok.data.network.dto.AlertChannelPreferenceDto
import com.anidra.areyouok.data.network.dto.UpdateAppUserRequest
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

    suspend fun updateAccountProfile(
        email: String,
        inactivityThresholdDays: Int?,
        alertChannelPreference: AlertChannelPreference,
        firstName: String?,
        lastName: String?,
        mobileNumber: String?
    ): AccountProfile {
        val request = UpdateAppUserRequest(
            email = email.trim(),
            inactivityThresholdDays = inactivityThresholdDays,
            alertChannelPreference = alertChannelPreference.toDto(),
            firstName = firstName.nullIfBlank(),
            lastName = lastName.nullIfBlank(),
            mobileNumber = mobileNumber.nullIfBlank()
        )

        return sessionManager.withAuthRetry { auth ->
            api.updateMe(auth, request).toDomain()
        }
    }
}

private fun AlertChannelPreference.toDto(): AlertChannelPreferenceDto {
    return when (this) {
        AlertChannelPreference.EMAIL -> AlertChannelPreferenceDto.EMAIL
        AlertChannelPreference.SMS -> AlertChannelPreferenceDto.SMS
        AlertChannelPreference.BOTH -> AlertChannelPreferenceDto.BOTH
    }
}

private fun String?.nullIfBlank(): String? {
    return this?.trim()?.takeIf { it.isNotEmpty() }
}