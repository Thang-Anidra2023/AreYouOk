package com.anidra.areyouok.data.network.dto

import com.anidra.areyouok.data.model.AccountProfile
import com.anidra.areyouok.data.model.AlertChannelPreference
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDetailsResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "lastLoginDate")
    val lastLoginDate: String?,
    @Json(name = "inactivityThresholdDays")
    val inactivityThresholdDays: Int?,
    @Json(name = "alertChannelPreference")
    val alertChannelPreference: AlertChannelPreferenceDto?,
    @Json(name = "firstName")
    val firstName: String?,
    @Json(name = "lastName")
    val lastName: String?,
    @Json(name = "mobileNumber")
    val mobileNumber: String?
)

enum class AlertChannelPreferenceDto {
    EMAIL,
    SMS,
    BOTH
}

fun UserDetailsResponse.toDomain(): AccountProfile {
    return AccountProfile(
        id = id,
        email = email,
        createdAt = createdAt,
        lastLoginDate = lastLoginDate,
        inactivityThresholdDays = inactivityThresholdDays,
        alertChannelPreference = when (alertChannelPreference) {
            AlertChannelPreferenceDto.EMAIL -> AlertChannelPreference.EMAIL
            AlertChannelPreferenceDto.SMS -> AlertChannelPreference.SMS
            AlertChannelPreferenceDto.BOTH, null -> AlertChannelPreference.BOTH
        },
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        mobileNumber = mobileNumber
    )
}