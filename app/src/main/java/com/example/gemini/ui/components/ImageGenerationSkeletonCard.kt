package com.example.gemini.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Inline Image Generation Skeleton Card:
 * - Width up to 320dp, aspect ratio 1:1, RoundedCornerShape(24.dp)
 * - Diagonal shimmer animation (#F0F2F5 -> #FFFFFF -> #F0F2F5)
 * - 3 cycling stages with animated text:
 *   Stage 1 (0–3s): "Creating your image"
 *   Stage 2 (3–7s): "Defining the Core Task"
 *   Stage 3 (7s+): "Refining the Composition"
 */
@Composable
fun ImageGenerationSkeletonCard(
    modifier: Modifier = Modifier
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    val stageText = when {
        elapsedSeconds < 3 -> "Creating your image"
        elapsedSeconds < 7 -> "Defining the Core Task"
        else -> "Refining the Composition"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF0F2F5),
            Color(0xFFFFFFFF),
            Color(0xFFF0F2F5)
        ),
        start = Offset(shimmerTranslate, shimmerTranslate),
        end = Offset(shimmerTranslate + 300f, shimmerTranslate + 300f)
    )

    Box(
        modifier = modifier
            .widthIn(max = 320.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(shimmerBrush)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF4B5563),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = stageText,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + slideInVertically { it / 2 })
                        .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutVertically { -it / 2 })
                },
                label = "skeleton_stage_text"
            ) { text ->
                Text(
                    text = text,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI Image Generator",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
