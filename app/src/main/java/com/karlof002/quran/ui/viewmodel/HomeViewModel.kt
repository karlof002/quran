package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import com.karlof002.quran.QuranApplication
import com.karlof002.quran.data.models.Juz
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.data.repository.QuranRepository

data class JuzSection(
    val juz: Juz,
    val surahs: List<Surah>
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuranRepository = (application as QuranApplication).repository

    private val _juzSections = MediatorLiveData<List<JuzSection>>()
    val juzSections: LiveData<List<JuzSection>> = _juzSections

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val allSurahs: LiveData<List<Surah>>
    val allJuz: LiveData<List<Juz>>

    init {
        allSurahs = repository.getAllSurahs()
        allJuz = repository.getAllJuz()

        // Set up MediatorLiveData to observe both allSurahs and allJuz
        _juzSections.addSource(allSurahs) { surahs ->
            combineData(surahs, allJuz.value ?: emptyList())
        }

        _juzSections.addSource(allJuz) { juzList ->
            combineData(allSurahs.value ?: emptyList(), juzList)
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
        // Data will automatically refresh through LiveData observers
        // No manual reload needed since database initialization happens in Application
    }
}
