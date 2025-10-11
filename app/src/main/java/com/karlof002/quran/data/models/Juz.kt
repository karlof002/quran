package com.karlof002.quran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juz")
data class Juz(
    @PrimaryKey
    val id: Int,
    val startSurah: Int,
    val startAyah: Int,
    val endSurah: Int,
    val endAyah: Int
)
