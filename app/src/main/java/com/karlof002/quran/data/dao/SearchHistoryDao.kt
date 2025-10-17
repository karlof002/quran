package com.karlof002.quran.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.karlof002.quran.data.models.SearchHistory

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getRecentSearches(): LiveData<List<SearchHistory>>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    suspend fun getRecentSearchesSync(): List<SearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearch(id: Int)

    @Query("DELETE FROM search_history")
    suspend fun clearAllHistory()

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchByQuery(query: String)
}
