package com.example.dreamaze.data.dao

import androidx.room.*
import com.example.dreamaze.data.model.Session
import com.example.dreamaze.data.model.SessionType
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Data Access Object for Session entities
 *
 * This DAO provides methods for accessing and manipulating session data
 * including sleep and meditation sessions with analytics support.
 */
@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE type = :type ORDER BY startTime DESC")
    fun getSessionsByType(type: SessionType): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getSessionById(id: String): Flow<Session?>

    @Query("SELECT * FROM sessions WHERE isCompleted = 1 ORDER BY startTime DESC LIMIT :limit")
    fun getCompletedSessions(limit: Int = 50): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE isCompleted = 0 ORDER BY startTime DESC")
    fun getActiveSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    fun getSessionsInDateRange(startDate: Long, endDate: Long): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE audioTrackId = :trackId ORDER BY startTime DESC")
    fun getSessionsByTrack(trackId: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE hypnagogicKeywords IS NOT NULL AND hypnagogicKeywords != '' ORDER BY startTime DESC")
    fun getHypnagogicSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE type = :type AND isCompleted = 1 ORDER BY startTime DESC LIMIT 1")
    fun getLastCompletedSession(type: SessionType): Flow<Session?>

    @Query("SELECT COUNT(*) FROM sessions WHERE type = :type AND isCompleted = 1")
    fun getCompletedSessionCount(type: SessionType): Flow<Int>

    @Query("SELECT AVG(duration) FROM sessions WHERE type = :type AND isCompleted = 1")
    fun getAverageSessionDuration(type: SessionType): Flow<Long?>

    @Query("SELECT MAX(duration) FROM sessions WHERE type = :type AND isCompleted = 1")
    fun getLongestSessionDuration(type: SessionType): Flow<Long?>

    @Query("SELECT COUNT(*) FROM sessions WHERE type = :type AND isCompleted = 1 AND startTime >= :startDate")
    fun getSessionCountSince(type: SessionType, startDate: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<Session>): List<Long>

    @Update
    suspend fun updateSession(session: Session): Int

    @Delete
    suspend fun deleteSession(session: Session): Int

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String): Int

    @Query("UPDATE sessions SET endTime = :endTime, duration = :duration, isCompleted = :isCompleted WHERE id = :id")
    suspend fun completeSession(id: String, endTime: Long, duration: Long, isCompleted: Boolean = true): Int

    @Query("UPDATE sessions SET sleepOnsetTime = :sleepOnsetTime WHERE id = :id")
    suspend fun updateSleepOnsetTime(id: String, sleepOnsetTime: Long): Int

    @Query("UPDATE sessions SET hypnagogicStartTime = :hypnagogicStartTime WHERE id = :id")
    suspend fun updateHypnagogicStartTime(id: String, hypnagogicStartTime: Long): Int

    @Query("UPDATE sessions SET rating = :rating WHERE id = :id")
    suspend fun updateSessionRating(id: String, rating: Int): Int

    @Query("UPDATE sessions SET notes = :notes WHERE id = :id")
    suspend fun updateSessionNotes(id: String, notes: String): Int

    @Query("DELETE FROM sessions WHERE startTime < :cutoffDate")
    suspend fun deleteOldSessions(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM sessions")
    fun getTotalSessionCount(): Flow<Int>

    @Query("SELECT SUM(duration) FROM sessions WHERE isCompleted = 1")
    fun getTotalSessionDuration(): Flow<Long?>
}