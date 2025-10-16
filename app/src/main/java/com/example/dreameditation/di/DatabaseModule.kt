package com.example.dreamaze.di

import android.content.Context
import androidx.room.Room
import com.example.dreamaze.data.dao.AudioTrackDao
import com.example.dreamaze.data.dao.SessionDao
import com.example.dreamaze.data.database.DreamFlowDatabase
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
    fun provideDreamFlowDatabase(@ApplicationContext context: Context): DreamFlowDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DreamFlowDatabase::class.java,
            "dreamflow_database"
        )
            .fallbackToDestructiveMigration() // For development only
            .build()
    }

    @Provides
    fun provideAudioTrackDao(database: DreamFlowDatabase): AudioTrackDao {
        return database.audioTrackDao()
    }

    @Provides
    fun provideSessionDao(database: DreamFlowDatabase): SessionDao {
        return database.sessionDao()
    }
}