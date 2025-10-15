package com.example.dreamaze.data.repository

import com.example.dreamaze.data.dao.AudioTrackDao
import com.example.dreamaze.data.model.AudioCategory
import com.example.dreamaze.data.model.AudioTrack
import com.example.dreamaze.data.model.AudioTrackMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepository @Inject constructor(
    private val audioTrackDao: AudioTrackDao
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

    suspend fun insertTracks(tracks: List<AudioTrack>): List<Long> = audioTrackDao.insertTracks(tracks)

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

    suspend fun clearAllTracks(): Int = audioTrackDao.clearAllTracks()



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
}