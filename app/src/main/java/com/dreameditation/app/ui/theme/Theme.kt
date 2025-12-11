package com.dreameditation.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat



private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    background = BackgroundDark,
    onBackground = TextColorLight,
    surface = CardBackgroundDark,
    onSurface = TextColorLight,
    onSurfaceVariant = SubtextColorLight,
    outline = HighlightColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    background = BackgroundLight,
    onBackground = TextColorDark,
    surface = CardBackgroundLight,
    onSurface = TextColorDark,
    onSurfaceVariant = SubtextColorDark,
    outline = HighlightColor
)

@Composable
fun dreameditationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontSize: String = "MEDIUM", // SMALL, MEDIUM, LARGE
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val scale = when (fontSize) {
        "SMALL" -> 0.85f
        "LARGE" -> 1.15f
        else -> 1.0f
    }
    
    val typography = getScaledTypography(scale)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}