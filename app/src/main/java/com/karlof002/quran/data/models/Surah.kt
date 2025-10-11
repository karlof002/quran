package com.karlof002.quran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class Surah(
    @PrimaryKey
    val id: Int,
    val arabicName: String,
    val transliteration: String,
    val translation: String,
    val verses: Int,
    val revelation: String, // "Meccan" or "Medinan"
    val juz: Int,
    val startPage: Int
)
