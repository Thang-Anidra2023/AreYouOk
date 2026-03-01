package com.anidra.areyouok.data.manager

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anidra.areyouok.data.room.dao.CheckInDao
import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import com.anidra.areyouok.data.room.entity.CheckInEntity
import com.anidra.areyouok.data.room.entity.EmergencyContactEntity

@Database(
    entities = [
        EmergencyContactEntity::class,
        CheckInEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun checkInDao(): CheckInDao
}