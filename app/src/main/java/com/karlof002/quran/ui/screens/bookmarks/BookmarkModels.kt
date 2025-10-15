package com.karlof002.quran.ui.screens.bookmarks

import com.karlof002.quran.data.models.Bookmark

enum class DateFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH, OLDER
}

data class BookmarkSection(
    val title: String,
    val bookmarks: List<Bookmark>
)

