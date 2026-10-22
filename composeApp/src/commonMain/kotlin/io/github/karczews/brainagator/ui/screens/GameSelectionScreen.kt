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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.coming_soon_speech
import brainagator.composeapp.generated.resources.game_intro_speech
import brainagator.composeapp.generated.resources.welcome_message
import io.github.karczews.brainagator.LocalIsDebugBuild
import io.github.karczews.brainagator.LocalSharedTransitionContext
import io.github.karczews.brainagator.Platform
import io.github.karczews.brainagator.SharedTransitionContext
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GatorGreen
import io.github.karczews.brainagator.theme.LogoBerry
import io.github.karczews.brainagator.theme.LogoGrape
import io.github.karczews.brainagator.tts.TextToSpeech
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.tts.speakNow
import io.github.karczews.brainagator.ui.elements.InstructionHeader
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.oddoneout.OddOneOutGameInfo
import io.github.karczews.brainagator.ui.screens.games.pattern.PatternGameInfo
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.sizeorder.SizeOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.spotdifference.SpotDifferenceGameInfo
import kotlinx.coroutines.launch
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

private const val BUILD_INFO_ALPHA = 0.6f
private const val LANDSCAPE_SIDE_WEIGHT = 0.42f
private const val LANDSCAPE_PAGER_WEIGHT = 0.58f

/**
 * Home screen, led by the gator: one big game at a time. The gator says what the game on screen is
 * (and says it again when pressed); arrows, swipes and the colored dots move between games, and pressing
 * the card plays it. Games that are not ready yet make the gator say so instead (debug builds still open
 * their placeholder, so the test win button stays reachable).
 */
@Composable
fun GameSelectionScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onGameSelected: (GameInfo) -> Unit = {},
    onWelcomeSpoken: () -> Unit = {},
    shouldSpeakWelcome: Boolean = true,
) {
    val tts = rememberTextToSpeech()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { games.size }
    val intros = games.map { gameIntro(it) }
    SpeakPageIntros(
        pagerState = pagerState,
        tts = tts,
        intros = intros,
        shouldSpeakWelcome = shouldSpeakWelcome,
        onWelcomeSpoken = onWelcomeSpoken,
    )

    val game = games[pagerState.currentPage]
    val accent by animateColorAsState(game.accentColor, label = "homeAccent")
    val comingSoon = stringResource(Res.string.coming_soon_speech)
    val isDebug = LocalIsDebugBuild.current
    val onCardClick: (Int) -> Unit = { page ->
        val pressed = games[page]
        when {
            // A peeking playable card starts its game on the first tap; only coming-soon
            // cards off-center scroll into view (a second tap then says they are coming soon).
            pressed.isPlayable || isDebug -> onGameSelected(pressed)
            page != pagerState.currentPage -> scope.launch { pagerState.animateScrollToPage(page) }
            else -> tts.speakNow(comingSoon)
        }
    }

    CompositionLocalProvider(
        LocalSharedTransitionContext provides SharedTransitionContext(sharedTransitionScope, animatedContentScope),
    ) {
        PlayfulBackground(modifier = Modifier.fillMaxSize(), accent = accent) {
            HomeLayout(
                speech = {
                    InstructionHeader(
                        text = stringResource(if (game.isPlayable) game.subtitleRes else Res.string.coming_soon_speech),
                        onGatorClick = { tts.speakNow(intros[pagerState.currentPage]) },
                        accent = game.accentColor,
                        modifier = Modifier.widthIn(max = Dimens.contentMaxWidth),
                    )
                },
                pager = { modifier -> GamePager(state = pagerState, games = games, onCardClick = onCardClick, modifier = modifier) },
                controls = { HomePagerControls(pagerState = pagerState) },
            )
        }
    }
}

/** Portrait: logo, gator, the pager filling the middle, controls at the bottom. Landscape: everything but the pager on the side. */
@Composable
private fun HomeLayout(
    speech: @Composable () -> Unit,
    pager: @Composable (Modifier) -> Unit,
    controls: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(vertical = Dimens.spaceMd),
    ) {
        if (maxWidth > maxHeight) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier =
                        Modifier
                            .weight(LANDSCAPE_SIDE_WEIGHT)
                            .fillMaxHeight()
                            .padding(start = Dimens.screenPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterVertically),
                ) {
                    Logo()
                    speech()
                    controls()
                    BuildInfo(modifier = Modifier.align(Alignment.End).padding(horizontal = Dimens.screenPadding))
                }
                pager(Modifier.weight(LANDSCAPE_PAGER_WEIGHT).fillMaxHeight())
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd),
            ) {
                Logo(modifier = Modifier.padding(horizontal = Dimens.screenPadding))
                Column(modifier = Modifier.padding(horizontal = Dimens.screenPadding)) { speech() }
                pager(Modifier.weight(1f).fillMaxWidth())
                controls()
                BuildInfo(modifier = Modifier.align(Alignment.End).padding(horizontal = Dimens.screenPadding))
            }
        }
    }
}

/** The gator introduces the first page after the welcome (once per session), and every later page once it settles. */
@Composable
private fun SpeakPageIntros(
    pagerState: PagerState,
    tts: TextToSpeech,
    intros: List<String>,
    shouldSpeakWelcome: Boolean,
    onWelcomeSpoken: () -> Unit,
) {
    val welcomeText = stringResource(Res.string.welcome_message)
    val currentIntros by rememberUpdatedState(intros)
    val speakWelcome by rememberUpdatedState(shouldSpeakWelcome)
    val welcomeSpoken by rememberUpdatedState(onWelcomeSpoken)
    LaunchedEffect(pagerState) {
        var isFirstPage = true
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (!isFirstPage) {
                tts.speakNow(currentIntros[page])
            } else {
                // Always introduce the first page: returning home skips only the welcome itself.
                if (speakWelcome) {
                    tts.speak(welcomeText)
                    welcomeSpoken()
                }
                tts.speak(currentIntros[page])
            }
            isFirstPage = false
        }
    }
}

@Composable
private fun HomePagerControls(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    // Wraps around at both ends, so the arrows always do something.
    PagerControls(
        games = games,
        currentPage = pagerState.currentPage,
        onPrevious = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1 + games.size) % games.size) } },
        onNext = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1) % games.size) } },
        onDotClick = { scope.launch { pagerState.animateScrollToPage(it) } },
    )
}

@Composable
private fun gameIntro(game: GameInfo): String =
    stringResource(
        Res.string.game_intro_speech,
        stringResource(game.titleRes),
        stringResource(if (game.isPlayable) game.subtitleRes else Res.string.coming_soon_speech),
    )

@Composable
private fun Logo(modifier: Modifier = Modifier) {
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
        modifier = modifier,
        style = MaterialTheme.typography.headlineLarge,
        maxLines = 1,
        autoSize =
            TextAutoSize.StepBased(
                minFontSize = AutoSizeMin.display,
                maxFontSize = MaterialTheme.typography.headlineLarge.fontSize,
            ),
    )
}

@Composable
private fun BuildInfo(modifier: Modifier = Modifier) {
    val platform = Platform()
    val buildType = if (LocalIsDebugBuild.current) "debug" else "release"
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
