package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karlof002.quran.data.database.QuranDatabase
import com.karlof002.quran.data.models.Settings
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDao = QuranDatabase.getDatabase(application).settingsDao()

    private val _isDarkMode = MutableLiveData(false)
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _fontSize = MutableLiveData(16)
    val fontSize: LiveData<Int> = _fontSize

    private val _arabicFont = MutableLiveData("Default")
    val arabicFont: LiveData<String> = _arabicFont

    private val _translationLanguage = MutableLiveData("English")
    val translationLanguage: LiveData<String> = _translationLanguage

    init {
        // Load settings from database
        viewModelScope.launch {
            val settings = settingsDao.getSettingsSync()
            if (settings != null) {
                _isDarkMode.postValue(settings.isDarkMode)
                _fontSize.postValue(settings.fontSize)
                _arabicFont.postValue(settings.arabicFont)
                _translationLanguage.postValue(settings.translationLanguage)
            } else {
                // Create default settings if none exist
                val defaultSettings = Settings()
                settingsDao.insertSettings(defaultSettings)
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch {
            settingsDao.updateDarkMode(enabled)
        }
    }

    fun setFontSize(size: Int) {
        _fontSize.value = size
        viewModelScope.launch {
            settingsDao.updateFontSize(size)
        }
    }

    fun setArabicFont(font: String) {
        _arabicFont.value = font
        viewModelScope.launch {
            settingsDao.updateArabicFont(font)
        }
    }

    fun setTranslationLanguage(language: String) {
        _translationLanguage.value = language
        viewModelScope.launch {
            settingsDao.updateTranslationLanguage(language)
        }
    }
}
