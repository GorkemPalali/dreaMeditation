package com.dreameditation.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dreameditation.app.R
import com.dreameditation.app.ui.component.CategoryChip
import com.dreameditation.app.ui.component.MeditationActionCard
import com.dreameditation.app.ui.component.ProgramCard
import com.dreameditation.app.ui.component.SectionHeader
import com.dreameditation.app.ui.util.getCategoryDisplayName
import com.dreameditation.app.ui.viewmodel.MeditationViewModel

@Composable
fun MeditationScreen(
    onNavigateToSession: (String, String?) -> Unit,
    viewModel: MeditationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Text(
                text = stringResource(R.string.meditation_header),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.meditation_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Meditation Actions
        item {
            SectionHeader(title = stringResource(id = R.string.quickstart_section_header_title))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MeditationActionCard(
                    title = stringResource(id = R.string.meditation_action_card_title1),
                    subtitle = stringResource(id = R.string.meditation_action_card_subtitle1),
                    onClick = { onNavigateToSession("meditation", null) },
                    modifier = Modifier.weight(1f)
                )
                MeditationActionCard(
                    title = stringResource(id = R.string.meditation_action_card_title2),
                    subtitle = stringResource(id = R.string.meditation_action_card_subtitle2),
                    onClick = { onNavigateToSession("meditation", null) },
                    modifier = Modifier.weight(1f)
                )
                MeditationActionCard(
                    title = stringResource(R.string.meditation_action_card_title3),
                    subtitle = stringResource(R.string.meditation_action_card_subtitle3),
                    onClick = { onNavigateToSession("meditation", null) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Meditation Categories
        item {
            SectionHeader(title = stringResource(id = R.string.meditation_section_header_categories))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        label = stringResource(id = R.string.library_category_all),
                        isSelected = selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) }
                    )
                }
                items(uiState.meditationCategories) { category ->
                    CategoryChip(
                        label = getCategoryDisplayName(category),
                        isSelected = selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) }
                    )
                }
            }
        }

        // Meditation Programs
        if (uiState.programs.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(id = R.string.meditation_section_header_programs))
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.programs.forEach { program ->
                        ProgramCard(
                            title = program.title,
                            description = program.description,
                            duration = program.duration,
                            progress = program.progress,
                            onClick = { onNavigateToSession("meditation", null) }
                        )
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
                    CircularProgressIndicator()
                }
            }
        }

        uiState.error?.let { error ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
