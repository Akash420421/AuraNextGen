package com.example.gemini.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini.theme.isAppInDarkTheme

/**
 * Premium, minimal "Add to chat" attachment bottom sheet styled like the Claude app.
 * Features 3 quick action tiles (Camera, Photos, Files) and a clean Model row.
 * Adheres strictly to the active app theme (Clean White Theme when in Light mode).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentSelectorBottomSheet(
    onTakePhoto: () -> Unit = {},
    onPickImage: () -> Unit,
    onPickFile: () -> Unit,
    onSelectModel: (() -> Unit)? = null,
    currentModelName: String? = null,
    onDismiss: () -> Unit
) {
    val isDark = isAppInDarkTheme()
    val sheetBg = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF8F8F6)
    val cardBg = if (isDark) Color(0xFF2A2A2C) else Color(0xFFFFFFFF)
    val circleBg = if (isDark) Color(0xFF3A3A3C) else Color(0xFFEDEDEB)
    val iconTint = if (isDark) Color(0xFFE5E5E7) else Color(0xFF2B2B2B)
    val textPrimary = if (isDark) Color(0xFFE5E5E7) else Color(0xFF1A1A1A)
    val textSecondary = if (isDark) Color(0xFF9E9EA2) else Color(0xFF7A7A7E)
    val dragHandleColor = if (isDark) Color(0xFF48484A) else Color(0xFFC9C9CC)
    val chevronTint = if (isDark) Color(0xFF8E8E93) else Color(0xFF9A9A9E)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = sheetBg,
        scrimColor = Color.Black.copy(alpha = 0.32f),
        tonalElevation = 0.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(dragHandleColor)
                )
            }
        },
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // 1. Header Row (Height 56dp, Close "X" + Centered "Add to chat" + Balancer Spacer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Add to chat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Quick Action Tiles Row (Camera, Photos, Files)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionTile(
                    label = "Camera",
                    icon = Icons.Outlined.PhotoCamera,
                    cardBg = cardBg,
                    circleBg = circleBg,
                    iconTint = iconTint,
                    textPrimary = textPrimary,
                    onClick = {
                        onDismiss()
                        onTakePhoto()
                    },
                    modifier = Modifier.weight(1f)
                )

                QuickActionTile(
                    label = "Photos",
                    icon = Icons.Outlined.Image,
                    cardBg = cardBg,
                    circleBg = circleBg,
                    iconTint = iconTint,
                    textPrimary = textPrimary,
                    onClick = {
                        onDismiss()
                        onPickImage()
                    },
                    modifier = Modifier.weight(1f)
                )

                QuickActionTile(
                    label = "Files",
                    icon = Icons.Outlined.UploadFile,
                    cardBg = cardBg,
                    circleBg = circleBg,
                    iconTint = iconTint,
                    textPrimary = textPrimary,
                    onClick = {
                        onDismiss()
                        onPickFile()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. List Rows (Model row, 12dp below tiles)
            if (onSelectModel != null) {
                Spacer(modifier = Modifier.height(12.dp))

                val subtitleText = if (!currentModelName.isNullOrBlank()) {
                    currentModelName
                } else {
                    "Select active AI model"
                }

                AttachmentListRow(
                    title = "Model",
                    subtitle = subtitleText,
                    icon = Icons.Outlined.AutoAwesome,
                    cardBg = cardBg,
                    circleBg = circleBg,
                    iconTint = iconTint,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    chevronTint = chevronTint,
                    onClick = {
                        onDismiss()
                        onSelectModel()
                    }
                )
            }
        }
    }
}

/**
 * Clean minimal quick action tile (Camera, Photos, Files).
 * Features a 48dp circle with outline icon, label below, and subtle press scale feedback.
 */
@Composable
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    cardBg: Color,
    circleBg: Color,
    iconTint: Color,
    textPrimary: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tileScale"
    )
    val haptic = LocalHapticFeedback.current

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        interactionSource = interactionSource,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .defaultMinSize(minHeight = 104.dp)
            .semantics { role = Role.Button }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(circleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Minimal rounded card list row (e.g. Model selection).
 * Features a 44dp circle leading icon, title + subtitle, and trailing chevron.
 */
@Composable
private fun AttachmentListRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    cardBg: Color,
    circleBg: Color,
    iconTint: Color,
    textPrimary: Color,
    textSecondary: Color,
    chevronTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "rowScale"
    )
    val haptic = LocalHapticFeedback.current

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .defaultMinSize(minHeight = 72.dp)
            .semantics { role = Role.Button }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(circleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = chevronTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
