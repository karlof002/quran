package com.karlof002.quran.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karlof002.quran.ui.screens.search.*
import com.karlof002.quran.ui.viewmodel.HomeViewModel

@Composable
fun SearchScreen(
    viewModel: HomeViewModel = viewModel(),
    onSurahClick: (Int, Int, String, Int, Int) -> Unit = { _, _, _, _, _ -> }
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Inactive search view with placeholder search bar
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(
                animationSpec = tween(ENTER_DURATION, easing = LinearOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(
                animationSpec = tween(EXIT_DURATION, easing = FastOutLinearInEasing)
            ) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(EXIT_DURATION, easing = LinearOutSlowInEasing)
            )
        ) {
            MainSearchView(
                onSearchBarClick = { isSearchActive = true }
            )
        }

        // Active search overlay with full functionality
        AnimatedVisibility(
            visible = isSearchActive,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = ENTER_DURATION,
                    easing = LinearOutSlowInEasing
                )
            ) + slideInVertically(
                initialOffsetY = { it / 6 },
                animationSpec = tween(
                    durationMillis = ENTER_DURATION,
                    easing = EmphasizedDecelerate
                )
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    durationMillis = ENTER_DURATION,
                    easing = EmphasizedDecelerate
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = EXIT_DURATION,
                    easing = FastOutLinearInEasing
                )
            ) + slideOutVertically(
                targetOffsetY = { it / 6 },
                animationSpec = tween(
                    durationMillis = EXIT_DURATION,
                    easing = EmphasizedAccelerate
                )
            ) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(
                    durationMillis = EXIT_DURATION,
                    easing = EmphasizedAccelerate
                )
            )
        ) {
            ActiveSearchView(
                viewModel = viewModel,
                onClose = { isSearchActive = false },
                onSurahClick = { surahId, page, name, verses, juz ->
                    isSearchActive = false
                    onSurahClick(surahId, page, name, verses, juz)
                }
            )
        }
    }
}
