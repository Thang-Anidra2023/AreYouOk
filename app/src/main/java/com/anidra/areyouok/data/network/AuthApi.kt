package com.anidra.areyouok.data.network

import com.anidra.areyouok.data.network.dto.AuthResponse
import com.anidra.areyouok.data.network.dto.LoginRequest
import com.anidra.areyouok.data.network.dto.RefreshTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // POST http://13.203.195.38:8080/checkin/api/user/login
    @POST("api/user/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    // POST http://13.203.195.38:8080/checkin/api/user/refresh
    @POST("api/user/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequest): AuthResponse
}