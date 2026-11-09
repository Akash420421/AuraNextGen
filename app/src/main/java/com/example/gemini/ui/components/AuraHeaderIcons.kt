package com.example.gemini.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

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
 * Clean thin-stroke rounded chat bubble outline with overlapping '+' badge in top-right corner.
 * - Size: 22dp x 22dp
 * - Stroke: 1.8dp with rounded caps, #111827
 */
@Composable
fun NewChatIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF111827)
) {
    Canvas(modifier = modifier.size(22.dp)) {
        val strokePx = 1.8.dp.toPx()
        val cap = StrokeCap.Round

        // Main speech bubble body (rounded rect) with speech tail
        val bubblePath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 2.dp.toPx(),
                    top = 5.dp.toPx(),
                    right = 16.5.dp.toPx(),
                    bottom = 17.5.dp.toPx(),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            )
            // Speech tail at bottom-left
            moveTo(5.dp.toPx(), 17.5.dp.toPx())
            lineTo(3.dp.toPx(), 20.5.dp.toPx())
            lineTo(8.dp.toPx(), 17.5.dp.toPx())
        }

        drawPath(
            path = bubblePath,
            color = tint,
            style = Stroke(width = strokePx, cap = cap)
        )

        // Overlapping '+' badge in top-right corner
        val cx = 17.5.dp.toPx()
        val cy = 5.5.dp.toPx()
        val arm = 3.5.dp.toPx()

        // Horizontal line of '+'
        drawLine(
            color = tint,
            start = Offset(cx - arm, cy),
            end = Offset(cx + arm, cy),
            strokeWidth = strokePx,
            cap = cap
        )
        // Vertical line of '+'
        drawLine(
            color = tint,
            start = Offset(cx, cy - arm),
            end = Offset(cx, cy + arm),
            strokeWidth = strokePx,
            cap = cap
        )
    }
}
