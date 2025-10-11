package com.karlof002.quran.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.karlof002.quran.data.models.Bookmark
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.BookmarkViewModel
import com.karlof002.quran.ui.viewmodel.SettingsViewModel
import com.karlof002.quran.ui.utils.calculateScaleFactor
import com.karlof002.quran.ui.utils.scaledSp
import kotlinx.coroutines.launch

// Modern Color Palette
private val MediumText = Color(0xFF6B7280)

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
    val fontSize by settingsViewModel.fontSize.observeAsState(16)

    // Calculate image scale factor based on font size
    val imageScaleFactor = calculateScaleFactor(fontSize)

    // Local state for dual page mode (not saved)
    val isTablet = windowSize != com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
    var dualPageMode by remember { mutableStateOf(true) }
    val showDualPages = isTablet && dualPageMode

    // Adaptive padding for tablet screens
    val horizontalPadding = when (windowSize) {
        com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT -> 0.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.MEDIUM -> 32.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.EXPANDED -> 64.dp
    }

    // Pager state - adjust for dual page mode
    // Single page mode: show pages 1-604, one at a time
    // Dual page mode: show pages in pairs (1-2, 3-4, 5-6, etc.), so we need 302 pairs
    val totalPages = if (showDualPages) 302 else 604

    // Detect navigation source based on parameters
    val isFromSurah = verses > 0 && endPage == 604
    val isFromJuz = verses == 0 && endPage != 604

    // Calculate initial page index based on startPage
    val initialPageIndex = if (showDualPages) {
        // For dual mode, we want to show the pair that contains startPage
        // If startPage is odd (1, 3, 5...), it should be on the right
        // If startPage is even (2, 4, 6...), it should be on the left
        // Pages 1-2 are pair 0, pages 3-4 are pair 1, etc.
        if (startPage % 2 == 1) {
            // Odd page (right side): pair index = (startPage - 1) / 2
            (startPage - 1) / 2
        } else {
            // Even page (left side): pair index = (startPage - 2) / 2
            (startPage - 2) / 2
        }
    } else {
        // For single page mode - simply use startPage from QuranRepository.kt
        // This matches exactly what's defined in the repository data
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
                // In RTL, the user was looking at both pages of the pair.
                // We want to land on the LEFT page (higher page number).
                // Pair index `i` corresponds to pages `2*i+1` (right) and `2*i+2` (left).
                // The single-page index for the left page `2*i+2` is `(2*i+2)-1 = 2*i+1`.
                (pagerState.currentPage * 2) + 1
            } else {
                // Switching FROM single page mode TO dual page mode
                // Current pagerState.currentPage is a single page index `i`.
                // This corresponds to page `i+1`.
                // We want to find the dual-page pair index.
                // Pair index for page `p` is `(p-1)/2`.
                // So, for page `i+1`, the pair index is `((i+1)-1)/2 = i/2`.
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
    // Must account for the startPage offset since pager index is relative to the range
    val currentPage = if (showDualPages) {
        // In dual mode, each pager index shows 2 pages
        // Index 0 = pages 1-2, Index 1 = pages 3-4, etc.
        // We show the FIRST page of the pair (odd number: 1, 3, 5, ...)
        (pagerState.currentPage * 2) + 1
    } else {
        // In single mode, for consistency with HorizontalPager calculation
        // HorizontalPager shows pageNumber = index + 1
        // So currentPage should match that
        pagerState.currentPage + 1
    }

    val currentRightPage = if (showDualPages) {
        // The second page of the pair (even number: 2, 4, 6, ...)
        (pagerState.currentPage * 2) + 2
    } else {
        null
    }

    // State to hold the surah info for the current page
    var currentSurahInfo by remember { mutableStateOf<Surah?>(null) }
    var currentRightSurahInfo by remember { mutableStateOf<Surah?>(null) }
    var isDatabaseReady by remember { mutableStateOf(false) }

    // Initialize database once when screen loads
    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("QuranReader", "Initializing database...")
            val database = com.karlof002.quran.data.database.QuranDatabase.getDatabase(context)
            val repository = com.karlof002.quran.data.repository.QuranRepository(
                database.surahDao(),
                database.juzDao(),
                database.ayahDao(),
                database.bookmarkDao()
            )

            // Initialize data and wait for it to complete
            repository.initializeData()

            // Mark database as ready
            isDatabaseReady = true
            android.util.Log.d("QuranReader", "Database initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("QuranReader", "Error initializing database", e)
        }
    }

    // Fetch surah info when page changes, but only after database is ready
    LaunchedEffect(currentPage, currentRightPage, isDatabaseReady) {
        if (!isDatabaseReady) {
            android.util.Log.d("QuranReader", "Database not ready yet, skipping query for page $currentPage")
            return@LaunchedEffect
        }

        try {
            android.util.Log.d("QuranReader", "Fetching surah for page $currentPage")

            // Use hardcoded surah data directly since database queries aren't working reliably
            val allSurahs = getHardcodedSurahs()

            // Find surah for current page
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
            reverseLayout = true  // This makes it scroll RTL - swipe left to go forward
        ) { index ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (showDualPages) {
                    // Dual page mode: show two pages side by side in RTL layout
                    // Index 0 = pages 2 (left) & 1 (right)
                    // Index 1 = pages 4 (left) & 3 (right)
                    val leftPageNumber = (index * 2) + 2  // Even pages: 2, 4, 6, ... (displayed on LEFT)
                    val rightPageNumber = (index * 2) + 1 // Odd pages: 1, 3, 5, ... (displayed on RIGHT)

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Left page (displayed on LEFT - higher page number)
                        if (leftPageNumber <= 604) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                QuranPageImage(
                                    pageNumber = leftPageNumber,
                                    isDarkMode = isDarkMode,
                                    onImageClick = { isTopBarVisible = !isTopBarVisible },
                                    scaleFactor = imageScaleFactor
                                )
                            }
                        }

                        // Small divider between pages
                        Spacer(modifier = Modifier.width(4.dp))

                        // Right page (displayed on RIGHT - lower page number)
                        if (rightPageNumber <= 604) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                QuranPageImage(
                                    pageNumber = rightPageNumber,
                                    isDarkMode = isDarkMode,
                                    onImageClick = { isTopBarVisible = !isTopBarVisible },
                                    scaleFactor = imageScaleFactor
                                )
                            }
                        }
                    }

                    // Bottom info bar - show individual surah info for each page
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side - page number and surah info for the page displayed on LEFT (leftPageNumber)
                        if (leftPageNumber <= 604) {
                            val leftPageSurah = getHardcodedSurahs().let { surahs ->
                                findSurahForPage(surahs, leftPageNumber)
                            }
                            val leftPageJuz = getJuzNumberForPage(leftPageNumber)
                            Text(
                                text = if (leftPageSurah != null) {
                                    "صفحة ${convertToArabicNumbers(leftPageNumber)} - سورة ${leftPageSurah.arabicName} - ${convertToArabicNumbers(leftPageSurah.verses)} آيات - الجزء ${convertToArabicNumbers(leftPageJuz)}"
                                } else {
                                    "صفحة ${convertToArabicNumbers(leftPageNumber)}"
                                },
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        // Small divider
                        Spacer(modifier = Modifier.width(8.dp))

                        // Right side - page number and surah info for the page displayed on RIGHT (rightPageNumber)
                        if (rightPageNumber <= 604) {
                            val rightPageSurah = getHardcodedSurahs().let { surahs ->
                                findSurahForPage(surahs, rightPageNumber)
                            }
                            val rightPageJuz = getJuzNumberForPage(rightPageNumber)
                            Text(
                                text = if (rightPageSurah != null) {
                                    "صفحة ${convertToArabicNumbers(rightPageNumber)} - سورة ${rightPageSurah.arabicName} - ${convertToArabicNumbers(rightPageSurah.verses)} آيات - الجزء ${convertToArabicNumbers(rightPageJuz)}"
                                } else {
                                    "صفحة ${convertToArabicNumbers(rightPageNumber)}"
                                },
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                } else {
                    // Single page mode: each pager index shows one page
                    // Images are 1-indexed (page_1.png, page_2.png, etc.)
                    // Index 0 → page_1.png, Index 1 → page_2.png, etc.
                    val pageNumber = index + 1

                    QuranPageImage(
                        pageNumber = pageNumber,
                        isDarkMode = isDarkMode,
                        onImageClick = { isTopBarVisible = !isTopBarVisible },
                        scaleFactor = imageScaleFactor
                    )

                    // Bottom info bar - surah name and page number together on right
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Surah name and page number on right side
                        if (currentSurahInfo != null) {
                            val pageJuz = getJuzNumberForPage(pageNumber)
                            Text(
                                text = "صفحة ${convertToArabicNumbers(pageNumber)} - سورة ${currentSurahInfo!!.arabicName} - ${convertToArabicNumbers(currentSurahInfo!!.verses)} آيات - الجزء ${convertToArabicNumbers(pageJuz)}",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            // If no surah info, just show page number
                            Text(
                                text = "صفحة ${convertToArabicNumbers(pageNumber)}",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Animated Top Bar - positioned last so it appears on top with proper z-index
        AnimatedVisibility(
            visible = isTopBarVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
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
                        Text(text = "Page $currentPage of 604",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Dual/Single page toggle button (only show on tablets)
                    if (isTablet) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    dualPageMode = !dualPageMode
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (showDualPages) Icons.AutoMirrored.Filled.MenuBook else Icons.Filled.Book,
                                contentDescription = if (showDualPages) "Switch to Single Page" else "Switch to Dual Page",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Bookmark button
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (isBookmarked) {
                                    // Delete by page number for more reliable deletion
                                    viewModel.deleteBookmarkByPage(currentPage)
                                } else {
                                    viewModel.addBookmark(
                                        Bookmark(
                                            pageNumber = currentPage,
                                            surahName = title,
                                            surahId = 0
                                        )
                                    )
                                }
                                isBookmarked = !isBookmarked
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                            tint = if (isBookmarked) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuranPageImage(
    pageNumber: Int,
    isDarkMode: Boolean = false,
    onImageClick: () -> Unit,
    scaleFactor: Float = 1f // New parameter for scale factor
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

// Helper function to convert English numbers to Arabic numerals
private fun convertToArabicNumbers(number: Int): String {
    val arabicNumerals = mapOf(
        '0' to '٠',
        '1' to '١',
        '2' to '٢',
        '3' to '٣',
        '4' to '٤',
        '5' to '٥',
        '6' to '٦',
        '7' to '٧',
        '8' to '٨',
        '9' to '٩'
    )

    return number.toString().map { arabicNumerals[it] ?: it }.joinToString("")
}

// Helper function to get correct Juz number for a page
private fun getJuzNumberForPage(pageNumber: Int): Int {
    val juzPageRanges = listOf(
        1 to (1..21),       // Juz 1
        2 to (22..41),      // Juz 2
        3 to (42..61),      // Juz 3
        4 to (62..81),      // Juz 4
        5 to (82..101),     // Juz 5
        6 to (102..121),    // Juz 6
        7 to (122..141),    // Juz 7
        8 to (142..161),    // Juz 8
        9 to (162..181),    // Juz 9
        10 to (182..201),   // Juz 10
        11 to (202..221),   // Juz 11
        12 to (222..241),   // Juz 12
        13 to (242..261),   // Juz 13
        14 to (262..281),   // Juz 14
        15 to (282..301),   // Juz 15
        16 to (302..321),   // Juz 16
        17 to (322..341),   // Juz 17
        18 to (342..361),   // Juz 18
        19 to (362..381),   // Juz 19
        20 to (382..401),   // Juz 20
        21 to (402..421),   // Juz 21
        22 to (422..441),   // Juz 22
        23 to (442..461),   // Juz 23
        24 to (462..481),   // Juz 24
        25 to (482..501),   // Juz 25
        26 to (502..521),   // Juz 26
        27 to (522..541),   // Juz 27
        28 to (542..561),   // Juz 28
        29 to (562..581),   // Juz 29
        30 to (582..604)    // Juz 30
    )

    return juzPageRanges.find { pageNumber in it.second }?.first ?: 1
}

// Helper function to find the surah that contains the given page
private fun findSurahForPage(allSurahs: List<Surah>, pageNumber: Int): Surah? {
    // Sort surahs by start page to ensure correct ordering
    val sortedSurahs = allSurahs.sortedBy { it.startPage }

    // Find the surah that contains this page
    for (i in sortedSurahs.indices) {
        val currentSurah = sortedSurahs[i]
        val nextSurah = if (i + 1 < sortedSurahs.size) sortedSurahs[i + 1] else null

        // Check if the page falls within this surah's range
        val startPage = currentSurah.startPage
        val endPage = nextSurah?.startPage?.minus(1) ?: 604 // If it's the last surah, goes to page 604

        if (pageNumber in startPage..endPage) {
            return currentSurah
        }
    }

    return null
}

// Helper function to get hardcoded surah data
private fun getHardcodedSurahs(): List<Surah> {
    return listOf(
        Surah(1, "الفاتحة", "Al-Fatiha", "The Opener", 7, "Meccan", 1, 1),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan", 1, 2),
        Surah(3, "آل عمران", "Al Imran", "Family of Imran", 200, "Medinan", 3, 50),
        Surah(4, "النساء", "An-Nisa", "The Women", 176, "Medinan", 4, 77),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan", 6, 106),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", 165, "Meccan", 7, 128),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", 206, "Meccan", 8, 151),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Medinan", 9, 177),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", 129, "Medinan", 10, 187),
        Surah(10, "يونس", "Yunus", "Jonah", 109, "Meccan", 11, 208),
        Surah(11, "هود", "Hud", "Hud", 123, "Meccan", 11, 221),
        Surah(12, "يوسف", "Yusuf", "Joseph", 111, "Meccan", 12, 235),
        Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", 43, "Medinan", 13, 249),
        Surah(14, "إبراهيم", "Ibrahim", "Abraham", 52, "Meccan", 13, 255),
        Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, "Meccan", 14, 262),
        Surah(16, "النحل", "An-Nahl", "The Bee", 128, "Meccan", 14, 267),
        Surah(17, "الإسراء", "Al-Isra", "The Night Journey", 111, "Meccan", 15, 282),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan", 15, 293),
        Surah(19, "مريم", "Maryam", "Mary", 98, "Meccan", 16, 305),
        Surah(20, "طه", "Ta-Ha", "Ta-Ha", 135, "Meccan", 16, 312),
        Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, "Meccan", 17, 322),
        Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", 78, "Medinan", 17, 332),
        Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, "Meccan", 18, 342),
        Surah(24, "النور", "An-Nur", "The Light", 64, "Medinan", 18, 350),
        Surah(25, "الفرقان", "Al-Furqan", "The Criterion", 77, "Meccan", 18, 359),
        Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, "Meccan", 19, 367),
        Surah(27, "النمل", "An-Naml", "The Ant", 93, "Meccan", 19, 377),
        Surah(28, "القصص", "Al-Qasas", "The Stories", 88, "Meccan", 20, 385),
        Surah(29, "العنكبوت", "Al-Ankabut", "The Spider", 69, "Meccan", 20, 396),
        Surah(30, "الروم", "Ar-Rum", "The Byzantines", 60, "Meccan", 21, 404),
        Surah(31, "لقمان", "Luqman", "Luqman", 34, "Meccan", 21, 411),
        Surah(32, "السجدة", "As-Sajdah", "The Prostration", 30, "Meccan", 21, 415),
        Surah(33, "الأحزاب", "Al-Ahzab", "The Confederates", 73, "Medinan", 21, 418),
        Surah(34, "سبأ", "Saba", "Sheba", 54, "Meccan", 22, 428),
        Surah(35, "فاطر", "Fatir", "Originator", 45, "Meccan", 22, 434),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", 83, "Meccan", 22, 440),
        Surah(37, "الصافات", "As-Saffat", "Those who set the Ranks", 182, "Meccan", 23, 446),
        Surah(38, "ص", "Sad", "Sad", 88, "Meccan", 23, 453),
        Surah(39, "الزمر", "Az-Zumar", "The Troops", 75, "Meccan", 23, 458),
        Surah(40, "غافر", "Ghafir", "The Forgiver", 85, "Meccan", 24, 467),
        Surah(41, "فصلت", "Fussilat", "Explained in Detail", 54, "Meccan", 24, 477),
        Surah(42, "الشورى", "Ash-Shura", "The Consultation", 53, "Meccan", 25, 483),
        Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Meccan", 25, 489),
        Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", 59, "Meccan", 25, 496),
        Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, "Meccan", 25, 499),
        Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Meccan", 26, 502),
        Surah(47, "محمد", "Muhammad", "Muhammad", 38, "Medinan", 26, 507),
        Surah(48, "الفتح", "Al-Fath", "The Victory", 29, "Medinan", 26, 511),
        Surah(49, "الحجرات", "Al-Hujurat", "The Rooms", 18, "Medinan", 26, 515),
        Surah(50, "ق", "Qaf", "Qaf", 45, "Meccan", 26, 518),
        Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Meccan", 26, 520),
        Surah(52, "الطور", "At-Tur", "The Mount", 49, "Meccan", 27, 523),
        Surah(53, "النجم", "An-Najm", "The Star", 62, "Meccan", 27, 526),
        Surah(54, "القمر", "Al-Qamar", "The Moon", 55, "Meccan", 27, 528),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Meccan", 27, 531),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan", 27, 534),
        Surah(57, "الحديد", "Al-Hadid", "The Iron", 29, "Medinan", 27, 537),
        Surah(58, "المجادلة", "Al-Mujadilah", "The Pleading Woman", 22, "Medinan", 28, 542),
        Surah(59, "الحشر", "Al-Hashr", "The Exile", 24, "Medinan", 28, 545),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", 13, "Medinan", 28, 549),
        Surah(61, "الصف", "As-Saff", "The Ranks", 14, "Medinan", 28, 551),
        Surah(62, "الجمعة", "Al-Jumu'ah", "Friday", 11, "Medinan", 28, 553),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Medinan", 28, 554),
        Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, "Medinan", 28, 556),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", 12, "Medinan", 28, 558),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Medinan", 28, 560),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan", 29, 562),
        Surah(68, "القلم", "Al-Qalam", "The Pen", 52, "Meccan", 29, 564),
        Surah(69, "الحاقة", "Al-Haqqah", "The Reality", 52, "Meccan", 29, 566),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Meccan", 29, 568),
        Surah(71, "نوح", "Nuh", "Noah", 28, "Meccan", 29, 570),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", 28, "Meccan", 29, 572),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Meccan", 29, 574),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Meccan", 29, 575),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Meccan", 29, 577),
        Surah(76, "الإنسان", "Al-Insan", "The Human", 31, "Medinan", 29, 578),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Meccan", 29, 580),
        Surah(78, "النبأ", "An-Naba", "The Tidings", 40, "Meccan", 30, 582),
        Surah(79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Meccan", 30, 583),
        Surah(80, "عبس", "Abasa", "He frowned", 42, "Meccan", 30, 585),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Meccan", 30, 586),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, "Meccan", 30, 587),
        Surah(83, "المطففين", "Al-Mutaffifin", "The Defrauding", 36, "Meccan", 30, 587),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Sundering", 25, "Meccan", 30, 589),
        Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Meccan", 30, 590),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", 17, "Meccan", 30, 591),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", 19, "Meccan", 30, 591),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, "Meccan", 30, 592),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", 30, "Meccan", 30, 593),
        Surah(90, "البلد", "Al-Balad", "The City", 20, "Meccan", 30, 594),
        Surah(91, "الشمس", "Ash-Shams", "The Sun", 15, "Meccan", 30, 595),
        Surah(92, "الليل", "Al-Layl", "The Night", 21, "Meccan", 30, 595),
        Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", 11, "Meccan", 30, 596),
        Surah(94, "الشرح", "Ash-Sharh", "The Relief", 8, "Meccan", 30, 596),
        Surah(95, "التين", "At-Tin", "The Fig", 8, "Meccan", 30, 597),
        Surah(96, "العلق", "Al-Alaq", "The Clot", 19, "Meccan", 30, 597),
        Surah(97, "القدر", "Al-Qadr", "The Power", 5, "Meccan", 30, 598),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, "Medinan", 30, 598),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Medinan", 30, 599),
        Surah(100, "العاديات", "Al-Adiyat", "The Courser", 11, "Meccan", 30, 599),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Meccan", 30, 600),
        Surah(102, "التكاثر", "At-Takathur", "The Rivalry in world increase", 8, "Meccan", 30, 600),
        Surah(103, "العصر", "Al-Asr", "The Declining Day", 3, "Meccan", 30, 601),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Meccan", 30, 601),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", 5, "Meccan", 30, 601),
        Surah(106, "قريش", "Quraysh", "Quraysh", 4, "Meccan", 30, 602),
        Surah(107, "الماعون", "Al-Ma'un", "The Small kindnesses", 7, "Meccan", 30, 602),
        Surah(108, "الكوثر", "Al-Kawthar", "The Abundance", 3, "Meccan", 30, 602),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Meccan", 30, 603),
        Surah(110, "النصر", "An-Nasr", "The Divine Support", 3, "Medinan", 30, 603),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Meccan", 30, 603),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan", 30, 604),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan", 30, 604),
        Surah(114, "الناس", "An-Nas", "Mankind", 6, "Meccan", 30, 604)
    )
}
