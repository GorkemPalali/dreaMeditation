package com.dreameditation.app.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : Screen("home")

    object AudioLibrary : Screen("audio_library")

    object Meditation : Screen("meditation")

    object Profile : Screen("profile")

    object SleepLibraryTab : Screen("library_sleep")
    object MeditationLibraryTab : Screen("library_meditation")

    object SessionDuration : Screen(
        route = "session_duration/{sessionType}/{trackId}",
        arguments = listOf(
            navArgument("sessionType") {
                type = NavType.StringType
                defaultValue = "sleep"
            },
            navArgument("trackId") {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {
        fun createRoute(sessionType: String, trackId: String? = null): String {
            return "session_duration/$sessionType/${trackId ?: "null"}"
        }
    }

    object Session : Screen(
        route = "session/{sessionType}/{trackId}/{duration}",
        arguments = listOf(
            navArgument("sessionType") {
                type = NavType.StringType
                defaultValue = "sleep"
            },
            navArgument("trackId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("duration") {
                type = NavType.LongType
                defaultValue = 60L * 60L * 1000L
            }
        )
    ) {
        fun createRoute(sessionType: String, trackId: String? = null, durationMs: Long = 60L * 60L * 1000L): String {
            return "session/$sessionType/${trackId ?: "null"}/$durationMs"
        }
    }
}