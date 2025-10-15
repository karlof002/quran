package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RatingViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("app_preferences", 0)

    private val _showRatingDialog = MutableLiveData(false)
    val showRatingDialog = _showRatingDialog

    private val _hasUserRated = MutableLiveData(false)
    val hasUserRated = _hasUserRated

    init {
        checkAndIncrementLaunchCount()
    }

    private fun checkAndIncrementLaunchCount() {
        viewModelScope.launch {
            val currentCount = sharedPreferences.getInt("launch_count", 0)
            val hasRated = sharedPreferences.getBoolean("has_rated", false)
            val hasDeclined = sharedPreferences.getBoolean("has_declined_rating", false)

            _hasUserRated.value = hasRated

            // Increment launch count
            val newCount = currentCount + 1
            sharedPreferences.edit { putInt("launch_count", newCount) }

            // Show rating dialog exactly on second launch if user hasn't rated or declined
            if (newCount == 2 && !hasRated && !hasDeclined) {
                _showRatingDialog.value = true
            }
        }
    }

    fun onUserRated() {
        sharedPreferences.edit { putBoolean("has_rated", true) }
        _hasUserRated.value = true
        _showRatingDialog.value = false
    }

    fun onUserDeclined() {
        sharedPreferences.edit { putBoolean("has_declined_rating", true) }
        _showRatingDialog.value = false
    }

    fun dismissDialog() {
        _showRatingDialog.value = false
    }
}
