/*
 * Copyright 2026 Krzysztof Karczewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.karczews.brainagator.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.games.shapematch.PaintBlue
import io.github.karczews.brainagator.ui.screens.games.shapematch.PaintGreen
import io.github.karczews.brainagator.ui.screens.games.shapematch.PaintOrange
import io.github.karczews.brainagator.ui.screens.games.shapematch.PaintPink
import io.github.karczews.brainagator.ui.screens.games.shapematch.PaintPurple
import io.github.karczews.brainagator.ui.screens.games.shapematch.StarShape
import io.github.karczews.brainagator.ui.screens.games.shapematch.TriangleShape

/** Gap between pictured items, as a fraction of one item's size. */
private const val GAP_FRACTION = 0.2f
private const val STROKE_FRACTION = 0.08f
private const val DIGIT_FRACTION = 0.6f
private val STEP_HEIGHTS = listOf(0.6f, 0.8f, 1f)

private val Triangle = TriangleShape()
private val Star = StarShape()
private val mixedShapes = listOf(CircleShape to PaintBlue.color, Triangle to PaintOrange.color, Star to PaintPurple.color)
private val oddOneOutShapes = listOf(CircleShape, CircleShape, RectangleShape, CircleShape)

/**
 * A small picture of what a game asks the child to do, drawn from the same shapes and colors as the games,
 * so a game can be picked without reading its title. Purely decorative: the tile's title carries the semantics.
 */
@Composable
fun GamePicture(
    gameType: GameType,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    if (gameType == GameType.NumberOrder) {
        // Digits need a text measurer, so this one picture is its own composable.
        NumberStepsPicture(accent = accent, modifier = modifier)
    } else {
        val outline = MaterialTheme.colorScheme.outline
        Canvas(modifier = modifier) { drawPicture(gameType, accent, outline) }
    }
}

private fun DrawScope.drawPicture(
    gameType: GameType,
    accent: Color,
    lineColor: Color,
) {
    when (gameType) {
        GameType.ShapeMatch -> drawShapes(rowSlots(count = 3), mixedShapes)
        GameType.ColorMatch -> drawMatchingPaint(lineColor)
        GameType.SizeOrder -> drawGrowingCircles(accent)
        GameType.Pattern -> drawPattern(accent, lineColor)
        GameType.OddOneOut -> drawShapes(rowSlots(count = 4), oddOneOutShapes.map { it to accent })
        GameType.SpotDifference -> drawTwoPictures(lineColor)
        GameType.NumberOrder -> Unit
    }
}

/** Three steps of growing height labelled 1, 2, 3: putting numbers in order. */
@Composable
private fun NumberStepsPicture(
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    val digitColor = AppTheme.extendedColors.onAccent
    val baseStyle = MaterialTheme.typography.titleMedium
    Canvas(modifier = modifier) {
        val slots = rowSlots(count = 3, heightFraction = 0.9f)
        // Every digit is sized to fit the lowest step, so all three match.
        val digitSize = slots.first().height * STEP_HEIGHTS.first() * DIGIT_FRACTION
        val style = baseStyle.merge(TextStyle(fontSize = digitSize.toDp().toSp()))
        slots.forEachIndexed { index, slot ->
            val stepHeight = slot.height * STEP_HEIGHTS[index]
            val stepTop = slot.bottom - stepHeight
            drawRoundRect(
                color = accent,
                topLeft = Offset(slot.left, stepTop),
                size = Size(slot.width, stepHeight),
                cornerRadius = CornerRadius(slot.width * 0.2f),
            )
            val digit = measurer.measure(text = "${index + 1}", style = style)
            drawText(
                textLayoutResult = digit,
                color = digitColor,
                topLeft = Offset(slot.center.x - digit.size.width / 2f, stepTop + (stepHeight - digit.size.height) / 2f),
            )
        }
    }
}

/** Square slots for [count] items in a centered row, as large as the canvas allows. */
private fun DrawScope.rowSlots(
    count: Int,
    heightFraction: Float = 0.75f,
): List<Rect> {
    val side = minOf(size.height * heightFraction, size.width / (count + (count - 1) * GAP_FRACTION))
    val gap = side * GAP_FRACTION
    val start = (size.width - (side * count + gap * (count - 1))) / 2f
    val top = (size.height - side) / 2f
    return List(count) { index -> Rect(Offset(start + index * (side + gap), top), Size(side, side)) }
}

