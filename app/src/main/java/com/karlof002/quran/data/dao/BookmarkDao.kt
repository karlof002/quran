package com.karlof002.quran.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.karlof002.quran.data.models.Bookmark

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): LiveData<List<Bookmark>>

    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    suspend fun getAllBookmarksSync(): List<Bookmark>

    @Query("SELECT * FROM bookmarks WHERE pageNumber = :pageNumber LIMIT 1")
    suspend fun getBookmarkByPage(pageNumber: Int): Bookmark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookmarks: List<Bookmark>)

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE pageNumber = :pageNumber")
    suspend fun deleteByPage(pageNumber: Int)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE pageNumber = :pageNumber)")
    suspend fun isPageBookmarked(pageNumber: Int): Boolean
}
