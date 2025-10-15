package com.karlof002.quran.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.karlof002.quran.data.dao.*
import com.karlof002.quran.data.models.*
import com.karlof002.quran.data.models.json.ChaptersResponse
import com.karlof002.quran.data.models.json.JuzsResponse
import java.io.InputStreamReader

class QuranRepository(
    private val surahDao: SurahDao,
    private val juzDao: JuzDao,
    private val ayahDao: AyahDao,
    private val bookmarkDao: BookmarkDao,
    private val settingsDao: SettingsDao,
    private val context: Context
) {
    // Cache for page to juz mapping loaded from JSON
    private var pageToJuzMap: Map<Int, Int>? = null

    // Surah operations
    fun getAllSurahs(): LiveData<List<Surah>> = surahDao.getAllSurahs()
    suspend fun getSurahById(id: Int): Surah? = surahDao.getSurahById(id)
    suspend fun getSurahByPage(pageNumber: Int): Surah? = surahDao.getSurahByPage(pageNumber)
    suspend fun searchSurahs(query: String): List<Surah> = surahDao.searchSurahs(query)

    // Juz operations
    fun getAllJuz(): LiveData<List<Juz>> = juzDao.getAllJuz()
    suspend fun getJuzById(id: Int): Juz? = juzDao.getJuzById(id)

    // Ayah operations
    suspend fun getAyahsBySurah(surahId: Int): List<Ayah> = ayahDao.getAyahsBySurah(surahId)
    suspend fun getAyahsByJuz(juzId: Int): List<Ayah> = ayahDao.getAyahsByJuz(juzId)
    suspend fun searchAyahs(query: String): List<Ayah> = ayahDao.searchAyahs(query)

    // Bookmark operations
    fun getAllBookmarks(): LiveData<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    suspend fun addBookmark(bookmark: Bookmark) = bookmarkDao.insert(bookmark)
    suspend fun removeBookmark(bookmark: Bookmark) = bookmarkDao.delete(bookmark)
    suspend fun removeBookmarkByPage(pageNumber: Int) = bookmarkDao.deleteByPage(pageNumber)
    suspend fun isPageBookmarked(pageNumber: Int): Boolean = bookmarkDao.isPageBookmarked(pageNumber)

    // Settings operations
    fun getSettings(): LiveData<Settings?> = settingsDao.getSettings()
    suspend fun getSettingsSync(): Settings? = settingsDao.getSettingsSync()
    suspend fun updateSettings(settings: Settings) = settingsDao.updateSettings(settings)
    suspend fun updateDarkMode(isDarkMode: Boolean) = settingsDao.updateDarkMode(isDarkMode)
    suspend fun updateFontSize(fontSize: Float) = settingsDao.updateFontSize(fontSize)
    suspend fun updateArabicFont(arabicFont: String) = settingsDao.updateArabicFont(arabicFont)
    suspend fun updateTranslationLanguage(language: String) = settingsDao.updateTranslationLanguage(language)
    suspend fun updateInfoTextSize(infoTextSize: Float) = settingsDao.updateInfoTextSize(infoTextSize)

    // Data initialization
    suspend fun initializeData() {
        // Check if data already exists by counting surahs directly
        val surahCount = surahDao.getSurahCount()
        if (surahCount == 0) {
            // Only initialize if no data exists
            val sampleSurahs = loadSurahsFromJson()
            val sampleJuz = loadJuzFromJson()

            surahDao.insertAll(sampleSurahs)
            juzDao.insertAll(sampleJuz)
        }

        // Initialize default settings if they don't exist
        val existingSettings = settingsDao.getSettingsSync()
        if (existingSettings == null) {
            val defaultSettings = Settings(
                id = 1,
                isDarkMode = false,
                fontSize = 16f,
                arabicFont = "Default",
                translationLanguage = "English",
                infoTextSize = 14f
            )
            settingsDao.insertSettings(defaultSettings)
        }
    }

    private fun loadSurahsFromJson(): List<Surah> {
        return try {
            val inputStream = context.assets.open("chapters.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val response = gson.fromJson(reader, ChaptersResponse::class.java)
            reader.close()

            // Build page to juz mapping if not already built
            if (pageToJuzMap == null) {
                pageToJuzMap = buildPageToJuzMapping()
            }

            response.chapters.map { chapter ->
                // Convert revelation_place from "makkah"/"madinah" to "Meccan"/"Medinan"
                val revelation = when (chapter.revelationPlace.lowercase()) {
                    "makkah" -> "Meccan"
                    "madinah" -> "Medinan"
                    else -> "Meccan"
                }

                // Calculate which Juz this surah starts in based on the start page
                val startPage = chapter.pages[0]
                val juzNumber = pageToJuzMap?.get(startPage) ?: calculateJuzFromSurahId(chapter.id)

                Surah(
                    id = chapter.id,
                    arabicName = chapter.nameArabic,
                    transliteration = chapter.nameSimple,
                    translation = chapter.translatedName.name,
                    verses = chapter.versesCount,
                    revelation = revelation,
                    juz = juzNumber,
                    startPage = startPage
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("QuranRepository", "Error loading surahs from JSON", e)
            emptyList()
        }
    }

    private fun loadJuzFromJson(): List<Juz> {
        return try {
            val inputStream = context.assets.open("juzs.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val response = gson.fromJson(reader, JuzsResponse::class.java)
            reader.close()

            // Remove duplicates by using distinct juz_number
            response.juzs.distinctBy { it.juzNumber }.map { juz ->
                // Parse the verse_mapping to get start and end surah/ayah
                val firstEntry = juz.verseMapping.entries.first()
                val lastEntry = juz.verseMapping.entries.last()

                val startSurah = firstEntry.key.toInt()
                val startAyahRange = firstEntry.value
                val startAyah = if (startAyahRange.contains("-")) {
                    startAyahRange.split("-")[0].toInt()
                } else {
                    startAyahRange.toInt()
                }

                val endSurah = lastEntry.key.toInt()
                val endAyahRange = lastEntry.value
                val endAyah = if (endAyahRange.contains("-")) {
                    endAyahRange.split("-")[1].toInt()
                } else {
                    endAyahRange.toInt()
                }

                Juz(
                    id = juz.juzNumber,
                    startSurah = startSurah,
                    startAyah = startAyah,
                    endSurah = endSurah,
                    endAyah = endAyah
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("QuranRepository", "Error loading juz from JSON", e)
            emptyList()
        }
    }

    // Build a mapping from page number to juz number using the JSON data
    private fun buildPageToJuzMapping(): Map<Int, Int> {
        val mapping = mutableMapOf<Int, Int>()

        try {
            // Load chapters to get page ranges
            val chaptersStream = context.assets.open("chapters.json")
            val chaptersReader = InputStreamReader(chaptersStream)
            val gson = Gson()
            val chaptersResponse = gson.fromJson(chaptersReader, ChaptersResponse::class.java)
            chaptersReader.close()

            // Load juzs to get surah mappings
            val juzsStream = context.assets.open("juzs.json")
            val juzsReader = InputStreamReader(juzsStream)
            val juzsResponse = gson.fromJson(juzsReader, JuzsResponse::class.java)
            juzsReader.close()

            // Create a map of surah ID to juz number from verse mappings
            val surahToJuzMap = mutableMapOf<Int, Int>()
            juzsResponse.juzs.distinctBy { it.juzNumber }.forEach { juz ->
                juz.verseMapping.keys.forEach { surahId ->
                    val id = surahId.toInt()
                    // If surah spans multiple juzs, map to the first juz it appears in
                    if (!surahToJuzMap.containsKey(id)) {
                        surahToJuzMap[id] = juz.juzNumber
                    }
                }
            }

            // Now map pages to juz using chapter data
            chaptersResponse.chapters.forEach { chapter ->
                val juzNumber = surahToJuzMap[chapter.id] ?: 1
                val startPage = chapter.pages[0]
                val endPage = chapter.pages[1]

                // Map all pages in this chapter's range to the juz
                for (page in startPage..endPage) {
                    if (!mapping.containsKey(page)) {
                        mapping[page] = juzNumber
                    }
                }
            }

        } catch (e: Exception) {
            android.util.Log.e("QuranRepository", "Error building page to juz mapping", e)
        }

        return mapping
    }

    // Fallback method to calculate juz from surah ID based on verse mappings
    private fun calculateJuzFromSurahId(surahId: Int): Int {
        return try {
            val inputStream = context.assets.open("juzs.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val response = gson.fromJson(reader, JuzsResponse::class.java)
            reader.close()

            // Find first juz that contains this surah
            response.juzs.distinctBy { it.juzNumber }.find { juz ->
                juz.verseMapping.keys.any { it.toInt() == surahId }
            }?.juzNumber ?: 1
        } catch (e: Exception) {
            android.util.Log.e("QuranRepository", "Error calculating juz from surah ID", e)
            1
        }
    }
}
