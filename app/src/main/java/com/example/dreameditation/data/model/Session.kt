package com.example.dreamaze.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/*** Including sleep and meditation activities with timing information and hypnagogic keyword integration */
@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: SessionType,
    val audioTrackId: String? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long = 0L,
    val isCompleted: Boolean = false,
    val hypnagogicKeywords: List<String> = emptyList(),
    val hypnagogicStartTime: Long? = null,
    val sleepOnsetTime: Long? = null,
    val volumeLevel: Float = 1.0f,
    val timerDuration: Long? = null,
    val notes: String? = null,
    val rating: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/** Which types of sessions supported by dreamFlow */
enum class SessionType {
    SLEEP_SESSION,
    MEDITATION_SESSION,
    FOCUS_SESSION,
    RELAXATION_SESSION
}

/** Session state for real time tracking */
data class SessionState(
    val session: Session,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val currentPosition: Long = 0L,
    val remainingTime: Long? = null,
    val isHypnagogicPhase: Boolean = false,
    val hypnagogicProgress: Float = 0f
)

/** Session statistics for analytics */
data class SessionStats(
    val totalSessions: Int = 0,
    val totalDuration: Long = 0L,
    val averageDuration: Long = 0L,
    val longestSession: Long = 0L,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val favoriteCategory: AudioCategory? = null,
    val mostUsedKeywords: List<String> = emptyList()
)

