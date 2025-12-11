package com.dreameditation.app.ui.util

import com.dreameditation.app.data.model.AudioCategory

fun getCategoryIcon(category: AudioCategory): String {
    return when (category) {
        AudioCategory.NATURE_SOUNDS -> "🌿"
        AudioCategory.BINAURAL_BEATS -> "🎵"
        AudioCategory.GUIDED_MEDITATION -> "🧘"
        AudioCategory.MEDITATION_MUSIC -> "🎼"
        AudioCategory.WHITE_NOISE -> "🔊"
        AudioCategory.SLEEP_STORIES -> "📖"
        AudioCategory.AMBIENT_SOUNDS -> "🌊"
    }
}

