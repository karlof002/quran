package com.karlof002.quran.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShareAppItem() {
    val context = LocalContext.current

    SettingsClickableItem(
        title = "Share App",
        subtitle = "Share this app with friends and family",
        icon = Icons.Default.Share,
        onClick = { shareApp(context) },
        iconColor = MaterialTheme.colorScheme.primary,
        iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    )
}

private fun shareApp(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this Quran app")
        putExtra(
            Intent.EXTRA_TEXT,
            "I'm using this amazing Quran app. Download it here: https://play.google.com/store/apps/details?id=com.karlof002.quran"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share App via"))
}
