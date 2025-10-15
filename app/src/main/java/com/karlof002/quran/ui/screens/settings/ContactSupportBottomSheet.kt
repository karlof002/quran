package com.karlof002.quran.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportBottomSheet(
    sheetState: SheetState,
    context: Context,
    scope: CoroutineScope,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                            sheetState.hide()
                            onDismiss()
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
                        sheetState.hide()
                        onDismiss()
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
                            sheetState.hide()
                            onDismiss()
                        }
                    } catch (_: Exception) {
                        // Handle if no email app is available
                    }
                }
            )
        }
    }
}

