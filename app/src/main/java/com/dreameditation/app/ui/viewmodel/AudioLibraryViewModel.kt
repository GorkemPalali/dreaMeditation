package com.dreameditation.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AudioLibraryViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    companion object {
        private val MEDITATION_CATEGORIES = listOf(
            AudioCategory.BINAURAL_BEATS,
            AudioCategory.MEDITATION_MUSIC
        )
        private val SLEEP_CATEGORIES = listOf(
            AudioCategory.NATURE_SOUNDS,
            AudioCategory.MEDITATION_MUSIC
        )
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    private val _uiState = MutableStateFlow(AudioLibraryUiState())
    val uiState: StateFlow<AudioLibraryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<AudioCategory?>(null)
    val selectedCategory: StateFlow<AudioCategory?> = _selectedCategory.asStateFlow()

    private val _prefilterMode = MutableStateFlow(PrefilterMode.None)
    val prefilterMode: StateFlow<PrefilterMode> = _prefilterMode.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadTracks()
    }

    private fun loadTracks() {
        loadJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        loadJob = viewModelScope.launch {
            combine(
                _prefilterMode,
                _selectedCategory,
                _searchQuery.debounce(SEARCH_DEBOUNCE_MS).distinctUntilChanged()
            ) { mode, category, query -> Triple(mode, category, query) }
                .flatMapLatest { (mode, category, query) ->
                    try {
                        val baseFlow: Flow<List<AudioTrack>> = when {
                            mode == PrefilterMode.Meditation -> 
                                audioRepository.getTracksByCategories(MEDITATION_CATEGORIES)
                            mode == PrefilterMode.Sleep -> 
                                audioRepository.getTracksByCategories(SLEEP_CATEGORIES)
                            mode == PrefilterMode.None && category != null -> 
                                audioRepository.getTracksByCategory(category)
                            else -> audioRepository.getAllTracks()
                        }
                        
                        baseFlow.map { tracks ->
                            if (query.isBlank()) {
                                tracks
                            } else {
                                tracks.filter {
                                    it.title.contains(query, ignoreCase = true) || 
                                    it.description.contains(query, ignoreCase = true)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        flow {
                            emit(emptyList<AudioTrack>())
                        }
                    }
                }
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred",
                        tracks = emptyList()
                    )
                }
                .collect { tracks ->
                    _uiState.value = _uiState.value.copy(
                        tracks = tracks,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun searchTracks(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: AudioCategory?) {
        _selectedCategory.value = category
    }

    fun setPrefilter(mode: PrefilterMode) {
        _prefilterMode.value = mode
        if (mode != PrefilterMode.None) {
            _selectedCategory.value = null
        }
    }

    fun refreshLibrary() {
        _prefilterMode.value = PrefilterMode.None
        _selectedCategory.value = null
        _searchQuery.value = ""
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun updateDownloadStatus(trackId: String, isDownloaded: Boolean) {
        viewModelScope.launch {
            try {
                audioRepository.updateDownloadStatus(trackId, isDownloaded)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update download status: ${e.message}"
                )
            }
        }
    }
}

data class AudioLibraryUiState(
    val tracks: List<AudioTrack> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class PrefilterMode {
    None,
    Meditation,
    Sleep
}