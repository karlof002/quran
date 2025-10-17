package com.karlof002.quran.ui.screens.search

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karlof002.quran.data.models.Surah

@Composable
fun SearchResultsList(
    results: List<SearchResultItem>,
    surahsList: List<Surah>,
    onResultClick: (SearchResultItem) -> Unit
) {
    if (results.isEmpty()) {
        NoResultsContent()
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item(key = "results_header") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .animateItem(
                        fadeInSpec = tween(CONTENT_ENTER_DURATION),
                        fadeOutSpec = tween(CONTENT_EXIT_DURATION),
                        placementSpec = tween(300, easing = EaseInOutCubic)
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${results.size} Result${if (results.size > 1) "s" else ""} Found",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(
            items = results,
            key = { result ->
                when (result) {
                    is SearchResultItem.SurahResult -> "surah_${result.surah.id}"
                    is SearchResultItem.JuzResult -> "juz_${result.juzNumber}"
                    is SearchResultItem.PageResult -> "page_${result.pageNumber}"
                }
            }
        ) { result ->
            Box(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(CONTENT_ENTER_DURATION),
                    fadeOutSpec = tween(CONTENT_EXIT_DURATION),
                    placementSpec = tween(300, easing = EaseInOutCubic)
                )
            ) {
                when (result) {
                    is SearchResultItem.SurahResult -> {
                        SurahSearchResultItem(
                            surah = result.surah,
                            onClick = { onResultClick(result) }
                        )
                    }
                    is SearchResultItem.JuzResult -> {
                        JuzSearchResultItem(
                            juzNumber = result.juzNumber,
                            startPage = result.startPage,
                            endPage = result.endPage,
                            onClick = { onResultClick(result) }
                        )
                    }
                    is SearchResultItem.PageResult -> {
                        PageSearchResultItem(
                            pageNumber = result.pageNumber,
                            onClick = { onResultClick(result) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoResultsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No results found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try searching for a different Surah, Juz, or Page",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
