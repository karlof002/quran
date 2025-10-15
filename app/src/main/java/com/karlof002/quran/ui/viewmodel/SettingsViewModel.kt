package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karlof002.quran.QuranApplication
import com.karlof002.quran.data.models.Settings
import com.karlof002.quran.data.repository.QuranRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuranRepository = (application as QuranApplication).repository

    private val _isDarkMode = MutableLiveData(false)
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _fontSize = MutableLiveData(16f)
    val fontSize: LiveData<Float> = _fontSize

    private val _arabicFont = MutableLiveData("Default")
    val arabicFont: LiveData<String> = _arabicFont

    private val _translationLanguage = MutableLiveData("English")
    val translationLanguage: LiveData<String> = _translationLanguage

    private val _infoTextSize = MutableLiveData(14f)
    val infoTextSize: LiveData<Float> = _infoTextSize

    val settings: LiveData<Settings?> = repository.getSettings()

    init {
        // Load settings from database
        viewModelScope.launch {
            val currentSettings = repository.getSettingsSync()
            if (currentSettings != null) {
                _isDarkMode.postValue(currentSettings.isDarkMode)
                _fontSize.postValue(currentSettings.fontSize)
                _arabicFont.postValue(currentSettings.arabicFont)
                _translationLanguage.postValue(currentSettings.translationLanguage)
                _infoTextSize.postValue(currentSettings.infoTextSize)
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch {
            repository.updateDarkMode(enabled)
        }
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size
        viewModelScope.launch {
            repository.updateFontSize(size)
        }
    }

    fun setArabicFont(font: String) {
        _arabicFont.value = font
        viewModelScope.launch {
            repository.updateArabicFont(font)
        }
    }

    fun setTranslationLanguage(language: String) {
        _translationLanguage.value = language
        viewModelScope.launch {
            repository.updateTranslationLanguage(language)
        }
    }

    fun setInfoTextSize(size: Float) {
        _infoTextSize.value = size
        viewModelScope.launch {
            repository.updateInfoTextSize(size)
        }
    }

    fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            // Update local state
            _isDarkMode.postValue(settings.isDarkMode)
            _fontSize.postValue(settings.fontSize)
            _arabicFont.postValue(settings.arabicFont)
            _translationLanguage.postValue(settings.translationLanguage)
            _infoTextSize.postValue(settings.infoTextSize)
        }
    }
}
