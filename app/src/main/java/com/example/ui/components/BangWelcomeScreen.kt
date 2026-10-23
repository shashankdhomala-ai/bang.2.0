package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PortableWifiOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime
import com.example.ui.theme.BangObsidianBg

/**
 * Screen 1: Splash / Welcome Screen
 * Matches Phone 1 of the production design layout.
 */
@Composable
fun BangWelcomeScreen(
    onGetStarted: () -> Unit,
    onLogInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcomeBloom")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val ribbonRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ribbonRotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidianBg)
            .testTag("bang_welcome_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Center Hero: Geometric BANG Logo + Neon Aurora Bloom Ribbons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Aurora energy swirls
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width * 0.46f * pulseGlow

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x5599FF00),
                                    Color(0x3300E5FF),
                                    Color(0x117B2CBF),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = radius
                            ),
                            radius = radius,
                            center = center
                        )

                        // Orbiting ring
                        drawCircle(
                            color = BangElectricKineticLime.copy(alpha = 0.35f),
                            radius = size.width * 0.42f,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Dark Center Sphere with Neon Lightning Bolt
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C141F))
                            .border(2.dp, BangElectricKineticLime, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "BANG Logo",
                            tint = BangElectricKineticLime,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Brand Name
                Text(
                    text = "BANG",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 42.sp,
                        letterSpacing = 4.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline
                Text(
                    text = "Connect Beyond\nThe Internet",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4 Feature Badges in a Horizontal Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WelcomeFeatureBadge(icon = Icons.Default.PortableWifiOff, label = "Offline\nMessaging")
                    WelcomeFeatureBadge(icon = Icons.Default.Hub, label = "Mesh\nNetwork")
                    WelcomeFeatureBadge(icon = Icons.Default.Shield, label = "End-to-End\nEncryption")
                    WelcomeFeatureBadge(icon = Icons.Default.AutoAwesome, label = "AI\nAssistant")
                }
            }

            // Bottom Actions: Get Started button & Log In link
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onGetStarted,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BangElectricKineticLime,
                        contentColor = Color(0xFF070B11)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("welcome_btn_get_started")
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BangElectricKineticLime,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { onLogInClick() }
                            .padding(4.dp)
                            .testTag("welcome_link_login")
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeFeatureBadge(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(74.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1722))
                .border(1.dp, BangCharcoalBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BangElectricKineticLime,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        )
    }
}
