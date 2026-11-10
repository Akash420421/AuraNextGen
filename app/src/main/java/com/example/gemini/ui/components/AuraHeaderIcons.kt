package com.example.gemini.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gemini.R

/**
 * Clean, thin-stroke staggered 3-line menu icon (☰) matching reference design.
 * - Line 1: length 18dp (y=6dp, x: 3dp..21dp)
 * - Line 2: length 13dp (y=12dp, x: 3dp..16dp)
 * - Line 3: length 8dp  (y=18dp, x: 3dp..11dp)
 * - Stroke: 1.8dp with rounded caps, #111827
 */
@Composable
fun StaggeredMenuIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF111827)
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokePx = 1.8.dp.toPx()
        val cap = StrokeCap.Round

        // Line 1: 18dp (x: 3dp to 21dp, y: 6dp)
        drawLine(
            color = tint,
            start = Offset(3.dp.toPx(), 6.dp.toPx()),
            end = Offset(21.dp.toPx(), 6.dp.toPx()),
            strokeWidth = strokePx,
            cap = cap
        )
        // Line 2: 13dp (x: 3dp to 16dp, y: 12dp)
        drawLine(
            color = tint,
            start = Offset(3.dp.toPx(), 12.dp.toPx()),
            end = Offset(16.dp.toPx(), 12.dp.toPx()),
            strokeWidth = strokePx,
            cap = cap
        )
        // Line 3: 8dp (x: 3dp to 11dp, y: 18dp)
        drawLine(
            color = tint,
            start = Offset(3.dp.toPx(), 18.dp.toPx()),
            end = Offset(11.dp.toPx(), 18.dp.toPx()),
            strokeWidth = strokePx,
            cap = cap
        )
    }
}

/**
 * Material Design "forum" outlined icon: TWO overlapping speech bubbles.
 * - Front bubble: top-left, OUTLINED only (hollow inside) with tail at bottom-left.
 * - Back bubble: bottom-right, SOLID filled with tail at bottom-right.
 * - 24dp x 24dp size.
 */
@Composable
fun NewChatIcon(
    modifier: Modifier = Modifier,
    tint: Color = if (com.example.gemini.theme.isAppInDarkTheme()) Color(0xFFE5E5E7) else Color(0xFF3C3C3E),
    enabled: Boolean = true
) {
    Icon(
        painter = painterResource(R.drawable.ic_new_chat),
        contentDescription = "New chat",
        tint = if (enabled) tint else tint.copy(alpha = 0.45f),
        modifier = modifier.size(24.dp)
    )
}
