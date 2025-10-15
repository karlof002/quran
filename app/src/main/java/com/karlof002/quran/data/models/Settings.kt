package com.karlof002.quran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1, // Single row for settings
    val isDarkMode: Boolean = false,
    val fontSize: Float = 16f,
    val arabicFont: String = "Default",
    val translationLanguage: String = "English",
    val infoTextSize: Float = 14f
)
