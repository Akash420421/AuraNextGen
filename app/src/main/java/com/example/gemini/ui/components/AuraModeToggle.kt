package com.example.gemini.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact, sleek segmented pill toggle between Chat and Voice modes.
 * Features a smoothly sliding white indicator thumb with spring physics.
 */
@Composable
fun AuraModeToggle(
    currentMode: String, // "chat" or "voice"
    onModeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isChat = currentMode == "chat"
    val containerBg = Color(0xFFE8E8ED)
    val thumbBg = Color.White
    val activeTextColor = Color(0xFF1A1A1A)
    val inactiveTextColor = Color(0xFF8E8E93)

    BoxWithConstraints(
        modifier = modifier
            .width(148.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(containerBg)
            .padding(2.5.dp)
    ) {
        val tabWidth = maxWidth / 2
        val thumbOffset by animateDpAsState(
            targetValue = if (isChat) 0.dp else tabWidth,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "mode_toggle_thumb_offset"
        )

        // Sliding white thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(15.dp), spotColor = Color(0x24000000))
                .clip(RoundedCornerShape(15.dp))
                .background(thumbBg)
        )

        // Clickable Labels Row
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chat Tab
            val chatTextColor by animateColorAsState(
                targetValue = if (isChat) activeTextColor else inactiveTextColor,
                animationSpec = tween(durationMillis = 180),
                label = "chat_text_color"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(15.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onModeSelected("chat") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chat",
                    fontSize = 13.5.sp,
                    fontWeight = if (isChat) FontWeight.SemiBold else FontWeight.Medium,
                    color = chatTextColor
                )
            }

            // Voice Tab
            val voiceTextColor by animateColorAsState(
                targetValue = if (!isChat) activeTextColor else inactiveTextColor,
                animationSpec = tween(durationMillis = 180),
                label = "voice_text_color"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(15.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onModeSelected("voice") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Voice",
                    fontSize = 13.5.sp,
                    fontWeight = if (!isChat) FontWeight.SemiBold else FontWeight.Medium,
                    color = voiceTextColor
                )
            }
        }
    }
}
