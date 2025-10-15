package com.karlof002.quran.ui.screens.search

import com.karlof002.quran.data.models.Surah

// Search result types
sealed class SearchResultItem {
    data class SurahResult(val surah: Surah) : SearchResultItem()
    data class JuzResult(val juzNumber: Int, val startPage: Int, val endPage: Int) : SearchResultItem()
    data class PageResult(val pageNumber: Int) : SearchResultItem()
}

