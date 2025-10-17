package com.karlof002.quran.ui.screens.donation

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

@Composable
fun DonationPhoneLayout(
    heartScale: Float,
    context: Context,
    scope: CoroutineScope
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Animated Heart Icon
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn()
        ) {
            AnimatedHeartIcon(
                heartScale = heartScale,
                size = 100.dp,
                iconSize = 50.dp
            )
        }

        // Header
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 200))
        ) {
            DonationHeader()
        }

        // PayPal Button
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 400))
        ) {
            PayPalDonationCard(context = context, scope = scope)
        }

        // Thank you message
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 600))
        ) {
            ThankYouCard()
        }
    }
}
