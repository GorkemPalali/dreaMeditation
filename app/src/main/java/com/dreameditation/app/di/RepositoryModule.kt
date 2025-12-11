package com.dreameditation.app.di

import com.dreameditation.app.data.repository.AudioRepository
import com.dreameditation.app.data.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAudioRepository(
        audioTrackDao: com.dreameditation.app.data.dao.AudioTrackDao,
        @ApplicationContext context: Context
    ): AudioRepository {
        return AudioRepository(audioTrackDao, context)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: com.dreameditation.app.data.dao.SessionDao): SessionRepository {
        return SessionRepository(sessionDao)
    }
}