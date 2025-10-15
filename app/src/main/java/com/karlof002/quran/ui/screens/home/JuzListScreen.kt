package com.karlof002.quran.ui.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karlof002.quran.ui.viewmodel.HomeViewModel
import com.karlof002.quran.ui.utils.WindowSizeClass

@Composable
fun JuzListScreen(
    viewModel: HomeViewModel,
    onJuzClick: (Int, Int, Int) -> Unit = { _, _, _ -> },
    windowSize: WindowSizeClass = WindowSizeClass.COMPACT
) {
    val allJuz by viewModel.allJuz.observeAsState(emptyList())
    val allSurahs by viewModel.allSurahs.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (allJuz.isEmpty()) {
            Text(
                "No juz available",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = allJuz.size,
                    key = { index -> "juz_${allJuz[index].id}" }
                ) { index ->
                    val juz = allJuz[index]
                    // Find start and end surah names - memoized per item
                    val startSurah = remember(juz.startSurah, allSurahs) {
                        allSurahs.find { it.id == juz.startSurah }
                    }
                    val endSurah = remember(juz.endSurah, allSurahs) {
                        allSurahs.find { it.id == juz.endSurah }
                    }

                    JuzListItem(
                        juz = juz,
                        startSurahName = startSurah?.arabicName ?: "",
                        endSurahName = endSurah?.arabicName ?: "",
                        onClick = {
                            // Calculate page range dynamically from surah data
                            val firstSurahInJuz = allSurahs.find { it.juz == juz.id }
                            val startPage = firstSurahInJuz?.startPage ?: 1

                            // Find the last surah in this juz to calculate end page
                            val surahsInThisJuz = allSurahs.filter { it.juz == juz.id }
                            val lastSurahInJuz = surahsInThisJuz.maxByOrNull { it.startPage }

                            // Calculate end page (start of next surah - 1, or 604 if last juz)
                            val sortedSurahs = allSurahs.sortedBy { it.startPage }
                            val lastSurahIndex = sortedSurahs.indexOfFirst { it.id == lastSurahInJuz?.id }
                            val endPage = if (lastSurahIndex >= 0 && lastSurahIndex < sortedSurahs.size - 1) {
                                sortedSurahs[lastSurahIndex + 1].startPage - 1
                            } else {
                                604
                            }

                            onJuzClick(juz.id, startPage, endPage)
                        },
                        windowSize = windowSize
                    )

                    if (index < allJuz.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

