package com.karlof002.quran.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.karlof002.quran.data.models.Juz

@Dao
interface JuzDao {
    @Query("SELECT * FROM juz ORDER BY id")
    fun getAllJuz(): LiveData<List<Juz>>

    @Query("SELECT * FROM juz WHERE id = :id")
    suspend fun getJuzById(id: Int): Juz?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(juzList: List<Juz>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(juz: Juz)

    @Query("DELETE FROM juz")
    suspend fun deleteAll()
}
