package com.anidra.areyouok.data.network

import com.anidra.areyouok.data.network.dto.UpdateAppUserRequest
import com.anidra.areyouok.data.network.dto.UserDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserApiService {
    @GET("api/user/me")
    suspend fun getMe(
        @Header("Authorization") auth: String
    ): UserDetailsResponse

    @PUT("api/user/details")
    suspend fun updateMe(
        @Header("Authorization") auth: String,
        @Body body: UpdateAppUserRequest
    ): UserDetailsResponse
}