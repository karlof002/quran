package com.karlof002.quran.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karlof002.quran.QuranApplication
import com.karlof002.quran.data.models.Bookmark
import com.karlof002.quran.data.repository.QuranRepository
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuranRepository = (application as QuranApplication).repository
    val allBookmarks: LiveData<List<Bookmark>> = repository.getAllBookmarks()

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.addBookmark(bookmark)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.removeBookmark(bookmark)
        }
    }

    fun deleteBookmarkByPage(pageNumber: Int) {
        viewModelScope.launch {
            repository.removeBookmarkByPage(pageNumber)
        }
    }

    fun deleteBookmarks(bookmarks: List<Bookmark>) {
        viewModelScope.launch {
            bookmarks.forEach { bookmark ->
                repository.removeBookmark(bookmark)
            }
        }
    }

    suspend fun isPageBookmarked(pageNumber: Int): Boolean {
        return repository.isPageBookmarked(pageNumber)
    }

    // Export bookmarks to JSON file
    suspend fun exportBookmarks(context: Context, uri: Uri): Result<Int> {
        return try {
            val bookmarks = repository.getAllBookmarksSync()
            if (bookmarks.isEmpty()) {
                return Result.failure(Exception("No bookmarks to export"))
            }

            val gson = Gson()
            val json = gson.toJson(bookmarks)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                }
            }

            Result.success(bookmarks.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Import bookmarks from JSON file
    suspend fun importBookmarks(context: Context, uri: Uri, replaceExisting: Boolean = false): Result<Int> {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: return Result.failure(Exception("Could not read file"))

            val gson = Gson()
            val type = object : TypeToken<List<Bookmark>>() {}.type
            val bookmarks: List<Bookmark> = gson.fromJson(json, type)

            if (bookmarks.isEmpty()) {
                return Result.failure(Exception("No bookmarks found in file"))
            }

            // If replace existing, delete all current bookmarks first
            if (replaceExisting) {
                repository.removeAllBookmarks()
            }

            // Import bookmarks (reset IDs to let Room auto-generate new ones)
            val bookmarksToImport = bookmarks.map { it.copy(id = 0) }
            repository.addBookmarks(bookmarksToImport)

            Result.success(bookmarks.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
