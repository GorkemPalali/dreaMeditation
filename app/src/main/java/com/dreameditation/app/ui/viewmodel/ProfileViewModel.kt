package com.dreameditation.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreameditation.app.data.model.FontSize
import com.dreameditation.app.data.model.Session
import com.dreameditation.app.data.model.SessionType
import com.dreameditation.app.data.preferences.AppPreferences
import com.dreameditation.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ProfileUiState(
    val totalSessions: Int = 0,
    val totalSleepHours: Long = 0L,
    val meditationStreakDays: Int = 0,
    val fontSize: FontSize = FontSize.MEDIUM,
    val language: String = "en"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sessionRepository.getCompletedSessions(),
                sessionRepository.getSessionsByType(SessionType.SLEEP_SESSION),
                sessionRepository.getSessionsByType(SessionType.MEDITATION_SESSION),
                AppPreferences.appLanguageFlow(context),
                AppPreferences.fontSizeFlow(context)
            ) { allCompleted, sleepSessions, meditationSessions, languageTag, fontSizeName ->
                val totalCompleted = allCompleted.size
                val totalSleepMs = sleepSessions.filter { it.isCompleted }
                    .sumOf { it.duration }
                val streak = calculateDailyStreak(meditationSessions.filter { it.isCompleted })
                
                val fontSize = try {
                    FontSize.valueOf(fontSizeName)
                } catch (e: IllegalArgumentException) {
                    FontSize.MEDIUM
                }
                
                // Extract language code from language tag (e.g., "tr-TR" -> "tr", "en-US" -> "en")
                val language = languageTag.split("-").firstOrNull() ?: "en"

                ProfileUiState(
                    totalSessions = totalCompleted,
                    totalSleepHours = TimeUnit.MILLISECONDS.toHours(totalSleepMs),
                    meditationStreakDays = streak,
                    fontSize = fontSize,
                    language = language
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    suspend fun updateLanguage(languageTag: String) {
        AppPreferences.setAppLanguage(context, languageTag)
    }

    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            AppPreferences.setFontSize(context, fontSize.name)
        }
    }

    private fun calculateDailyStreak(completedMeditations: List<Session>): Int {
        if (completedMeditations.isEmpty()) return 0
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        val todayDay = System.currentTimeMillis() / dayMillis
        val daysSet = completedMeditations.map { it.startTime / dayMillis }.toSet()
        var streak = 0
        var cursor = todayDay
        while (daysSet.contains(cursor)) {
            streak++
            cursor -= 1
        }
        return streak
    }
}