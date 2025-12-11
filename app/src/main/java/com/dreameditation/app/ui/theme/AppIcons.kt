package com.dreameditation.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

enum class IconName {
    Download,
    Logo,
    Play,
    Pause,
    Back,
    Next,
    Previous,
    VolumeDown,
    VolumeUp,
    Notifications,
    Language,
    ArrowForward,
    ArrowBack,
    Lock,
    Info,
    PlayFilled,
    ArrowUp,
    ArrowDown,
    Meditation,
    Sleep,
}

object AppIcons {
    @Composable
    fun painter(name: IconName): Painter = when (name) {
        IconName.Download -> rememberVectorPainter(Icons.Rounded.Download)
        IconName.Logo -> rememberVectorPainter(Icons.Rounded.AccountCircle)
        IconName.Play -> rememberVectorPainter(Icons.Rounded.PlayArrow)
        IconName.Pause -> rememberVectorPainter(Icons.Rounded.Pause)
        IconName.Back -> rememberVectorPainter(Icons.Rounded.ArrowBackIosNew)
        IconName.Next -> rememberVectorPainter(Icons.Rounded.SkipNext)
        IconName.Previous -> rememberVectorPainter(Icons.Rounded.SkipPrevious)
        IconName.VolumeDown -> rememberVectorPainter(Icons.Rounded.VolumeDown)
        IconName.VolumeUp -> rememberVectorPainter(Icons.Rounded.VolumeUp)
        IconName.Notifications -> rememberVectorPainter(Icons.Outlined.Notifications)
        IconName.Language -> rememberVectorPainter(Icons.Outlined.Language)
        IconName.ArrowForward -> rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward)
        IconName.ArrowBack -> rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack)
        IconName.Lock -> rememberVectorPainter(Icons.Outlined.Lock)
        IconName.Info -> rememberVectorPainter(Icons.Outlined.Info)
        IconName.PlayFilled -> rememberVectorPainter(Icons.Filled.PlayArrow)
        IconName.ArrowUp -> rememberVectorPainter(Icons.Rounded.KeyboardArrowUp)
        IconName.ArrowDown -> rememberVectorPainter(Icons.Rounded.KeyboardArrowDown)
        IconName.Meditation -> rememberVectorPainter(Icons.Rounded.SelfImprovement)
        IconName.Sleep -> rememberVectorPainter(Icons.Rounded.DarkMode)
    }
}

@Composable
fun AppIcon(
    name: IconName,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) = Icon(
    painter = AppIcons.painter(name),
    contentDescription = contentDescription,
    modifier = modifier,
    tint = tint
)

