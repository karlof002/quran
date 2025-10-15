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
fun SinglePageView(
    pageNumber: Int,
    isDarkMode: Boolean,
    imageScaleFactor: Float,
    currentSurahInfo: Surah?,
    allSurahs: List<Surah>,
    infoTextSize: Int,
    onImageClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        QuranPageImage(
            pageNumber = pageNumber,
            isDarkMode = isDarkMode,
            onImageClick = onImageClick,
            scaleFactor = imageScaleFactor
        )

        // Bottom info bar - centered at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentSurahInfo != null) {
                val pageJuz = getJuzForPage(allSurahs, pageNumber)
                Text(
                    text = "صفحة ${convertToArabicNumbers(pageNumber)} - سورة ${currentSurahInfo.arabicName} - ${convertToArabicNumbers(currentSurahInfo.verses)} آيات - الجزء ${convertToArabicNumbers(pageJuz)}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = infoTextSize.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "صفحة ${convertToArabicNumbers(pageNumber)}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = infoTextSize.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
