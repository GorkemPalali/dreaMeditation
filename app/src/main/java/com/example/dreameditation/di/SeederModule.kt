package com.example.dreamaze.di

import com.example.dreamaze.data.seed.SampleDataSeeder
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
        audioRepository: com.example.dreamaze.data.repository.AudioRepository
    ): SampleDataSeeder {
        return SampleDataSeeder(audioRepository)
    }
}

