package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BangCyanAccent
import com.example.ui.theme.BangEmeraldMesh
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import com.example.ui.theme.CyberSlateBg
import kotlinx.coroutines.delay

/**
 * Cold Start Animation
 * Obsidian Slate background (#080C14) with a pulsing Electric Cyan (#00E5FF) logo bloom
 * that smoothly resolves into the main application.
 */
@Composable
fun BangColdStartSplash(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulsingBloom")

    // Pulse animation for Electric Cyan glow bloom
    val bloomScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bloomScale"
    )

    val bloomAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bloomAlpha"
    )

    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotation"
    )

    // Automatically transition to main app after warm-up
    LaunchedEffect(Unit) {
        delay(1250)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberSlateBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSplashComplete() }
            .testTag("cold_start_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Multi-ring Electric Cyan bloom canvas
        Canvas(modifier = Modifier.size(280.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.width / 2

            // Outer pulse wave ring
            drawCircle(
                color = BangCyanAccent.copy(alpha = (0.25f * bloomAlpha)),
                radius = maxRadius * bloomScale,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Middle tactical ring
            drawCircle(
                color = BangCyanAccent.copy(alpha = 0.4f),
                radius = maxRadius * 0.72f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Inner glowing bloom core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BangCyanAccent.copy(alpha = 0.45f * bloomAlpha),
                        BangCyanAccent.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 0.6f
                ),
                radius = maxRadius * 0.6f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Central Glowing Hex Monogram with BANG App Logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF070F1A))
                    .border(2.dp, BangCyanAccent.copy(alpha = bloomAlpha), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.app_logo),
                    contentDescription = "BANG App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(22.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Typographic Title: BANG
            Text(
                text = "BANG",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    letterSpacing = 6.sp,
                    color = BangTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "DECENTRALIZED HYBRID MESH",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = BangCyanAccent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF101B2B))
                    .border(1.dp, Color(0xFF1B2F4A), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BangEmeraldMesh)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "INITIALIZING ED25519 STRONG-BOX ENCLAVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.5.sp,
                        color = BangTextMuted
                    )
                )
            }
        }
    }
}
