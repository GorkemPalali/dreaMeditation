package com.dreameditation.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.dreameditation.app.R
import com.dreameditation.app.MainActivity
import com.dreameditation.app.data.model.AudioTrack
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlaybackService : Service() {

    @Inject
    lateinit var audioManager: AudioManager

    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var autoStopTimer: CountDownTimer? = null

    private val binder = AudioPlaybackBinder()

    inner class AudioPlaybackBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializePlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            stopSelf()
                        }
                        Player.STATE_READY -> {
                            updateNotification()
                        }
                    }
                }
            })
        }

        mediaSession = MediaSession.Builder(this, exoPlayer!!).build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "dreameditation Audio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Audio playback for sessions"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("dreameditation")
            .setContentText("Playing audio")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    fun playTrack(track: AudioTrack) {
        android.util.Log.d("AudioService", "playTrack called with: ${track.title}, filePath: ${track.filePath}")
        exoPlayer?.let { player ->
            val mediaItem = MediaItem.fromUri(track.filePath)
            android.util.Log.d("AudioService", "Created MediaItem: $mediaItem")
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            android.util.Log.d("AudioService", "Player state after play: ${player.playbackState}")

            requestAudioFocus()

            startForeground(NOTIFICATION_ID, createNotification())
        } ?: run {
            android.util.Log.e("AudioService", "ExoPlayer is null!")
        }
    }


    fun startTimedLoop(track: AudioTrack, durationMs: Long) {
        autoStopTimer?.cancel()

        exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE

        playOrReplace(track)

        autoStopTimer = object : CountDownTimer(durationMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                android.util.Log.i("AudioService", "Timer finished, stopping playback")
                stopPlayback()
            }
        }.start()
    }

    fun pauseTimedLoop() {
        autoStopTimer?.cancel()
        autoStopTimer = null
    }

    // Resume looping without resetting media item
    fun resumeTimedLoop(remainingMs: Long) {
        autoStopTimer?.cancel()
        autoStopTimer = null
        exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE
        resumePlayback()
        autoStopTimer = object : CountDownTimer(remainingMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                android.util.Log.i("AudioService", "Resume timer finished, stopping playback")
                stopPlayback()
            }
        }.start()
    }

    fun playOrReplace(track: AudioTrack) {
        val currentUri = exoPlayer?.currentMediaItem?.localConfiguration?.uri?.toString()
        android.util.Log.d("AudioService", "playOrReplace - currentUri=$currentUri, new=${track.filePath}")
        if (currentUri != null && currentUri == track.filePath) {
            // Same media already loaded → resume
            resumePlayback()
            return
        }
        // Different or nothing loaded → set new media
        playTrack(track)
    }

    fun getCurrentMediaUri(): String? {
        return exoPlayer?.currentMediaItem?.localConfiguration?.uri?.toString()
    }

    fun pausePlayback() {
        android.util.Log.d("AudioService", "pausePlayback called")
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                android.util.Log.d("AudioService", "Pausing player")
                player.pause()
            } else {
                android.util.Log.d("AudioService", "Player is not playing, nothing to pause")
            }
        }
    }

    fun resumePlayback() {
        android.util.Log.d("AudioService", "resumePlayback called")
        exoPlayer?.let { player ->
            if (!player.isPlaying) {
                android.util.Log.d("AudioService", "Resuming player")
                player.play()
            } else {
                android.util.Log.d("AudioService", "Player is already playing")
            }
        }
    }

    fun stopPlayback() {
        autoStopTimer?.cancel()
        autoStopTimer = null
        exoPlayer?.stop()
        stopSelf()
    }

    fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }

    fun getCurrentVolume(): Float {
        return exoPlayer?.volume ?: 1.0f
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> resumePlayback()
                        AudioManager.AUDIOFOCUS_LOSS -> pausePlayback()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pausePlayback()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                            setVolume(0.3f)
                        }
                    }
                }
                .build()

            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { request ->
                audioManager.abandonAudioFocusRequest(request)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        autoStopTimer?.cancel()
        autoStopTimer = null
        abandonAudioFocus()
        exoPlayer?.release()
        mediaSession?.run {
            player.release()
            release()
        }
    }

    companion object {
        private const val CHANNEL_ID = "dreammaze_audio_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
