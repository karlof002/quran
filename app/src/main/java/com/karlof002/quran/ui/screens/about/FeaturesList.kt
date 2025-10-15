package com.karlof002.quran.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeaturesList() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // No Ads Feature
        FeatureItem(
            icon = Icons.Default.Block,
            iconColor = Color(0xFF43A047),
            iconBackgroundColor = Color(0xFF43A047).copy(alpha = 0.12f),
            title = "100% Ad-Free",
            description = "No advertisements, no distractions. Focus entirely on reading the Holy Quran in peace."
        )

        // Privacy Feature
        FeatureItem(
            icon = Icons.Default.Lock,
            iconColor = Color(0xFF5E35B1),
            iconBackgroundColor = Color(0xFF5E35B1).copy(alpha = 0.12f),
            title = "Complete Privacy",
            description = "Your reading is completely private. We don't collect, track, or share any of your personal data."
        )

        // Offline Feature
        FeatureItem(
            icon = Icons.Default.CloudOff,
            iconColor = Color(0xFF1E88E5),
            iconBackgroundColor = Color(0xFF1E88E5).copy(alpha = 0.12f),
            title = "Works Offline",
            description = "No internet connection required. Read the Quran anytime, anywhere, even without Wi-Fi or mobile data."
        )

        // Free Forever
        FeatureItem(
            icon = Icons.Default.Favorite,
            iconColor = Color(0xFFEC407A),
            iconBackgroundColor = Color(0xFFEC407A).copy(alpha = 0.12f),
            title = "Free Forever",
            description = "This app is and will always remain completely free. No subscriptions, no hidden costs."
        )
    }
}

