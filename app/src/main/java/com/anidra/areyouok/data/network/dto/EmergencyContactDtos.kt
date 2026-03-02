package com.anidra.areyouok.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmergencyContactRequest(
    val mobileNumber: String,
    val email: String,
    val label: String? = null
)

@JsonClass(generateAdapter = true)
data class EmergencyContactResponse(
    val id: String,
    val mobileNumber: String,
    val email: String,
    val contactIndex: Int? = null,
    val verified: Boolean? = null,
    val label: String? = null
)