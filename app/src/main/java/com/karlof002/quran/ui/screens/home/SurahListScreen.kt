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
fun SurahListScreen(
    viewModel: HomeViewModel,
    onSurahClick: (Int, Int, String, Int, Int) -> Unit = { _, _, _, _, _ -> },
    windowSize: WindowSizeClass = WindowSizeClass.COMPACT
) {
    val allSurahs by viewModel.allSurahs.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Memoize the grouped surahs with derivedStateOf for better performance
    val surahsByJuz by remember {
        derivedStateOf {
            allSurahs.groupBy { it.juz }.toSortedMap()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (allSurahs.isEmpty()) {
            Text(
                "No surahs available",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                surahsByJuz.forEach { (juzNumber, surahs) ->
                    // Juz Header
                    item(key = "juz_header_$juzNumber") {
                        JuzHeaderItem(juzNumber)
                    }

                    // Surahs in this Juz
                    items(
                        count = surahs.size,
                        key = { index -> "surah_${surahs[index].id}" }
                    ) { index ->
                        val surah = surahs[index]
                        SurahListItem(
                            surah = surah,
                            onClick = {
                                onSurahClick(surah.id, surah.startPage, surah.arabicName, surah.verses, surah.juz)
                            },
                            windowSize = windowSize
                        )

                        if (index < surahs.size - 1) {
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
}

