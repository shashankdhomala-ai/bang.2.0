package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Next-Gen Cyber-Obsidian UI Canvas.
 * Renders an obsidian space (#080C14) with structural cyber grid lines (#1E293B)
 * and dynamic glowing vector nodes that emit light rings whenever encrypted packets pass through the mesh.
 */
@Composable
fun ObsidianParticleMeshBackground(
    isPacketRelaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
    ) {
        val width = size.width
        val height = size.height

        // 1. Draw structural cyber grid lines (#1E293B)
        val step = 80.dp.toPx()
        var x = 0f
        while (x <= width) {
            drawLine(
                color = Color(0xFF1E293B).copy(alpha = 0.45f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 0.8f
            )
            x += step
        }

        var y = 0f
        while (y <= height) {
            drawLine(
                color = Color(0xFF1E293B).copy(alpha = 0.45f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 0.8f
            )
            y += step
        }

        // 2. Center ambient radial glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    if (isPacketRelaying) Color(0xFF00E5FF).copy(alpha = 0.12f * alphaAnim)
                    else Color(0xFF9D4EDD).copy(alpha = 0.08f * alphaAnim),
                    Color.Transparent
                ),
                center = Offset(width * 0.5f, height * 0.35f),
                radius = width * 0.75f
            ),
            radius = width * 0.75f,
            center = Offset(width * 0.5f, height * 0.35f)
        )

        // 3. Grid node intersections with animated Cyan/Purple glowing pulses
        val keyNodes = listOf(
            Offset(width * 0.25f, height * 0.2f),
            Offset(width * 0.75f, height * 0.25f),
            Offset(width * 0.5f, height * 0.35f),
            Offset(width * 0.2f, height * 0.55f),
            Offset(width * 0.8f, height * 0.6f),
            Offset(width * 0.45f, height * 0.75f)
        )

        // Draw connections between key nodes
        for (i in keyNodes.indices) {
            for (j in i + 1 until keyNodes.size) {
                val dist = (keyNodes[i] - keyNodes[j]).getDistance()
                if (dist < width * 0.6f) {
                    val lineColor = if (isPacketRelaying) Color(0xFF00E5FF) else Color(0xFF9D4EDD)
                    drawLine(
                        color = lineColor.copy(alpha = 0.15f * alphaAnim),
                        start = keyNodes[i],
                        end = keyNodes[j],
                        strokeWidth = 1.2f
                    )
                }
            }
        }

        // 4. Primary Relaying Nodes with Halo Emission Rings
        keyNodes.forEachIndexed { index, nodeOffset ->
            val nodeColor = if (isPacketRelaying || index % 2 == 0) Color(0xFF00E5FF) else Color(0xFF9D4EDD)

            // Outer pulse ring
            if (isPacketRelaying || index == 2) {
                drawCircle(
                    color = nodeColor.copy(alpha = (1f - (ringScale - 0.6f) / 0.8f).coerceIn(0f, 0.4f)),
                    radius = 16.dp.toPx() * ringScale,
                    center = nodeOffset,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Core node point
            drawCircle(
                color = nodeColor.copy(alpha = if (isPacketRelaying) 0.95f else 0.75f * alphaAnim),
                radius = if (isPacketRelaying) 5.5.dp.toPx() else 4.dp.toPx(),
                center = nodeOffset
            )
        }
    }
}
