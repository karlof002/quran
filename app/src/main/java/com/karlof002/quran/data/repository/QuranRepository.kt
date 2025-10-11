package com.karlof002.quran.data.repository

import androidx.lifecycle.LiveData
import com.karlof002.quran.data.dao.*
import com.karlof002.quran.data.models.*

class QuranRepository(
    private val surahDao: SurahDao,
    private val juzDao: JuzDao,
    private val bookmarkDao: BookmarkDao
) {
    // Surah operations
    fun getAllSurahs(): LiveData<List<Surah>> = surahDao.getAllSurahs()

    // Juz operations
    fun getAllJuz(): LiveData<List<Juz>> = juzDao.getAllJuz()

    // Bookmark operations
    fun getAllBookmarks(): LiveData<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    suspend fun addBookmark(bookmark: Bookmark) = bookmarkDao.insert(bookmark)
    suspend fun removeBookmark(bookmark: Bookmark) = bookmarkDao.delete(bookmark)
    suspend fun removeBookmarkByPage(pageNumber: Int) = bookmarkDao.deleteByPage(pageNumber)
    suspend fun isPageBookmarked(pageNumber: Int): Boolean = bookmarkDao.isPageBookmarked(pageNumber)

    // Data initialization
    suspend fun initializeData() {
        // Check if data already exists by counting surahs directly
        val surahCount = surahDao.getSurahCount()
        if (surahCount == 0) {
            // Only initialize if no data exists
            val sampleSurahs = getSurahs()
            val sampleJuz = getJuz()

            surahDao.insertAll(sampleSurahs)
            juzDao.insertAll(sampleJuz)
        }
    }

    private fun getSurahs(): List<Surah> {
        return listOf(
            Surah(1, "الفاتحة", "Al-Fatiha", "The Opener", 7, "Meccan", 1, 1),
            Surah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan", 1, 2),
            Surah(3, "آل عمران", "Al Imran", "Family of Imran", 200, "Medinan", 3, 50),
            Surah(4, "النساء", "An-Nisa", "The Women", 176, "Medinan", 4, 77),
            Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan", 6, 106),
            Surah(6, "الأنعام", "Al-An'am", "The Cattle", 165, "Meccan", 7, 128),
            Surah(7, "الأعراف", "Al-A'raf", "The Heights", 206, "Meccan", 8, 151),
            Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Medinan", 9, 177),
            Surah(9, "التوبة", "At-Tawbah", "The Repentance", 129, "Medinan", 10, 187),
            Surah(10, "يونس", "Yunus", "Jonah", 109, "Meccan", 11, 208),
            Surah(11, "هود", "Hud", "Hud", 123, "Meccan", 11, 221),
            Surah(12, "يوسف", "Yusuf", "Joseph", 111, "Meccan", 12, 235),
            Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", 43, "Medinan", 13, 249),
            Surah(14, "إبراهيم", "Ibrahim", "Abraham", 52, "Meccan", 13, 255),
            Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, "Meccan", 14, 262),
            Surah(16, "النحل", "An-Nahl", "The Bee", 128, "Meccan", 14, 267),
            Surah(17, "الإسراء", "Al-Isra", "The Night Journey", 111, "Meccan", 15, 282),
            Surah(18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan", 15, 293),
            Surah(19, "مريم", "Maryam", "Mary", 98, "Meccan", 16, 305),
            Surah(20, "طه", "Ta-Ha", "Ta-Ha", 135, "Meccan", 16, 312),
            Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, "Meccan", 17, 322),
            Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", 78, "Medinan", 17, 332),
            Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, "Meccan", 18, 342),
            Surah(24, "النور", "An-Nur", "The Light", 64, "Medinan", 18, 350),
            Surah(25, "الفرقان", "Al-Furqan", "The Criterion", 77, "Meccan", 18, 359),
            Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, "Meccan", 19, 367),
            Surah(27, "النمل", "An-Naml", "The Ant", 93, "Meccan", 19, 377),
            Surah(28, "القصص", "Al-Qasas", "The Stories", 88, "Meccan", 20, 385),
            Surah(29, "العنكبوت", "Al-Ankabut", "The Spider", 69, "Meccan", 20, 396),
            Surah(30, "الروم", "Ar-Rum", "The Byzantines", 60, "Meccan", 21, 404),
            Surah(31, "لقمان", "Luqman", "Luqman", 34, "Meccan", 21, 411),
            Surah(32, "السجدة", "As-Sajdah", "The Prostration", 30, "Meccan", 21, 415),
            Surah(33, "الأحزاب", "Al-Ahzab", "The Confederates", 73, "Medinan", 21, 418),
            Surah(34, "سبأ", "Saba", "Sheba", 54, "Meccan", 22, 428),
            Surah(35, "فاطر", "Fatir", "Originator", 45, "Meccan", 22, 434),
            Surah(36, "يس", "Ya-Sin", "Ya-Sin", 83, "Meccan", 22, 440),
            Surah(37, "الصافات", "As-Saffat", "Those who set the Ranks", 182, "Meccan", 23, 446),
            Surah(38, "ص", "Sad", "Sad", 88, "Meccan", 23, 453),
            Surah(39, "الزمر", "Az-Zumar", "The Troops", 75, "Meccan", 23, 458),
            Surah(40, "غافر", "Ghafir", "The Forgiver", 85, "Meccan", 24, 467),
            Surah(41, "فصلت", "Fussilat", "Explained in Detail", 54, "Meccan", 24, 477),
            Surah(42, "الشورى", "Ash-Shura", "The Consultation", 53, "Meccan", 25, 483),
            Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Meccan", 25, 489),
            Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", 59, "Meccan", 25, 496),
            Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, "Meccan", 25, 499),
            Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Meccan", 26, 502),
            Surah(47, "محمد", "Muhammad", "Muhammad", 38, "Medinan", 26, 507),
            Surah(48, "الفتح", "Al-Fath", "The Victory", 29, "Medinan", 26, 511),
            Surah(49, "الحجرات", "Al-Hujurat", "The Rooms", 18, "Medinan", 26, 515),
            Surah(50, "ق", "Qaf", "Qaf", 45, "Meccan", 26, 518),
            Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Meccan", 26, 520),
            Surah(52, "الطور", "At-Tur", "The Mount", 49, "Meccan", 27, 523),
            Surah(53, "النجم", "An-Najm", "The Star", 62, "Meccan", 27, 526),
            Surah(54, "القمر", "Al-Qamar", "The Moon", 55, "Meccan", 27, 528),
            Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Meccan", 27, 531),
            Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan", 27, 534),
            Surah(57, "الحديد", "Al-Hadid", "The Iron", 29, "Medinan", 27, 537),
            Surah(58, "المجادلة", "Al-Mujadilah", "The Pleading Woman", 22, "Medinan", 28, 542),
            Surah(59, "الحشر", "Al-Hashr", "The Exile", 24, "Medinan", 28, 545),
            Surah(60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", 13, "Medinan", 28, 549),
            Surah(61, "الصف", "As-Saff", "The Ranks", 14, "Medinan", 28, 551),
            Surah(62, "الجمعة", "Al-Jumu'ah", "Friday", 11, "Medinan", 28, 553),
            Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Medinan", 28, 554),
            Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, "Medinan", 28, 556),
            Surah(65, "الطلاق", "At-Talaq", "The Divorce", 12, "Medinan", 28, 558),
            Surah(66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Medinan", 28, 560),
            Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan", 29, 562),
            Surah(68, "القلم", "Al-Qalam", "The Pen", 52, "Meccan", 29, 564),
            Surah(69, "الحاقة", "Al-Haqqah", "The Reality", 52, "Meccan", 29, 566),
            Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Meccan", 29, 568),
            Surah(71, "نوح", "Nuh", "Noah", 28, "Meccan", 29, 570),
            Surah(72, "الجن", "Al-Jinn", "The Jinn", 28, "Meccan", 29, 572),
            Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Meccan", 29, 574),
            Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Meccan", 29, 575),
            Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Meccan", 29, 577),
            Surah(76, "الإنسان", "Al-Insan", "The Human", 31, "Medinan", 29, 578),
            Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Meccan", 29, 580),
            Surah(78, "النبأ", "An-Naba", "The Tidings", 40, "Meccan", 30, 582),
            Surah(79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Meccan", 30, 583),
            Surah(80, "عبس", "Abasa", "He frowned", 42, "Meccan", 30, 585),
            Surah(81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Meccan", 30, 586),
            Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, "Meccan", 30, 587),
            Surah(83, "المطففين", "Al-Mutaffifin", "The Defrauding", 36, "Meccan", 30, 587),
            Surah(84, "الانشقاق", "Al-Inshiqaq", "The Sundering", 25, "Meccan", 30, 589),
            Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Meccan", 30, 590),
            Surah(86, "الطارق", "At-Tariq", "The Morning Star", 17, "Meccan", 30, 591),
            Surah(87, "الأعلى", "Al-A'la", "The Most High", 19, "Meccan", 30, 591),
            Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, "Meccan", 30, 592),
            Surah(89, "الفجر", "Al-Fajr", "The Dawn", 30, "Meccan", 30, 593),
            Surah(90, "البلد", "Al-Balad", "The City", 20, "Meccan", 30, 594),
            Surah(91, "الشمس", "Ash-Shams", "The Sun", 15, "Meccan", 30, 595),
            Surah(92, "الليل", "Al-Layl", "The Night", 21, "Meccan", 30, 595),
            Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", 11, "Meccan", 30, 596),
            Surah(94, "الشرح", "Ash-Sharh", "The Relief", 8, "Meccan", 30, 596),
            Surah(95, "التين", "At-Tin", "The Fig", 8, "Meccan", 30, 597),
            Surah(96, "العلق", "Al-Alaq", "The Clot", 19, "Meccan", 30, 597),
            Surah(97, "القدر", "Al-Qadr", "The Power", 5, "Meccan", 30, 598),
            Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, "Medinan", 30, 598),
            Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Medinan", 30, 599),
            Surah(100, "العاديات", "Al-Adiyat", "The Courser", 11, "Meccan", 30, 599),
            Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Meccan", 30, 600),
            Surah(102, "التكاثر", "At-Takathur", "The Rivalry in world increase", 8, "Meccan", 30, 600),
            Surah(103, "العصر", "Al-Asr", "The Declining Day", 3, "Meccan", 30, 601),
            Surah(104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Meccan", 30, 601),
            Surah(105, "الفيل", "Al-Fil", "The Elephant", 5, "Meccan", 30, 601),
            Surah(106, "قريش", "Quraysh", "Quraysh", 4, "Meccan", 30, 602),
            Surah(107, "الماعون", "Al-Ma'un", "The Small kindnesses", 7, "Meccan", 30, 602),
            Surah(108, "الكوثر", "Al-Kawthar", "The Abundance", 3, "Meccan", 30, 602),
            Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Meccan", 30, 603),
            Surah(110, "النصر", "An-Nasr", "The Divine Support", 3, "Medinan", 30, 603),
            Surah(111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Meccan", 30, 603),
            Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan", 30, 604),
            Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan", 30, 604),
            Surah(114, "الناس", "An-Nas", "Mankind", 6, "Meccan", 30, 604)
        )
    }

    private fun getJuz(): List<Juz> {
        return listOf(
            Juz(1, 1, 1, 2, 141),
            Juz(2, 2, 142, 2, 252),
            Juz(3, 2, 253, 3, 92),
            Juz(4, 3, 93, 4, 23),
            Juz(5, 4, 24, 4, 147),
            Juz(6, 4, 148, 5, 81),
            Juz(7, 5, 82, 6, 110),
            Juz(8, 6, 111, 7, 87),
            Juz(9, 7, 88, 8, 40),
            Juz(10, 8, 41, 9, 92),
            Juz(11, 9, 93, 11, 5),
            Juz(12, 11, 6, 12, 52),
            Juz(13, 12, 53, 14, 52),
            Juz(14, 15, 1, 16, 128),
            Juz(15, 17, 1, 18, 74),
            Juz(16, 18, 75, 20, 135),
            Juz(17, 21, 1, 22, 78),
            Juz(18, 23, 1, 25, 20),
            Juz(19, 25, 21, 27, 55),
            Juz(20, 27, 56, 29, 45),
            Juz(21, 29, 46, 33, 30),
            Juz(22, 33, 31, 36, 27),
            Juz(23, 36, 28, 39, 31),
            Juz(24, 39, 32, 41, 46),
            Juz(25, 41, 47, 45, 37),
            Juz(26, 46, 1, 51, 30),
            Juz(27, 51, 31, 57, 29),
            Juz(28, 58, 1, 66, 12),
            Juz(29, 67, 1, 77, 50),
            Juz(30, 78, 1, 114, 6)
        )
    }
}
