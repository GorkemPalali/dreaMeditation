package com.dreameditation.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.dreameditation.app.ui.theme.AppIcons
import com.dreameditation.app.ui.theme.IconName
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.ui.component.AudioTrackCard
import com.dreameditation.app.ui.component.CategoryChip
import com.dreameditation.app.ui.util.getCategoryDisplayName
import com.dreameditation.app.ui.viewmodel.AudioLibraryViewModel
import com.dreameditation.app.ui.viewmodel.PrefilterMode
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import com.dreameditation.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLibraryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSession: (String, String?) -> Unit,
    viewModel: AudioLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val localPrefilter = LocalPrefilter.current

    val isPrefilterActive = localPrefilter != Prefilter.None
    val sessionType = when (localPrefilter) {
        Prefilter.Meditation -> "meditation"
        Prefilter.Sleep -> "sleep"
        Prefilter.None -> "sleep"
    }

    LaunchedEffect(localPrefilter) {
        when (localPrefilter) {
            Prefilter.Meditation -> viewModel.setPrefilter(PrefilterMode.Meditation)
            Prefilter.Sleep -> viewModel.setPrefilter(PrefilterMode.Sleep)
            Prefilter.None -> viewModel.setPrefilter(PrefilterMode.None)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AudioLibraryTopBar(
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Search Bar Section
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::searchTracks,
                        placeholder = { 
                            Text(
                                text = stringResource(id = R.string.library_search_placeholder),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(id = R.string.library_search_placeholder),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Category Filter Section
            if (!isPrefilterActive) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.library_categories),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            LazyRow(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    CategoryChip(
                                        label = stringResource(id = R.string.library_category_all),
                                        isSelected = selectedCategory == null,
                                        onClick = { viewModel.selectCategory(null) }
                                    )
                                }
                                items(AudioCategory.values().toList()) { category ->
                                    CategoryChip(
                                        label = getCategoryDisplayName(category),
                                        isSelected = selectedCategory == category,
                                        onClick = { viewModel.selectCategory(category) }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Show prefilter info when active
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = when (localPrefilter) {
                                Prefilter.Meditation -> stringResource(id = R.string.library_showing_meditation)
                                Prefilter.Sleep -> stringResource(id = R.string.library_showing_sleep)
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Error State
            uiState.error?.let { error ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Tracks List Section Header
            if (!uiState.isLoading && uiState.error == null) {
                item {
                    Text(
                        text = stringResource(id = R.string.library_tracks),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Loading State
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } 
            // Empty State
            else if (uiState.tracks.isEmpty() && uiState.error == null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = stringResource(id = R.string.library_no_tracks),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } 
            // Tracks List
            else if (uiState.tracks.isNotEmpty()) {
                items(uiState.tracks) { track ->
                    AudioTrackCard(
                        track = track,
                        onClick = { onNavigateToSession(sessionType, track.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

enum class Prefilter { Meditation, Sleep, None }

val LocalPrefilter = compositionLocalOf { Prefilter.None }

@Composable
private fun AudioLibraryTopBar(
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = AppIcons.painter(IconName.ArrowBack),
                contentDescription = stringResource(id = R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = stringResource(id = R.string.library_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}