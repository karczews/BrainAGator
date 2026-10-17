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

package io.github.karczews.brainagator.ui.screens.map

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import io.github.karczews.brainagator.theme.Dimens

/** Sideways swing of consecutive stops: centre, one side, centre, other side. */
private val Zigzag = intArrayOf(0, 1, 0, -1)

/** In portrait a stop's label may use at most this share of the width (three columns of stops). */
private const val PORTRAIT_STOP_WIDTH_FRACTION = 1f / 3f

/**
 * Where the stops of the map go. The trail winds down the screen in portrait and across it in landscape, so every
 * stop fits without scrolling. Each stop is a [stopWidth] x [stopHeight] box: the round face at the top, then its
 * stars and label.
 *
 * @property face Diameter of a stop's round face.
 * @property stopOrigins Top-left corner of each stop's box.
 * @property trailPoints Centre of each stop's face; the trail runs through these.
 * @property swings Which side each stop swings to (-1, 0 or 1), so decorations can face away from the trail.
 */
@Immutable
internal data class MapLayout(
    val isHorizontal: Boolean,
    val face: Dp,
    val stopWidth: Dp,
    val stopHeight: Dp,
    val stopOrigins: List<DpOffset>,
    val trailPoints: List<DpOffset>,
    val swings: List<Int>,
)

/** Height of everything in a stop's box below its face: lip, stars, label and the gaps between them. */
internal val StopExtraHeight: Dp = Dimens.lipHeight + Dimens.mapStopStar + Dimens.mapStopLabelHeight + Dimens.spaceXs

internal fun mapLayout(
    width: Dp,
    height: Dp,
    count: Int,
): MapLayout {
    val isHorizontal = width > height
    val gaps = (count - 1).coerceAtLeast(1)
    val gap = Dimens.spaceSm
    val (stopWidth, maxBoxHeight) =
        if (isHorizontal) {
            // Neighbours sit side by side, so the width is shared between all stops.
            ((width - gap * gaps) / count) to height
        } else {
            // Neighbours overlap in height but swing apart; every other stop shares a column and must clear the one
            // two places before it.
            (width * PORTRAIT_STOP_WIDTH_FRACTION) to (height * 2 - gap * gaps) / (count + 1)
        }
    val face =
        minOf(maxBoxHeight - StopExtraHeight, stopWidth.coerceAtMost(Dimens.mapStopMaxWidth), Dimens.mapStopMaxFace)
            .coerceAtLeast(Dimens.touchTarget)
    val boxWidth = stopWidth.coerceIn(face, Dimens.mapStopMaxWidth)
    val boxHeight = face + StopExtraHeight
    val swings = List(count) { Zigzag[it % Zigzag.size] }
    val origins =
        List(count) { index ->
            val along = index.toFloat() / gaps
            val swing = swings[index]
            if (isHorizontal) {
                DpOffset(x = (width - boxWidth) * along, y = (height - boxHeight) * (1 + swing) / 2)
            } else {
                DpOffset(x = (width - boxWidth) * (1 + swing) / 2, y = (height - boxHeight) * along)
            }
        }
    return MapLayout(
        isHorizontal = isHorizontal,
        face = face,
        stopWidth = boxWidth,
        stopHeight = boxHeight,
        stopOrigins = origins,
        trailPoints = origins.map { DpOffset(x = it.x + boxWidth / 2, y = it.y + face / 2) },
        swings = swings,
    )
}
