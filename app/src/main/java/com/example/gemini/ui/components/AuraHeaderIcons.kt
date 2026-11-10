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

        val r = 4.dp.toPx()

        // 1. Back chat bubble path (offset to top-right)
        val backBubblePath = Path().apply {
            val left = 8.5.dp.toPx()
            val top = 3.dp.toPx()
            val right = 23.5.dp.toPx()
            val bottom = 15.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            arcTo(
                androidx.compose.ui.geometry.Rect(right - 2 * r, top, right, top + 2 * r),
                -90f,
                90f,
                false
            )
            lineTo(right, 13.dp.toPx())
            lineTo(23.dp.toPx(), 17.5.dp.toPx())
            lineTo(19.dp.toPx(), bottom)
            lineTo(left + r, bottom)
            arcTo(
                androidx.compose.ui.geometry.Rect(left, bottom - 2 * r, left + 2 * r, bottom),
                90f,
                90f,
                false
            )
            lineTo(left, top + r)
            arcTo(
                androidx.compose.ui.geometry.Rect(left, top, left + 2 * r, top + 2 * r),
                180f,
                90f,
                false
            )
            close()
        }

        // Draw back bubble stroke
        drawPath(
            path = backBubblePath,
            color = tint,
            style = stroke
        )

        // 2. Front chat bubble path (offset to bottom-left with speech tail smoothly integrated)
        val frontBubblePath = Path().apply {
            val left = 2.5.dp.toPx()
            val top = 7.5.dp.toPx()
            val right = 17.5.dp.toPx()
            val bottom = 19.5.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            arcTo(
                androidx.compose.ui.geometry.Rect(right - 2 * r, top, right, top + 2 * r),
                -90f,
                90f,
                false
            )
            lineTo(right, bottom - r)
            arcTo(
                androidx.compose.ui.geometry.Rect(right - 2 * r, bottom - 2 * r, right, bottom),
                0f,
                90f,
                false
            )
            // Bottom edge leading to tail
            lineTo(10.dp.toPx(), bottom)
            // Integrated speech tail
            lineTo(3.5.dp.toPx(), 23.5.dp.toPx())
            lineTo(6.5.dp.toPx(), bottom)
            // Remainder of bottom edge
            lineTo(left + r, bottom)
            arcTo(
                androidx.compose.ui.geometry.Rect(left, bottom - 2 * r, left + 2 * r, bottom),
                90f,
                90f,
                false
            )
            lineTo(left, top + r)
            arcTo(
                androidx.compose.ui.geometry.Rect(left, top, left + 2 * r, top + 2 * r),
                180f,
                90f,
                false
            )
            close()
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
        val cx = 10.dp.toPx()
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
