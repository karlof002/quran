package com.karlof002.quran.ui.screens.search

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karlof002.quran.data.models.SearchHistory

@Composable
fun SearchHistoryAndSuggestions(
    searchHistory: List<SearchHistory>,
    onHistoryItemClick: (SearchHistory) -> Unit,
    onDeleteHistoryItem: (Int) -> Unit,
    onClearAllHistory: () -> Unit,
    onSuggestionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        // Search History Section
        if (searchHistory.isNotEmpty()) {
            stickyHeader(key = "history_header") {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onClearAllHistory) {
                            Text("Clear All")
                        }
                    }
                }
            }

            items(
                items = searchHistory,
                key = { it.id }
            ) { historyItem ->
                HistoryListItem(
                    searchHistory = historyItem,
                    onClick = { onHistoryItemClick(historyItem) },
                    onDelete = { onDeleteHistoryItem(historyItem.id) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(CONTENT_ENTER_DURATION),
                        fadeOutSpec = tween(CONTENT_EXIT_DURATION),
                        placementSpec = tween(300, easing = EaseInOutCubic)
                    )
                )
            }

            item(key = "spacer_1") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Suggestions Section
        stickyHeader(key = "suggestions_header") {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Quick Search",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = if (searchHistory.isEmpty()) 16.dp else 0.dp,
                        bottom = 12.dp
                    )
                )
            }
        }

        item(key = "suggestions_list") {
            SuggestionsList(
                onSuggestionClick = onSuggestionClick,
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(CONTENT_ENTER_DURATION),
                    fadeOutSpec = tween(CONTENT_EXIT_DURATION),
                    placementSpec = tween(300, easing = EaseInOutCubic)
                )
            )
        }
    }
}

@Composable
private fun HistoryListItem(
    searchHistory: SearchHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = searchHistory.query,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = searchHistory.resultType,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestionsList(
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        "Al-Fatihah" to "Surah",
        "Al-Baqarah" to "Surah",
        "Ya-Sin" to "Surah",
        "Ar-Rahman" to "Surah",
        "Al-Kahf" to "Surah",
        "Al-Mulk" to "Surah",
        "1" to "Juz",
        "30" to "Juz"
    )

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.forEach { (text, type) ->
            SuggestionChip(
                onClick = { onSuggestionClick(text) },
                label = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (type) {
                            "Surah" -> Icons.Default.Book
                            "Juz" -> Icons.Default.Category
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}
