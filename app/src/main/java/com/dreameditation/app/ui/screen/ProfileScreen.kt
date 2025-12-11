package com.dreameditation.app.ui.screen

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.painter.Painter
import com.dreameditation.app.ui.theme.IconName
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dreameditation.app.R
import com.dreameditation.app.ui.theme.AppIcons
import com.dreameditation.app.data.preferences.AppPreferences
import com.dreameditation.app.service.HypnagogicTimerService
import com.dreameditation.app.ui.theme.PrimaryColor
import com.dreameditation.app.ui.viewmodel.ProfileViewModel
import com.dreameditation.app.util.LocaleManager
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    val defaultName = stringResource(id = R.string.profile_name_default)
    var userName by remember { mutableStateOf(defaultName) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Avatar Selection
    val availableAvatars = listOf(R.drawable.avatar_1, R.drawable.avatar_2)
    var selectedAvatarRes by remember { mutableStateOf(R.drawable.avatar_1) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }

    var showDreamGuidanceDialog by remember { mutableStateOf(false) }
    var dreamGuidanceKeywords by remember { mutableStateOf("") }
    var hypnagogicService by remember { mutableStateOf<HypnagogicTimerService?>(null) }
    var isServiceBound by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.language) {
        dreamGuidanceKeywords = when (uiState.language) {
            "tr" -> "huzur, sakinlik, uyku, rüya"
            else -> "peace, calm, sleep, dream"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
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
                    text = stringResource(id = R.string.profile_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar with click to change
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .clickable { showAvatarDialog = true }
                    ) {
                        AsyncImage(
                            model = selectedAvatarRes,
                            contentDescription = "Profile Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7),
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.profile_edit),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Statistics Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.profile_your_stats),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatItem(
                            title = stringResource(id = R.string.stat_total_sessions),
                            value = uiState.totalSessions.toString(),
                            textColor = MaterialTheme.colorScheme.onBackground,
                            subtextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            title = stringResource(id = R.string.stat_sleep_hours),
                            value = "${uiState.totalSleepHours}h",
                            textColor = MaterialTheme.colorScheme.onBackground,
                            subtextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatItem(
                            title = stringResource(id = R.string.stat_meditation_streak),
                            value = stringResource(id = R.string.stat_days_suffix, uiState.meditationStreakDays),
                            textColor = MaterialTheme.colorScheme.onBackground,
                            subtextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            title = stringResource(id = R.string.stat_favorite_category),
                            value = "Nature",
                            textColor = MaterialTheme.colorScheme.onBackground,
                            subtextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // App Settings Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.app_settings),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    SettingsRow(
                        icon = AppIcons.painter(IconName.Notifications),
                        title = stringResource(id = R.string.notifications),
                        textColor = MaterialTheme.colorScheme.onBackground,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        trailingContent = {
                            var checked by remember { mutableStateOf(true) }
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD4D4D8)
                                ),
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    )
                    Divider(color = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7), thickness = 1.dp, modifier = Modifier.padding(start = 72.dp))
                    
                    SettingsRow(
                        icon = AppIcons.painter(IconName.Language),
                        title = stringResource(id = R.string.language),
                        textColor = MaterialTheme.colorScheme.onBackground,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (uiState.language == "tr") "Türkçe" else "English",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    painter = AppIcons.painter(IconName.ArrowForward),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        onClick = { showLanguageDialog = true }
                    )
                    Divider(color = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7), thickness = 1.dp, modifier = Modifier.padding(start = 72.dp))

                    SettingsRow(
                        icon = AppIcons.painter(IconName.Info), // Placeholder icon for Font
                        title = "Font Size", // Manually Added for now as string resource might be missing
                        textColor = MaterialTheme.colorScheme.onBackground,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = uiState.fontSize.name,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    painter = AppIcons.painter(IconName.ArrowForward),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        onClick = { showFontSizeDialog = true }
                    )
                    Divider(color = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7), thickness = 1.dp, modifier = Modifier.padding(start = 72.dp))

                    SettingsRow(
                        icon = AppIcons.painter(IconName.PlayFilled),
                        title = stringResource(id = R.string.dream_guidance_test_title),
                        textColor = MaterialTheme.colorScheme.onBackground,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        trailingContent = {
                             Icon(
                                painter = AppIcons.painter(IconName.ArrowForward),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = { showDreamGuidanceDialog = true }
                    )
                }
            }

            // Account Management Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.account_settings),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    SettingsRow(
                        icon = AppIcons.painter(IconName.Lock),
                        title = stringResource(id = R.string.change_password),
                        textColor = MaterialTheme.colorScheme.onBackground,
                        primaryColor = PrimaryColor,
                        trailingContent = {
                            Icon(
                                painter = AppIcons.painter(IconName.ArrowForward),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    Divider(color = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7), thickness = 1.dp, modifier = Modifier.padding(start = 72.dp))
                    
                    SettingsRow(
                        icon = AppIcons.painter(IconName.Info),
                        title = stringResource(id = R.string.politics),
                        textColor = MaterialTheme.colorScheme.onBackground,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        trailingContent = {
                            Icon(
                                painter = AppIcons.painter(IconName.ArrowForward),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { /* Logout */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showEditDialog) {
        EditNameDialog(
            currentName = userName,
            onDismiss = { showEditDialog = false },
            onSave = { newName ->
                userName = newName
                showEditDialog = false
            }
        )
    }

    if (showAvatarDialog) {
        AvatarSelectionDialog(
            avatars = availableAvatars,
            currentAvatar = selectedAvatarRes,
            onDismiss = { showAvatarDialog = false },
            onAvatarSelected = { newAvatar ->
                selectedAvatarRes = newAvatar
                showAvatarDialog = false
            }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = uiState.language,
            onDismiss = { showLanguageDialog = false },
            onSave = { newLanguage ->
                val languageTag = if (newLanguage == "tr") "tr-TR" else "en-US"
                
                scope.launch {
                    // Save language first - commit() ensures synchronous write to SharedPreferences
                    viewModel.updateLanguage(languageTag)
                    
                    // Close dialog after saving
                    showLanguageDialog = false
                    
                    // Apply language using AppCompatDelegate (for API 33+)
                    LocaleManager.applyLanguageTag(languageTag)
                    
                    // Recreate activity to apply language changes immediately
                    // attachBaseContext will read from SharedPreferences and apply new locale
                    (context as? Activity)?.recreate()
                }
            }
        )
    }

    if (showFontSizeDialog) {
        FontSizeSelectionDialog(
            currentFontSize = uiState.fontSize,
            onDismiss = { showFontSizeDialog = false },
            onSave = { newFontSize ->
                viewModel.updateFontSize(newFontSize)
                showFontSizeDialog = false
            }
        )
    }

    if (showDreamGuidanceDialog) {
        DreamGuidanceTestDialog(
            currentKeywords = dreamGuidanceKeywords,
            currentLanguage = uiState.language,
            onDismiss = { showDreamGuidanceDialog = false },
            onTest = { keywords ->
                scope.launch {
                    try {
                        val intent = Intent(context, HypnagogicTimerService::class.java)
                        var serviceConnection: ServiceConnection? = null
                        serviceConnection = object : ServiceConnection {
                            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                                val binder = service as HypnagogicTimerService.HypnagogicTimerBinder
                                hypnagogicService = binder.getService()
                                isServiceBound = true
                                val languageTag = if (uiState.language == "tr") "tr-TR" else "en-US"
                                hypnagogicService?.updateLanguagePreference(languageTag)
                                scope.launch {
                                    val keywordList = keywords.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                    for (index in keywordList.indices) {
                                        hypnagogicService?.speakKeyword(keywordList[index])
                                        if (index < keywordList.size - 1) kotlinx.coroutines.delay(2000)
                                    }
                                    serviceConnection?.let { context.unbindService(it) }
                                    isServiceBound = false
                                    hypnagogicService = null
                                }
                            }
                            override fun onServiceDisconnected(name: ComponentName?) {
                                isServiceBound = false
                                hypnagogicService = null
                            }
                        }
                        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                        showDreamGuidanceDialog = false
                    } catch (e: Exception) {
                        showDreamGuidanceDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    textColor: Color,
    subtextColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = subtextColor
        )
    }
}

@Composable
fun SettingsRow(
    icon: Painter,
    title: String,
    textColor: Color,
    primaryColor: Color,
    trailingContent: @Composable () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(primaryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
        trailingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var nameText by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.profile_name_dialog_title)) },
        text = {
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text(stringResource(id = R.string.profile_name_dialog_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(nameText) },
                enabled = nameText.isNotBlank()
            ) { Text(stringResource(id = R.string.profile_name_dialog_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.profile_name_dialog_cancel)) }
        }
    )
}

@Composable
private fun AvatarSelectionDialog(
    avatars: List<Int>,
    currentAvatar: Int,
    onDismiss: () -> Unit,
    onAvatarSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.avatar_chosing)) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                avatars.forEach { avatarRes ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (avatarRes == currentAvatar) 4.dp else 0.dp,
                                color = if (avatarRes == currentAvatar) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onAvatarSelected(avatarRes) }
                    ) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.profile_name_dialog_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.language_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguage = "tr" }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedLanguage == "tr", onClick = { selectedLanguage = "tr" })
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.language_turkish_full))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguage = "en" }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedLanguage == "en", onClick = { selectedLanguage = "en" })
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.language_english_full))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedLanguage) },
                enabled = selectedLanguage != currentLanguage
            ) { Text(stringResource(id = R.string.language_dialog_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.language_dialog_cancel)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DreamGuidanceTestDialog(
    currentKeywords: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onTest: (String) -> Unit
) {
    var keywordsText by remember { mutableStateOf(currentKeywords) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.dream_guidance_test_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = keywordsText,
                    onValueChange = { keywordsText = it },
                    label = { Text(stringResource(id = R.string.dream_guidance_test_dialog_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onTest(keywordsText) },
                enabled = keywordsText.isNotBlank()
            ) {
                Icon(AppIcons.painter(IconName.PlayFilled), null, Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(id = R.string.dream_guidance_test_dialog_test))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.dream_guidance_test_dialog_cancel)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontSizeSelectionDialog(
    currentFontSize: com.dreameditation.app.data.model.FontSize,
    onDismiss: () -> Unit,
    onSave: (com.dreameditation.app.data.model.FontSize) -> Unit
) {
    var selectedFontSize by remember { mutableStateOf(currentFontSize) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Font Size") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.dreameditation.app.data.model.FontSize.values().forEach { fontSize ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFontSize = fontSize }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFontSize == fontSize,
                            onClick = { selectedFontSize = fontSize }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = fontSize.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedFontSize) },
                enabled = selectedFontSize != currentFontSize
            ) { Text(stringResource(id = R.string.language_dialog_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.language_dialog_cancel)) }
        }
    )
}
