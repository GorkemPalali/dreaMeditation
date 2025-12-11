package com.dreameditation.app.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.ui.util.getCategoryIcon

private const val PREMIUM_ICON = "★"
private const val PLAYS_TEXT = " plays"
private const val CARD_PADDING = 12
private const val CARD_CORNER_RADIUS = 12
private const val CARD_ELEVATION = 2
private const val VERTICAL_SPACING = 8

@Composable
fun AudioTrackCard(
    track: AudioTrack,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING.dp),
            verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getCategoryIcon(track.category),
                    style = MaterialTheme.typography.headlineSmall
                )

                if (track.isPremium) {
                    Text(
                        text = PREMIUM_ICON,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = track.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = track.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (track.playCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${track.playCount}$PLAYS_TEXT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
