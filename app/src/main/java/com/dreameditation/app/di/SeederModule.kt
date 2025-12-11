package com.dreameditation.app.di

import com.dreameditation.app.data.seed.SampleDataSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SeederModule {

    @Provides
    @Singleton
    fun provideSampleDataSeeder(
        audioRepository: com.dreameditation.app.data.repository.AudioRepository
    ): SampleDataSeeder {
        return SampleDataSeeder(audioRepository)
    }
}

