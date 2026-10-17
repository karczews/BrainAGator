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

package io.github.karczews.brainagator.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens

private val BubbleBorder = 2.dp
private val BubbleCorner = 24.dp

/** Which edge of a [SpeechBubble] the tail sticks out of, pointing at whoever is talking. */
enum class BubbleTail { Start, Bottom }

/**
 * A flat speech bubble: what the gator is saying. Like [PlayCard] it has no lip, because it is
 * read (and heard), not pressed.
 */
@Composable
fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    tail: BubbleTail = BubbleTail.Start,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    singleLine: Boolean = false,
) {
    val shape = remember(tail) { SpeechBubbleShape(tail, BubbleCorner, Dimens.bubbleTailWidth, Dimens.bubbleTailHeight) }
    // Keep the text clear of the tail.
    val contentPadding =
        when (tail) {
            BubbleTail.Start ->
                Modifier
                    .padding(start = Dimens.bubbleTailHeight)
                    .padding(Dimens.spaceMd)
            BubbleTail.Bottom ->
                Modifier
                    .padding(bottom = Dimens.bubbleTailHeight)
                    .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceMd)
        }
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(BubbleBorder, MaterialTheme.colorScheme.outlineVariant, shape)
                .then(contentPadding),
        contentAlignment = if (tail == BubbleTail.Start) Alignment.CenterStart else Alignment.Center,
    ) {
        Text(
            text = text,
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = if (tail == BubbleTail.Start) TextAlign.Start else TextAlign.Center,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            // A single-line bubble shrinks its text on narrow screens rather than leaving one word on its own line.
            autoSize = if (singleLine) TextAutoSize.StepBased(AutoSizeMin.title, textStyle.fontSize) else null,
        )
    }
}

/** A rounded rectangle with a triangular tail on one edge, drawn as one outline so the border has no seam. */
private class SpeechBubbleShape(
    private val tail: BubbleTail,
    private val cornerRadius: Dp,
    private val tailWidth: Dp,
    private val tailHeight: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = with(density) { cornerRadius.toPx() }
        val halfTail = with(density) { tailWidth.toPx() } / 2
        val tailLength = with(density) { tailHeight.toPx() }
        val body = Path()
        val pointer = Path()
        when (tail) {
            BubbleTail.Start -> {
                // In right-to-left layouts the gator sits on the right, so the tail flips with it.
                val rtl = layoutDirection == LayoutDirection.Rtl
                val bodyLeft = if (rtl) 0f else tailLength
                val bodyRight = if (rtl) size.width - tailLength else size.width
                val edge = if (rtl) bodyRight + 1f else bodyLeft - 1f
                val tip = if (rtl) size.width else 0f
                val centerY = size.height / 2
                body.addRoundRect(RoundRect(bodyLeft, 0f, bodyRight, size.height, CornerRadius(radius)))
                pointer.moveTo(edge, centerY - halfTail)
                pointer.lineTo(tip, centerY)
                pointer.lineTo(edge, centerY + halfTail)
                pointer.close()
            }
            BubbleTail.Bottom -> {
                val bodyBottom = size.height - tailLength
                val centerX = size.width / 2
                body.addRoundRect(RoundRect(0f, 0f, size.width, bodyBottom, CornerRadius(radius)))
                pointer.moveTo(centerX - halfTail, bodyBottom - 1f)
                pointer.lineTo(centerX, size.height)
                pointer.lineTo(centerX + halfTail, bodyBottom - 1f)
                pointer.close()
            }
        }
        return Outline.Generic(Path.combine(PathOperation.Union, body, pointer))
    }
}

@Preview
@Composable
private fun SpeechBubblePreview() {
    AppTheme {
        SpeechBubble(text = "Find the colored shape!", modifier = Modifier.padding(Dimens.spaceMd))
    }
}
