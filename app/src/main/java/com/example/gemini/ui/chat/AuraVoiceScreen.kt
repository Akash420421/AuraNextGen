package com.example.gemini.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini.data.jarvis.JarvisController
import com.example.gemini.data.jarvis.JarvisController.Phase
import com.example.gemini.data.jarvis.JarvisPreferences
import com.example.gemini.ui.components.AuraModeToggle
import com.example.gemini.ui.components.AuraOrbView
import com.example.gemini.ui.components.NewChatIcon
import com.example.gemini.ui.components.StaggeredMenuIcon

@Composable
fun AuraVoiceScreen(
    onSwitchToChat: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onNewChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val phase by JarvisController.phase.collectAsState()
    val isAutoSend by JarvisPreferences.autoSend.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Top Bar (Matching Reference Header) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Staggered 3-line Menu Icon
            Surface(
                onClick = onOpenDrawer,
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    StaggeredMenuIcon(tint = Color(0xFF111827))
                }
            }

            // Center: App Title
            Text(
                text = "AURA AI",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                letterSpacing = (-0.2).sp
            )

            // Right: New Chat Icon
            IconButton(
                onClick = onNewChat,
                modifier = Modifier.size(48.dp)
            ) {
                NewChatIcon()
            }
        }

        // --- Greeting Section ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "Hi Sir,",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (phase) {
                    Phase.LISTENING -> "Listening to your voice…"
                    Phase.THINKING -> "Thinking…"
                    Phase.SPEAKING -> "Delivering response…"
                    else -> "Tap the orb to start conversation"
                },
                fontSize = 14.5.sp,
                color = Color(0xFF6B7280)
            )
        }

        // --- Center: 3D Morphing Particle Orb (260dp x 260dp) ---
        Box(
            modifier = Modifier
                .size(260.dp),
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

        // --- Bottom Controls Section ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            // Segmented Toggle: [ Chat | Voice ]
            AuraModeToggle(
                currentMode = "voice",
                onModeSelected = { mode ->
                    if (mode == "chat") onSwitchToChat()
                }
            )

            // Exact 20dp spacing below toggle
            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Action Row: [Screen]  [Tap to Start]  [Auto]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- Left: Screen Button ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape, spotColor = Color(0x28000000))
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(BorderStroke(0.5.dp, Color(0xFFEAECEF)), CircleShape)
                            .clickable {
                                JarvisController.postStatus("Screen analysis ready", speak = false)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DesktopWindows,
                            contentDescription = "Screen",
                            tint = Color(0xFF1F2937),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Screen",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                }

                // --- Center: Tap to Start Pill Button (156dp x 46dp) ---
                val isBusy = phase == Phase.LISTENING || phase == Phase.THINKING || phase == Phase.SPEAKING
                val pillText = when (phase) {
                    Phase.LISTENING -> "Listening…"
                    Phase.THINKING -> "Thinking…"
                    Phase.SPEAKING -> "Speaking…"
                    else -> "Tap to Start"
                }

                Box(
                    modifier = Modifier
                        .width(156.dp)
                        .height(46.dp)
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(50), spotColor = Color(0x30000000))
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .border(BorderStroke(0.5.dp, Color(0xFFEAECEF)), RoundedCornerShape(50))
                        .clickable {
                            when (phase) {
                                Phase.LISTENING -> JarvisController.finishListening()
                                Phase.SPEAKING -> JarvisController.silence()
                                Phase.THINKING -> JarvisController.stopEverything()
                                else -> JarvisController.onMicPressed()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (phase == Phase.SPEAKING) Icons.Rounded.Stop else Icons.Rounded.Mic,
                            contentDescription = null,
                            tint = Color(0xFF1A73E8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pillText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A73E8)
                        )
                    }
                }

                // --- Right: Auto Button ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape, spotColor = Color(0x28000000))
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(BorderStroke(0.5.dp, Color(0xFFEAECEF)), CircleShape)
                            .clickable {
                                JarvisPreferences.autoSend.set(!isAutoSend)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = "Auto",
                            tint = if (isAutoSend) Color(0xFF1A73E8) else Color(0xFF1F2937),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Auto",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                }
            }
        }
    }
}
