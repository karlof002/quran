package com.karlof002.quran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.ui.viewmodel.HomeViewModel
import com.karlof002.quran.ui.utils.WindowSizeClass
import com.karlof002.quran.ui.screens.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onSurahClick: (Int, Int, String, Int, Int) -> Unit = { _, _, _, _, _ -> },
    onJuzClick: (Int, Int, Int) -> Unit = { _, _, _ -> },
    windowSize: WindowSizeClass = WindowSizeClass.COMPACT
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Surahs", "Juz")

    val titleFontSize = when (windowSize) {
        WindowSizeClass.COMPACT -> 24.sp
        WindowSizeClass.MEDIUM -> 28.sp
        WindowSizeClass.EXPANDED -> 32.sp
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)

            ) {
                // Top App Bar
                TopAppBar(
                    title = {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {

                            Text(
                                "Quran Al-Kareem",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = titleFontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Material 3 Tab Row
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> SurahListScreen(viewModel, onSurahClick, windowSize)
                1 -> JuzListScreen(viewModel, onJuzClick, windowSize)
            }
        }
    }
}
