package com.karlof002.quran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.HomeViewModel
import com.karlof002.quran.ui.screens.search.*

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
    val context = LocalContext.current

    // Load all surahs for looking up page data
    var surahsList by remember { mutableStateOf<List<Surah>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val database = com.karlof002.quran.data.database.QuranDatabase.getDatabase(context)
            val surahs = database.surahDao().getAllSurahs().value ?: emptyList()
            surahsList = surahs.ifEmpty {
                (1..114).mapNotNull { id ->
                    try {
                        database.surahDao().getSurahById(id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SearchScreen", "Error loading surahs", e)
        }
    }

    // Adaptive padding based on screen size
    val horizontalPadding = when (windowSize) {
        com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT -> 0.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.MEDIUM -> 16.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.EXPANDED -> 32.dp
    }

    // Filter results based on search query
    val searchResults by remember {
        derivedStateOf {
            performSearch(searchQuery, allSurahs, allJuz, surahsList)
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
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearClick = { searchQuery = "" }
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
            when {
                searchQuery.isBlank() -> SearchEmptyState()
                searchResults.isEmpty() -> SearchNoResultsState()
                else -> {
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
                                            val surahForPage = findSurahForPage(surahsList, result.pageNumber)
                                            onSurahClick(
                                                surahForPage?.id ?: 0,
                                                result.pageNumber,
                                                surahForPage?.arabicName ?: "Page ${result.pageNumber}",
                                                surahForPage?.verses ?: 0,
                                                surahForPage?.juz ?: 0
                                            )
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
}
