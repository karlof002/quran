package com.karlof002.quran.ui.screens.donation

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PayPalDonationCard(
    context: Context,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Animated Donate Button
        var buttonPressed by remember { mutableStateOf(false) }
        val buttonScale by animateFloatAsState(
            targetValue = if (buttonPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "button scale"
        )

        FilledTonalButton(
            onClick = {
                buttonPressed = true
                scope.launch {
                    delay(150)
                    buttonPressed = false

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://paypal.me/abdDevAT?country.x=AT&locale.x=de_DE".toUri()
                    )
                    context.startActivity(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(buttonScale),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Donate",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Donate with PayPal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
