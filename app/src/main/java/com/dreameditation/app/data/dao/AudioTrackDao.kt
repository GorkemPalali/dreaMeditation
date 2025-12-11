package com.dreameditation.app.data.dao

import androidx.room.*
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.model.AudioTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioTrackDao {

    @Query("SELECT * FROM audio_tracks ORDER BY lastPlayed DESC, playCount DESC")
    fun getAllTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE category = :category ORDER BY lastPlayed DESC, playCount DESC")
    fun getTracksByCategory(category: AudioCategory): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE category IN (:categories) ORDER BY lastPlayed DESC, playCount DESC")
    fun getTracksByCategories(categories: List<AudioCategory>): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE isDownloaded = 1 ORDER BY lastPlayed DESC")
    fun getDownloadedTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE isPremium = 0 ORDER BY lastPlayed DESC, playCount DESC")
    fun getFreeTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE id = :id")
    fun getTrackById(id: String): Flow<AudioTrack?>

    @Query("SELECT * FROM audio_tracks WHERE title LIKE :query OR description LIKE :query ORDER BY playCount DESC")
    fun searchTracks(query: String): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayedTracks(limit: Int = 10): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE lastPlayed IS NOT NULL ORDER BY lastPlayed DESC LIMIT :limit")
    fun getRecentlyPlayedTracks(limit: Int = 10): Flow<List<AudioTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: AudioTrack): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<AudioTrack>): List<Long>

    @Update
    suspend fun updateTrack(track: AudioTrack): Int

    @Delete
    suspend fun deleteTrack(track: AudioTrack): Int

    @Query("DELETE FROM audio_tracks WHERE id = :id")
    suspend fun deleteTrackById(id: String): Int

    @Query("UPDATE audio_tracks SET playCount = playCount + 1, lastPlayed = :timestamp WHERE id = :id")
    suspend fun incrementPlayCount(id: String, timestamp: Long = System.currentTimeMillis()): Int

    @Query("UPDATE audio_tracks SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean): Int

    @Query("SELECT COUNT(*) FROM audio_tracks")
    fun getTrackCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM audio_tracks WHERE category = :category")
    fun getTrackCountByCategory(category: AudioCategory): Flow<Int>

    @Query("SELECT COUNT(*) FROM audio_tracks WHERE isDownloaded = 1")
    fun getDownloadedTrackCount(): Flow<Int>

    @Query("DELETE FROM audio_tracks")
    suspend fun clearAllTracks(): Int
}