package com.karlof002.quran.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.data.models.Bookmark
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.BookmarkViewModel
import com.karlof002.quran.ui.screens.bookmarks.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarkViewModel = viewModel(),
    onBookmarkClick: (Int, String, Int, Int) -> Unit = { _, _, _, _ -> },
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
) {
    val allBookmarks by viewModel.allBookmarks.observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(DateFilter.ALL) }
    val sheetState = rememberModalBottomSheetState()

    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var bookmarkToDelete by remember { mutableStateOf<Bookmark?>(null) }

    // Load all surahs from database to get verse counts and juz numbers
    var allSurahs by remember { mutableStateOf<List<Surah>>(emptyList()) }

    // Load surahs from database once
    LaunchedEffect(Unit) {
        try {
            val database = com.karlof002.quran.data.database.QuranDatabase.getDatabase(context)
            val surahs = database.surahDao().getAllSurahs().value ?: emptyList()
            allSurahs = surahs.ifEmpty {
                // Get surahs synchronously from database if LiveData is empty
                (1..114).mapNotNull { id ->
                    try {
                        database.surahDao().getSurahById(id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BookmarksScreen", "Error loading surahs", e)
        }
    }

    // Adaptive padding based on screen size
    val horizontalPadding = when (windowSize) {
        com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT -> 0.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.MEDIUM -> 16.dp
        com.karlof002.quran.ui.utils.WindowSizeClass.EXPANDED -> 32.dp
    }

    // Group bookmarks by date sections
    val bookmarkSections = remember(allBookmarks, selectedFilter) {
        val calendar = Calendar.getInstance()
        val todayStart = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Group bookmarks by sections
        val todayBookmarks = allBookmarks.filter { it.timestamp >= todayStart }
        val thisWeekBookmarks = allBookmarks.filter { it.timestamp >= weekStart && it.timestamp < todayStart }
        val thisMonthBookmarks = allBookmarks.filter { it.timestamp >= monthStart && it.timestamp < weekStart }
        val olderBookmarks = allBookmarks.filter { it.timestamp < monthStart }

        // Apply filter and create sections
        when (selectedFilter) {
            DateFilter.ALL -> buildList {
                if (todayBookmarks.isNotEmpty()) add(BookmarkSection("Today", todayBookmarks))
                if (thisWeekBookmarks.isNotEmpty()) add(BookmarkSection("This Week", thisWeekBookmarks))
                if (thisMonthBookmarks.isNotEmpty()) add(BookmarkSection("This Month", thisMonthBookmarks))
                if (olderBookmarks.isNotEmpty()) add(BookmarkSection("Older", olderBookmarks))
            }
            DateFilter.TODAY -> if (todayBookmarks.isNotEmpty()) listOf(BookmarkSection("Today", todayBookmarks)) else emptyList()
            DateFilter.THIS_WEEK -> if (thisWeekBookmarks.isNotEmpty()) listOf(BookmarkSection("This Week", thisWeekBookmarks)) else emptyList()
            DateFilter.THIS_MONTH -> if (thisMonthBookmarks.isNotEmpty()) listOf(BookmarkSection("This Month", thisMonthBookmarks)) else emptyList()
            DateFilter.OLDER -> if (olderBookmarks.isNotEmpty()) listOf(BookmarkSection("Older", olderBookmarks)) else emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bookmarks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (bookmarkSections.isEmpty()) {
                BookmarkEmptyState(selectedFilter = selectedFilter)
            } else {
                // Bookmarks list with sections
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    bookmarkSections.forEach { section ->
                        // Section Header
                        item(key = "header_${section.title}") {
                            BookmarkSectionHeader(title = section.title)
                        }

                        // Section Items
                        items(
                            count = section.bookmarks.size,
                            key = { index -> "bookmark_${section.bookmarks[index].id}" }
                        ) { index ->
                            val bookmark = section.bookmarks[index]
                            BookmarkListItem(
                                bookmark = bookmark,
                                onClick = {
                                    // Find the surah details for the clicked bookmark
                                    val surah = allSurahs.find { it.id == bookmark.surahId }
                                    val verseCount = surah?.verses ?: 0
                                    val juzNumber = surah?.juz ?: 0

                                    onBookmarkClick(bookmark.pageNumber, bookmark.surahName, verseCount, juzNumber)
                                },
                                onDeleteClick = {
                                    bookmarkToDelete = bookmark
                                    showDeleteDialog = true
                                }
                            )

                            if (index < section.bookmarks.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    thickness = 0.5.dp
                                )
                            }
                        }

                        // Add spacing after each section
                        item(key = "spacer_${section.title}") {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // Filter Bottom Sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                sheetState = sheetState,
                selectedFilter = selectedFilter,
                onFilterSelected = { filter ->
                    selectedFilter = filter
                },
                onDismiss = { showFilterSheet = false }
            )
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
            DeleteBookmarkDialog(
                bookmark = bookmarkToDelete,
                onConfirm = {
                    bookmarkToDelete?.let { bookmark ->
                        scope.launch {
                            viewModel.deleteBookmark(bookmark)
                        }
                    }
                    showDeleteDialog = false
                    bookmarkToDelete = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    bookmarkToDelete = null
                }
            )
        }
    }
}
