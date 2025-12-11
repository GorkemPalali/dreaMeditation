package com.dreameditation.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.dreameditation.app.ui.theme.AppIcon
import com.dreameditation.app.ui.theme.IconName
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreameditation.app.ui.component.SectionHeader
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.ui.platform.LocalContext
import com.dreameditation.app.service.AudioPlaybackService
import com.dreameditation.app.service.AudioPlaybackService.AudioPlaybackBinder
import com.dreameditation.app.service.HypnagogicTimerService
import com.dreameditation.app.service.HypnagogicTimerService.HypnagogicTimerBinder
import android.content.Intent
import com.dreameditation.app.R
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.model.AudioCategory
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import com.dreameditation.app.ui.viewmodel.SessionViewModel
import android.util.Log
import androidx.compose.ui.res.stringResource
import com.dreameditation.app.data.preferences.AppPreferences



@Composable
fun SessionScreen(
    sessionType: String,
    trackId: String?,
    sessionDurationMs: Long,
    onNavigateBack: () -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var currentLanguage by remember { mutableStateOf("en") }
    LaunchedEffect(Unit) {
        AppPreferences.appLanguageFlow(context).collect { languageTag ->
            currentLanguage = languageTag.split("-")[0]
        }
    }
    
    // Services
    var service by remember { mutableStateOf<AudioPlaybackService?>(null) }
    var isServiceBound by remember { mutableStateOf(false) }
    var hypnagogicService by remember { mutableStateOf<HypnagogicTimerService?>(null) }
    var isHypnagogicServiceBound by remember { mutableStateOf(false) }

    // Init Session
    LaunchedEffect(sessionType, trackId) {
        viewModel.initSession(sessionType, trackId)
    }

    // Local state for UI only (animations/progress)
    var currentPosition by remember { mutableStateOf(0L) }
    var sessionDuration by remember { mutableStateOf(sessionDurationMs) }
    var sessionStartAt by remember { mutableStateOf<Long?>(null) }
    var elapsedMs by remember { mutableStateOf(0L) }
    var trackElapsedMs by remember { mutableStateOf(0L) }
    var lastPlayerPosition by remember { mutableStateOf(0L) }
    var volume by remember { mutableStateOf(0.7f) }
    
    var showHypnagogicSettings by remember { mutableStateOf(false) }
    var hypnagogicKeywords by remember { mutableStateOf("") }
    var isHypnagogicTimerActive by remember { mutableStateOf(false) }
    var testTtsTrigger by remember { mutableStateOf(0) }

    // Service Connections
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val audioBinder = binder as? AudioPlaybackBinder
                service = audioBinder?.getService()
                isServiceBound = true
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
                service = null
            }
        }
    }

    val hypnagogicServiceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val hypnagogicBinder = binder as? HypnagogicTimerBinder
                hypnagogicService = hypnagogicBinder?.getService()
                isHypnagogicServiceBound = true
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                isHypnagogicServiceBound = false
                hypnagogicService = null
            }
        }
    }

    // Bind Services
    LaunchedEffect(Unit) {
        val audioIntent = Intent(context, AudioPlaybackService::class.java)
        context.startService(audioIntent)
        context.bindService(audioIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        val hypnagogicIntent = Intent(context, HypnagogicTimerService::class.java)
        context.startService(hypnagogicIntent)
        context.bindService(hypnagogicIntent, hypnagogicServiceConnection, Context.BIND_AUTO_CREATE)
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isServiceBound) context.unbindService(serviceConnection)
            if (isHypnagogicServiceBound) context.unbindService(hypnagogicServiceConnection)
        }
    }

    // React to Current Track Changes from VM
    // React to Current Track Changes from VM
    LaunchedEffect(uiState.currentTrack, service) {
        val track = uiState.currentTrack
        val srv = service
        if (track != null && srv != null && track.filePath.isNotBlank()) {
            // Check if we need to play (basically if track is different or we are stopped)
            // Ideally service exposes current track, assuming we play on change:
            srv.playTrack(track)
            srv.setVolume(volume)
            viewModel.setPlaying(true)
            // Reset local progress
            currentPosition = 0L
            trackElapsedMs = 0L
            lastPlayerPosition = 0L
        }
    }

    // Sync Playback State
    // Handle specific Play/Pause/Next commands by observing state changes or events?
    // Simplified: We call VM methods, they update state. 
    // BUT we also need to command Service. 
    // To decouple perfectly, UI observes State -> Calls Service.
    
    // Let's keep the Service commands in the UI interaction callbacks for now, 
    // but update VM state to reflect it.
    
    // Poll current position
    LaunchedEffect(uiState.isPlaying, service) {
        while (uiState.isPlaying && service != null) {
            val pos = service?.getCurrentPosition() ?: 0L
            currentPosition = pos
            val delta = if (pos >= lastPlayerPosition) (pos - lastPlayerPosition) else pos
            trackElapsedMs = (trackElapsedMs + delta).coerceAtLeast(0L)
            lastPlayerPosition = pos
            sessionStartAt?.let { start ->
                val now = System.currentTimeMillis()
                elapsedMs = (now - start).coerceAtMost(sessionDuration)
                if (elapsedMs >= sessionDuration) {
                    viewModel.setPlaying(false)
                }
            }
            hypnagogicService?.setAudioPlayingState(true)
            delay(500)
        }
        if (!uiState.isPlaying) {
            hypnagogicService?.setAudioPlayingState(false)
        }
    }

    // Update HypnagogicTimerService language when currentLanguage changes
    LaunchedEffect(currentLanguage) {
        hypnagogicService?.let { service ->
            val languageTag = if (currentLanguage == "tr") "tr-TR" else "en-US"
            service.updateLanguagePreference(languageTag)
        }
    }

    // Test TTS
    LaunchedEffect(testTtsTrigger) {
        if (testTtsTrigger > 0 && hypnagogicKeywords.isNotBlank()) {
            val keywords = hypnagogicKeywords.split(",").map { it.trim() }.filter { it.isNotBlank() }
            hypnagogicService?.let { service ->
                // Update language before testing
                val languageTag = if (currentLanguage == "tr") "tr-TR" else "en-US"
                service.updateLanguagePreference(languageTag)
                
                keywords.forEach { keyword ->
                    service.speakKeyword(keyword)
                    delay(2000)
                }
            }
        }
    }

    // Background Gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        com.dreameditation.app.ui.theme.BackgroundDark,
                        com.dreameditation.app.ui.theme.dreameditationBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    AppIcon(name = IconName.Back, contentDescription = stringResource(id = R.string.back), tint = MaterialTheme.colorScheme.onBackground)
                }

                Text(
                    text = "Now Dreaming",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Main Player Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Track Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = uiState.currentTrack?.title ?: "",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Progress Bar
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.widthIn(max = 400.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)) {
                        Box(modifier = Modifier.fillMaxWidth(fraction = if (sessionDuration > 0) (trackElapsedMs.toFloat() / sessionDuration).coerceIn(0f, 1f) else 0f).fillMaxHeight().background(com.dreameditation.app.ui.theme.PrimaryColor, androidx.compose.foundation.shape.CircleShape))
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = formatTime(trackElapsedMs), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        Text(text = formatTime(sessionDuration), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                    }
                }

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous
                    IconButton(
                        onClick = { viewModel.onPrevious() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        AppIcon(name = IconName.Previous, contentDescription = "Previous", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                    }

                    // Play/Pause
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(com.dreameditation.app.ui.theme.PrimaryColor, androidx.compose.foundation.shape.CircleShape)
                            .clickable {
                                val srv = service
                                if (srv != null) {
                                    if (uiState.isPlaying) {
                                        srv.pausePlayback()
                                        srv.pauseTimedLoop()
                                        viewModel.setPlaying(false)
                                    } else {
                                        val track = uiState.currentTrack
                                        // If resume
                                        if (track != null) {
                                            if (sessionStartAt == null) {
                                                // Initial start might be handled by track change, but if just Paused -> Resume:
                                                 if (track.filePath.isNotBlank()) {
                                                     srv.playTrack(track)
                                                 }
                                                 sessionStartAt = System.currentTimeMillis()
                                                 elapsedMs = 0L
                                            } else {
                                                val remaining = (sessionDuration - elapsedMs).coerceAtLeast(0L)
                                                srv.resumeTimedLoop(remaining)
                                            }
                                            srv.setVolume(volume)
                                            viewModel.setPlaying(true)
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            name = if (uiState.isPlaying) IconName.Pause else IconName.Play,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }

                    // Next
                    IconButton(
                        onClick = { viewModel.onNext() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        AppIcon(name = IconName.Next, contentDescription = "Next", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Hypnagogic / Bottom Section
            if (sessionType == "sleep") {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).animateContentSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showHypnagogicSettings = !showHypnagogicSettings },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✨", style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = stringResource(id = R.string.hypnagogic_keywords), style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = stringResource(id = R.string.section_dream_guidance), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            AppIcon(name = if (showHypnagogicSettings) IconName.ArrowUp else IconName.ArrowDown, contentDescription = "Expand", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }

                        if (showHypnagogicSettings) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = hypnagogicKeywords,
                                onValueChange = { hypnagogicKeywords = it },
                                placeholder = { Text(stringResource(id = R.string.hypnagogic_keywords_placeholder), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3, maxLines = 5,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = com.dreameditation.app.ui.theme.PrimaryColor,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    cursorColor = com.dreameditation.app.ui.theme.PrimaryColor,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = stringResource(id = R.string.keyword_subtext), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                IconButton(
                                    onClick = {
                                        if (isHypnagogicTimerActive) {
                                            hypnagogicService?.stopHypnagogicTimer()
                                            isHypnagogicTimerActive = false
                                        } else {
                                            if (hypnagogicKeywords.isNotBlank()) {
                                                val keywords = hypnagogicKeywords.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                                hypnagogicService?.startHypnagogicTimer(keywords = keywords, repetitions = 3, volumePercentage = 0.2f)
                                                isHypnagogicTimerActive = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(48.dp).background(com.dreameditation.app.ui.theme.dreameditationSecondary, androidx.compose.foundation.shape.CircleShape)
                                ) {
                                    AppIcon(name = if (isHypnagogicTimerActive) IconName.Pause else IconName.Play, contentDescription = "Toggle Timer", tint = Color.Black)
                                }
                                
                                Button(
                                    onClick = { if (hypnagogicKeywords.isNotBlank()) testTtsTrigger++ },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.onSurface)
                                ) {
                                    Text("Test TTS", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

