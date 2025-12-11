package com.dreameditation.app.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private const val CARD_HEIGHT = 120
private const val CARD_ELEVATION = 4
private const val CARD_PADDING = 16
private const val ICON_SPACER_HEIGHT = 8

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: String,
    iconRes: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(CARD_HEIGHT.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CARD_PADDING.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title
                )
            } else {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Spacer(modifier = Modifier.height(ICON_SPACER_HEIGHT.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}