package com.example.dreamaze.data.seed

import com.example.dreamaze.data.model.AudioCategory
import com.example.dreamaze.data.model.AudioTrack
import com.example.dreamaze.data.repository.AudioRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataSeeder @Inject constructor(
    private val audioRepository: AudioRepository
) {

    suspend fun seedSampleData() {
        audioRepository.clearAllTracks()
        val sampleTracks = createSampleTracks()
        audioRepository.insertTracks(sampleTracks)
    }

    private fun createSampleTracks(): List<AudioTrack> {
        return listOf(
            AudioTrack(
                id = "ocean_sound",
                title = "Ocean Waves",
                description = "Gentle ocean waves for deep relaxation and sleep",
                category = AudioCategory.NATURE_SOUNDS,
                duration = 60 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/ocean_sound",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.8f
            ),
            AudioTrack(
                id = "nature_rain_forest",
                title = "Rain in Forest",
                description = "Heavy rain in a peaceful forest setting",
                category = AudioCategory.NATURE_SOUNDS,
                duration = 45 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/nature_rain_forest",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "thunderstorm",
                title = "Thunderstorm",
                description = "Powerful thunderstorm sounds for deep sleep",
                category = AudioCategory.NATURE_SOUNDS,
                duration = 30 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/thunderstorm",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.8f
            ),
            AudioTrack(
                id = "summer_night_crickets",
                title = "Summer Night Crickets",
                description = "Peaceful cricket sounds for relaxation",
                category = AudioCategory.NATURE_SOUNDS,
                duration = 90 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/summer_night_crickets",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "pure_delta_waves",
                title = "Delta Waves (0.5-4Hz)",
                description = "Deep delta waves for deep sleep",
                category = AudioCategory.BINAURAL_BEATS,
                duration = 60 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/pure_delta_waves",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            ),
            AudioTrack(
                id = "theta_waves",
                title = "Theta Waves (4-8Hz)",
                description = "Theta waves for meditation and relaxation",
                category = AudioCategory.BINAURAL_BEATS,
                duration = 60 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/theta_waves",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            ),
            AudioTrack(
                id = "pure_alpha_waves",
                title = "Alpha Waves (8-13Hz)",
                description = "Alpha waves for focus and relaxation",
                category = AudioCategory.BINAURAL_BEATS,
                duration = 20 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/pure_alpha_waves",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            ),
            AudioTrack(
                id = "tibetian_singing_bowls",
                title = "Tibetian Singing Bowls",
                description = "Meditative singing bowls for deep relaxation",
                category = AudioCategory.MEDITATION_MUSIC,
                duration = 30 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/tibetian_singing_bowls",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "energy_boost_tibetian_singing_bowls",
                title = "Breathing Exercise",
                description = "Energizing singing bowls for morning meditation",
                category = AudioCategory.MEDITATION_MUSIC,
                duration = 45 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/energy_boost_tibetian_singing_bowls",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "parasympathetic_nervous_system_activation_tsb",
                title = "Body Scan Meditation",
                description = "Guided body scan meditation for deep relaxation",
                category = AudioCategory.MEDITATION_MUSIC,
                duration = 60 * 60 * 1000L,
                filePath = "android.resource://com.example.dreamaze/raw/parasympathetic_nervous_system_activation_tsb",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            )
        )
    }
}