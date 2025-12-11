package com.dreameditation.app.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dreameditation.app.data.preferences.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale

object LocaleManager {
    private var currentLanguageTag = "en-US"

    fun applyLanguageTag(tag: String) {
        try {
            if (currentLanguageTag == tag) {
                return
            }
            currentLanguageTag = tag
            val locales = LocaleListCompat.forLanguageTags(tag)
            AppCompatDelegate.setApplicationLocales(locales)
            android.util.Log.i("LocaleManager", "Applied language tag: $tag")
        } catch (e: Exception) {
            android.util.Log.e("LocaleManager", "Failed to apply language tag: ${e.message}", e)
        }
    }

    fun getCurrentLanguageTag(): String = currentLanguageTag

    fun observeAndApply(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                AppPreferences.appLanguageFlow(context)
                    .onEach { tag ->
                        android.util.Log.i("LocaleManager", "Observed language tag: $tag, current: $currentLanguageTag")
                        if (tag != currentLanguageTag) {
                            applyLanguageTag(tag)
                        }
                    }
                    .catch { e ->
                        android.util.Log.e("LocaleManager", "Failed to apply locales: ${e.message}")
                    }
                    .collect { }
            } catch (e: Exception) {
                android.util.Log.e("LocaleManager", "observeAndApply error: ${e.message}")
            }
        }
    }
    
    fun loadAndApplyLanguage(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val savedLanguage = AppPreferences.appLanguageFlow(context).first()
                android.util.Log.i("LocaleManager", "Loading saved language: $savedLanguage")
                applyLanguageTag(savedLanguage)
            } catch (e: Exception) {
                android.util.Log.e("LocaleManager", "Failed to load language: ${e.message}")
            }
        }
    }
}