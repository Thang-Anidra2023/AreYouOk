package com.anidra.areyouok.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {

    @Query("""
        SELECT * FROM emergency_contacts
        ORDER BY updatedAtMillis DESC
    """)
    fun observeActive(): Flow<List<EmergencyContactEntity>>

    @Query("""
        SELECT * FROM emergency_contacts
        ORDER BY updatedAtMillis DESC
    """)
    suspend fun getAllOnce(): List<EmergencyContactEntity>

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun countAll(): Int

    @Query("SELECT * FROM emergency_contacts WHERE localId = :localId LIMIT 1")
    suspend fun getByLocalId(localId: String): EmergencyContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EmergencyContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<EmergencyContactEntity>)

    @Query("DELETE FROM emergency_contacts WHERE localId = :localId")
    suspend fun deleteByLocalId(localId: String)

    @Query("DELETE FROM emergency_contacts")
    suspend fun clearAll()

    @Query("""
        UPDATE emergency_contacts
        SET syncState = :syncState,
            updatedAtMillis = :updatedAtMillis,
            lastError = NULL
        WHERE localId = :localId
    """)
    suspend fun setSyncState(
        localId: String,
        syncState: Int,
        updatedAtMillis: Long
    )

    @Query("""
        UPDATE emergency_contacts
        SET contactIndex = :contactIndex,
            verified = :verified,
            syncState = 1,
            syncedAtMillis = :syncedAtMillis,
            lastAttemptAtMillis = :attemptAtMillis,
            attemptCount = :attemptCount,
            lastError = NULL
        WHERE localId = :localId
    """)
    suspend fun markSynced(
        localId: String,
        contactIndex: Int?,
        verified: Boolean?,
        syncedAtMillis: Long,
        attemptAtMillis: Long,
        attemptCount: Int
    )

    @Query("""
        UPDATE emergency_contacts
        SET syncState = 2,
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

    @Transaction
    suspend fun replaceAll(entities: List<EmergencyContactEntity>) {
        clearAll()
        if (entities.isNotEmpty()) {
            upsertAll(entities)
        }
    }
}