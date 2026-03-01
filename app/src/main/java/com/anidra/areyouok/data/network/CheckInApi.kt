package com.anidra.areyouok.data.network

import com.anidra.areyouok.data.network.dto.CheckInCreateRequest
import com.anidra.areyouok.data.network.dto.CheckInCreateResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CheckInApi {
    @POST("v1/checkins")
    suspend fun createCheckIn(
        @Header("Authorization") authorization: String,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body body: CheckInCreateRequest
    ): CheckInCreateResponse
}