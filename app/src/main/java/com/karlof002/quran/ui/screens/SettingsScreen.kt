package com.karlof002.quran.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.ui.viewmodel.SettingsViewModel
import androidx.core.net.toUri
import com.karlof002.quran.ui.components.UpdateAvailableDialog
import com.karlof002.quran.ui.utils.UpdateInfo
import com.karlof002.quran.ui.screens.settings.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT,
    onNavigateToDonation: () -> Unit = {}
) {
    val isDarkMode by viewModel.isDarkMode.observeAsState(false)
    val fontSize by viewModel.fontSize.observeAsState(16f)
    val infoTextSize by viewModel.infoTextSize.observeAsState(14f)
    val context = LocalContext.current

    var showAboutDialog by remember { mutableStateOf(false) }
    var showContactSheet by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    val contactSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
                item { SettingsSectionHeader(title = "DISPLAY") }
                item { Spacer(modifier = Modifier.height(4.dp)) }
                item {
                    SettingsSwitchItem(
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme",
                        icon = Icons.Default.DarkMode,
                        isChecked = isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                }
                item { Spacer(modifier = Modifier.height(1.dp)) }
                item {
                    SettingsSliderItem(
                        title = "Font Size",
                        subtitle = "Adjust text size for better readability",
                        icon = Icons.Default.FormatSize,
                        value = fontSize,
                        onValueChange = { viewModel.setFontSize(it) },
                        valueRange = 12f..24f,
                        steps = 23
                    )
                }
                item { Spacer(modifier = Modifier.height(1.dp)) }
                item {
                    SettingsSliderItem(
                        title = "Info Text Size",
                        subtitle = "Adjust info text size",
                        icon = Icons.Default.TextFields,
                        value = infoTextSize,
                        onValueChange = { viewModel.setInfoTextSize(it) },
                        valueRange = 10f..18f,
                        steps = 15
                    )
                }

                // App Info Section
                item { Spacer(modifier = Modifier.height(32.dp)) }
                item { SettingsSectionHeader(title = "APP INFO") }
                item { Spacer(modifier = Modifier.height(4.dp)) }
                item {
                    SettingsClickableItem(
                        title = "About App",
                        subtitle = "Learn more about this app",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                }
                item { Spacer(modifier = Modifier.height(1.dp)) }
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

                // More Apps Section
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { SettingsSectionHeader(title = "MORE APPS") }
                item { Spacer(modifier = Modifier.height(4.dp)) }
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

                // Support Section
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { SettingsSectionHeader(title = "SUPPORT") }
                item { Spacer(modifier = Modifier.height(4.dp)) }
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
                item { Spacer(modifier = Modifier.height(1.dp)) }
                item {
                    SettingsClickableItem(
                        title = "Contact Support",
                        subtitle = "Get in touch with us",
                        icon = Icons.Default.Email,
                        onClick = { showContactSheet = true }
                    )
                }
                item { Spacer(modifier = Modifier.height(1.dp)) }

                // Donate Card
                item { DonateCard(onClick = onNavigateToDonation) }

                // Bottom padding
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // Dialogs and Bottom Sheets
    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    if (showContactSheet) {
        ContactSupportBottomSheet(
            sheetState = contactSheetState,
            context = context,
            scope = scope,
            onDismiss = { showContactSheet = false }
        )
    }

    if (showUpdateDialog) {
        UpdateAvailableDialog(
            updateInfo = UpdateInfo(
                isUpdateAvailable = true,
                isImmediateUpdateAllowed = true,
                isFlexibleUpdateAllowed = true
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
