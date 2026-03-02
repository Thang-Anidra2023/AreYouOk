package com.anidra.areyouok.data.network

import com.anidra.areyouok.data.network.dto.EmergencyContactRequest
import com.anidra.areyouok.data.network.dto.EmergencyContactResponse
import retrofit2.http.*

interface EmergencyContactsApi {
    @GET("api/emergency-contacts")
    suspend fun list(
        @Header("Authorization") authorization: String
    ): List<EmergencyContactResponse>

    @POST("api/emergency-contacts")
    suspend fun add(
        @Header("Authorization") authorization: String,
        @Body body: EmergencyContactRequest
    ): EmergencyContactResponse

    @PUT("api/emergency-contacts/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body body: EmergencyContactRequest
    ): EmergencyContactResponse

    @DELETE("api/emergency-contacts/{id}")
    suspend fun delete(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    )
}