package com.anidra.areyouok.data.room.dao

import androidx.room.*
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {

    @Query("SELECT * FROM emergency_contacts ORDER BY id DESC")
    fun observeAll(): Flow<List<EmergencyContactEntity>>

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(contact: EmergencyContactEntity)

    @Update
    suspend fun update(contact: EmergencyContactEntity)

    @Delete
    suspend fun delete(contact: EmergencyContactEntity)
}