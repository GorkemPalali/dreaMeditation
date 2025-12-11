package com.dreameditation.app.di

import android.content.Context
import androidx.room.Room
import com.dreameditation.app.data.dao.AudioTrackDao
import com.dreameditation.app.data.dao.SessionDao
import com.dreameditation.app.data.database.DreaMeditationDatabase
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
    fun provideDreaMeditationDatabase(@ApplicationContext context: Context): DreaMeditationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DreaMeditationDatabase::class.java,
            "dreaMeditation_database"
        )
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    fun provideAudioTrackDao(database: DreaMeditationDatabase): AudioTrackDao {
        return database.audioTrackDao()
    }

    @Provides
    fun provideSessionDao(database: DreaMeditationDatabase): SessionDao {
        return database.sessionDao()
    }
}