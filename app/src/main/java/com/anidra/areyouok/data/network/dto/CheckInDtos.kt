package com.anidra.areyouok.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckInRequest(
    val snoozeDays: Int? = null
)

@JsonClass(generateAdapter = true)
data class MessageResponse(
    val message: String
)