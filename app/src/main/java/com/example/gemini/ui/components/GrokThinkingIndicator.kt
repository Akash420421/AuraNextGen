package com.example.gemini.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Grok-style Thinking Animation:
 * - 8-dot circular chasing spinner (18dp canvas, 8 dots at 45° intervals, radius 6.2dp from center,
 *   dot radius 1.6dp, pulsing alpha 0.18f to 1.0f #1F2937 every 800ms)
 * - Shimmering "Thinking" text (15sp, medium weight, #4B5563 with horizontal shimmer sweep)
 */
@Composable
fun GrokThinkingIndicator(
    modifier: Modifier = Modifier,
    text: String = "Thinking"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grok_thinking_transition")

    val spinnerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grok_spinner_phase"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grok_shimmer_offset"
    )

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val orbitRadius = 6.2.dp.toPx()
            val dotRadius = 1.6.dp.toPx()
            val baseColor = Color(0xFF1F2937)

            for (i in 0 until 8) {
                val angleDeg = i * 45f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val x = center.x + orbitRadius * cos(angleRad).toFloat()
                val y = center.y + orbitRadius * sin(angleRad).toFloat()

                val dotFraction = (spinnerPhase - (i / 8f) + 1f) % 1f
                val alpha = 0.18f + 0.82f * (1f - dotFraction)

                drawCircle(
                    color = baseColor.copy(alpha = alpha),
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }

        val shimmerBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF4B5563),
                Color(0xFF9CA3AF),
                Color(0xFF4B5563)
            ),
            start = Offset(shimmerOffset, 0f),
            end = Offset(shimmerOffset + 150f, 0f)
        )

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(brush = shimmerBrush)
        )
    }
}
