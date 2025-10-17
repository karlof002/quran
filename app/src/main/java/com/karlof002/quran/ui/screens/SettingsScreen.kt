package com.karlof002.quran.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.karlof002.quran.ui.viewmodel.BookmarkViewModel
import androidx.core.net.toUri
import com.karlof002.quran.ui.components.UpdateAvailableDialog
import com.karlof002.quran.ui.utils.UpdateInfo
import com.karlof002.quran.ui.screens.settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel(),
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT,
    onNavigateToDonation: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val isDarkMode by viewModel.isDarkMode.observeAsState(false)
    val fontSize by viewModel.fontSize.observeAsState(16f)
    val infoTextSize by viewModel.infoTextSize.observeAsState(14f)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showContactSheet by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val contactSheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    // File picker for export
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = bookmarkViewModel.exportBookmarks(context, it)
                result.onSuccess { count ->
                    snackbarMessage = "Successfully exported $count bookmark${if (count != 1) "s" else ""}"
                    snackbarHostState.showSnackbar(snackbarMessage)
                }.onFailure { error ->
                    snackbarMessage = "Export failed: ${error.message}"
                    snackbarHostState.showSnackbar(snackbarMessage)
                }
            }
        }
    }

    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            showImportDialog = true
            scope.launch {
                // We'll handle the actual import in the dialog confirmation
            }
        }
    }

    // Store the selected URI for import
    var selectedImportUri by remember { mutableStateOf<Uri?>(null) }
    val importLauncherWithDialog = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        selectedImportUri = uri
        if (uri != null) {
            showImportDialog = true
        }
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
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
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
                            onCheckedChange = { viewModel.setDarkMode(it) },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }
                    item {
                        SettingsSliderItem(
                            title = "Font Size",
                            subtitle = "Adjust text size for better readability",
                            icon = Icons.Default.FormatSize,
                            value = fontSize,
                            onValueChange = { viewModel.setFontSize(it) },
                            valueRange = 12f..24f,
                            steps = 23,
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }
                    item {
                        SettingsSliderItem(
                            title = "Info Text Size",
                            subtitle = "Adjust info text size",
                            icon = Icons.Default.TextFields,
                            value = infoTextSize,
                            onValueChange = { viewModel.setInfoTextSize(it) },
                            valueRange = 10f..18f,
                            steps = 15,
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }

                    // Data Management Section (Bookmarks)
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item { SettingsSectionHeader(title = "DATA") }
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    item {
                        SettingsClickableItem(
                            title = "Export Bookmarks",
                            subtitle = "Save your bookmarks to a file",
                            icon = Icons.Default.SaveAlt,
                            onClick = { exportLauncher.launch("bookmarks_${getCurrentDate()}.json") },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }
                    item {
                        SettingsClickableItem(
                            title = "Import Bookmarks",
                            subtitle = "Load bookmarks from a file",
                            icon = Icons.Default.FolderOpen,
                            onClick = { importLauncherWithDialog.launch(arrayOf("application/json")) },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }

                    // About & Info Section (consolidated)
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item { SettingsSectionHeader(title = "ABOUT") }
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    item {
                        SettingsClickableItem(
                            title = "About App",
                            subtitle = "Learn more about this app",
                            icon = Icons.Default.Info,
                            onClick = { onNavigateToAbout() },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }
                    item {
                        ShareAppItem()
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
                            },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
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
                            },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }
                    item {
                        CheckForUpdatesItem()
                    }

                    // Support Section (consolidated)
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item { SettingsSectionHeader(title = "HELP & SUPPORT") }
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    item {
                        SettingsClickableItem(
                            title = "Contact Support",
                            subtitle = "Get in touch with us",
                            icon = Icons.Default.Email,
                            onClick = { showContactSheet = true },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
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
                            },
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }
                    item {
                        SettingsClickableItem(
                            title = "Support Us",
                            subtitle = "Help keep this app free for everyone",
                            icon = Icons.Default.Favorite,
                            onClick = onNavigateToDonation,
                            iconColor = MaterialTheme.colorScheme.primary,
                            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }

                    // Bottom padding
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            // SnackbarHost for displaying messages
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Dialogs and Bottom Sheets
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

    if (showImportDialog) {
        ImportBookmarksDialog(
            onDismiss = { showImportDialog = false },
            onConfirm = { uri ->
                uri?.let {
                    scope.launch {
                        val result = bookmarkViewModel.importBookmarks(context, it)
                        result.onSuccess { count ->
                            snackbarMessage = "Successfully imported $count bookmark${if (count != 1) "s" else ""}"
                            snackbarHostState.showSnackbar(snackbarMessage)
                        }.onFailure { error ->
                            snackbarMessage = "Import failed: ${error.message}"
                            snackbarHostState.showSnackbar(snackbarMessage)
                        }
                    }
                }
                showImportDialog = false
            },
            selectedUri = selectedImportUri
        )
    }
}

@Composable
fun ImportBookmarksDialog(
    onDismiss: () -> Unit,
    onConfirm: (Uri?) -> Unit,
    selectedUri: Uri?
) {
    val uriString = selectedUri?.toString() ?: ""
    val fileName = if (uriString.isNotEmpty()) {
        uriString.substringAfterLast("/")
    } else {
        ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Bookmarks") },
        text = {
            Column {
                Text("Selected file: $fileName")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Do you want to import bookmarks from this file?", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedUri) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    return dateFormat.format(Date())
}
