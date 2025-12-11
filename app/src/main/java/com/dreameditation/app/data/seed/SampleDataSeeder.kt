package com.dreameditation.app.data.seed

import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.repository.AudioRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataSeeder @Inject constructor(
    private val audioRepository: AudioRepository
) {

    suspend fun seedSampleData() {
        android.util.Log.d("SampleDataSeeder", "Starting to seed sample data...")
        audioRepository.clearAllTracks()
        val sampleTracks = createSampleTracks()
        android.util.Log.d("SampleDataSeeder", "Created ${sampleTracks.size} sample tracks")
        audioRepository.insertTracks(sampleTracks)
        android.util.Log.d("SampleDataSeeder", "Sample data seeded successfully")
    }

    private fun createSampleTracks(): List<AudioTrack> {
        return listOf(
            AudioTrack(
                id = "ocean_sound",
                title = "Ocean Waves",
                description = "Gentle ocean waves for deep relaxation and sleep",
                category = AudioCategory.NATURE_SOUNDS,
                filePath = "android.resource://com.dreameditation.app/raw/ocean_sound",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.8f
            ),
            AudioTrack(
                id = "nature_rain_forest",
                title = "Rain in Forest",
                description = "Heavy rain in a peaceful forest setting",
                category = AudioCategory.NATURE_SOUNDS,
                filePath = "android.resource://com.dreameditation.app/raw/nature_rain_forest",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "thunderstorm",
                title = "Thunderstorm",
                description = "Powerful thunderstorm sounds for deep sleep",
                category = AudioCategory.NATURE_SOUNDS,
                filePath = "android.resource://com.dreameditation.app/raw/thunderstorm",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.8f
            ),
            AudioTrack(
                id = "summer_night_crickets",
                title = "Summer Night Crickets",
                description = "Peaceful cricket sounds for relaxation",
                category = AudioCategory.NATURE_SOUNDS,
                filePath = "android.resource://com.dreameditation.app/raw/summer_night_crickets",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "pure_delta_waves",
                title = "Delta Waves (0.5-4Hz)",
                description = "Deep delta waves for deep sleep",
                category = AudioCategory.BINAURAL_BEATS,
                filePath = "android.resource://com.dreameditation.app/raw/pure_delta_waves",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            ),
            AudioTrack(
                id = "theta_waves",
                title = "Theta Waves (4-8Hz)",
                description = "Theta waves for meditation and relaxation",
                category = AudioCategory.BINAURAL_BEATS,
                filePath = "android.resource://com.dreameditation.app/raw/theta_waves",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            ),
            AudioTrack(
                id = "pure_alpha_waves",
                title = "Alpha Waves (8-13Hz)",
                description = "Alpha waves for focus and relaxation",
                category = AudioCategory.BINAURAL_BEATS,
                filePath = "android.resource://com.dreameditation.app/raw/pure_alpha_waves",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            ),
            AudioTrack(
                id = "tibetian_singing_bowls",
                title = "Tibetian Singing Bowls",
                description = "Meditative singing bowls for deep relaxation",
                category = AudioCategory.MEDITATION_MUSIC,
                filePath = "android.resource://com.dreameditation.app/raw/tibetian_singing_bowls",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "energy_boost_tibetian_singing_bowls",
                title = "Breathing Exercise",
                description = "Energizing singing bowls for morning meditation",
                category = AudioCategory.MEDITATION_MUSIC,
                filePath = "android.resource://com.dreameditation.app/raw/energy_boost_tibetian_singing_bowls",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.7f
            ),
            AudioTrack(
                id = "parasympathetic_nervous_system_activation_tsb",
                title = "Body Scan Meditation",
                description = "Guided body scan meditation for deep relaxation",
                category = AudioCategory.MEDITATION_MUSIC,
                filePath = "android.resource://com.dreameditation.app/raw/parasympathetic_nervous_system_activation_tsb",
                isDownloaded = true,
                isPremium = false,
                volumeLevel = 0.6f
            )
        )
    }
}