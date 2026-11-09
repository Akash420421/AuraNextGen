package com.example.gemini.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini.data.jarvis.JarvisController
import com.example.gemini.data.jarvis.JarvisController.Phase
import com.example.gemini.data.jarvis.JarvisPreferences
import com.example.gemini.ui.components.AuraModeToggle
import com.example.gemini.ui.components.AuraOrbView

@Composable
fun AuraVoiceScreen(
    onSwitchToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phase by JarvisController.phase.collectAsState()
    val isAutoSend by JarvisPreferences.autoSend.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Greeting Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hi Sir,",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (phase) {
                    Phase.LISTENING -> "Listening to your voice…"
                    Phase.THINKING -> "Thinking…"
                    Phase.SPEAKING -> "Delivering response…"
                    else -> "Tap the orb to start conversation"
                },
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Center: 3D Morphing Particle Orb
        Box(
            modifier = Modifier
                .size(310.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AuraOrbView(
                modifier = Modifier.fillMaxSize(),
                phase = phase,
                onOrbTapped = {
                    when (phase) {
                        Phase.LISTENING -> JarvisController.finishListening()
                        Phase.SPEAKING -> JarvisController.silence()
                        Phase.THINKING -> JarvisController.stopEverything()
                        else -> JarvisController.onMicPressed()
                    }
                }
            )
        }

        // Bottom Controls Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            // Segmented Toggle: [ Chat | Voice ]
            AuraModeToggle(
                currentMode = "voice",
                onModeSelected = { mode ->
                    if (mode == "chat") onSwitchToChat()
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Action Row: [Screen]   [Tap to Start]   [Auto]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Screen Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        onClick = {
                            // Toggles screen focus or screen inspection
                            JarvisController.postStatus("Screen analysis ready", speak = false)
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PresentToAll,
                                contentDescription = "Screen",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Screen",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Center: Tap to Start Pill Button
                val isBusy = phase == Phase.LISTENING || phase == Phase.THINKING || phase == Phase.SPEAKING
                val pillBg = if (isBusy) Color(0xFFDBEAFE) else Color(0xFFEFF6FF)
                val pillBorder = if (isBusy) Color(0xFF60A5FA) else Color(0xFFBFDBFE)
                val pillText = when (phase) {
                    Phase.LISTENING -> "Listening…"
                    Phase.THINKING -> "Thinking…"
                    Phase.SPEAKING -> "Speaking…"
                    else -> "Tap to Start"
                }

                Surface(
                    onClick = {
                        when (phase) {
                            Phase.LISTENING -> JarvisController.finishListening()
                            Phase.SPEAKING -> JarvisController.silence()
                            Phase.THINKING -> JarvisController.stopEverything()
                            else -> JarvisController.onMicPressed()
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    color = pillBg,
                    border = BorderStroke(1.5.dp, pillBorder),
                    modifier = Modifier
                        .height(54.dp)
                        .defaultMinSize(minWidth = 170.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (phase == Phase.SPEAKING) Icons.Rounded.Stop else Icons.Rounded.Mic,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pillText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }

                // Right: Auto Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        onClick = {
                            JarvisPreferences.autoSend.set(!isAutoSend)
                        },
                        shape = CircleShape,
                        color = if (isAutoSend) Color(0xFFDBEAFE) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        border = BorderStroke(
                            1.dp,
                            if (isAutoSend) Color(0xFF3B82F6) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Language,
                                contentDescription = "Auto",
                                tint = if (isAutoSend) Color(0xFF2563EB) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Auto",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
