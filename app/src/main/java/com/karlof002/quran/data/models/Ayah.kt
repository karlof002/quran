package com.karlof002.quran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayahs")
data class Ayah(
    @PrimaryKey
    val id: Int,
    val surahId: Int,
    val ayahNumber: Int,
    val text: String,
    val translation: String? = null,
    val juzId: Int
)
