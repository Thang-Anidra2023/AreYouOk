package com.anidra.areyouok.data.model

data class AccountProfile(
    val id: String,
    val email: String,
    val createdAt: String,
    val lastLoginDate: String?,
    val inactivityThresholdDays: Int?,
    val alertChannelPreference: AlertChannelPreference,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String?
)

enum class AlertChannelPreference {
    EMAIL,
    SMS,
    BOTH
}