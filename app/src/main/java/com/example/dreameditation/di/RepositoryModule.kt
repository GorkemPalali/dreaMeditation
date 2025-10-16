package com.example.dreamaze.di

import com.example.dreamaze.data.repository.AudioRepository
import com.example.dreamaze.data.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAudioRepository(audioTrackDao: com.example.dreamaze.data.dao.AudioTrackDao): AudioRepository {
        return AudioRepository(audioTrackDao)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: com.example.dreamaze.data.dao.SessionDao): SessionRepository {
        return SessionRepository(sessionDao)
    }
}