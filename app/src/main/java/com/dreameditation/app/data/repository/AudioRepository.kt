package com.dreameditation.app.data.repository

import com.dreameditation.app.data.dao.AudioTrackDao
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.model.AudioTrackMetadata
import com.dreameditation.app.ui.util.getCategoryDisplayNameFull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepository @Inject constructor(
    private val audioTrackDao: AudioTrackDao,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) {

    fun getAllTracks(): Flow<List<AudioTrack>> = audioTrackDao.getAllTracks()

    fun getTracksByCategory(category: AudioCategory): Flow<List<AudioTrack>> =
        audioTrackDao.getTracksByCategory(category)

    fun getTracksByCategories(categories: List<AudioCategory>): Flow<List<AudioTrack>> =
        audioTrackDao.getTracksByCategories(categories)

    fun getDownloadedTracks(): Flow<List<AudioTrack>> = audioTrackDao.getDownloadedTracks()

    fun getFreeTracks(): Flow<List<AudioTrack>> = audioTrackDao.getFreeTracks()

    fun getTrackById(id: String): Flow<AudioTrack?> = audioTrackDao.getTrackById(id)

    fun searchTracks(query: String): Flow<List<AudioTrack>> = audioTrackDao.searchTracks(query)

    fun getMostPlayedTracks(limit: Int = 10): Flow<List<AudioTrack>> =
        audioTrackDao.getMostPlayedTracks(limit)

    fun getRecentlyPlayedTracks(limit: Int = 10): Flow<List<AudioTrack>> =
        audioTrackDao.getRecentlyPlayedTracks(limit)

    suspend fun insertTrack(track: AudioTrack): Long = audioTrackDao.insertTrack(track)

    suspend fun insertTracks(tracks: List<AudioTrack>): List<Long> {
        android.util.Log.d("AudioRepository", "Inserting ${tracks.size} tracks")
        val result = audioTrackDao.insertTracks(tracks)
        android.util.Log.d("AudioRepository", "Inserted tracks with IDs: $result")
        return result
    }

    suspend fun updateTrack(track: AudioTrack): Int = audioTrackDao.updateTrack(track)

    suspend fun deleteTrack(track: AudioTrack): Int = audioTrackDao.deleteTrack(track)

    suspend fun deleteTrackById(id: String): Int = audioTrackDao.deleteTrackById(id)

    suspend fun incrementPlayCount(id: String, timestamp: Long = System.currentTimeMillis()): Int =
        audioTrackDao.incrementPlayCount(id, timestamp)

    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean): Int =
        audioTrackDao.updateDownloadStatus(id, isDownloaded)

    fun getTrackCount(): Flow<Int> = audioTrackDao.getTrackCount()

    fun getTrackCountByCategory(category: AudioCategory): Flow<Int> =
        audioTrackDao.getTrackCountByCategory(category)

    fun getDownloadedTrackCount(): Flow<Int> = audioTrackDao.getDownloadedTrackCount()

    suspend fun clearAllTracks(): Int {
        android.util.Log.d("AudioRepository", "Clearing all tracks")
        val result = audioTrackDao.clearAllTracks()
        android.util.Log.d("AudioRepository", "Cleared $result tracks")
        return result
    }

    fun getTracksWithMetadata(): Flow<List<AudioTrackMetadata>> =
        getAllTracks().map { tracks ->
            tracks.map { track ->
                AudioTrackMetadata(track = track)
            }
        }

    fun getTracksByCategoryWithMetadata(category: AudioCategory): Flow<List<AudioTrackMetadata>> =
        getTracksByCategory(category).map { tracks ->
            tracks.map { track ->
                AudioTrackMetadata(track = track)
            }
        }

    // Static Track Registry
    private data class StaticTrackInfo(
        val resId: Int,
        val title: String,
        val category: AudioCategory,
        val minutes: Int
    )

    private val TRACK_REGISTRY = mapOf(
        "thunderstorm" to StaticTrackInfo(com.dreameditation.app.R.raw.thunderstorm, "Thunderstorm", AudioCategory.NATURE_SOUNDS, 30),
        "summer_night_crickets" to StaticTrackInfo(com.dreameditation.app.R.raw.summer_night_crickets, "Summer Night Crickets", AudioCategory.NATURE_SOUNDS, 90),
        "ocean_sound" to StaticTrackInfo(com.dreameditation.app.R.raw.ocean_sound, "Ocean Waves", AudioCategory.NATURE_SOUNDS, 60),
        "nature_rain_forest" to StaticTrackInfo(com.dreameditation.app.R.raw.nature_rain_forest, "Rain in Forest", AudioCategory.NATURE_SOUNDS, 45),
        "pure_delta_waves" to StaticTrackInfo(com.dreameditation.app.R.raw.pure_delta_waves, "Delta Waves (0.5-4Hz)", AudioCategory.BINAURAL_BEATS, 60),
        "theta_waves" to StaticTrackInfo(com.dreameditation.app.R.raw.theta_waves, "Theta Waves (4-8 Hz)", AudioCategory.BINAURAL_BEATS, 60),
        "pure_alpha_waves" to StaticTrackInfo(com.dreameditation.app.R.raw.pure_alpha_waves, "Alpha Waves (8-13Hz)", AudioCategory.BINAURAL_BEATS, 20),
        "tibetian_singing_bowls" to StaticTrackInfo(com.dreameditation.app.R.raw.tibetian_singing_bowls, "Tibetian Singing Bowls", AudioCategory.MEDITATION_MUSIC, 30),
        "energy_boost_tibetian_singing_bowls" to StaticTrackInfo(com.dreameditation.app.R.raw.energy_boost_tibetian_singing_bowls, "Energy Boost - Tibetian Singing Bowls", AudioCategory.MEDITATION_MUSIC, 45),
        "parasympathetic_nervous_system_activation_tsb" to StaticTrackInfo(com.dreameditation.app.R.raw.parasympathetic_nervous_system_activation_tsb, "Body Scan Meditation", AudioCategory.MEDITATION_MUSIC, 60),
    )

    fun getStaticTrack(trackId: String): AudioTrack? {
        val info = TRACK_REGISTRY[trackId] ?: return null
        return AudioTrack(
            id = trackId,
            title = info.title,
            description = getCategoryDisplayNameFull(info.category),
            category = info.category,
            filePath = "android.resource://${context.packageName}/${info.resId}"
        )
    }

    fun resolveStaticTrackByTitle(title: String): AudioTrack? {
        val entry = TRACK_REGISTRY.entries.find { it.value.title.equals(title, ignoreCase = true) }
        return entry?.let { (id, info) ->
            AudioTrack(
                id = id,
                title = info.title,
                description = getCategoryDisplayNameFull(info.category),
                category = info.category,
                filePath = "android.resource://${context.packageName}/${info.resId}"
            )
        }
    }
}