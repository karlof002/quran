package com.karlof002.quran

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.karlof002.quran.data.database.QuranDatabase
import com.karlof002.quran.data.repository.QuranRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuranApplication : Application(), ImageLoaderFactory {

    // Database and Repository
    val database by lazy { QuranDatabase.getDatabase(this) }
    val repository by lazy {
        QuranRepository(
            database.surahDao(),
            database.juzDao(),
            database.ayahDao(),
            database.bookmarkDao(),
            database.settingsDao(),
            this  // Pass application context
        )
    }

    override fun onCreate() {
        super.onCreate()

        // Setup crash handler for JNI errors
        setupCrashHandler()

        // Initialize database with data
        initializeDatabase()
    }

    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeData()
        }
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Log the crash but don't propagate JNI-related crashes from profiler
            if (throwable.message?.contains("GetStringUTFChars") == true ||
                throwable.message?.contains("nativeAttachAgent") == true) {
                // Ignore startup profiler crashes
                android.util.Log.e("QuranApplication", "Ignoring profiler crash", throwable)
            } else {
                // Pass other crashes to default handler
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of app's available memory
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("quran_image_cache"))
                    .maxSizeBytes(200 * 1024 * 1024) // 200 MB disk cache
                    .build()
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()
    }
}
