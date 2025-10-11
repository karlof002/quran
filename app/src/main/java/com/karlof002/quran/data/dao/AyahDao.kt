package com.karlof002.quran.data.dao

import androidx.room.*
import com.karlof002.quran.data.models.Ayah

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surahId = :surahId ORDER BY ayahNumber")
    suspend fun getAyahsBySurah(surahId: Int): List<Ayah>

    @Query("SELECT * FROM ayahs WHERE juzId = :juzId ORDER BY surahId, ayahNumber")
    suspend fun getAyahsByJuz(juzId: Int): List<Ayah>

    @Query("SELECT * FROM ayahs WHERE text LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'")
    suspend fun searchAyahs(query: String): List<Ayah>

    @Query("SELECT * FROM ayahs WHERE id = :id")
    suspend fun getAyahById(id: Int): Ayah?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ayahs: List<Ayah>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ayah: Ayah)
}
