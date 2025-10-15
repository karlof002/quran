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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Animated Heart Icon
        AnimatedHeartIcon(
            heartScale = heartScale,
            size = 100.dp,
            iconSize = 50.dp
        )

        // Title with animated visibility
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically()
        ) {
            DonationHeader()
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated PayPal Card
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 300))
        ) {
            PayPalDonationCard(context = context, scope = scope)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated thank you message
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 500)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 500))
        ) {
            ThankYouCard()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

