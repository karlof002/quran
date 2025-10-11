package com.karlof002.quran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.HomeViewModel

// Theme-aware colors for badges that should stay the same in both themes
private val MeccanBg = Color(0xFFFEF3C7)
private val MeccanText = Color(0xFF92400E)
private val MedinanBg = Color(0xFFDCFCE7)
private val MedinanText = Color(0xFF166534)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: HomeViewModel = viewModel(),
    onSurahClick: (Int, Int, String, Int, Int) -> Unit = { _, _, _, _, _ -> },
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
) {
    var searchQuery by remember { mutableStateOf("") }
    val allSurahs by viewModel.allSurahs.observeAsState(emptyList())
    val allJuz by viewModel.allJuz.observeAsState(emptyList())

    // Adaptive padding based on screen size
    val horizontalPadding = when (windowSize) {
        com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT -> 0.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.MEDIUM -> 16.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.EXPANDED -> 32.dp
    }

    // Filter results based on search query
    val searchResults by remember {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                val query = searchQuery.trim()
                val results = mutableListOf<SearchResultItem>()

                // Search by Juz number
                if (query.toIntOrNull() != null) {
                    val juzNumber = query.toInt()
                    if (juzNumber in 1..30) {
                        val juz = allJuz.find { it.id == juzNumber }
                        juz?.let {
                            val pageRange = com.karlof002.quran.data.models.JuzPageMappings.getPageRangeForJuz(it.id)
                            if (pageRange != null) {
                                results.add(
                                    SearchResultItem.JuzResult(
                                        juzNumber = it.id,
                                        startPage = pageRange.startPage,
                                        endPage = pageRange.endPage
                                    )
                                )
                            }
                        }
                    }

                    // Search by page number
                    if (juzNumber in 1..604) {
                        results.add(
                            SearchResultItem.PageResult(
                                pageNumber = juzNumber
                            )
                        )
                    }
                }

                // Search by Surah name (Arabic or transliteration)
                allSurahs.forEach { surah ->
                    if (surah.arabicName.contains(query, ignoreCase = true) ||
                        surah.transliteration.contains(query, ignoreCase = true) ||
                        surah.translation.contains(query, ignoreCase = true) ||
                        surah.id.toString() == query
                    ) {
                        results.add(SearchResultItem.SurahResult(surah))
                    }
                }

                results
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = horizontalPadding)
            ) {
                // Top App Bar with search
                TopAppBar(
                    title = {
                        Text(
                            "Search Quran",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text("Search by Surah, Juz, or Page number...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = horizontalPadding)
        ) {
            // Search Results
            if (searchQuery.isBlank()) {
                // Empty state - show instructions
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Search for Surahs, Juz, or Pages",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try:\n• Surah name (e.g., \"Fatiha\" or \"الفاتحة\")\n• Juz number (1-30)\n• Page number (1-604)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (searchResults.isEmpty()) {
                // No results state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try a different search term",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                // Results list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults.size) { index ->
                        when (val result = searchResults[index]) {
                            is SearchResultItem.SurahResult -> {
                                SurahSearchResultItem(
                                    surah = result.surah,
                                    onClick = {
                                        onSurahClick(
                                            result.surah.id,
                                            result.surah.startPage,
                                            result.surah.arabicName,
                                            result.surah.verses,
                                            result.surah.juz
                                        )
                                    }
                                )
                            }
                            is SearchResultItem.JuzResult -> {
                                JuzSearchResultItem(
                                    juzNumber = result.juzNumber,
                                    startPage = result.startPage,
                                    endPage = result.endPage,
                                    onClick = {
                                        onSurahClick(0, result.startPage, "Juz ${result.juzNumber}", 0, result.juzNumber)
                                    }
                                )
                            }
                            is SearchResultItem.PageResult -> {
                                PageSearchResultItem(
                                    pageNumber = result.pageNumber,
                                    onClick = {
                                        onSurahClick(0, result.pageNumber, "Page ${result.pageNumber}", 0, 0)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Search result types
sealed class SearchResultItem {
    data class SurahResult(val surah: Surah) : SearchResultItem()
    data class JuzResult(val juzNumber: Int, val startPage: Int, val endPage: Int) : SearchResultItem()
    data class PageResult(val pageNumber: Int) : SearchResultItem()
}

@Composable
fun SurahSearchResultItem(
    surah: Surah,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Surah Number Badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = surah.id.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Surah Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "سورة ${surah.arabicName}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Surah ${surah.transliteration}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            // Meta Information
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (surah.revelation) {
                                "Meccan" -> MeccanBg
                                else -> MedinanBg
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = surah.revelation,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (surah.revelation) {
                            "Meccan" -> MeccanText
                            else -> MedinanText
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "• ${surah.verses} Verses",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun JuzSearchResultItem(
    juzNumber: Int,
    startPage: Int,
    endPage: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = juzNumber.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Juz $juzNumber",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "Pages $startPage - $endPage",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun PageSearchResultItem(
    pageNumber: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pageNumber.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Page $pageNumber",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "Go to page $pageNumber of the Quran",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
