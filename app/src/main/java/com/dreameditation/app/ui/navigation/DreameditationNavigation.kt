package com.dreameditation.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dreameditation.app.ui.screen.HomeScreen
import com.dreameditation.app.ui.screen.AudioLibraryScreen
import androidx.compose.runtime.CompositionLocalProvider
import com.dreameditation.app.ui.screen.LocalPrefilter
import com.dreameditation.app.ui.screen.Prefilter
import com.dreameditation.app.ui.screen.MeditationScreen
import com.dreameditation.app.ui.screen.ProfileScreen
import com.dreameditation.app.ui.screen.SessionScreen
import com.dreameditation.app.ui.screen.SessionDurationScreen
import com.dreameditation.app.ui.screen.AudioLibraryScreen

@Composable
fun DreameditationNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSession = { sessionType, trackId ->
                    navController.navigate(Screen.SessionDuration.createRoute(sessionType, trackId))
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.AudioLibrary.route)
                }
            )
        }

        composable(Screen.AudioLibrary.route) {
            AudioLibraryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSession = { sessionType, trackId ->
                    navController.navigate(Screen.SessionDuration.createRoute(sessionType, trackId))
                }
            )
        }

        composable(Screen.Meditation.route) {
            MeditationScreen(
                onNavigateToSession = { sessionType, trackId ->
                    navController.navigate(Screen.SessionDuration.createRoute(sessionType, trackId))
                }
            )
        }
        composable(
            route = Screen.SessionDuration.route,
            arguments = Screen.SessionDuration.arguments
        ) { backStackEntry ->
            val sessionType = backStackEntry.arguments?.getString("sessionType") ?: "sleep"
            val trackId = backStackEntry.arguments?.getString("trackId")

            SessionDurationScreen(
                sessionType = sessionType,
                trackId = trackId,
                onSelectDuration = { durationMs ->
                    navController.navigate(Screen.Session.createRoute(sessionType, trackId, durationMs))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MeditationLibraryTab.route) {
            CompositionLocalProvider(LocalPrefilter provides Prefilter.Meditation) {
                AudioLibraryScreen(
                    onNavigateBack = {
                        // keep user in tab
                        navController.popBackStack()
                    },
                    onNavigateToSession = { _, trackId ->
                        navController.navigate(Screen.SessionDuration.createRoute("meditation", trackId))
                    }
                )
            }
        }

        composable(Screen.SleepLibraryTab.route) {
            CompositionLocalProvider(LocalPrefilter provides Prefilter.Sleep) {
                AudioLibraryScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSession = { _, trackId ->
                        navController.navigate(Screen.SessionDuration.createRoute("sleep", trackId))
                    }
                )
            }
        }

        composable(
            route = Screen.Session.route,
            arguments = Screen.Session.arguments
        ) { backStackEntry ->
            val sessionType = backStackEntry.arguments?.getString("sessionType") ?: "sleep"
            val trackId = backStackEntry.arguments?.getString("trackId")
            val duration = backStackEntry.arguments?.getLong("duration") ?: 60L * 60L * 1000L

            SessionScreen(
                sessionType = sessionType,
                trackId = trackId,
                sessionDurationMs = duration,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}