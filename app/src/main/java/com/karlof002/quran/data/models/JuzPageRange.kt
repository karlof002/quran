package com.karlof002.quran.data.models

data class JuzPageRange(
    val juzNumber: Int,
    val startPage: Int,
    val endPage: Int
)

object JuzPageMappings {
    val JUZ_PAGE_RANGES = listOf(
        JuzPageRange(1, 1, 21),       // Juz 1
        JuzPageRange(2, 22, 41),      // Juz 2
        JuzPageRange(3, 42, 61),      // Juz 3
        JuzPageRange(4, 62, 81),      // Juz 4
        JuzPageRange(5, 82, 101),     // Juz 5
        JuzPageRange(6, 102, 121),    // Juz 6
        JuzPageRange(7, 122, 141),    // Juz 7
        JuzPageRange(8, 142, 161),    // Juz 8
        JuzPageRange(9, 162, 181),    // Juz 9
        JuzPageRange(10, 182, 201),   // Juz 10
        JuzPageRange(11, 202, 221),   // Juz 11
        JuzPageRange(12, 222, 241),   // Juz 12
        JuzPageRange(13, 242, 261),   // Juz 13
        JuzPageRange(14, 262, 281),   // Juz 14
        JuzPageRange(15, 282, 301),   // Juz 15
        JuzPageRange(16, 302, 321),   // Juz 16
        JuzPageRange(17, 322, 341),   // Juz 17
        JuzPageRange(18, 342, 361),   // Juz 18
        JuzPageRange(19, 362, 381),   // Juz 19
        JuzPageRange(20, 382, 401),   // Juz 20
        JuzPageRange(21, 402, 421),   // Juz 21
        JuzPageRange(22, 422, 441),   // Juz 22
        JuzPageRange(23, 442, 461),   // Juz 23
        JuzPageRange(24, 462, 481),   // Juz 24
        JuzPageRange(25, 482, 501),   // Juz 25
        JuzPageRange(26, 502, 521),   // Juz 26
        JuzPageRange(27, 522, 541),   // Juz 27
        JuzPageRange(28, 542, 561),   // Juz 28
        JuzPageRange(29, 562, 581),   // Juz 29
        JuzPageRange(30, 582, 604)    // Juz 30
    )

    fun getPageRangeForJuz(juzNumber: Int): JuzPageRange? {
        return JUZ_PAGE_RANGES.find { it.juzNumber == juzNumber }
    }
}
