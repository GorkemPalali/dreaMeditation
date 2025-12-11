package com.dreameditation.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dreameditation.app.ui.theme.AppIcon
import com.dreameditation.app.ui.theme.IconName

private const val CARD_HEIGHT = 140
private const val CARD_CORNER_RADIUS = 24
private const val CARD_ELEVATION = 8
private const val DECORATIVE_CIRCLE_SIZE = 100
private const val DECORATIVE_CIRCLE_OFFSET = 20
private const val CARD_PADDING = 24
private const val ICON_CONTAINER_SIZE = 48
private const val ICON_SIZE = 24
private const val SPACER_WIDTH = 16
private const val TEXT_SPACER_HEIGHT = 8
private const val DECORATIVE_ALPHA = 0.1f
private const val ICON_CONTAINER_ALPHA = 0.2f
private const val SUBTITLE_ALPHA = 0.8f

@Composable
fun HomeFeatureCard(
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    icon: IconName,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
        ) {
            Box(
                modifier = Modifier
                    .size(DECORATIVE_CIRCLE_SIZE.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = DECORATIVE_CIRCLE_OFFSET.dp, y = (-DECORATIVE_CIRCLE_OFFSET).dp)
                    .background(
                        Color.White.copy(alpha = DECORATIVE_ALPHA),
                        CircleShape
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(CARD_PADDING.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(TEXT_SPACER_HEIGHT.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = SUBTITLE_ALPHA),
                        maxLines = 2,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }

                Spacer(modifier = Modifier.width(SPACER_WIDTH.dp))

                Box(
                    modifier = Modifier
                        .size(ICON_CONTAINER_SIZE.dp)
                        .background(
                            Color.White.copy(alpha = ICON_CONTAINER_ALPHA),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        name = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(ICON_SIZE.dp)
                    )
                }
            }
        }
    }
}
