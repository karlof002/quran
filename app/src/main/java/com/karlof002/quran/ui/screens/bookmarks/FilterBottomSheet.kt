package com.karlof002.quran.ui.screens.bookmarks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    selectedFilter: DateFilter,
    onFilterSelected: (DateFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Filter by Date",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            FilterOption(
                title = "All Bookmarks",
                isSelected = selectedFilter == DateFilter.ALL,
                onClick = {
                    scope.launch {
                        onFilterSelected(DateFilter.ALL)
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            FilterOption(
                title = "Today",
                isSelected = selectedFilter == DateFilter.TODAY,
                onClick = {
                    scope.launch {
                        onFilterSelected(DateFilter.TODAY)
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            FilterOption(
                title = "This Week",
                isSelected = selectedFilter == DateFilter.THIS_WEEK,
                onClick = {
                    scope.launch {
                        onFilterSelected(DateFilter.THIS_WEEK)
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            FilterOption(
                title = "This Month",
                isSelected = selectedFilter == DateFilter.THIS_MONTH,
                onClick = {
                    scope.launch {
                        onFilterSelected(DateFilter.THIS_MONTH)
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            FilterOption(
                title = "Older",
                isSelected = selectedFilter == DateFilter.OLDER,
                onClick = {
                    scope.launch {
                        onFilterSelected(DateFilter.OLDER)
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )
        }
    }
}

@Composable
fun FilterOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

