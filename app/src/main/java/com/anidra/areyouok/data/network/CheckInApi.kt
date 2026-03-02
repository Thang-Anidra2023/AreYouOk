package com.anidra.areyouok.data.network

import com.anidra.areyouok.data.network.dto.CheckInRequest
import com.anidra.areyouok.data.network.dto.MessageResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CheckInApi {

    // POST http://13.203.195.38:8080/checkin/api/user/check-in
    @POST("api/user/check-in")
    suspend fun checkIn(
        @Header("Authorization") authorization: String,
        @Body body: CheckInRequest
    ): MessageResponse
}