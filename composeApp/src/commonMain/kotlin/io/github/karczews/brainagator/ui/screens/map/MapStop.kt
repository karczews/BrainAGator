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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.gator_kid_in_suit_1
import brainagator.composeapp.generated.resources.map_stop_locked
import brainagator.composeapp.generated.resources.map_stop_stars
import io.github.karczews.brainagator.isDebugBuild
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.darken
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileSurface
import io.github.karczews.brainagator.ui.screens.GameInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val ICON_FRACTION = 0.45f
private const val LOCK_FRACTION = 0.45f
private const val STONE_FRACTION = 0.7f
private const val PULSE_SCALE = 1.06f
private const val PULSE_MILLIS = 900
private const val HALO_ALPHA = 0.25f
private const val LABEL_DARKEN_FRACTION = 0.2f
private const val LABEL_BACKDROP_ALPHA = 0.85f

/**
 * The gator is drawn this tall relative to the stop it stands beside, and this share of its width sits outside the
 * stop: it only touches the circle's rim, never the icon, and still fits within the screen padding at the edges.
 */
private const val RIDER_HEIGHT_FRACTION = 0.6f
private const val RIDER_ASPECT = 84f / 96f
private const val RIDER_OVERHANG = 0.8f

/**
 * One stop on the map: the game's round face, the stars won there this session and its name. Playable stops are
 * tactile buttons in the game's accent; games that are not built yet are flat stones with a padlock. The suggested
 * stop pulses and has the gator standing at it, on the [riderOnEnd] side.
 */
@Composable
internal fun MapStop(
    game: GameInfo,
    stars: Int,
    isSuggested: Boolean,
    riderOnEnd: Boolean,
    face: Dp,
    labelWidth: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(game.titleRes)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs / 2),
    ) {
        if (game.isComingSoon) {
            // PROTOTYPE: release builds can't open unbuilt games; debug builds still can, to reach "Test: Trigger Win".
            LockedStone(
                face = face,
                description = stringResource(Res.string.map_stop_locked, title),
                onClick = onClick.takeIf { isDebugBuild },
            )
            StopLabel(title = title, width = labelWidth, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Box(modifier = Modifier.size(width = face, height = face + Dimens.lipHeight)) {
                PlayableStop(
                    game = game,
                    face = face,
                    isSuggested = isSuggested,
                    description = stringResource(Res.string.map_stop_stars, title, stars, MAX_STOP_STARS),
                    onClick = onClick,
                )
                if (isSuggested) {
                    GatorRider(face = face, onEnd = riderOnEnd)
                }
            }
            StopStars(stars = stars)
            StopLabel(title = title, width = labelWidth, color = game.accentColor.darken(LABEL_DARKEN_FRACTION))
        }
    }
}

/** A stop's name, right under it. May be wider than the stop itself: neighbouring labels never sit at the same height. */
@Composable
private fun StopLabel(
    title: String,
    width: Dp,
    color: Color,
) {
    Box(modifier = Modifier.requiredWidth(width), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            modifier =
                Modifier
                    // A soft backdrop keeps the trail's dots from running through the name.
                    .background(MaterialTheme.colorScheme.background.copy(alpha = LABEL_BACKDROP_ALPHA), MaterialTheme.shapes.small)
                    .padding(horizontal = Dimens.spaceXs)
                    .clearAndSetSemantics {},
            style = MaterialTheme.typography.labelSmall,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = AutoSizeMin.caption,
                    maxFontSize = MaterialTheme.typography.labelSmall.fontSize,
                ),
        )
    }
}

@Composable
private fun PlayableStop(
    game: GameInfo,
    face: Dp,
    isSuggested: Boolean,
    description: String,
    onClick: () -> Unit,
) {
    val pulse by rememberInfiniteTransition(label = "suggestedStop").animateFloat(
        initialValue = 1f,
        targetValue = if (isSuggested) PULSE_SCALE else 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(PULSE_MILLIS, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulse",
    )
    val halo = game.accentColor.copy(alpha = if (isSuggested) HALO_ALPHA else 0f)
    TactileSurface(
        modifier =
            Modifier
                .size(width = face, height = face + Dimens.lipHeight)
                .drawBehind {
                    // Centred on the face, not the face plus lip.
                    val faceCenter = Offset(size.width / 2, size.width / 2)
                    drawCircle(color = halo, radius = size.width / 2 + Dimens.spaceSm.toPx(), center = faceCenter)
                }.scale(pulse)
                .semantics { contentDescription = description },
        colors = TactileDefaults.colors(game.accentColor),
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(imageVector = game.icon, contentDescription = null, modifier = Modifier.size(face * ICON_FRACTION))
    }
}

/**
 * A flat (lipless) stone: nothing to press until the game exists. Smaller than a playable stop so the games you can
 * play stand out, and centred where a stop's face would be, so the trail still runs through it.
 */
@Composable
private fun LockedStone(
    face: Dp,
    description: String,
    onClick: (() -> Unit)?,
) {
    val stone = face * STONE_FRACTION
    Box(
        modifier =
            Modifier
                .padding(top = (face - stone) / 2)
                .size(stone)
                .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(stone * LOCK_FRACTION),
        )
    }
}

/** The pixel-art gator standing beside the stop it suggests, just touching its rim. */
@Composable
private fun GatorRider(
    face: Dp,
    onEnd: Boolean,
) {
    val height = face * RIDER_HEIGHT_FRACTION
    val width = height * RIDER_ASPECT
    val overhang = width * RIDER_OVERHANG
    val x = if (onEnd) face - width + overhang else -overhang
    // Feet level with the stop's stars, so its head stays below neighbouring labels.
    val feet = face + StopExtraHeight - Dimens.mapStopLabelHeight
    Image(
        painter = painterResource(Res.drawable.gator_kid_in_suit_1),
        contentDescription = null,
        modifier =
            Modifier
                .size(width = width, height = height)
                .offset(x = x, y = feet - height),
    )
}

/** Stars won at a playable stop. */
@Composable
private fun StopStars(stars: Int) {
    val earnedFill = AppTheme.extendedColors.reward
    val earnedOutline = AppTheme.extendedColors.onReward
    val emptyOutline = MaterialTheme.colorScheme.outline
    Row(modifier = Modifier.height(Dimens.mapStopStar).clearAndSetSemantics {}) {
        repeat(MAX_STOP_STARS) { index ->
            val isEarned = index < stars
            Box(modifier = Modifier.size(Dimens.mapStopStar)) {
                if (isEarned) {
                    StarIcon(filled = true, tint = earnedFill)
                }
                StarIcon(filled = false, tint = if (isEarned) earnedOutline else emptyOutline)
            }
        }
    }
}

@Composable
private fun StarIcon(
    filled: Boolean,
    tint: Color,
) {
    Icon(
        imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(Dimens.mapStopStar),
    )
}
