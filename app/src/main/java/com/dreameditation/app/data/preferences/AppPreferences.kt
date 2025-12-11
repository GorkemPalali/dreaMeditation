package com.dreameditation.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "dreameditationlow_prefs"
private const val SHARED_PREFS_NAME = "dreameditation_locale"
private const val SHARED_PREFS_KEY_LANGUAGE = "language"
private const val DEFAULT_LANGUAGE = "en-US"
private const val DEFAULT_FONT_SIZE = "MEDIUM"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object AppPreferences {
    private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")
    private val KEY_FONT_SIZE = stringPreferencesKey("font_size")
    
    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun appLanguageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { prefs: Preferences ->
            prefs[KEY_APP_LANGUAGE] ?: DEFAULT_LANGUAGE
        }

    fun fontSizeFlow(context: Context): Flow<String> =
        context.dataStore.data.map { prefs: Preferences ->
            prefs[KEY_FONT_SIZE] ?: DEFAULT_FONT_SIZE
        }

    suspend fun setAppLanguage(context: Context, languageTag: String) {
        try {
            // Save to SharedPreferences first (synchronous, for attachBaseContext)
            saveLanguageToSharedPreferences(context, languageTag)
            
            // Then save to DataStore (async, for reactive flows)
            saveLanguageToDataStore(context, languageTag)
            
            android.util.Log.i("AppPreferences", "Language saved: $languageTag")
        } catch (e: Exception) {
            android.util.Log.e("AppPreferences", "Failed to save language: ${e.message}", e)
            throw e
        }
    }
    
    private fun saveLanguageToSharedPreferences(context: Context, languageTag: String) {
        getSharedPreferences(context)
            .edit()
            .putString(SHARED_PREFS_KEY_LANGUAGE, languageTag)
            .commit()
    }
    
    private suspend fun saveLanguageToDataStore(context: Context, languageTag: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_APP_LANGUAGE] = languageTag
        }
    }
    
    fun getAppLanguageSync(context: Context): String {
        return try {
            // Try SharedPreferences first (faster, synchronous)
            val language = getSharedPreferences(context)
                .getString(SHARED_PREFS_KEY_LANGUAGE, null)
            
            language ?: DEFAULT_LANGUAGE
        } catch (e: Exception) {
            android.util.Log.e("AppPreferences", "Failed to get language sync: ${e.message}", e)
            DEFAULT_LANGUAGE
        }
    }

    suspend fun setFontSize(context: Context, fontSizeName: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[KEY_FONT_SIZE] = fontSizeName
            }
            android.util.Log.d("AppPreferences", "Font size saved: $fontSizeName")
        } catch (e: Exception) {
            android.util.Log.e("AppPreferences", "Failed to save font size: ${e.message}", e)
            throw e
        }
    }
}