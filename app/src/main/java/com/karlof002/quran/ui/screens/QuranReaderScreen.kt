package com.karlof002.quran.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.data.models.Bookmark
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.BookmarkViewModel
import com.karlof002.quran.ui.viewmodel.SettingsViewModel
import com.karlof002.quran.ui.utils.calculateScaleFactor
import com.karlof002.quran.ui.screens.reader.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuranReaderScreen(
    startPage: Int,
    endPage: Int,
    title: String,
    verses: Int = 0,
    juzNumber: Int = 0,
    onBackClick: () -> Unit,
    viewModel: BookmarkViewModel = viewModel(),
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
) {
    // State for showing/hiding the top bar
    var isTopBarVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Get settings view model to access font size settings
    val settingsViewModel: SettingsViewModel = viewModel()
    val isDarkMode by settingsViewModel.isDarkMode.observeAsState(false)
    val fontSizeValue by settingsViewModel.fontSize.observeAsState(16)
    val infoTextSizeValue by settingsViewModel.infoTextSize.observeAsState(14)

    // Convert to Int for use
    val fontSize = fontSizeValue.toInt()
    val infoTextSize = infoTextSizeValue.toInt()

    // Calculate image scale factor based on font size
    val imageScaleFactor = calculateScaleFactor(fontSize)

    // Local state for dual page mode (not saved)
    val isTablet = windowSize != com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
    var dualPageMode by remember { mutableStateOf(true) }
    val showDualPages = isTablet && dualPageMode

    // Pager state - adjust for dual page mode
    val totalPages = if (showDualPages) 302 else 604

    // Calculate initial page index based on startPage
    val initialPageIndex = if (showDualPages) {
        if (startPage % 2 == 1) {
            (startPage - 1) / 2
        } else {
            (startPage - 2) / 2
        }
    } else {
        startPage - 1
    }

    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { totalPages }
    )

    // Track the previous mode to detect actual changes (not initial load)
    var previousDualPageMode by remember { mutableStateOf<Boolean?>(null) }

    // Handle mode switching - recalculate page index when switching between single/dual page modes
    LaunchedEffect(showDualPages) {
        // Skip on first run (initial load)
        if (previousDualPageMode == null) {
            previousDualPageMode = showDualPages
            return@LaunchedEffect
        }

        // Only recalculate if mode actually changed
        if (previousDualPageMode != showDualPages) {
            val newPageIndex = if (previousDualPageMode == true) {
                // Switching FROM dual page mode TO single page mode
                (pagerState.currentPage * 2) + 1
            } else {
                // Switching FROM single page mode TO dual page mode
                pagerState.currentPage / 2
            }

            // Scroll to the correct page index for the new mode
            if (newPageIndex < pagerState.pageCount) {
                pagerState.scrollToPage(newPageIndex)
            }

            previousDualPageMode = showDualPages
        }
    }

    // Calculate current page number for display (1-based)
    val currentPage = if (showDualPages) {
        (pagerState.currentPage * 2) + 1
    } else {
        pagerState.currentPage + 1
    }

    val currentRightPage = if (showDualPages) {
        (pagerState.currentPage * 2) + 2
    } else {
        null
    }

    // State to hold the surah info for the current page
    var currentSurahInfo by remember { mutableStateOf<Surah?>(null) }
    var currentRightSurahInfo by remember { mutableStateOf<Surah?>(null) }
    var isDatabaseReady by remember { mutableStateOf(false) }

    // Store repository and all data
    var repository by remember { mutableStateOf<com.karlof002.quran.data.repository.QuranRepository?>(null) }
    var allSurahs by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var allJuz by remember { mutableStateOf<List<com.karlof002.quran.data.models.Juz>>(emptyList()) }

    // Initialize database once when screen loads
    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("QuranReader", "Initializing database...")
            val database = com.karlof002.quran.data.database.QuranDatabase.getDatabase(context)
            val repo = com.karlof002.quran.data.repository.QuranRepository(
                database.surahDao(),
                database.juzDao(),
                database.ayahDao(),
                database.bookmarkDao(),
                database.settingsDao(),
                context
            )

            // Initialize data and wait for it to complete
            repo.initializeData()

            // Load all surahs and juz from database
            val surahs = database.surahDao().getAllSurahs().value ?: emptyList()
            val surahsList = surahs.ifEmpty {
                (1..114).mapNotNull { id ->
                    try {
                        database.surahDao().getSurahById(id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            val juzList = (1..30).mapNotNull { id ->
                try {
                    database.juzDao().getJuzById(id)
                } catch (e: Exception) {
                    null
                }
            }

            repository = repo
            allSurahs = surahsList
            allJuz = juzList
            isDatabaseReady = true
            android.util.Log.d("QuranReader", "Database initialized successfully with ${surahsList.size} surahs and ${juzList.size} juz")
        } catch (e: Exception) {
            android.util.Log.e("QuranReader", "Error initializing database", e)
        }
    }

    // Fetch surah info when page changes, but only after database is ready
    LaunchedEffect(currentPage, currentRightPage, isDatabaseReady, allSurahs) {
        if (!isDatabaseReady || allSurahs.isEmpty()) {
            android.util.Log.d("QuranReader", "Database not ready yet, skipping query for page $currentPage")
            return@LaunchedEffect
        }

        try {
            android.util.Log.d("QuranReader", "Fetching surah for page $currentPage")

            // Find surah for current page from database data
            val surahForCurrentPage = findSurahForPage(allSurahs, currentPage)
            if (surahForCurrentPage != null) {
                currentSurahInfo = surahForCurrentPage
                android.util.Log.d("QuranReader", "Page $currentPage -> Surah: ${surahForCurrentPage.arabicName} (${surahForCurrentPage.verses} آيات) - الجزء ${surahForCurrentPage.juz}")
            } else {
                android.util.Log.w("QuranReader", "No surah found for page $currentPage")
                currentSurahInfo = null
            }

            // Find surah for right page in dual page mode
            if (showDualPages && currentRightPage != null && currentRightPage <= 604) {
                val surahForRightPage = findSurahForPage(allSurahs, currentRightPage)
                if (surahForRightPage != null) {
                    currentRightSurahInfo = surahForRightPage
                    android.util.Log.d("QuranReader", "Page $currentRightPage -> Surah: ${surahForRightPage.arabicName} (${surahForRightPage.verses} آيات) - الجزء ${surahForRightPage.juz}")
                } else {
                    currentRightSurahInfo = null
                }
            } else {
                currentRightSurahInfo = null
            }
        } catch (e: Exception) {
            android.util.Log.e("QuranReader", "Error fetching surah for page $currentPage: ${e.message}", e)
        }
    }

    // Track bookmark state for current page
    var isBookmarked by remember { mutableStateOf(false) }

    // Check if current page is bookmarked
    LaunchedEffect(currentPage) {
        try {
            isBookmarked = viewModel.isPageBookmarked(currentPage)
        } catch (e: Exception) {
            android.util.Log.e("QuranReader", "Error checking bookmark status", e)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fullscreen page viewer with RTL scrolling
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true
        ) { index ->
            if (showDualPages) {
                // Dual page mode: show two pages side by side in RTL layout
                val leftPageNumber = (index * 2) + 2
                val rightPageNumber = (index * 2) + 1

                DualPageView(
                    leftPageNumber = leftPageNumber,
                    rightPageNumber = rightPageNumber,
                    isDarkMode = isDarkMode,
                    imageScaleFactor = imageScaleFactor,
                    allSurahs = allSurahs,
                    infoTextSize = infoTextSize,
                    onImageClick = { isTopBarVisible = !isTopBarVisible }
                )
            } else {
                // Single page mode
                val pageNumber = index + 1

                SinglePageView(
                    pageNumber = pageNumber,
                    isDarkMode = isDarkMode,
                    imageScaleFactor = imageScaleFactor,
                    currentSurahInfo = currentSurahInfo,
                    allSurahs = allSurahs,
                    infoTextSize = infoTextSize,
                    onImageClick = { isTopBarVisible = !isTopBarVisible }
                )
            }
        }

        // Animated Top Bar
        AnimatedVisibility(
            visible = isTopBarVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ReaderTopBar(
                currentPage = currentPage,
                isBookmarked = isBookmarked,
                isTablet = isTablet,
                showDualPages = showDualPages,
                onBackClick = onBackClick,
                onTogglePageMode = {
                    scope.launch {
                        dualPageMode = !dualPageMode
                    }
                },
                onToggleBookmark = {
                    scope.launch {
                        if (isBookmarked) {
                            viewModel.deleteBookmarkByPage(currentPage)
                        } else {
                            val surahName = currentSurahInfo?.arabicName ?: "غير معروف"
                            val surahId = currentSurahInfo?.id ?: 0

                            viewModel.addBookmark(
                                Bookmark(
                                    pageNumber = currentPage,
                                    surahName = surahName,
                                    surahId = surahId
                                )
                            )
                        }
                        isBookmarked = !isBookmarked
                    }
                }
            )
        }
    }
}
