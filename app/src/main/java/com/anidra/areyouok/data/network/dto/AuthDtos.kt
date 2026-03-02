package com.anidra.areyouok.data.network.dto

import com.squareup.moshi.JsonClass

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