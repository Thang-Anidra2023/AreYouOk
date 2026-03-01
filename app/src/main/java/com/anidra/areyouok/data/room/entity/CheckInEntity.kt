package com.anidra.areyouok.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CheckInSyncState(val value: Int) {
    PENDING(0),
    SYNCED(1),
    FAILED(2);

    companion object {
        fun fromInt(v: Int): CheckInSyncState = entries.firstOrNull { it.value == v } ?: PENDING
    }
}

@Entity(tableName = "checkins")
data class CheckInEntity(
    /**
     * One row per day:
     * LocalDate.now().toEpochDay()
     */
    @PrimaryKey val epochDay: Long,

    val createdAtMillis: Long,
    val timeZoneId: String,

    /** 0=PENDING, 1=SYNCED, 2=FAILED */
    val syncState: Int = CheckInSyncState.PENDING.value,

    val serverId: String? = null,
    val syncedAtMillis: Long? = null,

    val lastAttemptAtMillis: Long? = null,
    val attemptCount: Int = 0,
    val lastError: String? = null
)