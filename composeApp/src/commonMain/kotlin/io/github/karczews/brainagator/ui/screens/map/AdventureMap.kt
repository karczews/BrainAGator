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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.ui.screens.GameInfo

private const val TRAIL_WIDTH_FRACTION = 0.4f
private const val TRAIL_ALPHA = 0.75f

/**
 * PROTOTYPE: the swamp map. A winding trail links one stop per game in order; the trail between won stops is
 * dotted green, the rest is dotted grey. [suggested] marks the stop the gator stands at.
 */
@Composable
internal fun AdventureMap(
    games: List<GameInfo>,
    progress: SessionProgress,
    suggested: GameInfo?,
    onGameSelected: (GameInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val layout = remember(maxWidth, maxHeight, games.size) { mapLayout(maxWidth, maxHeight, games.size) }
        MapTrail(
            layout = layout,
            completed = games.map { progress.starsFor(it.gameType) > 0 },
            modifier = Modifier.fillMaxSize(),
        )
        games.forEachIndexed { index, game ->
            val origin = layout.stopOrigins[index]
            MapStop(
                game = game,
                stars = progress.starsFor(game.gameType),
                isSuggested = game == suggested,
                // Away from the trail's centre line in portrait; facing onwards along a landscape trail.
                riderOnEnd = layout.isHorizontal || layout.swings[index] > 0,
                face = layout.face,
                labelWidth = layout.stopWidth + Dimens.spaceLg,
                onClick = { onGameSelected(game) },
                modifier =
                    Modifier
                        .absoluteOffset(x = origin.x, y = origin.y)
                        .size(width = layout.stopWidth, height = layout.stopHeight),
            )
        }
    }
}

@Composable
private fun MapTrail(
    layout: MapLayout,
    completed: List<Boolean>,
    modifier: Modifier = Modifier,
) {
    val trailColor = MaterialTheme.colorScheme.surface.copy(alpha = TRAIL_ALPHA)
    val dotColor = MaterialTheme.colorScheme.outline
    val doneColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val points = layout.trailPoints.map { Offset(it.x.toPx(), it.y.toPx()) }
        val trailWidth = layout.face.toPx() * TRAIL_WIDTH_FRACTION
        val dots =
            PathEffect.dashPathEffect(
                intervals = floatArrayOf(0f, Dimens.mapTrailDotGap.toPx()),
                phase = 0f,
            )
        points.zipWithNext().forEachIndexed { index, (from, to) ->
            val segment = trailSegment(from, to, layout.isHorizontal)
            drawPath(segment, trailColor, style = Stroke(width = trailWidth, cap = StrokeCap.Round))
            // Green only between two won stops: the part of the journey already travelled.
            val isTravelled = completed[index] && completed[index + 1]
            drawDots(segment, if (isTravelled) doneColor else dotColor, dots)
        }
    }
}

private fun DrawScope.drawDots(
    segment: Path,
    color: Color,
    dots: PathEffect,
) {
    drawPath(
        path = segment,
        color = color,
        style = Stroke(width = Dimens.mapTrailDot.toPx(), cap = StrokeCap.Round, pathEffect = dots),
    )
}

/** An S-bend from [from] to [to] that leaves and arrives along the trail's main direction. */
private fun trailSegment(
    from: Offset,
    to: Offset,
    isHorizontal: Boolean,
): Path {
    val mid = (from + to) / 2f
    return Path().apply {
        moveTo(from.x, from.y)
        if (isHorizontal) {
            cubicTo(mid.x, from.y, mid.x, to.y, to.x, to.y)
        } else {
            cubicTo(from.x, mid.y, to.x, mid.y, to.x, to.y)
        }
    }
}