private fun DrawScope.drawShapes(
    slots: List<Rect>,
    shapes: List<Pair<Shape, Color>>,
) {
    slots.zip(shapes).forEach { (slot, item) ->
        val (shape, color) = item
        translate(slot.left, slot.top) {
            drawOutline(shape.createOutline(slot.size, layoutDirection, this), color)
        }
    }
}

/** Two drops of the same paint with an equals sign between them. */
private fun DrawScope.drawMatchingPaint(lineColor: Color) {
    val (left, middle, right) = rowSlots(count = 3)
    drawDrop(left, PaintPink.color)
    drawDrop(right, PaintPink.color)
    val stroke = middle.width * STROKE_FRACTION * 1.5f
    listOf(0.38f, 0.62f).forEach { fraction ->
        val y = middle.top + middle.height * fraction
        drawLine(lineColor, Offset(middle.left + stroke, y), Offset(middle.right - stroke, y), stroke, StrokeCap.Round)
    }
}

private fun DrawScope.drawDrop(
    slot: Rect,
    color: Color,
) {
    val radius = slot.width * 0.36f
    val center = Offset(slot.center.x, slot.bottom - radius)
    val tip =
        Path().apply {
            moveTo(slot.center.x, slot.top)
            lineTo(center.x + radius * 0.87f, center.y - radius * 0.5f)
            lineTo(center.x - radius * 0.87f, center.y - radius * 0.5f)
            close()
        }
    drawPath(tip, color)
    drawCircle(color, radius, center)
}

/** Three circles from small to big, standing on one line. */
private fun DrawScope.drawGrowingCircles(color: Color) {
    val scales = listOf(0.45f, 0.7f, 1f)
    val unit = minOf(size.height * 0.85f, size.width / (scales.sum() + GAP_FRACTION * 2))
    var x = (size.width - unit * (scales.sum() + GAP_FRACTION * 2)) / 2f
    val bottom = (size.height + unit) / 2f
    scales.forEach { scale ->
        val radius = unit * scale / 2f
        drawCircle(color, radius, Offset(x + radius, bottom - radius))
        x += unit * scale + unit * GAP_FRACTION
    }
}

/** Circle, triangle, circle, triangle and an empty dashed slot for what comes next. */
private fun DrawScope.drawPattern(
    accent: Color,
    lineColor: Color,
) {
    val slots = rowSlots(count = 5, heightFraction = 0.6f)
    drawShapes(
        slots.take(4),
        listOf(
            CircleShape to accent,
            Triangle to PaintOrange.color,
            CircleShape to accent,
            Triangle to PaintOrange.color,
        ),
    )
    val stroke = slots.last().width * STROKE_FRACTION
    val slot = slots.last().deflate(stroke / 2f)
    drawRoundRect(
        color = lineColor,
        topLeft = slot.topLeft,
        size = slot.size,
        cornerRadius = CornerRadius(slot.width * 0.25f),
        style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(stroke * 1.5f, stroke * 1.2f))),
    )
}

/** Two little framed landscapes; the second is missing its sun. */
private fun DrawScope.drawTwoPictures(lineColor: Color) {
    rowSlots(count = 2, heightFraction = 0.85f).forEachIndexed { index, slot ->
        // Inset by half the border, so the border stays inside the picture instead of being cut off at its edge.
        val frame = slot.deflate(slot.width * STROKE_FRACTION / 2f)
        val corner = CornerRadius(frame.width * 0.18f)
        val framePath = Path().apply { addRoundRect(RoundRect(frame, corner)) }
        clipPath(framePath) {
            drawCircle(PaintGreen.color, frame.width * 0.5f, Offset(frame.center.x, frame.bottom + frame.height * 0.2f))
            if (index == 0) {
                drawCircle(PaintOrange.color, frame.width * 0.14f, Offset(frame.left + frame.width * 0.7f, frame.top + frame.height * 0.3f))
            }
        }
        drawRoundRect(lineColor, frame.topLeft, frame.size, corner, style = Stroke(frame.width * STROKE_FRACTION))
    }
}
