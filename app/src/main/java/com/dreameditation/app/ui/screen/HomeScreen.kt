package com.dreameditation.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dreameditation.app.R
import com.dreameditation.app.ui.component.HomeFeatureCard
import com.dreameditation.app.ui.component.HomeRecommendationCard
import com.dreameditation.app.ui.theme.*
import com.dreameditation.app.ui.viewmodel.HomeViewModel

private const val TOP_PADDING = 16
private const val BOTTOM_PADDING = 24
private const val VERTICAL_SPACING = 24
private const val HORIZONTAL_PADDING = 16
private const val FEATURE_CARDS_SPACING = 16
private const val RECOMMENDED_SECTION_SPACING = 12
private const val RECOMMENDED_ROW_SPACING = 16
private const val ERROR_PADDING = 16

private const val SESSION_TYPE_MEDITATION = "meditation"
private const val SESSION_TYPE_SLEEP = "sleep"

@Composable
fun HomeScreen(
    onNavigateToSession: (String, String?) -> Unit,
    onNavigateToLibrary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(top = TOP_PADDING.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = BOTTOM_PADDING.dp),
            verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HORIZONTAL_PADDING.dp),
                    verticalArrangement = Arrangement.spacedBy(FEATURE_CARDS_SPACING.dp)
                ) {
                    HomeFeatureCard(
                        title = stringResource(id = R.string.home_meditation_title),
                        subtitle = stringResource(id = R.string.home_meditation_subtitle),
                        gradientColors = listOf(MeditationGradientStart, MeditationGradientEnd),
                        icon = IconName.Meditation,
                        onClick = { onNavigateToSession(SESSION_TYPE_MEDITATION, null) }
                    )

                    HomeFeatureCard(
                        title = stringResource(id = R.string.home_sleep_title),
                        subtitle = stringResource(id = R.string.home_sleep_subtitle),
                        gradientColors = listOf(SleepGradientStart, SleepGradientEnd),
                        icon = IconName.Sleep,
                        onClick = { onNavigateToSession(SESSION_TYPE_SLEEP, null) }
                    )
                }
            }

            if (uiState.popularTracks.isNotEmpty() || uiState.recentTracks.isNotEmpty()) {
                val recommendedTracks = if (uiState.popularTracks.isNotEmpty()) {
                    uiState.popularTracks
                } else {
                    uiState.recentTracks
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(RECOMMENDED_SECTION_SPACING.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = HORIZONTAL_PADDING.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.home_recommended),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextColorLight
                            )

                            TextButton(onClick = onNavigateToLibrary) {
                                Text(
                                    text = stringResource(id = R.string.home_view_all),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryColor
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING.dp),
                            horizontalArrangement = Arrangement.spacedBy(RECOMMENDED_ROW_SPACING.dp)
                        ) {
                            items(recommendedTracks) { track ->
                                HomeRecommendationCard(
                                    track = track,
                                    onClick = { onNavigateToSession(SESSION_TYPE_SLEEP, track.id) }
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                }
            }

            uiState.error?.let { error ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ERROR_PADDING.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}