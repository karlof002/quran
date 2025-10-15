package com.karlof002.quran.ui.screens.reader

import com.karlof002.quran.data.models.Surah

// Helper function to convert English numbers to Arabic numerals
fun convertToArabicNumbers(number: Int): String {
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

// Helper function to get Juz number for a page using the loaded surah data
fun getJuzForPage(allSurahs: List<Surah>, pageNumber: Int): Int {
    // Find the surah that contains this page
    val surah = findSurahForPage(allSurahs, pageNumber)
    // Return the juz number from the surah data, or default to 1
    return surah?.juz ?: 1
}

// Helper function to find the surah that contains the given page
fun findSurahForPage(allSurahs: List<Surah>, pageNumber: Int): Surah? {
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

