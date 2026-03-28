package com.anidra.areyouok.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val email: String,
    val password: String,
    val registrationType: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val country: String? = null,
    val state: String? = null,
    val mobileNumber: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class UserResponse(
    val id: String,
    val email: String,
    val createdAt: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val tokenType: String? = null,
    val accessToken: String,
    val refreshToken: String? = null,
    val user: UserResponse? = null
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    val refreshToken: String
)