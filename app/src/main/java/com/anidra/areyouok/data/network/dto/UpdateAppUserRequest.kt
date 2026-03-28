package com.anidra.areyouok.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateAppUserRequest(
    val email: String? = null,
    val inactivityThresholdDays: Int? = null,
    val alertChannelPreference: AlertChannelPreferenceDto? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null
)