package com.dreameditation.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.dreameditation.app.util.LocaleManager
import com.dreameditation.app.data.seed.SampleDataSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class DreamMeditationApplication : Application() {

    @Inject
    lateinit var sampleDataSeeder: SampleDataSeeder

    override fun onCreate() {
        super.onCreate()

        android.util.Log.i("DreaMeditationApplication", "Application started")

        LocaleManager.loadAndApplyLanguage(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                android.util.Log.d("DreaMeditationApplication", "Starting sample data seeding...")
                sampleDataSeeder.seedSampleData()
                android.util.Log.i("DreaMeditationApplication", "Sample data seeded successfully")
            } catch (e: Exception) {
                android.util.Log.e("DreaMeditationApplication", "Failed to seed sample data: ${e.message}", e)
            }
        }
    }

    override fun attachBaseContext(base: android.content.Context) {
        val savedLanguage = com.dreameditation.app.data.preferences.AppPreferences.getAppLanguageSync(base)
        
        val locale = java.util.Locale.forLanguageTag(savedLanguage)
        java.util.Locale.setDefault(locale)
        
        val configuration = android.content.res.Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        
        val context = base.createConfigurationContext(configuration)
        super.attachBaseContext(context)
        
        android.util.Log.i("DreaMeditationApplication", "Applied language: $savedLanguage")
    }
}