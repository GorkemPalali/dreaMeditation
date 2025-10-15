package com.example.dreamaze.data.repository

import com.example.dreamaze.data.dao.SessionDao
import com.example.dreamaze.data.model.Session
import com.example.dreamaze.data.model.SessionStats
import com.example.dreamaze.data.model.SessionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) {

    fun getAllSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    fun getSessionsByType(type: SessionType): Flow<List<Session>> =
        sessionDao.getSessionsByType(type)

    fun getSessionById(id: String): Flow<Session?> = sessionDao.getSessionById(id)

    fun getCompletedSessions(limit: Int = 50): Flow<List<Session>> =
        sessionDao.getCompletedSessions(limit)

    fun getActiveSessions(): Flow<List<Session>> = sessionDao.getActiveSessions()

    fun getSessionsInDateRange(startDate: Long, endDate: Long): Flow<List<Session>> =
        sessionDao.getSessionsInDateRange(startDate, endDate)

    fun getSessionsByTrack(trackId: String): Flow<List<Session>> =
        sessionDao.getSessionsByTrack(trackId)

    fun getHypnagogicSessions(): Flow<List<Session>> = sessionDao.getHypnagogicSessions()

    fun getLastCompletedSession(type: SessionType): Flow<Session?> =
        sessionDao.getLastCompletedSession(type)

    suspend fun insertSession(session: Session): Long = sessionDao.insertSession(session)

    suspend fun updateSession(session: Session): Int = sessionDao.updateSession(session)

    suspend fun deleteSession(session: Session): Int = sessionDao.deleteSession(session)

    suspend fun completeSession(id: String, endTime: Long, duration: Long, isCompleted: Boolean = true): Int =
        sessionDao.completeSession(id, endTime, duration, isCompleted)

    suspend fun updateSleepOnsetTime(id: String, sleepOnsetTime: Long): Int =
        sessionDao.updateSleepOnsetTime(id, sleepOnsetTime)

    suspend fun updateHypnagogicStartTime(id: String, hypnagogicStartTime: Long): Int =
        sessionDao.updateHypnagogicStartTime(id, hypnagogicStartTime)

    suspend fun updateSessionRating(id: String, rating: Int): Int =
        sessionDao.updateSessionRating(id, rating)

    suspend fun updateSessionNotes(id: String, notes: String): Int =
        sessionDao.updateSessionNotes(id, notes)

    suspend fun deleteOldSessions(cutoffDate: Long): Int = sessionDao.deleteOldSessions(cutoffDate)

    //  TO-DO
    /** Get session statistics for analytics */
    suspend fun getSessionStats(type: SessionType? = null): SessionStats {
        val unused = if (type != null) {
            getSessionsByType(type)
        } else {
            getAllSessions()
        }

        // This would need to be implemented with a suspend function that collects the flow
        // !!! Şimdilik boş kalsın !!!
        return SessionStats()
    }



    suspend fun getMeditationStreak(): Int {
        // Implementation would calculate current streak based on completed meditation sessions
        // !!! 0 dödürelim, daha sonra eklemesini yapacam !!!
        return 0
    }



    suspend fun getSleepQualityMetrics(): Map<String, Any> {
        // Implementation would calculate sleep quality metrics
        // For now, returning empty map
        return emptyMap()
    }
}