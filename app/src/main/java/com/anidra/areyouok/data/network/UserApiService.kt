package com.anidra.areyouok.data.network


import com.anidra.areyouok.data.network.dto.UserDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApiService {
    @GET("api/user/me")
    suspend fun getMe(
        @Header("Authorization") auth: String
    ): UserDetailsResponse
}