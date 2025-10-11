package com.karlof002.quran.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.ui.viewmodel.SettingsViewModel
import com.karlof002.quran.ui.utils.scaledDp
import com.karlof002.quran.ui.utils.scaledSp
import androidx.core.net.toUri
import com.karlof002.quran.ui.components.UpdateAvailableDialog
import com.karlof002.quran.ui.components.UpdateInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
) {
    val isDarkMode by viewModel.isDarkMode.observeAsState(false)
    val fontSize by viewModel.fontSize.observeAsState(16)
    val context = LocalContext.current

    var showAboutDialog by remember { mutableStateOf(false) }
    var showContactSheet by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    val contactSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Check if device is tablet
    val isTablet = windowSize != com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT

    // Adaptive padding based on screen size
    val horizontalPadding = when (windowSize) {
        com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT -> 0.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.MEDIUM -> 16.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.EXPANDED -> 32.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Center content with max width like homepage
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Display Section
                item {
                    SettingsSectionHeader(title = "DISPLAY")
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    SettingsSwitchItem(
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme",
                        icon = Icons.Default.DarkMode,
                        isChecked = isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                item {
                    SettingsSliderItem(
                        title = "Font Size",
                        subtitle = "Adjust text size for better readability",
                        icon = Icons.Default.FormatSize,
                        value = fontSize.toFloat(),
                        onValueChange = { viewModel.setFontSize(it.toInt()) },
                        valueRange = 12f..24f,
                        steps = 11
                    )
                }


                // Spacer between sections
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // App Info Section
                item {
                    SettingsSectionHeader(title = "APP INFO")
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    SettingsClickableItem(
                        title = "About App",
                        subtitle = "Learn more about this app",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                item {
                    SettingsClickableItem(
                        title = "Rate App",
                        subtitle = "Help us improve by rating the app",
                        icon = Icons.Default.Star,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/details?id=com.karlof002.quran".toUri())
                            context.startActivity(intent)
                        }
                    )
                }


                // Spacer between subsections
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // More Apps Section
                item {
                    SettingsSectionHeader(title = "MORE APPS")
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    SettingsClickableItem(
                        title = "Other Apps",
                        subtitle = "Check out our other apps",
                        icon = Icons.Default.Apps,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/dev?id=4775249914643092447".toUri())
                            context.startActivity(intent)
                        }
                    )
                }

                // Spacer between subsections
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Support Section
                item {
                    SettingsSectionHeader(title = "SUPPORT")
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    SettingsClickableItem(
                        title = "Privacy Policy",
                        subtitle = "Read our privacy policy",
                        icon = Icons.Default.PrivacyTip,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://quran.abd-dev.at/privacy".toUri())
                            context.startActivity(intent)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                item {
                    SettingsClickableItem(
                        title = "Contact Support",
                        subtitle = "Get in touch with us",
                        icon = Icons.Default.Email,
                        onClick = { showContactSheet = true }
                    )
                }

                // Bottom padding
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Quran Al-Kareem",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A beautiful and modern Quran reading app designed to help you connect with the Holy Quran.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2025 Quran Al-Kareem",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showAboutDialog = false }
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Contact Support Bottom Sheet
    if (showContactSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContactSheet = false },
            sheetState = contactSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Contact Support",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Contact Support",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "How would you like to contact us?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email Option
                ContactOption(
                    icon = Icons.Default.Email,
                    title = "Send Email",
                    subtitle = "hello@abd-dev.at",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:hello@abd-dev.at".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Quran Al-Kareem Support")
                        }
                        try {
                            context.startActivity(intent)
                            scope.launch {
                                contactSheetState.hide()
                                showContactSheet = false
                            }
                        } catch (_: Exception) {
                            // Handle if no email app is available
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Website Option
                ContactOption(
                    icon = Icons.Default.Language,
                    title = "Visit Website",
                    subtitle = "quran.abd-dev.at",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://quran.abd-dev.at".toUri())
                        context.startActivity(intent)
                        scope.launch {
                            contactSheetState.hide()
                            showContactSheet = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Report Issue Option
                ContactOption(
                    icon = Icons.Default.BugReport,
                    title = "Report Issue",
                    subtitle = "Report bugs or problems",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:hello@abd-dev.at".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Quran Al-Kareem - Issue Report")
                            putExtra(Intent.EXTRA_TEXT, "Please describe the issue:\n\n")
                        }
                        try {
                            context.startActivity(intent)
                            scope.launch {
                                contactSheetState.hide()
                                showContactSheet = false
                            }
                        } catch (_: Exception) {
                            // Handle if no email app is available
                        }
                    }
                )
            }
        }
    }

    // Update Available Dialog (Demo)
    if (showUpdateDialog) {
        UpdateAvailableDialog(
            updateInfo = UpdateInfo(
                version = "1.1.0",
                description = "New features and improvements",
                url = "https://play.google.com/store/apps/details?id=com.karlof002.quran"
            ),
            onDismiss = { showUpdateDialog = false },
            onUpdate = {
                val intent = Intent(Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=com.karlof002.quran".toUri())
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        fontSize = 12.sp,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${value.toInt()}sp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(start = 68.dp, top = 16.dp, end = 0.dp)
        )
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go to $title",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ContactOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}