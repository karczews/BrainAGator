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

package io.github.karczews.brainagator.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.coming_soon_sticker
import brainagator.composeapp.generated.resources.go_to_game
import brainagator.composeapp.generated.resources.next_game
import brainagator.composeapp.generated.resources.previous_game
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.darken
import io.github.karczews.brainagator.theme.lighten
import io.github.karczews.brainagator.ui.elements.TactileColors
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileIconButton
import io.github.karczews.brainagator.ui.elements.TactileSurface
import org.jetbrains.compose.resources.stringResource

private const val HERO_ICON_FRACTION = 0.55f
private const val HERO_GLYPH_FRACTION = 0.6f
private const val MUTED_FACE_LIGHTEN = 0.72f
private const val MUTED_CONTENT_DARKEN = 0.3f
private const val IDLE_DOT_LIGHTEN = 0.55f
private const val STICKER_TILT_DEGREES = -8f

/**
 * One giant game card per page, with the neighbouring cards peeking in at the sides so children see
 * there is more to swipe to. [onCardClick] receives the page of the card that was pressed.
 */
@Composable
internal fun GamePager(
    state: PagerState,
    games: List<GameInfo>,
    onCardClick: (page: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Dimens.pagerPeek),
        pageSpacing = Dimens.spaceMd,
        verticalAlignment = Alignment.CenterVertically,
    ) { page ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GameHeroCard(
                game = games[page],
                onClick = { onCardClick(page) },
                modifier =
                    Modifier
                        .widthIn(max = Dimens.heroCardMaxWidth)
                        .heightIn(max = Dimens.heroCardMaxHeight)
                        .fillMaxSize(),
            )
        }
    }
}

/** A game as one big pressable block: its icon huge in a white circle, its name below. */
@Composable
private fun GameHeroCard(
    game: GameInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        if (game.isPlayable) {
            TactileDefaults.colors(game.accentColor)
        } else {
            // Coming soon: a pale version of the game's color, so it still reads as that game but clearly not ready.
            TactileDefaults.colors(
                face = game.accentColor.lighten(MUTED_FACE_LIGHTEN),
                content = game.accentColor.darken(MUTED_CONTENT_DARKEN),
            )
        }
    TactileSurface(
        modifier = modifier,
        colors = colors,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(Dimens.spaceLg)) {
            val iconSize = minOf(minOf(maxWidth, maxHeight) * HERO_ICON_FRACTION, Dimens.heroCardIconMax)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceLg, Alignment.CenterVertically),
            ) {
                HeroIcon(game = game, size = iconSize)
                Text(
                    text = stringResource(game.titleRes),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    autoSize =
                        TextAutoSize.StepBased(
                            minFontSize = AutoSizeMin.display,
                            maxFontSize = MaterialTheme.typography.displaySmall.fontSize,
                        ),
                )
            }
            if (!game.isPlayable) {
                ComingSoonSticker(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}

@Composable
private fun HeroIcon(
    game: GameInfo,
    size: Dp,
) {
    Box(
        modifier =
            Modifier
                .size(size)
                .background(MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = game.icon,
            contentDescription = null,
            tint = game.accentColor,
            modifier = Modifier.size(size * HERO_GLYPH_FRACTION),
        )
    }
}

/** A tilted "Soon" sticker: square-ish and askew so it reads as a label stuck on, not a button. */
@Composable
private fun ComingSoonSticker(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .rotate(STICKER_TILT_DEGREES)
                .background(AppTheme.extendedColors.reward, MaterialTheme.shapes.small)
                .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceXs),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val ink = AppTheme.extendedColors.onReward
        Icon(
            imageVector = Icons.Rounded.HourglassTop,
            contentDescription = null,
            tint = ink,
            modifier = Modifier.size(Dimens.speakerBadgeIcon),
        )
        Text(text = stringResource(Res.string.coming_soon_sticker), style = MaterialTheme.typography.labelLarge, color = ink)
    }
}

/**
 * Previous / next arrows around a row of page dots, each dot in its game's color and tappable
 * to jump to that game. In narrow columns (landscape phones) the dots move to their own line so
 * the arrows keep their full touch targets.
 */
@Composable
internal fun PagerControls(
    games: List<GameInfo>,
    currentPage: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDotClick: (page: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = games[currentPage].accentColor
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth >= pagerSingleRowMinWidth(games.size)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PagerArrow(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(Res.string.previous_game),
                    onClick = onPrevious,
                    colors = TactileDefaults.surfaceColors(content = accent),
                )
                PageDots(games = games, currentPage = currentPage, onDotClick = onDotClick)
                PagerArrow(
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = stringResource(Res.string.next_game),
                    onClick = onNext,
                    colors = TactileDefaults.colors(accent),
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm, Alignment.CenterVertically),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PagerArrow(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(Res.string.previous_game),
                        onClick = onPrevious,
                        colors = TactileDefaults.surfaceColors(content = accent),
                    )
                    PagerArrow(
                        icon = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = stringResource(Res.string.next_game),
                        onClick = onNext,
                        colors = TactileDefaults.colors(accent),
                    )
                }
                PageDots(games = games, currentPage = currentPage, onDotClick = onDotClick)
            }
        }
    }
}

/** Two arrows plus dots and their gaps: the narrowest width the single-row controls fit in. */
private fun pagerSingleRowMinWidth(dotCount: Int): Dp =
    Dimens.iconButton * 2 + Dimens.spaceMd * 2 +
        Dimens.pageDotSelected + Dimens.pageDot * (dotCount - 1) + Dimens.spaceXs * (dotCount - 1)

@Composable
private fun PagerArrow(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    colors: TactileColors,
) {
    TactileIconButton(
        icon = icon,
        contentDescription = contentDescription,
        onClick = onClick,
        colors = colors,
    )
}

@Composable
private fun PageDots(
    games: List<GameInfo>,
    currentPage: Int,
    onDotClick: (page: Int) -> Unit,
) {
    val goToGame = stringResource(Res.string.go_to_game)
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        games.forEachIndexed { index, game ->
            val selected = index == currentPage
            val width by animateDpAsState(if (selected) Dimens.pageDotSelected else Dimens.pageDot, label = "pageDotWidth")
            val label = stringResource(game.titleRes)
            // Hit area is taller than the visible dot; the width stays visual so the row math holds.
            Box(
                modifier =
                    Modifier
                        .size(width = width, height = Dimens.pageDotHitHeight)
                        .semantics { contentDescription = label }
                        .clickable(role = Role.Button, onClickLabel = goToGame) { onDotClick(index) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(width = width, height = Dimens.pageDot)
                        .background(if (selected) game.accentColor else game.accentColor.lighten(IDLE_DOT_LIGHTEN), CircleShape),
                )
            }
        }
    }
}
