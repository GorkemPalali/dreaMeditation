package com.dreameditation.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Base typography definition
private val baseTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

val dreameditationTypography = baseTypography

fun getScaledTypography(scale: Float): Typography {
    if (scale == 1.0f) return baseTypography
    
    return Typography(
        displayLarge = baseTypography.displayLarge.copy(fontSize = baseTypography.displayLarge.fontSize * scale, lineHeight = baseTypography.displayLarge.lineHeight * scale),
        displayMedium = baseTypography.displayMedium.copy(fontSize = baseTypography.displayMedium.fontSize * scale, lineHeight = baseTypography.displayMedium.lineHeight * scale),
        displaySmall = baseTypography.displaySmall.copy(fontSize = baseTypography.displaySmall.fontSize * scale, lineHeight = baseTypography.displaySmall.lineHeight * scale),
        
        headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * scale, lineHeight = baseTypography.headlineLarge.lineHeight * scale),
        headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * scale, lineHeight = baseTypography.headlineMedium.lineHeight * scale),
        headlineSmall = baseTypography.headlineSmall.copy(fontSize = baseTypography.headlineSmall.fontSize * scale, lineHeight = baseTypography.headlineSmall.lineHeight * scale),
        
        titleLarge = baseTypography.titleLarge.copy(fontSize = baseTypography.titleLarge.fontSize * scale, lineHeight = baseTypography.titleLarge.lineHeight * scale),
        titleMedium = baseTypography.titleMedium.copy(fontSize = baseTypography.titleMedium.fontSize * scale, lineHeight = baseTypography.titleMedium.lineHeight * scale),
        titleSmall = baseTypography.titleSmall.copy(fontSize = baseTypography.titleSmall.fontSize * scale, lineHeight = baseTypography.titleSmall.lineHeight * scale),
        
        bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * scale, lineHeight = baseTypography.bodyLarge.lineHeight * scale),
        bodyMedium = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * scale, lineHeight = baseTypography.bodyMedium.lineHeight * scale),
        bodySmall = baseTypography.bodySmall.copy(fontSize = baseTypography.bodySmall.fontSize * scale, lineHeight = baseTypography.bodySmall.lineHeight * scale),
        
        labelLarge = baseTypography.labelLarge.copy(fontSize = baseTypography.labelLarge.fontSize * scale, lineHeight = baseTypography.labelLarge.lineHeight * scale),
        labelMedium = baseTypography.labelMedium.copy(fontSize = baseTypography.labelMedium.fontSize * scale, lineHeight = baseTypography.labelMedium.lineHeight * scale),
        labelSmall = baseTypography.labelSmall.copy(fontSize = baseTypography.labelSmall.fontSize * scale, lineHeight = baseTypography.labelSmall.lineHeight * scale)
    )
}

// Legacy typography for compatibility
val Typography = dreameditationTypography