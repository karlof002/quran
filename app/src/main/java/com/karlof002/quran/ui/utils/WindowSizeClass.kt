package com.karlof002.quran.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT,  // Phone in portrait
    MEDIUM,   // Tablet in portrait or phone in landscape
    EXPANDED  // Tablet in landscape
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    return when {
        screenWidth < 600.dp -> WindowSizeClass.COMPACT
        screenWidth < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

enum class NavigationType {
    BOTTOM_NAVIGATION,
    NAVIGATION_RAIL,
    PERMANENT_NAVIGATION_DRAWER
}

@Composable
fun getNavigationType(windowSize: WindowSizeClass): NavigationType {
    return when (windowSize) {
        WindowSizeClass.COMPACT -> NavigationType.BOTTOM_NAVIGATION
        WindowSizeClass.MEDIUM -> NavigationType.NAVIGATION_RAIL
        WindowSizeClass.EXPANDED -> NavigationType.NAVIGATION_RAIL
    }
}

enum class ContentType {
    SINGLE_PANE,
    DUAL_PANE
}

@Composable
fun getContentType(windowSize: WindowSizeClass): ContentType {
    return when (windowSize) {
        WindowSizeClass.COMPACT -> ContentType.SINGLE_PANE
        WindowSizeClass.MEDIUM -> ContentType.SINGLE_PANE
        WindowSizeClass.EXPANDED -> ContentType.DUAL_PANE
    }
}

