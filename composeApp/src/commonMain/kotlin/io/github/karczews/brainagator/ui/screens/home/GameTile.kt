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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.coming_soon_badge
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.darken
import io.github.karczews.brainagator.theme.lighten
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileSurface
import io.github.karczews.brainagator.ui.screens.GameInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val SOON_FACE_TINT = 0.82f
private const val SOON_BORDER_TINT = 0.55f
private const val SOON_PICTURE_ALPHA = 0.45f
private const val SOON_TEXT_DARKEN = 0.25f
private const val STICKER_POP_SCALE = 1.35f
private val SoonBorderWidth = 2.dp

/**
 * A game's tile on the home screen: a picture of the game in a white window above its title, on the game's accent.
 * Games that are not built yet get a flat, muted tile with a "Soon" sticker: no lip, because tapping does not open it.
 */
@Composable
fun GameTile(
    game: GameInfo,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title: @Composable () -> Unit = {
        GameTitleText(game = game, sharedTransitionScope = sharedTransitionScope, animatedContentScope = animatedContentScope)
    }
    if (game.isAvailable) {
        TactileSurface(
            modifier = modifier,
            colors = TactileDefaults.colors(game.accentColor),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = onClick,
        ) {
            TileContent(game = game, title = title)
        }
    } else {
        ComingSoonTile(game = game, onClick = onClick, modifier = modifier, title = title)
    }
}

@Composable
private fun ComingSoonTile(
    game: GameInfo,
    onClick: () -> Unit,
    modifier: Modifier,
    title: @Composable () -> Unit,
) {
    val shape = MaterialTheme.shapes.extraLarge
    val stickerPop = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier =
            modifier
                // No lip: the face sits where a pressable tile's face would, so rows stay aligned.
                .padding(bottom = Dimens.lipHeight)
                .clip(shape)
                .background(game.accentColor.lighten(SOON_FACE_TINT))
                .border(SoonBorderWidth, game.accentColor.lighten(SOON_BORDER_TINT), shape)
                .clickable(interactionSource = null, indication = null, role = Role.Button) {
                    // The sticker pops so the tap is acknowledged even with the sound off.
                    scope.launch {
                        stickerPop.snapTo(STICKER_POP_SCALE)
                        stickerPop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
                    }
                    onClick()
                },
    ) {
        CompositionLocalProvider(LocalContentColor provides game.accentColor.darken(SOON_TEXT_DARKEN)) {
            TileContent(game = game, title = title, pictureAlpha = SOON_PICTURE_ALPHA)
        }
        SoonSticker(
            game = game,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.spaceXs)
                    .graphicsLayer {
                        scaleX = stickerPop.value
                        scaleY = stickerPop.value
                    },
        )
    }
}

@Composable
private fun TileContent(
    game: GameInfo,
    title: @Composable () -> Unit,
    pictureAlpha: Float = 1f,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(Dimens.spaceSm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
    ) {
        GamePicture(
            gameType = game.gameType,
            accent = game.accentColor,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
                    .padding(Dimens.spaceSm)
                    .alpha(pictureAlpha),
        )
        title()
    }
}

@Composable
private fun GameTitleText(
    game: GameInfo,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    with(sharedTransitionScope) {
        Text(
            modifier =
                Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = game.titleRes),
                    animatedVisibilityScope = animatedContentScope,
                ),
            text = stringResource(game.titleRes),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            // One line keeps every tile's picture the same size; long (e.g. Polish) titles shrink instead,
            // down to the caption floor, since the picture above carries the meaning.
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = AutoSizeMin.caption,
                    maxFontSize = MaterialTheme.typography.titleMedium.fontSize,
                ),
        )
    }
}

@Composable
private fun SoonSticker(
    game: GameInfo,
    modifier: Modifier = Modifier,
) {
    val content = AppTheme.extendedColors.onAccent
    Row(
        modifier =
            modifier
                .background(game.accentColor.darken(SOON_TEXT_DARKEN), CircleShape)
                .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceXs),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(Dimens.stickerIcon),
        )
        Text(
            text = stringResource(Res.string.coming_soon_badge),
            style = MaterialTheme.typography.labelMedium,
            color = content,
        )
    }
}
