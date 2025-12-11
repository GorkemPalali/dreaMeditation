package com.dreameditation.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.dreameditation.app.ui.component.BottomNavigationBar
import com.dreameditation.app.ui.navigation.DreameditationNavigation
import com.dreameditation.app.ui.theme.dreameditationTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.dreameditation.app.data.preferences.AppPreferences

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun attachBaseContext(base: android.content.Context) {
        val savedLanguage = AppPreferences.getAppLanguageSync(base)
        
        val locale = java.util.Locale.forLanguageTag(savedLanguage)
        java.util.Locale.setDefault(locale)
        
        val configuration = android.content.res.Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        
        val context = base.createConfigurationContext(configuration)
        super.attachBaseContext(context)
        
        android.util.Log.i("MainActivity", "Applied language: $savedLanguage")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val context = LocalContext.current
            val fontSize by AppPreferences.fontSizeFlow(context).collectAsState(initial = "MEDIUM")
            
            dreameditationTheme(fontSize = fontSize) {
                dreameditationApp()
            }
        }
    }
}

@Composable
fun dreameditationApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        DreameditationNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}