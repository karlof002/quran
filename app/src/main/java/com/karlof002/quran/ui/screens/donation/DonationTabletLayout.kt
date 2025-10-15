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
fun DonationTabletLayout(
    heartScale: Float,
    context: Context,
    scope: CoroutineScope
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .widthIn(max = 900.dp)
            .verticalScroll(scrollState)
            .padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left column - Icon and description
        Column(
            modifier = Modifier.weight(1f),
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
                    size = 120.dp,
                    iconSize = 60.dp
                )
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                        slideInVertically(animationSpec = tween(600, delayMillis = 200))
            ) {
                DonationHeader()
            }
        }

        // Right column - Donation cards
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // PayPal Card
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(600, delayMillis = 300)
                        )
            ) {
                PayPalDonationCard(context = context, scope = scope)
            }

            // Thank you card
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 500)) +
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(600, delayMillis = 500)
                        )
            ) {
                ThankYouCard()
            }
        }
    }
}

