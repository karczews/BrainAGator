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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.app_tagline
import brainagator.composeapp.generated.resources.coming_soon_spoken
import brainagator.composeapp.generated.resources.gator_kid_in_suit_1
import brainagator.composeapp.generated.resources.welcome_message
import io.github.karczews.brainagator.Platform
import io.github.karczews.brainagator.isDebugBuild
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GatorGreen
import io.github.karczews.brainagator.theme.LogoBerry
import io.github.karczews.brainagator.theme.LogoGrape
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.oddoneout.OddOneOutGameInfo
import io.github.karczews.brainagator.ui.screens.games.pattern.PatternGameInfo
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.sizeorder.SizeOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.spotdifference.SpotDifferenceGameInfo
import io.github.karczews.brainagator.ui.screens.home.GameTile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

val games =
    listOf(
        ShapeMatchGameInfo,
        NumberOrderGameInfo,
        ColorMatchGameInfo,
        SizeOrderGameInfo,
        PatternGameInfo,
        OddOneOutGameInfo,
        SpotDifferenceGameInfo,
    )

private const val MIN_COLUMNS = 2
private const val MAX_COLUMNS = 4
private const val BUILD_INFO_ALPHA = 0.6f

/** Widest a tile gets relative to its height, so landscape and tablet tiles stay tile-shaped. */
private const val MAX_TILE_ASPECT = 1.6f

@Composable
fun GameSelectionScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onGameSelected: (GameInfo) -> Unit = {},
    onWelcomeSpoken: () -> Unit = {},
    shouldSpeakWelcome: Boolean = true,
) {
    val tts = rememberTextToSpeech()
    val welcomeText = stringResource(Res.string.welcome_message)
    val comingSoonTexts =
        games.filterNot { it.isAvailable }.associate { game ->
            game.gameType to stringResource(Res.string.coming_soon_spoken, stringResource(game.titleRes))
        }

    // Speak welcome message only once per app session
    LaunchedEffect(Unit) {
        if (shouldSpeakWelcome) {
            tts.speak(welcomeText)
            onWelcomeSpoken()
        }
    }
    PlayfulBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceSm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
        ) {
            SelectionHeader()
            GameGrid(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onGameSelected = { game ->
                    // Unbuilt games say so instead of opening a dead end. Debug builds still open their
                    // placeholder, which holds the "Test: Trigger Win" shortcut.
                    val comingSoonText = comingSoonTexts[game.gameType]
                    if (comingSoonText == null || isDebugBuild) {
                        onGameSelected(game)
                    } else {
                        tts.speak(comingSoonText)
                    }
                },
            )
            BuildInfo(modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
private fun SelectionHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(Dimens.headerMascotCompact),
            painter = painterResource(Res.drawable.gator_kid_in_suit_1),
            contentDescription = null,
        )
        Column(modifier = Modifier.weight(1f, fill = false)) {
            // Shrinks on narrow phones rather than breaking the name across lines.
            BasicText(
                text =
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = LogoGrape)) {
                            append("Brain")
                        }
                        withStyle(SpanStyle(color = LogoBerry)) {
                            append("a")
                        }
                        withStyle(SpanStyle(color = GatorGreen)) {
                            append("gator")
                        }
                    },
                style = MaterialTheme.typography.displaySmall,
                maxLines = 1,
                autoSize =
                    TextAutoSize.StepBased(
                        minFontSize = AutoSizeMin.display,
                        maxFontSize = MaterialTheme.typography.displaySmall.fontSize,
                    ),
            )
            // Plain text, not a pill: in this app pill shapes are buttons.
            Text(
                text = stringResource(Res.string.app_tagline),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * All games at once, with tiles sized from the space left under the header (see [gridLayoutFor]), so a child
 * never has to scroll to find a game. Scrolls only if even the smallest readable tiles do not fit.
 */
@Composable
private fun GameGrid(
    modifier: Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onGameSelected: (GameInfo) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val grid = gridLayoutFor(maxWidth = maxWidth, maxHeight = maxHeight, count = games.size)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight),
            verticalArrangement = Arrangement.spacedBy(Dimens.gameTileGap, Alignment.CenterVertically),
        ) {
            games.chunked(grid.columns).forEach { rowGames ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.gameTileGap, Alignment.CenterHorizontally),
                ) {
                    rowGames.forEach { game ->
                        GameTile(
                            game = game,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
                            onClick = { onGameSelected(game) },
                            modifier = Modifier.size(width = grid.tileWidth, height = grid.tileHeight),
                        )
                    }
                }
            }
        }
    }
}

private data class GridLayout(
    val columns: Int,
    val tileWidth: Dp,
    val tileHeight: Dp,
)

/**
 * Picks the column count (2 to 4) whose tiles come out largest while every game is on screen, preferring more
 * columns on a tie. Tiles are never narrower than [Dimens.gameTileMinWidth] (unless even two columns are) nor
 * shorter than [Dimens.gameTileMinHeight]; at that floor the grid scrolls.
 */
private fun gridLayoutFor(
    maxWidth: Dp,
    maxHeight: Dp,
    count: Int,
): GridLayout {
    val gap = Dimens.gameTileGap
    val options =
        (MAX_COLUMNS downTo MIN_COLUMNS).map { columns ->
            val rows = (count + columns - 1) / columns
            val cellWidth = (maxWidth - gap * (columns - 1)) / columns
            val tileHeight = minOf((maxHeight - gap * (rows - 1)) / rows, Dimens.gameTileHeight)
            GridLayout(columns = columns, tileWidth = minOf(cellWidth, tileHeight * MAX_TILE_ASPECT), tileHeight = tileHeight)
        }
    val best =
        options
            .filter { it.tileWidth >= Dimens.gameTileMinWidth }
            .maxByOrNull { minOf(it.tileWidth, it.tileHeight) }
            ?: options.last()
    return best.copy(tileHeight = best.tileHeight.coerceAtLeast(Dimens.gameTileMinHeight))
}

@Composable
private fun BuildInfo(modifier: Modifier = Modifier) {
    val platform = Platform()
    val buildType = if (isDebugBuild) "debug" else "release"
    Text(
        text = "${platform.name} • $buildType",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = BUILD_INFO_ALPHA),
        modifier = modifier,
    )
}

@Preview
@Composable
private fun GameSelectionScreenPreview() {
    AppTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "preview",
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { _ ->
                GameSelectionScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    onGameSelected = {},
                    onWelcomeSpoken = {},
                    shouldSpeakWelcome = false,
                )
            }
        }
    }
}
