package com.karlof002.quran.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.karlof002.quran.QuranApplication
import com.karlof002.quran.data.models.Bookmark
import com.karlof002.quran.data.repository.QuranRepository
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuranRepository = (application as QuranApplication).repository
    val allBookmarks: LiveData<List<Bookmark>>

    init {
        allBookmarks = repository.getAllBookmarks()
    }

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

    suspend fun isPageBookmarked(pageNumber: Int): Boolean {
        return repository.isPageBookmarked(pageNumber)
    }
}
