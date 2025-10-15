package com.karlof002.quran.ui.screens.search

import com.karlof002.quran.data.models.Juz
import com.karlof002.quran.data.models.Surah

fun performSearch(
    query: String,
    allSurahs: List<Surah>,
    allJuz: List<Juz>,
    surahsList: List<Surah>
): List<SearchResultItem> {
    if (query.isBlank()) {
        return emptyList()
    }

    val trimmedQuery = query.trim()
    val results = mutableListOf<SearchResultItem>()

    // Search by Juz number
    if (trimmedQuery.toIntOrNull() != null) {
        val juzNumber = trimmedQuery.toInt()
        if (juzNumber in 1..30) {
            val juz = allJuz.find { it.id == juzNumber }
            juz?.let {
                // Find the first surah in this juz to get the start page
                val firstSurahInJuz = surahsList.find { surah -> surah.juz == juzNumber }
                val startPage = firstSurahInJuz?.startPage ?: 1

                // Find the last surah in this juz to calculate end page
                val surahsInThisJuz = surahsList.filter { surah -> surah.juz == juzNumber }
                val lastSurahInJuz = surahsInThisJuz.maxByOrNull { surah -> surah.startPage }

                // Calculate end page (start of next surah - 1, or 604 if last juz)
                val sortedSurahs = surahsList.sortedBy { surah -> surah.startPage }
                val lastSurahIndex = sortedSurahs.indexOfFirst { it.id == lastSurahInJuz?.id }
                val endPage = if (lastSurahIndex >= 0 && lastSurahIndex < sortedSurahs.size - 1) {
                    sortedSurahs[lastSurahIndex + 1].startPage - 1
                } else {
                    604
                }

                results.add(
                    SearchResultItem.JuzResult(
                        juzNumber = it.id,
                        startPage = startPage,
                        endPage = endPage
                    )
                )
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
        if (surah.arabicName.contains(trimmedQuery, ignoreCase = true) ||
            surah.transliteration.contains(trimmedQuery, ignoreCase = true) ||
            surah.translation.contains(trimmedQuery, ignoreCase = true) ||
            surah.id.toString() == trimmedQuery
        ) {
            results.add(SearchResultItem.SurahResult(surah))
        }
    }

    return results
}

fun findSurahForPage(surahsList: List<Surah>, pageNumber: Int): Surah? {
    val sortedSurahs = surahsList.sortedBy { it.startPage }

    for (i in sortedSurahs.indices) {
        val currentSurah = sortedSurahs[i]
        val nextSurah = if (i + 1 < sortedSurahs.size) sortedSurahs[i + 1] else null
        val startPage = currentSurah.startPage
        val endPage = nextSurah?.startPage?.minus(1) ?: 604

        if (pageNumber in startPage..endPage) {
            return currentSurah
        }
    }

    return null
}

