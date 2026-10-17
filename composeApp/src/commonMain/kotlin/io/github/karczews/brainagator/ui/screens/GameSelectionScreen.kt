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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.app_tagline
import brainagator.composeapp.generated.resources.gator_kid_in_suit_1
import brainagator.composeapp.generated.resources.gator_pose_1_4_crawl
import brainagator.composeapp.generated.resources.welcome_message
import io.github.karczews.brainagator.Platform
import io.github.karczews.brainagator.isDebugBuild
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GatorGreen
import io.github.karczews.brainagator.theme.LogoBerry
import io.github.karczews.brainagator.theme.LogoGrape
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileSurface
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.oddoneout.OddOneOutGameInfo
import io.github.karczews.brainagator.ui.screens.games.pattern.PatternGameInfo
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.sizeorder.SizeOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.spotdifference.SpotDifferenceGameInfo
import org.jetbrains.compose.resources.StringResource
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

private val MinTileWidth = 160.dp
private val TileHeight = 184.dp
private val LogoMinFontSize = 24.sp
private const val MIN_COLUMNS = 2
private const val MAX_COLUMNS = 4
private const val BUILD_INFO_ALPHA = 0.6f

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
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLg),
        ) {
            SelectionHeader()
            GameGrid(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onGameSelected = onGameSelected,
            )
            Image(
                modifier = Modifier.height(110.dp),
                painter = painterResource(Res.drawable.gator_pose_1_4_crawl),
                contentDescription = null,
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
            modifier = Modifier.size(88.dp),
            painter = painterResource(Res.drawable.gator_kid_in_suit_1),
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
        ) {
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
                        minFontSize = LogoMinFontSize,
                        maxFontSize = MaterialTheme.typography.displaySmall.fontSize,
                    ),
            )
            Text(
                text = stringResource(Res.string.app_tagline),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}

/** Lays games out in as many columns as fit (2 to 4), centering a shorter last row. */
@Composable
private fun GameGrid(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onGameSelected: (GameInfo) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        // Count the gaps too, so each tile really gets at least MinTileWidth.
        val columns = ((maxWidth + Dimens.spaceMd) / (MinTileWidth + Dimens.spaceMd)).toInt().coerceIn(MIN_COLUMNS, MAX_COLUMNS)
        val tileWidth = (maxWidth - Dimens.spaceMd * (columns - 1)) / columns
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)) {
            games.chunked(columns).forEach { rowGames ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterHorizontally),
                ) {
                    rowGames.forEach { game ->
                        GameTile(
                            modifier = Modifier.width(tileWidth),
                            game = game,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
                            onClick = { onGameSelected(game) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameTile(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    game: GameInfo,
    onClick: () -> Unit,
) {
    TactileSurface(
        modifier = modifier.height(TileHeight),
        colors = TactileDefaults.colors(game.accentColor),
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            GameIcon(icon = game.icon, tint = game.accentColor)

            Spacer(modifier = Modifier.height(10.dp))

            GameTitleText(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                titleRes = game.titleRes,
            )

            Text(
                text = stringResource(game.subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun GameIcon(
    icon: ImageVector,
    tint: Color,
) {
    Box(
        modifier =
            Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun GameTitleText(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    titleRes: StringResource,
) {
    with(sharedTransitionScope) {
        Text(
            modifier =
                Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = titleRes),
                    animatedVisibilityScope = animatedContentScope,
                ),
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
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
