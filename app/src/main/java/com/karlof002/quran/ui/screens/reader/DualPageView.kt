package com.karlof002.quran.ui.screens.reader

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karlof002.quran.data.models.Surah

@Composable
fun DualPageView(
    leftPageNumber: Int,
    rightPageNumber: Int,
    isDarkMode: Boolean,
    imageScaleFactor: Float,
    allSurahs: List<Surah>,
    infoTextSize: Int,
    onImageClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Left page (displayed on LEFT - higher page number)
            if (leftPageNumber <= 604) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    QuranPageImage(
                        pageNumber = leftPageNumber,
                        isDarkMode = isDarkMode,
                        onImageClick = onImageClick,
                        scaleFactor = imageScaleFactor
                    )
                }
            }

            // Small divider between pages
            Spacer(modifier = Modifier.width(4.dp))

            // Right page (displayed on RIGHT - lower page number)
            if (rightPageNumber <= 604) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    QuranPageImage(
                        pageNumber = rightPageNumber,
                        isDarkMode = isDarkMode,
                        onImageClick = onImageClick,
                        scaleFactor = imageScaleFactor
                    )
                }
            }
        }

        // Bottom info bar - centered at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DualPageInfoBar(
                leftPageNumber = leftPageNumber,
                rightPageNumber = rightPageNumber,
                allSurahs = allSurahs,
                infoTextSize = infoTextSize
            )
        }
    }
}
