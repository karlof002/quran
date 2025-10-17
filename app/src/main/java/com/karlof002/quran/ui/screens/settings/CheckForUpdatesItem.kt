package com.karlof002.quran.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.karlof002.quran.ui.components.NoUpdateDialog
import com.karlof002.quran.ui.components.UpdateAvailableDialog
import com.karlof002.quran.ui.components.UpdateInProgressDialog
import com.karlof002.quran.ui.utils.UpdateManager
import kotlinx.coroutines.launch

@Composable
fun CheckForUpdatesItem() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showCheckingDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showNoUpdateDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<com.karlof002.quran.ui.utils.UpdateInfo?>(null) }

    SettingsClickableItem(
        title = "Check for Updates",
        subtitle = "Manually check for app updates",
        icon = Icons.Default.SystemUpdate,
        onClick = {
            showCheckingDialog = true
            scope.launch {
                val manager = UpdateManager(context)
                val result = manager.checkForUpdates()
                showCheckingDialog = false
                
                if (result.isUpdateAvailable && !result.isUpdateInProgress) {
                    updateInfo = result
                    showUpdateDialog = true
                } else {
                    showNoUpdateDialog = true
                }
            }
        },
        iconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        iconBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    )

    // Dialogs
    if (showCheckingDialog) {
        UpdateInProgressDialog(
            onDismiss = { showCheckingDialog = false }
        )
    }

    if (showUpdateDialog && updateInfo != null) {
        UpdateAvailableDialog(
            updateInfo = updateInfo!!,
            onDismiss = { showUpdateDialog = false },
            onUpdate = {
                // Open Play Store
                val intent = android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=com.karlof002.quran".toUri()
                )
                context.startActivity(intent)
                showUpdateDialog = false
            }
        )
    }

    if (showNoUpdateDialog) {
        NoUpdateDialog(
            onDismiss = { showNoUpdateDialog = false }
        )
    }
}