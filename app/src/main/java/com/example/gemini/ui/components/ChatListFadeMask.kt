package com.example.gemini.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a smooth alpha fade-out mask to the scrolling chat list content.
 * Over the top [topFadeHeight] and bottom [bottomFadeHeight], content fades
 * smoothly to transparent using BlendMode.DstIn on an offscreen compositing layer.
 * Works seamlessly over any background without hardcoding colors.
 */
fun Modifier.chatListFadeMask(
    topFadeHeight: Dp = 24.dp,
    bottomFadeHeight: Dp = 56.dp
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val height = size.height
        if (height > 0f) {
            val topFadePx = topFadeHeight.toPx()
            val bottomFadePx = bottomFadeHeight.toPx()
            val topStop = (topFadePx / height).coerceIn(0f, 0.4f)
            val bottomStop = ((height - bottomFadePx) / height).coerceIn(0.6f, 1f)

            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    topStop to Color.Black,
                    bottomStop to Color.Black,
                    1.0f to Color.Transparent
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }
