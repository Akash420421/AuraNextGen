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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Clean, bold three-horizontal-line Hamburger Menu icon (☰) matching reference design.
 * - Three bold, smooth black lines with rounded ends.
 * - Consistent spacing and proper proportions, centered inside 26dp x 26dp canvas.
 * - Stroke width: 2.2dp, #111827
 */
@Composable
fun StaggeredMenuIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF111827)
) {
    Canvas(modifier = modifier.size(26.dp)) {
        val strokePx = 2.2.dp.toPx()
        val cap = StrokeCap.Round
        val startX = 3.5.dp.toPx()
        val endX = 22.5.dp.toPx()

        // Top line
        drawLine(
            color = tint,
            start = Offset(startX, 6.dp.toPx()),
            end = Offset(endX, 6.dp.toPx()),
            strokeWidth = strokePx,
            cap = cap
        )
        // Middle line
        drawLine(
            color = tint,
            start = Offset(startX, 13.dp.toPx()),
            end = Offset(endX, 13.dp.toPx()),
            strokeWidth = strokePx,
            cap = cap
        )
        // Bottom line
        drawLine(
            color = tint,
            start = Offset(startX, 20.dp.toPx()),
            end = Offset(endX, 20.dp.toPx()),
            strokeWidth = strokePx,
            cap = cap
        )
    }
}

/**
 * Clean overlapping double-chat-bubble icon with a '+' symbol matching reference design.
 * - Back chat bubble slightly offset to top-right.
 * - Front primary chat bubble with speech tail.
 * - Clear, centered '+' symbol inside the front bubble to represent creating a new chat.
 * - Consistent bold stroke (2.0dp) matching the Hamburger Menu lines.
 */
@Composable
fun NewChatIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF111827),
    backgroundColor: Color = Color.White
) {
    Canvas(modifier = modifier.size(26.dp)) {
        val strokePx = 2.0.dp.toPx()
        val stroke = Stroke(
            width = strokePx,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // 1. Back chat bubble path (offset to top-right)
        val backBubblePath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 9.dp.toPx(),
                    top = 3.dp.toPx(),
                    right = 23.dp.toPx(),
                    bottom = 15.dp.toPx(),
                    cornerRadius = CornerRadius(4.5.dp.toPx(), 4.5.dp.toPx())
                )
            )
            // Speech tail at bottom-right of back bubble
            moveTo(19.5.dp.toPx(), 15.dp.toPx())
            lineTo(22.5.dp.toPx(), 18.5.dp.toPx())
            lineTo(21.5.dp.toPx(), 14.5.dp.toPx())
        }

        // Draw back bubble stroke
        drawPath(
            path = backBubblePath,
            color = tint,
            style = stroke
        )

        // 2. Front chat bubble path (offset to bottom-left)
        val frontBubblePath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 3.dp.toPx(),
                    top = 7.5.dp.toPx(),
                    right = 17.5.dp.toPx(),
                    bottom = 19.5.dp.toPx(),
                    cornerRadius = CornerRadius(4.5.dp.toPx(), 4.5.dp.toPx())
                )
            )
            // Speech tail at bottom-left of front bubble
            moveTo(6.5.dp.toPx(), 19.5.dp.toPx())
            lineTo(3.5.dp.toPx(), 23.dp.toPx())
            lineTo(9.5.dp.toPx(), 19.5.dp.toPx())
        }

        // Fill front bubble with background color to cleanly mask overlapping back bubble lines
        drawPath(
            path = frontBubblePath,
            color = backgroundColor,
            style = Fill
        )

        // Draw front bubble stroke
        drawPath(
            path = frontBubblePath,
            color = tint,
            style = stroke
        )

        // 3. Clean '+' symbol centered inside the front chat bubble
        val cx = 10.25.dp.toPx()
        val cy = 13.5.dp.toPx()
        val arm = 2.8.dp.toPx()

        // Horizontal line of '+'
        drawLine(
            color = tint,
            start = Offset(cx - arm, cy),
            end = Offset(cx + arm, cy),
            strokeWidth = strokePx,
            cap = StrokeCap.Round
        )
        // Vertical line of '+'
        drawLine(
            color = tint,
            start = Offset(cx, cy - arm),
            end = Offset(cx, cy + arm),
            strokeWidth = strokePx,
            cap = StrokeCap.Round
        )
    }
}
