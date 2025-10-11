package com.karlof002.quran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1, // Single row for settings
    val isDarkMode: Boolean = false,
    val fontSize: Int = 16,
    val arabicFont: String = "Default",
    val translationLanguage: String = "English"
)
