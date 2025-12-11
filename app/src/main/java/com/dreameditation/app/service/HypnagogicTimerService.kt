package com.dreameditation.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.dreameditation.app.MainActivity
import com.dreameditation.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HypnagogicTimerService : Service(), TextToSpeech.OnInitListener {

    private val binder = HypnagogicTimerBinder()
    private var textToSpeech: TextToSpeech? = null
    private var countDownTimer: CountDownTimer? = null
    private var keywords: List<String> = emptyList()
    private var repetitions: Int = 3
    private var volumePercentage: Float = 0.2f
    private var isActive: Boolean = false
    private var isAudioPlaying: Boolean = false
    private var latestLanguageTag: String = "en-US"

    inner class HypnagogicTimerBinder : Binder() {
        fun getService(): HypnagogicTimerService = this@HypnagogicTimerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeTextToSpeech()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "dreameditation Hypnagogic",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hypnagogic dream guidance timer"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setTTSLanguage()
        }
    }

    private fun setTTSLanguage() {
        val currentLocale = resources.configuration.locales[0]
        val setRes = textToSpeech?.setLanguage(currentLocale)
        android.util.Log.i("HypnagogicTTS", "TTS language set to: ${currentLocale.language}, result: $setRes")

        if (setRes == TextToSpeech.LANG_MISSING_DATA || setRes == TextToSpeech.LANG_NOT_SUPPORTED) {
            val fallbackRes = textToSpeech?.setLanguage(Locale("en", "US"))
            android.util.Log.w("HypnagogicTTS", "Locale not supported, fallback en-US, res=$fallbackRes")
        }

        textToSpeech?.setSpeechRate(0.85f)
        textToSpeech?.setPitch(1.0f)
    }

    fun startHypnagogicTimer(
        keywords: List<String>,
        repetitions: Int = 3,
        volumePercentage: Float = 0.2f
    ) {
        android.util.Log.d("HypnagogicTTS", "Starting timer with keywords: $keywords")

        this.keywords = keywords
        this.repetitions = repetitions
        this.volumePercentage = volumePercentage
        this.isActive = true
        this.isAudioPlaying = true

        // (Test için) 1 dakikalık countdown başlatır
        countDownTimer = object : CountDownTimer(1 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateNotification(millisUntilFinished)
            }

            override fun onFinish() {
                android.util.Log.d("HypnagogicTTS", "Timer finished, starting hypnagogic phase")
                startHypnagogicPhase()
            }
        }

        countDownTimer?.start()
        postForegroundNotification(createNotification(1 * 60 * 1000))
    }

    private fun startHypnagogicPhase() {
        if (!isActive || keywords.isEmpty()) return

        CoroutineScope(Dispatchers.Main).launch {
            repeat(repetitions) { repetition ->
                if (!isActive || !isAudioPlaying) return@repeat

                keywords.forEach { keyword ->
                    if (!isActive || !isAudioPlaying) return@forEach
                    speakKeyword(keyword)
                    kotlinx.coroutines.delay(5000) // 5 seconds interval between keywords (test)
                }

                // (Test için) dream keyword için kelime aralarına 5 saniyelik bir bekleme
                if (repetition < repetitions - 1) {
                    kotlinx.coroutines.delay(5000)
                }
            }

            android.util.Log.d("HypnagogicTTS", "Hypnagogic phase completed")
            stopSelf()
        }
    }

    fun updateLanguagePreference(languageTag: String) {
        latestLanguageTag = languageTag
        android.util.Log.d("HypnagogicTTS", "Language preference updated to: $languageTag")
    }

    fun speakKeyword(keyword: String) {
        textToSpeech?.let { tts ->
            val lang = latestLanguageTag.ifBlank { "en-US" }
            val targetLocale = try { Locale.forLanguageTag(lang) } catch (_: Exception) { Locale("en", "US") }
            val res = tts.setLanguage(targetLocale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                android.util.Log.w("HypnagogicTTS", "speakKeyword: target locale not supported $targetLocale, fallback en-US")
                tts.setLanguage(Locale("en", "US"))
            }
            // Constant speed/pitch is maintained
            tts.setSpeechRate(0.85f)
            tts.setPitch(1.0f)

            android.util.Log.d("HypnagogicTTS", "Speaking keyword='$keyword' with locale=${tts.language?.language} rate=0.85 pitch=1.0")

            val result = tts.speak(keyword, TextToSpeech.QUEUE_FLUSH, null, "hypnagogic_$keyword")
            android.util.Log.d("HypnagogicTTS", "TTS speak result: $result")
        } ?: run {
            android.util.Log.e("HypnagogicTTS", "TTS is null, cannot speak")
        }
    }

    private fun createNotification(remainingTime: Long): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val minutes = (remainingTime / 60000).toInt()
        val seconds = ((remainingTime % 60000) / 1000).toInt()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("dreameditation Hypnagogic")
            .setContentText("Dream guidance in ${minutes}:${seconds.toString().padStart(2, '0')}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun updateNotification(remainingTime: Long) {
        postForegroundNotification(createNotification(remainingTime))
    }

    private fun postForegroundNotification(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    fun stopHypnagogicTimer() {
        isActive = false
        isAudioPlaying = false
        countDownTimer?.cancel()
        textToSpeech?.stop()
        stopSelf()
    }

    fun setAudioPlayingState(playing: Boolean) {
        isAudioPlaying = playing
        android.util.Log.d("HypnagogicTTS", "Audio playing state changed: $playing")

        if (!playing && isActive) {
            android.util.Log.d("HypnagogicTTS", "Audio stopped, pausing dream guidance")
            textToSpeech?.stop()
        }
    }

    fun isTimerActive(): Boolean = isActive

    fun getRemainingTime(): Long {
        return countDownTimer?.let { unused ->
            // This would need to be implemented to track remaining time
            0L
        } ?: 0L
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        textToSpeech?.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    companion object {
        private const val CHANNEL_ID = "dreammaze_hypnagogic_channel"
        private const val NOTIFICATION_ID = 1002
    }
}
