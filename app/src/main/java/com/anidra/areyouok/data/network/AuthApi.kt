package com.anidra.areyouok.data.network

import com.anidra.areyouok.data.network.dto.AuthResponse
import com.anidra.areyouok.data.network.dto.LoginRequest
import com.anidra.areyouok.data.network.dto.RefreshTokenRequest
import com.anidra.areyouok.data.network.dto.RegisterRequest
import com.anidra.areyouok.data.network.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/user/register")
    suspend fun register(@Body body: RegisterRequest): UserResponse

    @POST("api/user/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/user/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequest): AuthResponse
}