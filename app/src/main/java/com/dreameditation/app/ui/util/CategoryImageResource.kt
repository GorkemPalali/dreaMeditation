package com.dreameditation.app.ui.util

import com.dreameditation.app.R
import com.dreameditation.app.data.model.AudioCategory

fun getImageResourceForCategory(category: AudioCategory): Int {
    return when (category) {
        AudioCategory.NATURE_SOUNDS,
        AudioCategory.AMBIENT_SOUNDS -> R.drawable.img_nature_bg

        AudioCategory.SLEEP_STORIES,
        AudioCategory.WHITE_NOISE -> R.drawable.img_sleep_bg

        else -> R.drawable.img_meditation_bg
    }
}

