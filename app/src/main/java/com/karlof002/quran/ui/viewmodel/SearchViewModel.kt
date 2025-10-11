package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karlof002.quran.QuranApplication
import com.karlof002.quran.data.repository.QuranRepository
import kotlinx.coroutines.launch

data class SearchResult(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val translation: String
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuranRepository = (application as QuranApplication).repository

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _searchResults = MutableLiveData<List<SearchResult>>(emptyList())
    val searchResults: LiveData<List<SearchResult>> = _searchResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search() {
        val query = _searchQuery.value
        if (query.isNullOrEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Search in both surahs and ayahs
                val surahResults = repository.searchSurahs(query)
                val ayahResults = repository.searchAyahs(query)

                // Convert to SearchResult format
                val results = mutableListOf<SearchResult>()

                // Add surah results
                surahResults.forEach { surah ->
                    results.add(
                        SearchResult(
                            surahNumber = surah.id,
                            surahName = surah.arabicName,
                            ayahNumber = 0, // 0 indicates this is a surah result
                            arabicText = surah.arabicName,
                            translation = "${surah.transliteration} - ${surah.translation}"
                        )
                    )
                }

                // Add ayah results
                ayahResults.forEach { ayah ->
                    val surah = repository.getSurahById(ayah.surahId)
                    results.add(
                        SearchResult(
                            surahNumber = ayah.surahId,
                            surahName = surah?.arabicName ?: "",
                            ayahNumber = ayah.ayahNumber,
                            arabicText = ayah.text,
                            translation = ayah.translation ?: ""
                        )
                    )
                }

                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}
