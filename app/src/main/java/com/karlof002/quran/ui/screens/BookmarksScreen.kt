package com.karlof002.quran.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ChecklistRtl
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

    // Selection mode state
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedBookmarks by remember { mutableStateOf(setOf<Long>()) } // Store bookmark IDs
    var showDeleteMultipleDialog by remember { mutableStateOf(false) }

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
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        } catch (_: Exception) {
            android.util.Log.e("BookmarksScreen", "Error loading surahs")
        }
    }

    // Exit selection mode when no bookmarks are selected
    LaunchedEffect(selectedBookmarks.isEmpty()) {
        if (selectedBookmarks.isEmpty() && isSelectionMode) {
            isSelectionMode = false
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
            if (isSelectionMode) {
                // Selection mode top bar
                TopAppBar(
                    title = {
                        Text(
                            "${selectedBookmarks.size} selected",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedBookmarks = emptySet()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel selection",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        // Select all / Deselect all button
                        TextButton(
                            onClick = {
                                val allBookmarkIds = allBookmarks.map { it.id }.toSet()
                                selectedBookmarks = if (selectedBookmarks.size == allBookmarks.size) {
                                    emptySet()
                                } else {
                                    allBookmarkIds
                                }
                            }
                        ) {
                            Text(
                                if (selectedBookmarks.size == allBookmarks.size) "Deselect All" else "Select All",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Delete button
                        IconButton(
                            onClick = { showDeleteMultipleDialog = true },
                            enabled = selectedBookmarks.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete selected",
                                tint = if (selectedBookmarks.isNotEmpty())
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            } else {
                // Normal mode top bar
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
                        // Enter selection mode button
                        if (allBookmarks.isNotEmpty()) {
                            IconButton(onClick = { isSelectionMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.ChecklistRtl,
                                    contentDescription = "Select bookmarks",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

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
            }
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
                                isSelectionMode = isSelectionMode,
                                isSelected = selectedBookmarks.contains(bookmark.id),
                                onSelectionToggle = {
                                    selectedBookmarks = if (selectedBookmarks.contains(bookmark.id)) {
                                        selectedBookmarks - bookmark.id
                                    } else {
                                        selectedBookmarks + bookmark.id
                                    }
                                },
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

        // Delete single bookmark confirmation dialog
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

        // Delete multiple bookmarks confirmation dialog
        if (showDeleteMultipleDialog) {
            DeleteMultipleBookmarksDialog(
                bookmarkCount = selectedBookmarks.size,
                onConfirm = {
                    val bookmarksToDelete = allBookmarks.filter { selectedBookmarks.contains(it.id) }
                    scope.launch {
                        viewModel.deleteBookmarks(bookmarksToDelete)
                    }
                    selectedBookmarks = emptySet()
                    isSelectionMode = false
                    showDeleteMultipleDialog = false
                },
                onDismiss = {
                    showDeleteMultipleDialog = false
                }
            )
        }
    }
}
