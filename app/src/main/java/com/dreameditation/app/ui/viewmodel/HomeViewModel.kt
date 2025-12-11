package com.dreameditation.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.model.Session
import com.dreameditation.app.data.model.SessionType
import com.dreameditation.app.data.repository.AudioRepository
import com.dreameditation.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                audioRepository.getRecentlyPlayedTracks(5).catch { e ->
                    android.util.Log.e("HomeViewModel", "Error loading recent tracks: ${e.message}", e)
                    emit(emptyList())
                },
                audioRepository.getMostPlayedTracks(5).catch { e ->
                    android.util.Log.e("HomeViewModel", "Error loading popular tracks: ${e.message}", e)
                    emit(emptyList())
                },
                sessionRepository.getLastCompletedSession(SessionType.SLEEP_SESSION).catch { e ->
                    android.util.Log.e("HomeViewModel", "Error loading last sleep session: ${e.message}", e)
                    emit(null)
                },
                sessionRepository.getLastCompletedSession(SessionType.MEDITATION_SESSION).catch { e ->
                    android.util.Log.e("HomeViewModel", "Error loading last meditation session: ${e.message}", e)
                    emit(null)
                }
            ) { recentTracks, popularTracks, lastSleepSession, lastMeditationSession ->
                HomeUiState(
                    recentTracks = recentTracks,
                    popularTracks = popularTracks,
                    lastSleepSession = lastSleepSession,
                    lastMeditationSession = lastMeditationSession,
                    isLoading = false,
                    error = null
                )
            }.catch { e ->
                android.util.Log.e("HomeViewModel", "Error combining data: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadHomeData()
    }
}

data class HomeUiState(
    val recentTracks: List<AudioTrack> = emptyList(),
    val popularTracks: List<AudioTrack> = emptyList(),
    val lastSleepSession: Session? = null,
    val lastMeditationSession: Session? = null,
    val isLoading: Boolean = true,
    val isStartingSession: Boolean = false,
    val error: String? = null
)