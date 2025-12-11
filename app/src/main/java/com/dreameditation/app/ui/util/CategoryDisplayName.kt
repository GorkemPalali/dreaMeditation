package com.dreameditation.app.ui.util

import com.dreameditation.app.data.model.AudioCategory

fun getCategoryDisplayName(category: AudioCategory): String {
    return when (category) {
        AudioCategory.NATURE_SOUNDS -> "Nature"
        AudioCategory.BINAURAL_BEATS -> "Binaural"
        AudioCategory.GUIDED_MEDITATION -> "Guided"
        AudioCategory.MEDITATION_MUSIC -> "Music"
        AudioCategory.WHITE_NOISE -> "Noise"
        AudioCategory.SLEEP_STORIES -> "Stories"
        AudioCategory.AMBIENT_SOUNDS -> "Ambient"
    }
}

fun getCategoryDisplayNameFull(category: AudioCategory): String {
    return when (category) {
        AudioCategory.NATURE_SOUNDS -> "Nature Sounds"
        AudioCategory.BINAURAL_BEATS -> "Binaural Beats"
        AudioCategory.GUIDED_MEDITATION -> "Guided Meditation"
        AudioCategory.MEDITATION_MUSIC -> "Meditation Music"
        AudioCategory.WHITE_NOISE -> "White Noise"
        AudioCategory.SLEEP_STORIES -> "Sleep Stories"
        AudioCategory.AMBIENT_SOUNDS -> "Ambient Sounds"
    }
}
