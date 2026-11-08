package com.example.gemini.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuraModeToggle(
    currentMode: String, // "chat" or "voice"
    onModeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = com.example.gemini.theme.isAppInDarkTheme()
    val isChat = currentMode == "chat"
    val containerBg = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE8E8ED)
    val activeBg = if (isDark) Color(0xFF3A3A3C) else Color.White
    val activeTextColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val inactiveTextColor = Color(0xFF8E8E93)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(containerBg)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chat Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isChat) Modifier.shadow(1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x18000000)).background(activeBg)
                        else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onModeSelected("chat") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chat",
                    fontSize = 14.sp,
                    fontWeight = if (isChat) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isChat) activeTextColor else inactiveTextColor
                )
            }

            // Voice Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (!isChat) Modifier.shadow(1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x18000000)).background(activeBg)
                        else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onModeSelected("voice") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Voice",
                    fontSize = 14.sp,
                    fontWeight = if (!isChat) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (!isChat) activeTextColor else inactiveTextColor
                )
            }
        }
    }
}
