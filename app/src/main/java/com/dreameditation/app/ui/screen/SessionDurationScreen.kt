package com.dreameditation.app.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.dreameditation.app.R
import kotlin.math.absoluteValue



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionDurationScreen(
    sessionType: String,
    trackId: String?,
    onSelectDuration: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {

    val duration30MinLabel = stringResource(id = R.string.duration_30min)
    val duration1HourLabel = stringResource(id = R.string.duration_1h)
    val duration3HourLabel = stringResource(id = R.string.duration_3h)

    val durations = remember {
        listOf(
            DurationOption(30L * 60L * 1000L, duration30MinLabel),
            DurationOption(60L * 60L * 1000L, duration1HourLabel),
            DurationOption(3L * 60L * 60L * 1000L, duration3HourLabel)
        )
    }

    // Initial selection: 30 minutes (index 0)
    val initialPage = 0
    val pagerState = rememberPagerState(initialPage = initialPage) { durations.size }
    
    // Update selected duration when page changes
    val selectedDuration = durations[pagerState.currentPage].durationMillis

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Text(
                    text = stringResource(id = R.string.duration_pick_prompt),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // Scrollable Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Central Highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                )

                // Vertical Pager
                VerticalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(vertical = 70.dp), // (200 - 60) / 2 = 70
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue

                    val scale = lerp(
                        start = 0.7f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    
                    val alpha = lerp(
                        start = 0.3f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = durations[page].label,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Button(
                onClick = { onSelectDuration(selectedDuration) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.start_session),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

data class DurationOption(
    val durationMillis: Long,
    val label: String
)


