package com.karlof002.quran.ui.screens.search

import androidx.compose.animation.core.CubicBezierEasing

// Gmail-style animation durations
internal const val ENTER_DURATION = 350
internal const val EXIT_DURATION = 200
internal const val CONTENT_ENTER_DURATION = 400
internal const val CONTENT_EXIT_DURATION = 150

// Enhanced Material Design 3 easing curves
internal val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
internal val EaseInCubic = CubicBezierEasing(0.32f, 0f, 0.67f, 0f)
internal val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
internal val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
internal val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

