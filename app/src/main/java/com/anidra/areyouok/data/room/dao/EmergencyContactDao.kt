package com.anidra.areyouok.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {

    /** What UI observes */
    @Query("SELECT * FROM emergency_contacts WHERE isDeleted = 0 ORDER BY createdAtMillis DESC")
    fun observeActive(): Flow<List<EmergencyContactEntity>>

    @Query("SELECT * FROM emergency_contacts WHERE localId = :localId LIMIT 1")
    suspend fun getByLocalId(localId: String): EmergencyContactEntity?

    /** Items that need syncing */
    @Query("""
        SELECT * FROM emergency_contacts
        WHERE pendingOp != 0 OR syncState != 1
        ORDER BY updatedAtMillis ASC
        LIMIT :limit
    """)
    suspend fun getPending(limit: Int): List<EmergencyContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EmergencyContactEntity)

    @Query("""
        UPDATE emergency_contacts
        SET
            serverId = :serverId,
            contactIndex = :contactIndex,
            verified = :verified,
            label = :label,

            isDeleted = 0,
            pendingOp = 0,
            syncState = 1,

            syncedAtMillis = :syncedAtMillis,
            lastAttemptAtMillis = :attemptAtMillis,
            attemptCount = :attemptCount,
            lastError = NULL
        WHERE localId = :localId
    """)
    suspend fun markSynced(
        localId: String,
        serverId: String?,
        contactIndex: Int?,
        verified: Boolean?,
        label: String?,
        syncedAtMillis: Long,
        attemptAtMillis: Long,
        attemptCount: Int
    )

    @Query("""
        UPDATE emergency_contacts
        SET
            syncState = 2,
            lastAttemptAtMillis = :attemptAtMillis,
            attemptCount = :attemptCount,
            lastError = :error
        WHERE localId = :localId
    """)
    suspend fun markFailed(
        localId: String,
        attemptAtMillis: Long,
        attemptCount: Int,
        error: String
    )

    @Query("""
        UPDATE emergency_contacts
        SET
            isDeleted = 1,
            pendingOp = 2,
            syncState = 0,
            updatedAtMillis = :updatedAtMillis
        WHERE localId = :localId
    """)
    suspend fun markPendingDelete(localId: String, updatedAtMillis: Long)

    @Query("DELETE FROM emergency_contacts WHERE localId = :localId")
    suspend fun hardDelete(localId: String)
}