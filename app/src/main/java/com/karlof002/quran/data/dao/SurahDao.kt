package com.karlof002.quran.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.karlof002.quran.data.models.Surah

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY id")
    fun getAllSurahs(): LiveData<List<Surah>>

    @Query("SELECT * FROM surahs WHERE id = :id")
    suspend fun getSurahById(id: Int): Surah?

    @Query("""
        SELECT * FROM surahs 
        WHERE startPage <= :pageNumber 
        AND (id = 114 OR :pageNumber < (SELECT startPage FROM surahs WHERE id = surahs.id + 1))
        LIMIT 1
    """)
    suspend fun getSurahByPage(pageNumber: Int): Surah?

    @Query("SELECT * FROM surahs WHERE arabicName LIKE '%' || :query || '%' OR transliteration LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'")
    suspend fun searchSurahs(query: String): List<Surah>

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(surahs: List<Surah>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(surah: Surah)

    @Query("DELETE FROM surahs")
    suspend fun deleteAll()
}
