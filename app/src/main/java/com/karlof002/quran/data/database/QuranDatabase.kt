package com.karlof002.quran.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.karlof002.quran.data.dao.*
import com.karlof002.quran.data.models.*

@Database(
    entities = [Surah::class, Juz::class, Ayah::class, Bookmark::class, Settings::class],
    version = 16,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun juzDao(): JuzDao
    abstract fun ayahDao(): AyahDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: QuranDatabase? = null

        fun getDatabase(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_database"
                )
                    .fallbackToDestructiveMigration() // Allow destructive migration for development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
