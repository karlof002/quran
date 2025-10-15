package com.karlof002.quran.data.models.json

import com.google.gson.annotations.SerializedName

// Response wrapper for chapters.json
data class ChaptersResponse(
    val chapters: List<ChapterJson>
)

// JSON model for a chapter from Quran.com API
data class ChapterJson(
    val id: Int,
    @SerializedName("revelation_place")
    val revelationPlace: String,
    @SerializedName("name_arabic")
    val nameArabic: String,
    @SerializedName("name_simple")
    val nameSimple: String,
    @SerializedName("verses_count")
    val versesCount: Int,
    val pages: List<Int>,
    @SerializedName("translated_name")
    val translatedName: TranslatedName
)

data class TranslatedName(
    val name: String
)

// Response wrapper for juzs.json
data class JuzsResponse(
    val juzs: List<JuzJson>
)

// JSON model for a juz from Quran.com API
data class JuzJson(
    @SerializedName("juz_number")
    val juzNumber: Int,
    @SerializedName("verse_mapping")
    val verseMapping: Map<String, String>
)

