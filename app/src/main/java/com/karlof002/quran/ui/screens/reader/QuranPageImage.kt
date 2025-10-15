package com.karlof002.quran.ui.screens.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun QuranPageImage(
    pageNumber: Int,
    isDarkMode: Boolean = false,
    onImageClick: () -> Unit,
    scaleFactor: Float = 1f
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onImageClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Apply scaling to the image container based on font size preference
        Box(
            modifier = Modifier
                .fillMaxSize(scaleFactor.coerceIn(0.5f, 2.0f)), // Limit scale between 50% and 200%
            contentAlignment = Alignment.Center
        ) {
            // Use Coil with aggressive disk caching for smooth performance
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/images/page_$pageNumber.png")
                    .memoryCacheKey("page_$pageNumber")
                    .diskCacheKey("page_$pageNumber")
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .crossfade(150)
                    .build(),
                contentDescription = "Quran Page $pageNumber",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                colorFilter = if (isDarkMode) androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                    androidx.compose.ui.graphics.ColorMatrix().apply {
                        // Invert colors: black becomes white, transparent stays transparent
                        setToScale(-1f, -1f, -1f, 1f)
                        set(0, 4, 255f)
                        set(1, 4, 255f)
                        set(2, 4, 255f)
                    }
                ) else null,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF059669),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
                error = {
                    // Fallback if image not found
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Page $pageNumber",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MediumText,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Image not found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MediumText
                        )
                    }
                }
            )
        }
    }
}

