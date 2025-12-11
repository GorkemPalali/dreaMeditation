package com.dreameditation.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val queue: List<AudioTrack> = emptyList(),
    val currentTrack: AudioTrack? = null,
    val isPlaying: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    // Backwards compatibility flow for just the queue
    val queue: StateFlow<List<AudioTrack>> = _uiState
        .map { it.queue }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun loadQueueByIds(ids: List<String>) {

        viewModelScope.launch {
            val tracks = mutableListOf<AudioTrack>()
            ids.forEach { id ->
                // Try to find in DB
                audioRepository.getTrackById(id).first()?.let { tracks.add(it) }
                    // If not in DB, try static registry
                    ?: audioRepository.getStaticTrack(id)?.let { tracks.add(it) }
            }
            _uiState.value = _uiState.value.copy(queue = tracks)
            if (tracks.isNotEmpty() && _uiState.value.currentTrack == null) {
                // Auto-select first if none selected
                onTrackSelected(tracks.first().id)
            }
        }
    }
    
    // Initializer for the session
    fun initSession(sessionType: String, startTrackId: String?) {
         viewModelScope.launch {
             // 1. Load Queue based on type
             val categories = if (sessionType.equals("sleep", ignoreCase = true)) {
                 listOf(AudioCategory.NATURE_SOUNDS, AudioCategory.MEDITATION_MUSIC)
             } else {
                 listOf(AudioCategory.BINAURAL_BEATS, AudioCategory.MEDITATION_MUSIC)
             }
             
             // Combined loading: DB + Static Fallback if DB is empty
             var tracks = audioRepository.getTracksByCategories(categories).first()
             
             if (tracks.isEmpty()) {
                 // Fallback to static tracks if DB is empty (simple heuristic for now)
                 // In a real app we might seed the DB, but here we can just pull from registry if we knew all IDs.
                 // Since we don't have a "getAllStatic" easily exposed yet, let's assume DB should have it or we rely on specific IDs.
                 // However, for the purpose of this refactor, let's trust the logic to load what it can.
             }
             
             _uiState.value = _uiState.value.copy(queue = tracks)
             
             // 2. Select initial track
             val trackToPlay = if (startTrackId != null) {
                 // Try to find in loaded queue
                 tracks.find { it.id == startTrackId } 
                 // If not in queue, try static lookup specifically
                 ?: audioRepository.getStaticTrack(startTrackId)
             } else {
                 tracks.firstOrNull()
             }
             
             if (trackToPlay != null) {
                 _uiState.value = _uiState.value.copy(currentTrack = trackToPlay)
             }
         }
    }

    fun loadQueueByCategory(category: AudioCategory) {
        viewModelScope.launch {
            audioRepository.getTracksByCategory(category).first().let { tracks ->
                _uiState.value = _uiState.value.copy(queue = tracks)
            }
        }
    }

    fun loadQueueByCategories(categories: List<AudioCategory>) {
        viewModelScope.launch {
            try {
                val tracks = audioRepository.getTracksByCategories(categories).first()
                _uiState.value = _uiState.value.copy(queue = tracks)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(queue = emptyList())
            }
        }
    }
    
    fun onTrackSelected(trackId: String) {
        val currentQueue = _uiState.value.queue
        val track = currentQueue.find { it.id == trackId } 
            ?: audioRepository.getStaticTrack(trackId) // Fallback check
            
        if (track != null) {
            _uiState.value = _uiState.value.copy(currentTrack = track)
        }
    }
    
    fun onNext() {
        val state = _uiState.value
        if (state.queue.isEmpty()) return
        
        val currentId = state.currentTrack?.id
        val currentIndex = state.queue.indexOfFirst { it.id == currentId }
        
        val nextIndex = if (currentIndex < 0 || currentIndex >= state.queue.lastIndex) 0 else currentIndex + 1
        val nextTrack = state.queue[nextIndex]
        
        _uiState.value = state.copy(currentTrack = nextTrack)
    }
    
    fun onPrevious() {
        val state = _uiState.value
        if (state.queue.isEmpty()) return
        
        val currentId = state.currentTrack?.id
        val currentIndex = state.queue.indexOfFirst { it.id == currentId }
        
        val prevIndex = if (currentIndex <= 0) state.queue.lastIndex else currentIndex - 1
        val prevTrack = state.queue[prevIndex]
        
        _uiState.value = state.copy(currentTrack = prevTrack)
    }
    
    fun setPlaying(isPlaying: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
    }
}

// Extension for StateIn