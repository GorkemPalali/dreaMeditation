package com.dreameditation.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.model.Session
import com.dreameditation.app.data.model.SessionType
import com.dreameditation.app.data.repository.AudioRepository
import com.dreameditation.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeditationProgram(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val progress: Float = 0f
)

data class MeditationUiState(
    val meditationCategories: List<AudioCategory> = emptyList(),
    val selectedCategory: AudioCategory? = null,
    val meditationTracks: List<AudioTrack> = emptyList(),
    val programs: List<MeditationProgram> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MeditationViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object {
        private val MEDITATION_CATEGORIES = listOf(
            AudioCategory.GUIDED_MEDITATION,
            AudioCategory.MEDITATION_MUSIC,
            AudioCategory.BINAURAL_BEATS,
            AudioCategory.AMBIENT_SOUNDS
        )
    }

    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState: StateFlow<MeditationUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<AudioCategory?>(null)
    val selectedCategory: StateFlow<AudioCategory?> = _selectedCategory.asStateFlow()

    init {
        loadMeditationData()
    }

    private fun loadMeditationData() {
        viewModelScope.launch {
            _selectedCategory
                .flatMapLatest { selectedCat ->
                    val tracksFlow = if (selectedCat != null) {
                        audioRepository.getTracksByCategory(selectedCat)
                    } else {
                        audioRepository.getTracksByCategories(MEDITATION_CATEGORIES)
                    }

                    combine(
                        tracksFlow.catch { e ->
                            android.util.Log.e("MeditationViewModel", "Error loading tracks: ${e.message}", e)
                            emit(emptyList())
                        },
                        sessionRepository.getSessionsByType(SessionType.MEDITATION_SESSION).catch { e ->
                            android.util.Log.e("MeditationViewModel", "Error loading sessions: ${e.message}", e)
                            emit(emptyList())
                        }
                    ) { tracks, sessions ->
                        val programs = calculateProgramProgress(sessions)
                        
                        MeditationUiState(
                            meditationCategories = MEDITATION_CATEGORIES,
                            selectedCategory = selectedCat,
                            meditationTracks = tracks,
                            programs = programs,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .catch { e ->
                    android.util.Log.e("MeditationViewModel", "Error combining data: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred"
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun selectCategory(category: AudioCategory?) {
        _selectedCategory.value = category
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadMeditationData()
    }

    private fun calculateProgramProgress(sessions: List<Session>): List<MeditationProgram> {
        // TODO: Implement actual program progress calculation based on sessions
        // For now, return placeholder programs
        return listOf(
            MeditationProgram(
                id = "program_1",
                title = "Beginner's Guide",
                description = "Start your meditation journey",
                duration = "7 days",
                progress = if (sessions.isNotEmpty()) 0.3f else 0f
            ),
            MeditationProgram(
                id = "program_2",
                title = "Advanced Practice",
                description = "Deepen your meditation practice",
                duration = "14 days",
                progress = 0f
            )
        )
    }
}

