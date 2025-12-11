package com.dreameditation.app.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.ui.theme.CardBackgroundDark
import com.dreameditation.app.ui.theme.TextColorLight
import com.dreameditation.app.ui.util.getImageResourceForCategory

private const val CARD_WIDTH = 140
private const val CARD_CORNER_RADIUS = 12
private const val CARD_ASPECT_RATIO = 0.75f
private const val OVERLAY_ALPHA = 0.2f
private const val VERTICAL_SPACING = 8

@Composable
fun HomeRecommendationCard(
    track: AudioTrack,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = getImageResourceForCategory(track.category)

    Column(
        modifier = modifier
            .width(CARD_WIDTH.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(CARD_ASPECT_RATIO),
            shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = OVERLAY_ALPHA))
                )
            }
        }

        Text(
            text = track.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = TextColorLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
