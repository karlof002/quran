package com.karlof002.quran.ui.screens.reader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karlof002.quran.data.models.Surah

@Composable
fun PageInfoBar(
    pageNumber: Int,
    surahInfo: Surah?,
    allSurahs: List<Surah>,
    infoTextSize: Int
) {
    val pageJuz = getJuzForPage(allSurahs, pageNumber)

    Text(
        text = if (surahInfo != null) {
            "صفحة ${convertToArabicNumbers(pageNumber)} - سورة ${surahInfo.arabicName} - ${convertToArabicNumbers(surahInfo.verses)} آيات - الجزء ${convertToArabicNumbers(pageJuz)}"
        } else {
            "صفحة ${convertToArabicNumbers(pageNumber)}"
        },
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        fontSize = infoTextSize.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun DualPageInfoBar(
    leftPageNumber: Int,
    rightPageNumber: Int,
    allSurahs: List<Surah>,
    infoTextSize: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left page info (displayed on LEFT - higher page number) - centered in its half
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (leftPageNumber <= 604) {
                val leftPageSurah = findSurahForPage(allSurahs, leftPageNumber)
                val leftPageJuz = getJuzForPage(allSurahs, leftPageNumber)
                Text(
                    text = if (leftPageSurah != null) {
                        "صفحة ${convertToArabicNumbers(leftPageNumber)} - سورة ${leftPageSurah.arabicName} - ${convertToArabicNumbers(leftPageSurah.verses)} آيات - الجزء ${convertToArabicNumbers(leftPageJuz)}"
                    } else {
                        "صفحة ${convertToArabicNumbers(leftPageNumber)}"
                    },
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = infoTextSize.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Small divider between page infos
        Spacer(modifier = Modifier.width(8.dp))

        // Right page info (displayed on RIGHT - lower page number) - centered in its half
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (rightPageNumber <= 604) {
                val rightPageSurah = findSurahForPage(allSurahs, rightPageNumber)
                val rightPageJuz = getJuzForPage(allSurahs, rightPageNumber)
                Text(
                    text = if (rightPageSurah != null) {
                        "صفحة ${convertToArabicNumbers(rightPageNumber)} - سورة ${rightPageSurah.arabicName} - ${convertToArabicNumbers(rightPageSurah.verses)} آيات - الجزء ${convertToArabicNumbers(rightPageJuz)}"
                    } else {
                        "صفحة ${convertToArabicNumbers(rightPageNumber)}"
                    },
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = infoTextSize.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
