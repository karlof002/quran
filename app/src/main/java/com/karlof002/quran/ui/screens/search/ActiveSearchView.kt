package com.karlof002.quran.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.karlof002.quran.data.database.QuranDatabase
import com.karlof002.quran.data.models.SearchHistory
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSearchView(
    viewModel: HomeViewModel,
    onClose: () -> Unit,
    onSurahClick: (Int, Int, String, Int, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val allSurahs by viewModel.allSurahs.observeAsState(emptyList())
    val allJuz by viewModel.allJuz.observeAsState(emptyList())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var surahsList by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var searchHistory by remember { mutableStateOf<List<SearchHistory>>(emptyList()) }
    val database = remember { QuranDatabase.getDatabase(context) }

    // Initialize data
    LaunchedEffect(Unit) {
        try {
            val surahs = database.surahDao().getAllSurahs().value ?: emptyList()
            surahsList = surahs.ifEmpty {
                (1..114).mapNotNull { id ->
                    try {
                        database.surahDao().getSurahById(id)
                    } catch (_: Exception) {
                        null
                    }
                }
            }
            searchHistory = database.searchHistoryDao().getRecentSearchesSync()
        } catch (_: Exception) {
            // Silently handle error
        }

        // Auto-focus with delay
        delay(100)
        focusRequester.requestFocus()
    }

    // Memoized search results
    val searchResults by remember {
        derivedStateOf {
            performSearch(searchQuery, allSurahs, allJuz, surahsList)
        }
    }

    fun saveSearchHistory(query: String, resultType: String) {
        scope.launch {
            try {
                database.searchHistoryDao().insertSearch(
                    SearchHistory(query = query, resultType = resultType)
                )
                searchHistory = database.searchHistoryDao().getRecentSearchesSync()
            } catch (_: Exception) {
                // Silently handle error
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.graphicsLayer {
                            transformOrigin = TransformOrigin.Center
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                "Search Surah, Juz, or Page...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )

                    // Clear button
                    AnimatedVisibility(
                        visible = searchQuery.isNotEmpty(),
                        enter = fadeIn(tween(150)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(150, easing = EaseOutCubic)
                        ),
                        exit = fadeOut(tween(100)) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(100, easing = EaseInCubic)
                        )
                    ) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Content area with fade through transition
            AnimatedContent(
                targetState = searchQuery.isNotEmpty(),
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = CONTENT_ENTER_DURATION,
                            delayMillis = CONTENT_EXIT_DURATION / 2,
                            easing = LinearOutSlowInEasing
                        )
                    ) + slideInVertically(
                        initialOffsetY = { it / 20 },
                        animationSpec = tween(
                            durationMillis = CONTENT_ENTER_DURATION,
                            delayMillis = CONTENT_EXIT_DURATION / 2,
                            easing = EaseOutCubic
                        )
                    ) togetherWith fadeOut(
                        animationSpec = tween(
                            durationMillis = CONTENT_EXIT_DURATION,
                            easing = FastOutLinearInEasing
                        )
                    ) + slideOutVertically(
                        targetOffsetY = { -it / 20 },
                        animationSpec = tween(
                            durationMillis = CONTENT_EXIT_DURATION,
                            easing = EaseInCubic
                        )
                    )
                },
                label = "search content"
            ) { hasQuery ->
                if (hasQuery) {
                    SearchResultsList(
                        results = searchResults,
                        surahsList = surahsList,
                        onResultClick = { result ->
                            when (result) {
                                is SearchResultItem.SurahResult -> {
                                    saveSearchHistory(searchQuery, "Surah")
                                    onSurahClick(
                                        result.surah.id,
                                        result.surah.startPage,
                                        result.surah.arabicName,
                                        result.surah.verses,
                                        result.surah.juz
                                    )
                                }
                                is SearchResultItem.JuzResult -> {
                                    saveSearchHistory(searchQuery, "Juz")
                                    onSurahClick(0, result.startPage, "Juz ${result.juzNumber}", 0, result.juzNumber)
                                }
                                is SearchResultItem.PageResult -> {
                                    saveSearchHistory(searchQuery, "Page")
                                    val surahForPage = findSurahForPage(surahsList, result.pageNumber)
                                    onSurahClick(
                                        surahForPage?.id ?: 0,
                                        result.pageNumber,
                                        surahForPage?.arabicName ?: "Page ${result.pageNumber}",
                                        surahForPage?.verses ?: 0,
                                        surahForPage?.juz ?: 0
                                    )
                                }
                            }
                        }
                    )
                } else {
                    SearchHistoryAndSuggestions(
                        searchHistory = searchHistory,
                        onHistoryItemClick = { historyItem ->
                            searchQuery = historyItem.query
                        },
                        onDeleteHistoryItem = { id ->
                            scope.launch {
                                try {
                                    database.searchHistoryDao().deleteSearch(id)
                                    searchHistory = database.searchHistoryDao().getRecentSearchesSync()
                                } catch (_: Exception) {
                                    // Silently handle error
                                }
                            }
                        },
                        onClearAllHistory = {
                            scope.launch {
                                try {
                                    database.searchHistoryDao().clearAllHistory()
                                    searchHistory = emptyList()
                                } catch (_: Exception) {
                                    // Silently handle error
                                }
                            }
                        },
                        onSuggestionClick = { suggestion ->
                            searchQuery = suggestion
                        }
                    )
                }
            }
        }
    }
}
