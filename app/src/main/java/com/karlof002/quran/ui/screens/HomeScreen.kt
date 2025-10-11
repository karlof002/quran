package com.karlof002.quran.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.data.models.Juz
import com.karlof002.quran.data.models.Surah
import com.karlof002.quran.ui.viewmodel.HomeViewModel
import com.karlof002.quran.ui.utils.WindowSizeClass

private val MeccanBg = Color(0xFFFEF3C7)
private val MeccanText = Color(0xFF92400E)
private val MedinanBg = Color(0xFFDCFCE7)
private val MedinanText = Color(0xFF166534)

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

    // Adaptive padding and sizing based on screen size
    val horizontalPadding = when (windowSize) {
        WindowSizeClass.COMPACT -> 0.dp
        WindowSizeClass.MEDIUM -> 16.dp
        WindowSizeClass.EXPANDED -> 32.dp
    }

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
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
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
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                            // Get page range for this Juz
                            val pageRange = com.karlof002.quran.data.models.JuzPageMappings.getPageRangeForJuz(juz.id)
                            if (pageRange != null) {
                                onJuzClick(juz.id, pageRange.startPage, pageRange.endPage)
                            }
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

@Composable
private fun JuzHeaderItem(juzNumber: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Juz $juzNumber",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SurahListItem(
    surah: Surah,
    onClick: () -> Unit,
    windowSize: WindowSizeClass = WindowSizeClass.COMPACT
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Surah Number Badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = surah.id.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Surah Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Arabic Name & Transliteration
            Column {
                Text(
                    text = "سورة ${surah.arabicName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Surah ${surah.transliteration}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Meta Information
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Revelation Type Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (surah.revelation) {
                                "Meccan" -> MeccanBg
                                else -> MedinanBg
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = surah.revelation,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (surah.revelation) {
                            "Meccan" -> MeccanText
                            else -> MedinanText
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )

                Text(
                    text = "${surah.verses} Verses",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Arrow Icon
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun JuzListItem(
    juz: Juz,
    startSurahName: String,
    endSurahName: String,
    onClick: () -> Unit,
    windowSize: WindowSizeClass = WindowSizeClass.COMPACT
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Juz Number Badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = juz.id.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // Juz Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Juz Title
            Text(
                text = "Juz ${juz.id}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Range Information
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (startSurahName.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "From:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "سورة $startSurahName (${juz.startSurah}:${juz.startAyah})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (endSurahName.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "To:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "سورة $endSurahName (${juz.endSurah}:${juz.endAyah})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Arrow Icon
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}
