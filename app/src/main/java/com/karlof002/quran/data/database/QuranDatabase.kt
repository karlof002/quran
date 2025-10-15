package com.karlof002.quran.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.karlof002.quran.data.dao.*
import com.karlof002.quran.data.models.*

@Database(
    entities = [Surah::class, Juz::class, Ayah::class, Bookmark::class, Settings::class],
    version = 20,
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

        // Migration from version 16 to 17: Add infoTextSize column to settings table
        private val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column with a default value of 14
                database.execSQL("ALTER TABLE settings ADD COLUMN infoTextSize INTEGER NOT NULL DEFAULT 14")
            }
        }

        // Migration from version 17 to 18: Change fontSize and infoTextSize from INTEGER to REAL
        private val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQLite doesn't support ALTER COLUMN, so we need to recreate the table
                // 1. Create new table with correct types
                database.execSQL("""
                    CREATE TABLE settings_new (
                        id INTEGER PRIMARY KEY NOT NULL,
                        isDarkMode INTEGER NOT NULL,
                        fontSize REAL NOT NULL,
                        arabicFont TEXT NOT NULL,
                        translationLanguage TEXT NOT NULL,
                        infoTextSize REAL NOT NULL
                    )
                """)

                // 2. Copy data from old table to new table
                database.execSQL("""
                    INSERT INTO settings_new (id, isDarkMode, fontSize, arabicFont, translationLanguage, infoTextSize)
                    SELECT id, isDarkMode, CAST(fontSize AS REAL), arabicFont, translationLanguage, CAST(infoTextSize AS REAL)
                    FROM settings
                """)

                // 3. Drop old table
                database.execSQL("DROP TABLE settings")

                // 4. Rename new table to old name
                database.execSQL("ALTER TABLE settings_new RENAME TO settings")
            }
        }

        // Migration from version 18 to 19: Add primaryColor column to settings table
        private val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column with default teal green color
                database.execSQL("ALTER TABLE settings ADD COLUMN primaryColor INTEGER NOT NULL DEFAULT 16738909")
            }
        }

        // Migration from version 19 to 20: Remove primaryColor column from settings table
        private val MIGRATION_19_20 = object : Migration(19, 20) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQLite doesn't support DROP COLUMN, so we need to recreate the table
                // 1. Create new table without primaryColor
                database.execSQL("""
                    CREATE TABLE settings_new (
                        id INTEGER PRIMARY KEY NOT NULL,
                        isDarkMode INTEGER NOT NULL,
                        fontSize REAL NOT NULL,
                        arabicFont TEXT NOT NULL,
                        translationLanguage TEXT NOT NULL,
                        infoTextSize REAL NOT NULL
                    )
                """)

                // 2. Copy data from old table to new table (excluding primaryColor)
                database.execSQL("""
                    INSERT INTO settings_new (id, isDarkMode, fontSize, arabicFont, translationLanguage, infoTextSize)
                    SELECT id, isDarkMode, fontSize, arabicFont, translationLanguage, infoTextSize
                    FROM settings
                """)

                // 3. Drop old table
                database.execSQL("DROP TABLE settings")

                // 4. Rename new table to old name
                database.execSQL("ALTER TABLE settings_new RENAME TO settings")
            }
        }

        fun getDatabase(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_database"
                )
                    .addMigrations(MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20)
                    .fallbackToDestructiveMigration() // Only as last resort for other migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
