package com.anidra.areyouok.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anidra.areyouok.data.room.entity.CheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    @Query("SELECT * FROM checkins WHERE epochDay = :epochDay LIMIT 1")
    fun observeByDay(epochDay: Long): Flow<CheckInEntity?>

    @Query("SELECT * FROM checkins WHERE epochDay = :epochDay LIMIT 1")
    suspend fun getByDay(epochDay: Long): CheckInEntity?

    /**
     * Anything not SYNCED (syncState != 1) will be retried.
     */
    @Query("SELECT * FROM checkins WHERE syncState != 1 ORDER BY epochDay DESC LIMIT :limit")
    suspend fun getNotSynced(limit: Int): List<CheckInEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CheckInEntity)

    @Query("""
        UPDATE checkins
        SET syncState = 1,
            serverId = :serverId,
            syncedAtMillis = :syncedAtMillis,
            lastAttemptAtMillis = :attemptAtMillis,
            attemptCount = :attemptCount,
            lastError = NULL
        WHERE epochDay = :epochDay
    """)
    suspend fun markSynced(
        epochDay: Long,
        serverId: String?,
        syncedAtMillis: Long,
        attemptAtMillis: Long,
        attemptCount: Int
    )

    @Query("""
        UPDATE checkins
        SET syncState = 2,
            lastAttemptAtMillis = :attemptAtMillis,
            attemptCount = :attemptCount,
            lastError = :error
        WHERE epochDay = :epochDay
    """)
    suspend fun markFailed(
        epochDay: Long,
        attemptAtMillis: Long,
        attemptCount: Int,
        error: String
    )
}