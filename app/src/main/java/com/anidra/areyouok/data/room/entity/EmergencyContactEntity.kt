package com.anidra.areyouok.data.room.entity

import androidx.room.Entity
import androidx.room.Index
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

enum class EmergencyContactPendingOp(val value: Int) {
    NONE(0),
    UPSERT(1),
    DELETE(2);

    companion object {
        fun fromInt(v: Int): EmergencyContactPendingOp =
            entries.firstOrNull { it.value == v } ?: NONE
    }
}

@Entity(
    tableName = "emergency_contacts",
    indices = [
        Index(value = ["serverId"], unique = true)
    ]
)
data class EmergencyContactEntity(
    /** Local primary key so we can create/edit offline */
    @PrimaryKey val localId: String,

    /** Server UUID (null until created on server) */
    val serverId: String? = null,

    val mobileNumber: String,
    val email: String,
    val label: String? = null,

    /** Returned by server */
    val contactIndex: Int? = null,
    val verified: Boolean? = null,

    /** Soft delete for offline delete UX */
    val isDeleted: Boolean = false,

    /** 0=NONE, 1=UPSERT, 2=DELETE */
    val pendingOp: Int = EmergencyContactPendingOp.UPSERT.value,

    /** 0=PENDING, 1=SYNCED, 2=FAILED */
    val syncState: Int = EmergencyContactSyncState.PENDING.value,

    val createdAtMillis: Long,
    val updatedAtMillis: Long,

    val syncedAtMillis: Long? = null,
    val lastAttemptAtMillis: Long? = null,
    val attemptCount: Int = 0,
    val lastError: String? = null
)