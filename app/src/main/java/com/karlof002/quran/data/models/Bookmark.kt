package com.karlof002.quran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pageNumber: Int,
    val surahName: String,
    val surahId: Int,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
