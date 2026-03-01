package com.anidra.areyouok.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckInCreateRequest(
    val epochDay: Long,
    val date: String,         // "YYYY-MM-DD"
    val timeZoneId: String,
    val createdAtMillis: Long
)

@JsonClass(generateAdapter = true)
data class CheckInCreateResponse(
    val id: String,
    val receivedAtMillis: Long
)