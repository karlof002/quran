package com.karlof002.quran.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.ui.viewmodel.SettingsViewModel

/**
 * Utility functions for scaling font sizes and UI elements based on user settings
 */

// Base font size (what the app was designed with)
private const val BASE_FONT_SIZE = 16

/**
 * Calculate the scaling factor based on user's font size preference
 */
fun calculateScaleFactor(userFontSize: Int): Float {
    return userFontSize.toFloat() / BASE_FONT_SIZE
}

/**
 * Scale a font size based on user's preference
 */
fun scaleFontSize(baseFontSize: Int, userFontSize: Int): TextUnit {
    val scaleFactor = calculateScaleFactor(userFontSize)
    return (baseFontSize * scaleFactor).sp
}

/**
 * Scale a dimension (like image size, padding) based on user's font size preference
 * Images and other UI elements should scale proportionally but less aggressively than text
 */
fun scaleSize(baseSize: Int, userFontSize: Int): Dp {
    val scaleFactor = calculateScaleFactor(userFontSize)
    // Scale images and UI elements by square root of font scale for more balanced proportions
    val adjustedScaleFactor = kotlin.math.sqrt(scaleFactor.toDouble()).toFloat()
    return (baseSize * adjustedScaleFactor).dp
}

/**
 * Composable function to get scaled font size
 */
@Composable
fun getScaledFontSize(
    baseFontSize: Int,
    settingsViewModel: SettingsViewModel = viewModel()
): TextUnit {
    val userFontSize = settingsViewModel.fontSize.observeAsState(16).value
    return scaleFontSize(baseFontSize, userFontSize)
}

/**
 * Composable function to get scaled size for images and UI elements
 */
@Composable
fun getScaledSize(
    baseSize: Int,
    settingsViewModel: SettingsViewModel = viewModel()
): Dp {
    val userFontSize = settingsViewModel.fontSize.observeAsState(16).value
    return scaleSize(baseSize, userFontSize)
}

/**
 * Extension function to scale TextUnit based on user settings
 */
@Composable
fun Int.scaledSp(settingsViewModel: SettingsViewModel = viewModel()): TextUnit {
    return getScaledFontSize(this, settingsViewModel)
}

/**
 * Extension function to scale Dp based on user settings
 */
@Composable
fun Int.scaledDp(settingsViewModel: SettingsViewModel = viewModel()): Dp {
    return getScaledSize(this, settingsViewModel)
}
