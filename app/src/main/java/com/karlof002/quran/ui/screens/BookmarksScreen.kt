package com.karlof002.quran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.data.models.Bookmark
import com.karlof002.quran.ui.viewmodel.BookmarkViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class DateFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH, OLDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarkViewModel = viewModel(),
    onBookmarkClick: (Int, String, Int, Int) -> Unit = { _, _, _, _ -> },
    windowSize: com.karlof002.quran.ui.utils.WindowSizeClass = com.karlof002.quran.ui.utils.WindowSizeClass.COMPACT
) {
    val allBookmarks by viewModel.allBookmarks.observeAsState(emptyList())
    val scope = rememberCoroutineScope()

    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(DateFilter.ALL) }
    val sheetState = rememberModalBottomSheetState()

    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var bookmarkToDelete by remember { mutableStateOf<Bookmark?>(null) }

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
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkRemove,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (selectedFilter == DateFilter.ALL) "No bookmarks yet" else "No bookmarks found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (selectedFilter == DateFilter.ALL) "Bookmark pages while reading the Quran" else "Try a different filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
                                    onBookmarkClick(bookmark.pageNumber, bookmark.surahName, 0, 0)
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

        // Filter Bottom Sheet using Material 3
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Filter by Date",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )

                    FilterOption(
                        title = "All Bookmarks",
                        isSelected = selectedFilter == DateFilter.ALL,
                        onClick = {
                            scope.launch {
                                selectedFilter = DateFilter.ALL
                                sheetState.hide()
                                showFilterSheet = false
                            }
                        }
                    )

                    FilterOption(
                        title = "Today",
                        isSelected = selectedFilter == DateFilter.TODAY,
                        onClick = {
                            scope.launch {
                                selectedFilter = DateFilter.TODAY
                                sheetState.hide()
                                showFilterSheet = false
                            }
                        }
                    )

                    FilterOption(
                        title = "This Week",
                        isSelected = selectedFilter == DateFilter.THIS_WEEK,
                        onClick = {
                            scope.launch {
                                selectedFilter = DateFilter.THIS_WEEK
                                sheetState.hide()
                                showFilterSheet = false
                            }
                        }
                    )

                    FilterOption(
                        title = "This Month",
                        isSelected = selectedFilter == DateFilter.THIS_MONTH,
                        onClick = {
                            scope.launch {
                                selectedFilter = DateFilter.THIS_MONTH
                                sheetState.hide()
                                showFilterSheet = false
                            }
                        }
                    )

                    FilterOption(
                        title = "Older",
                        isSelected = selectedFilter == DateFilter.OLDER,
                        onClick = {
                            scope.launch {
                                selectedFilter = DateFilter.OLDER
                                sheetState.hide()
                                showFilterSheet = false
                            }
                        }
                    )
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && bookmarkToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    bookmarkToDelete = null
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        "Delete Bookmark",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete the bookmark for page ${bookmarkToDelete?.pageNumber}?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Delete the bookmark
                            bookmarkToDelete?.let { bookmark ->
                                scope.launch {
                                    viewModel.deleteBookmark(bookmark)
                                }
                            }
                            showDeleteDialog = false
                            bookmarkToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            bookmarkToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun BookmarkListItem(
    bookmark: Bookmark,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
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
        // Page Number Badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bookmark.pageNumber.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Bookmark Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "سورة ${bookmark.surahName}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Page ${bookmark.pageNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Text(
                text = formatDate(bookmark.timestamp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }

        // Delete Button
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.BookmarkRemove,
                contentDescription = "Delete bookmark",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FilterOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

@Composable
fun BookmarkSectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}

data class BookmarkSection(
    val title: String,
    val bookmarks: List<Bookmark>
)
