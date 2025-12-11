package com.dreameditation.app.data.repository

import com.dreameditation.app.data.dao.SessionDao
import com.dreameditation.app.data.model.Session
import com.dreameditation.app.data.model.SessionStats
import com.dreameditation.app.data.model.SessionType
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
}