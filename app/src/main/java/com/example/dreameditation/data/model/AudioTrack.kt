package com.example.dreamaze.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_tracks")
data class AudioTrack(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: AudioCategory,
    val duration: Long,
    val filePath: String,
    val isDownloaded: Boolean = false,
    val isPremium: Boolean = false,
    val volumeLevel: Float = 1.0f,
    val isLoopable: Boolean = true,
    val fadeInDuration: Long = 5000,
    val fadeOutDuration: Long = 5000,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayed: Long? = null,
    val playCount: Int = 0
)


enum class AudioCategory {
    NATURE_SOUNDS,
    BINAURAL_BEATS,
    GUIDED_MEDITATION,
    MEDITATION_MUSIC,
    WHITE_NOISE,
    SLEEP_STORIES,
    AMBIENT_SOUNDS
}


data class AudioTrackMetadata(
    val track: AudioTrack,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPosition: Long = 0L,
    val isFavorite: Boolean = false
)