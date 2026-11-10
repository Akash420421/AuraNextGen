package com.example.gemini.ui.chat

import android.os.SystemClock
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
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini.data.jarvis.JarvisController
import com.example.gemini.data.voice.AgyVoiceCall
import com.example.gemini.data.voice.AgyVoicePreferences
import com.example.gemini.ui.components.AuraModeToggle
import com.example.gemini.ui.components.AuraOrbView
import com.example.gemini.ui.components.NewChatIcon
import com.example.gemini.ui.components.StaggeredMenuIcon
import com.example.gemini.ui.voice.AgyVoicePickerDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AuraVoiceScreen(
    onSwitchToChat: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onNewChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Antigravity Voice Call state
    val callState by AgyVoiceCall.state.collectAsState()
    val micLevel by AgyVoiceCall.micLevel.collectAsState()
    val voiceLevel by AgyVoiceCall.voiceLevel.collectAsState()

    // Dialog state for Voice Settings
    var showVoiceSettings by remember { mutableStateOf(false) }

    // Tap tracking for Single-tap (Mute) vs Double-tap (End Call)
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var pendingSingleTapJob by remember { mutableStateOf<Job?>(null) }

    // 1. Dynamic Greeting based on time of day
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greetingText = remember(currentHour) {
        when (currentHour) {
            in 5..11 -> "Good Morning Sir,"
            in 12..16 -> "Good Afternoon Sir,"
            in 17..21 -> "Good Evening Sir,"
            else -> "How are you Sir,"
        }
    }

    // 2. Computed Orb phase for 3D Particle Orb
    val orbPhase = remember(callState.phase, callState.muted, voiceLevel, callState.active) {
        when {
            !callState.active || callState.phase == AgyVoiceCall.Phase.IDLE -> JarvisController.Phase.IDLE
            callState.phase == AgyVoiceCall.Phase.CONNECTING || callState.phase == AgyVoiceCall.Phase.RECONNECTING -> JarvisController.Phase.THINKING
            callState.phase == AgyVoiceCall.Phase.LIVE -> {
                if (callState.muted) JarvisController.Phase.IDLE
                else if (voiceLevel > 0.05f) JarvisController.Phase.SPEAKING
                else JarvisController.Phase.LISTENING
            }
            else -> JarvisController.Phase.IDLE
        }
    }

    // 3. Subtext status
    val statusSubtext = when {
        !callState.active -> "Tap the orb to start conversation"
        callState.phase == AgyVoiceCall.Phase.CONNECTING -> "Connecting…"
        callState.phase == AgyVoiceCall.Phase.RECONNECTING -> "Reconnecting…"
        callState.phase == AgyVoiceCall.Phase.LIVE -> {
            if (callState.muted) "Microphone muted • Tap to unmute"
            else if (voiceLevel > 0.05f) "Speaking…"
            else "Listening to your voice…"
        }
        callState.phase == AgyVoiceCall.Phase.FAILED -> "Call disconnected • Tap to retry"
        else -> "Tap the orb to start conversation"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Top Bar (Centered AURA AI Header with 48dp Touch Targets) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Hamburger Menu Icon (48dp Touch Area)
            Surface(
                onClick = onOpenDrawer,
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    StaggeredMenuIcon(tint = Color(0xFF111827))
                }
            }

            // Center: App Title (Perfect Center Alignment)
            Text(
                text = "AURA AI",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                letterSpacing = (-0.2).sp
            )

            // Right: Double Chat Bubble + Plus New Chat Icon (48dp Touch Area)
            Surface(
                onClick = onNewChat,
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    NewChatIcon(tint = Color(0xFF111827))
                }
            }
        }

        // --- Greeting Section ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = greetingText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = statusSubtext,
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
                phase = orbPhase,
                onOrbTapped = {
                    if (!callState.active || callState.phase == AgyVoiceCall.Phase.IDLE) {
                        AgyVoiceCall.start(context, null)
                    } else {
                        // Toggle mute on orb tap as well
                        AgyVoiceCall.toggleMute()
                    }
                }
            )
        }

        // --- Bottom Controls Section ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 28.dp)
        ) {
            // Chat / Voice Segmented Pill Switcher
            AuraModeToggle(
                selectedMode = "voice",
                onModeSelected = { mode ->
                    if (mode == "chat") onSwitchToChat()
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Action Row: Screen | Tap to Start (Gesture-Controlled) | Setting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- Left: Screen Share Button ---
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

                // --- Center: Dynamic Tap to Start Pill Button (Single Tap: Mute, Double Tap: End Call) ---
                val isCallActive = callState.active && callState.phase == AgyVoiceCall.Phase.LIVE
                val isConnecting = callState.active && (callState.phase == AgyVoiceCall.Phase.CONNECTING || callState.phase == AgyVoiceCall.Phase.RECONNECTING)

                val pillText = when {
                    isConnecting -> "Connecting…"
                    isCallActive -> {
                        if (callState.muted) "Muted"
                        else if (voiceLevel > 0.05f) "Speaking…"
                        else "Listening…"
                    }
                    callState.phase == AgyVoiceCall.Phase.FAILED -> "Retry Call"
                    else -> "Tap to Start"
                }

                val pillTint = when {
                    isConnecting -> Color(0xFFD97706) // Amber
                    isCallActive -> {
                        if (callState.muted) Color(0xFFDC2626) // Red for muted
                        else if (voiceLevel > 0.05f) Color(0xFF059669) // Green for speaking
                        else Color(0xFF1A73E8) // Blue for listening
                    }
                    callState.phase == AgyVoiceCall.Phase.FAILED -> Color(0xFFDC2626)
                    else -> Color(0xFF1A73E8)
                }

                val pillIcon = when {
                    isCallActive && callState.muted -> Icons.Rounded.MicOff
                    isCallActive && voiceLevel > 0.05f -> Icons.Rounded.VolumeUp
                    else -> Icons.Rounded.Mic
                }

                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(46.dp)
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(50), spotColor = Color(0x30000000))
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .border(BorderStroke(0.5.dp, Color(0xFFEAECEF)), RoundedCornerShape(50))
                        .clickable {
                            if (!callState.active || callState.phase == AgyVoiceCall.Phase.IDLE || callState.phase == AgyVoiceCall.Phase.FAILED) {
                                // Start call immediately
                                AgyVoiceCall.start(context, "Voice chat") { viewModel.agyBackend.prepareVoiceCall() }
                            } else {
                                val now = SystemClock.uptimeMillis()
                                if (now - lastTapTime < 340) {
                                    // Double Tap Detected: End the Call immediately!
                                    pendingSingleTapJob?.cancel()
                                    pendingSingleTapJob = null
                                    lastTapTime = 0L
                                    AgyVoiceCall.end()
                                } else {
                                    // Potential Single Tap: Wait for double-tap window to elapse
                                    lastTapTime = now
                                    pendingSingleTapJob?.cancel()
                                    pendingSingleTapJob = scope.launch {
                                        delay(340)
                                        // Confirmed Single Tap: Toggle Mic Mute (Call stays active in background)
                                        AgyVoiceCall.toggleMute()
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = pillIcon,
                            contentDescription = null,
                            tint = pillTint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pillText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = pillTint
                        )
                    }
                }

                // --- Right: Setting Button (Opens Voice Selection & Call Preferences) ---
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
                                showVoiceSettings = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Setting",
                            tint = Color(0xFF1F2937),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Setting",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                }
            }
        }
    }

    // Voice Picker & Settings Dialog
    if (showVoiceSettings) {
        AgyVoicePickerDialog(
            initialVoice = AgyVoicePreferences.voice.value,
            onChoose = { selectedVoice ->
                AgyVoicePreferences.setVoice(selectedVoice)
                AgyVoiceCall.changeVoice(selectedVoice)
                showVoiceSettings = false
            },
            onDismiss = {
                showVoiceSettings = false
            }
        )
    }
}
