package com.karlof002.quran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "About App",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Name & Version
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = "Quran",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "Quran Al-Kareem",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Privacy-Focused Features Section
                item {
                    Text(
                        text = "Why Choose Us?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // No Ads Feature
                item {
                    FeatureItem(
                        icon = Icons.Default.Block,
                        iconColor = Color(0xFF43A047),
                        iconBackgroundColor = Color(0xFF43A047).copy(alpha = 0.12f),
                        title = "100% Ad-Free",
                        description = "No advertisements, no distractions. Focus entirely on reading the Holy Quran in peace."
                    )
                }

                // Privacy Feature
                item {
                    FeatureItem(
                        icon = Icons.Default.Lock,
                        iconColor = Color(0xFF5E35B1),
                        iconBackgroundColor = Color(0xFF5E35B1).copy(alpha = 0.12f),
                        title = "Complete Privacy",
                        description = "Your reading is completely private. We don't collect, track, or share any of your personal data."
                    )
                }

                // Offline Feature
                item {
                    FeatureItem(
                        icon = Icons.Default.CloudOff,
                        iconColor = Color(0xFF1E88E5),
                        iconBackgroundColor = Color(0xFF1E88E5).copy(alpha = 0.12f),
                        title = "Works Offline",
                        description = "No internet connection required. Read the Quran anytime, anywhere, even without Wi-Fi or mobile data."
                    )
                }

                // Free Forever
                item {
                    FeatureItem(
                        icon = Icons.Default.Favorite,
                        iconColor = Color(0xFFEC407A),
                        iconBackgroundColor = Color(0xFFEC407A).copy(alpha = 0.12f),
                        title = "Free Forever",
                        description = "This app is and will always remain completely free. No subscriptions, no hidden costs."
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // About Section
                item {
                    Text(
                        text = "About This App",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Quran Al-Kareem is a simple, elegant, and respectful way to read the Holy Quran on your device.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                        Text(
                            text = "Our mission is to make the Quran accessible to everyone, everywhere, while respecting your privacy and providing a distraction-free reading experience.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "May Allah accept this effort and make it beneficial for all Muslims. Ameen. 🤲",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    iconColor: Color,
    iconBackgroundColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    iconBackgroundColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                fontSize = 14.sp
            )
        }
    }
}
