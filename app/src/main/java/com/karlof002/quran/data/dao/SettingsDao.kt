package com.karlof002.quran.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.karlof002.quran.data.models.Settings

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettings(): LiveData<Settings?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)

    @Update
    suspend fun updateSettings(settings: Settings)

    @Query("UPDATE settings SET isDarkMode = :isDarkMode WHERE id = 1")
    suspend fun updateDarkMode(isDarkMode: Boolean)

    @Query("UPDATE settings SET fontSize = :fontSize WHERE id = 1")
    suspend fun updateFontSize(fontSize: Float)

    @Query("UPDATE settings SET arabicFont = :arabicFont WHERE id = 1")
    suspend fun updateArabicFont(arabicFont: String)

    @Query("UPDATE settings SET translationLanguage = :language WHERE id = 1")
    suspend fun updateTranslationLanguage(language: String)

    @Query("UPDATE settings SET infoTextSize = :infoTextSize WHERE id = 1")
    suspend fun updateInfoTextSize(infoTextSize: Float)
}
