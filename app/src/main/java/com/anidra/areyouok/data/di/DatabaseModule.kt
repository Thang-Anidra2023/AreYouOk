package com.anidra.areyouok.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anidra.areyouok.data.manager.AppDatabase
import com.anidra.areyouok.data.room.dao.CheckInDao
import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Recreate emergency_contacts table with new schema
        db.execSQL("DROP TABLE IF EXISTS emergency_contacts")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS emergency_contacts (
              localId TEXT NOT NULL PRIMARY KEY,
              serverId TEXT,
              mobileNumber TEXT NOT NULL,
              email TEXT NOT NULL,
              label TEXT,
              contactIndex INTEGER,
              verified INTEGER,
              isDeleted INTEGER NOT NULL,
              pendingOp INTEGER NOT NULL,
              syncState INTEGER NOT NULL,
              createdAtMillis INTEGER NOT NULL,
              updatedAtMillis INTEGER NOT NULL,
              syncedAtMillis INTEGER,
              lastAttemptAtMillis INTEGER,
              attemptCount INTEGER NOT NULL,
              lastError TEXT
            )
            """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_emergency_contacts_serverId ON emergency_contacts(serverId)")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "areyouok.db")
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration() // keeps app working even if future migrations missing
            .build()
    }

    @Provides
    fun provideEmergencyContactDao(db: AppDatabase): EmergencyContactDao = db.emergencyContactDao()

    @Provides
    fun provideCheckInDao(db: AppDatabase): CheckInDao = db.checkInDao()
}