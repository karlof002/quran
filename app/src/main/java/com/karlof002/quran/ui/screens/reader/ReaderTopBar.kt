package com.karlof002.quran.ui.screens.reader

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReaderTopBar(
    currentPage: Int,
    isBookmarked: Boolean,
    isTablet: Boolean,
    showDualPages: Boolean,
    onBackClick: () -> Unit,
    onTogglePageMode: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Page $currentPage of 604",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Dual/Single page toggle button (only show on tablets)
            if (isTablet) {
                IconButton(onClick = onTogglePageMode) {
                    Icon(
                        imageVector = if (showDualPages) Icons.AutoMirrored.Filled.MenuBook else Icons.Filled.Book,
                        contentDescription = if (showDualPages) "Switch to Single Page" else "Switch to Dual Page",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Bookmark button
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                    tint = if (isBookmarked) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

