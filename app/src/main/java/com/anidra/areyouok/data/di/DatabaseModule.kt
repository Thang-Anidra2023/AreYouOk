package com.anidra.areyouok.data.di

import android.content.Context
import androidx.room.Room
import com.anidra.areyouok.data.manager.AppDatabase
import com.anidra.areyouok.data.room.dao.CheckInDao
import com.anidra.areyouok.data.room.dao.EmergencyContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "areyouok.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideEmergencyContactDao(db: AppDatabase): EmergencyContactDao {
        return db.emergencyContactDao()
    }

    @Provides
    fun provideCheckInDao(db: AppDatabase): CheckInDao {
        return db.checkInDao()
    }
}