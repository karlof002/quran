package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.karlof002.quran.data.database.QuranDatabase
import com.karlof002.quran.data.models.Juz
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.data.repository.QuranRepository
import kotlinx.coroutines.launch

data class JuzSection(
    val juz: Juz,
    val surahs: List<Surah>
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuranRepository

    private val _juzSections = MediatorLiveData<List<JuzSection>>()
    val juzSections: LiveData<List<JuzSection>> = _juzSections

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val allSurahs: LiveData<List<Surah>>
    val allJuz: LiveData<List<Juz>>

    init {
        val database = QuranDatabase.getDatabase(application)
        repository = QuranRepository(
            database.surahDao(),
            database.juzDao(),
            database.ayahDao(),
            database.bookmarkDao()
        )

        allSurahs = repository.getAllSurahs()
        allJuz = repository.getAllJuz()

        // Set up MediatorLiveData to observe both allSurahs and allJuz
        _juzSections.addSource(allSurahs) { surahs ->
            combineData(surahs, allJuz.value ?: emptyList())
        }

        _juzSections.addSource(allJuz) { juzList ->
            combineData(allSurahs.value ?: emptyList(), juzList)
        }

        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Initialize data if not already present
                repository.initializeData()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun combineData(surahs: List<Surah>, juzList: List<Juz>) {
        if (surahs.isNotEmpty() && juzList.isNotEmpty()) {
            val sections = juzList.map { juz ->
                val surahsInJuz = surahs.filter { it.juz == juz.id }
                JuzSection(juz, surahsInJuz)
            }
            _juzSections.value = sections
        }
    }

    fun refreshData() {
        // The data will automatically refresh when LiveData changes
        // But we can force a reload if needed
        loadData()
    }
}
