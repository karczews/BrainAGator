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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
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
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.app_tagline
import brainagator.composeapp.generated.resources.map_next_stop_prompt
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
import io.github.karczews.brainagator.ui.elements.TactileButton
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.oddoneout.OddOneOutGameInfo
import io.github.karczews.brainagator.ui.screens.games.pattern.PatternGameInfo
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.sizeorder.SizeOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.spotdifference.SpotDifferenceGameInfo
import io.github.karczews.brainagator.ui.screens.map.AdventureMap
import io.github.karczews.brainagator.ui.screens.map.SessionProgress
import org.jetbrains.compose.resources.stringResource

/** The games in map order: the trail starts at the first and ends at the last. */
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

private const val BUILD_INFO_ALPHA = 0.6f

/**
 * PROTOTYPE home screen: the swamp map. Instead of a menu of tiles, the gator stands at the suggested next stop and
 * one big button starts it; any other playable stop can still be tapped directly.
 */
@Composable
fun GameSelectionScreen(
    progress: SessionProgress = SessionProgress(),
    onGameSelected: (GameInfo) -> Unit = {},
    onWelcomeSpoken: () -> Unit = {},
    shouldSpeakWelcome: Boolean = true,
) {
    val tts = rememberTextToSpeech()
    val suggested = progress.suggestedGame(games)
    val welcomeText = stringResource(Res.string.welcome_message)
    val nextStopText = suggested?.let { stringResource(Res.string.map_next_stop_prompt, stringResource(it.titleRes)) }

    // Welcome once per app session; after a win, the gator announces where the journey goes next.
    LaunchedEffect(Unit) {
        if (shouldSpeakWelcome) {
            tts.speak(welcomeText)
            onWelcomeSpoken()
        } else if (progress.lastWon != null && nextStopText != null) {
            tts.speak(nextStopText)
        }
    }
    PlayfulBackground(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            val isLandscape = maxWidth > maxHeight
            // Short portrait windows scroll rather than overlapping stops; landscape always fits across.
            val scrolls = !isLandscape && maxHeight < Dimens.mapScreenMinHeight
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceSm),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
            ) {
                MapHeader(isLandscape = isLandscape)
                // Only the map scrolls, so the big button stays on screen.
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .then(if (scrolls) Modifier.verticalScroll(rememberScrollState()) else Modifier),
                ) {
                    AdventureMap(
                        games = games,
                        progress = progress,
                        suggested = suggested,
                        onGameSelected = onGameSelected,
                        modifier =
                            if (scrolls) {
                                // Inset so the suggested stop's halo isn't clipped by the scroll area.
                                Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSm).height(Dimens.mapScrollHeight)
                            } else {
                                Modifier.fillMaxSize()
                            },
                    )
                }
                if (suggested != null) {
                    TactileButton(
                        text = stringResource(suggested.titleRes),
                        onClick = { onGameSelected(suggested) },
                        icon = Icons.Rounded.PlayArrow,
                        colors = TactileDefaults.colors(suggested.accentColor),
                        modifier = Modifier.widthIn(max = Dimens.buttonMaxWidth).fillMaxWidth(),
                    )
                }
                BuildInfo(modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

/** Logo and tagline: stacked in portrait, side by side in landscape to leave the map more height. */
@Composable
private fun MapHeader(isLandscape: Boolean) {
    if (isLandscape) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Logo()
            Tagline()
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Logo()
            Tagline()
        }
    }
}

@Composable
private fun Logo() {
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
        style = MaterialTheme.typography.headlineLarge,
        maxLines = 1,
        autoSize =
            TextAutoSize.StepBased(
                minFontSize = AutoSizeMin.display,
                maxFontSize = MaterialTheme.typography.headlineLarge.fontSize,
            ),
    )
}

/** Plain text rather than a pill, so it doesn't read as a button. */
@Composable
private fun Tagline() {
    Text(
        text = stringResource(Res.string.app_tagline),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
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
        GameSelectionScreen(
            progress = SessionProgress().withWin(GameType.ShapeMatch),
            shouldSpeakWelcome = false,
        )
    }
}
