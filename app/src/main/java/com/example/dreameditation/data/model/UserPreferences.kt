package com.example.dreamaze.data.model

/*** Stores user preferences including audio settings,hypnagogic preferences and app configuration */
data class UserPreferences(
    val isFirstLaunch: Boolean = true,
    val isDarkMode: Boolean = true, // Always true for sleep app(for eye comfort before sleep)
    val defaultVolume: Float = 0.7f,
    val maxVolume: Float = 0.85f, // Safe listening guidelines
    val fadeInDuration: Long = 5000L,
    val fadeOutDuration: Long = 5000L,
    val autoPlayNext: Boolean = false,
    val keepScreenOn: Boolean = false,
    val showSleepTimer: Boolean = true,
    val showMeditationTimer: Boolean = true,
    val enableNotifications: Boolean = true,
    val notificationTime: String = "21:00", // Default to 9 PM
    val enableHypnagogic: Boolean = true,
    val hypnagogicDelay: Long = 25 * 60 * 1000L, // 25 minutes in milliseconds
    val hypnagogicRepetitions: Int = 3,
    val hypnagogicVolume: Float = 0.2f, // 20% of main audio volume
    val enableSleepTracking: Boolean = true,
    val enableAnalytics: Boolean = false,
    val language: String = "en",
    val timezone: String = "UTC",
    val lastBackup: Long? = null,
    val isPremium: Boolean = false,
    val premiumExpiry: Long? = null
)


data class HypnagogicSettings(
    val isEnabled: Boolean = true,
    val delayMinutes: Int = 25,
    val repetitions: Int = 3,
    val volumePercentage: Float = 0.2f,
    val keywords: List<String> = emptyList(),
    val customKeywords: List<String> = emptyList(),
    val isCustomVoice: Boolean = false,
    val voiceGender: VoiceGender = VoiceGender.NEUTRAL
)


enum class VoiceGender {
    MALE,
    FEMALE,
    NEUTRAL
}


data class ThemeSettings(
    val isDarkMode: Boolean = true,
    val primaryColor: String = "dream_flow_primary",
    val accentColor: String = "dream_flow_secondary",
    val fontSize: FontSize = FontSize.MEDIUM,
    val reduceAnimations: Boolean = false
)


enum class FontSize {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE
}