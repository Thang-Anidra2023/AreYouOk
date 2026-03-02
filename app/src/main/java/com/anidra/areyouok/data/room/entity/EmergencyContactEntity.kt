package com.anidra.areyouok.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EmergencyContactSyncState(val value: Int) {
    PENDING(0),
    SYNCED(1),
    FAILED(2);

    companion object {
        fun fromInt(v: Int): EmergencyContactSyncState =
            entries.firstOrNull { it.value == v } ?: PENDING
    }
}

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey
    val localId: String,

    val mobileNumber: String,
    val email: String,
    val label: String? = null,

    // Optional metadata returned by server
    val contactIndex: Int? = null,
    val verified: Boolean? = null,

    // Local sync status only
    val syncState: Int = EmergencyContactSyncState.PENDING.value,

    val createdAtMillis: Long,
    val updatedAtMillis: Long,

    val syncedAtMillis: Long? = null,
    val lastAttemptAtMillis: Long? = null,
    val attemptCount: Int = 0,
    val lastError: String? = null
)