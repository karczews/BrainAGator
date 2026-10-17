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

package io.github.karczews.brainagator

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.navigation.Route
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.GameSelectionScreen
import io.github.karczews.brainagator.ui.screens.GameWonScreen
import io.github.karczews.brainagator.ui.screens.games
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameScreen
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameScreen
import io.github.karczews.brainagator.ui.screens.games.oddoneout.OddOneOutGameScreen
import io.github.karczews.brainagator.ui.screens.games.pattern.PatternGameScreen
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameScreen
import io.github.karczews.brainagator.ui.screens.games.sizeorder.SizeOrderGameScreen
import io.github.karczews.brainagator.ui.screens.games.spotdifference.SpotDifferenceGameScreen
import io.github.karczews.brainagator.ui.screens.map.SessionProgress

@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        Logger.i { "App composable started" }
    }
    AppTheme {
        AppNavigation(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    // Navigation 3 back stack - simple mutable list
    val backStack: MutableList<Route> = remember { mutableStateListOf(Route.GameSelection) }

    // Track if a welcome message has been spoken - persists across navigation
    var hasSpokenWelcome by rememberSaveable { mutableStateOf(false) }

    // PROTOTYPE (adventure map): stars per game for this session only; nothing is persisted.
    var progress by remember { mutableStateOf(SessionProgress()) }

    // Create a lookup map for GameInfo by GameType
    val gameInfoMap = remember { games.associateBy { it.gameType } }

    fun showMap() {
        backStack.clear()
        backStack.add(Route.GameSelection)
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        entryProvider =
            entryProvider {
                entry<Route.GameSelection> {
                    GameSelectionScreen(
                        progress = progress,
                        onGameSelected = { game ->
                            backStack.add(Route.Game(game.gameType))
                        },
                        onWelcomeSpoken = {
                            hasSpokenWelcome = true
                        },
                        shouldSpeakWelcome = !hasSpokenWelcome,
                    )
                }
                entry<Route.Game> { route ->
                    val gameInfo =
                        gameInfoMap[route.gameType]
                            ?: error("Unknown game type: ${route.gameType}")
                    val onBackClick: () -> Unit = { backStack.removeLastOrNull() }
                    val onGameWon: () -> Unit = {
                        progress = progress.withWin(route.gameType)
                        backStack.add(Route.GameWon(route.gameType))
                    }
                    GameScreen(gameType = route.gameType, gameInfo = gameInfo, onBackClick = onBackClick, onGameWon = onGameWon)
                }
                entry<Route.GameWon> { route ->
                    GameWonScreen(
                        onNextStopClick = ::showMap,
                        onPlayAgainClick = {
                            // Map underneath, so back from the replayed game returns to the journey.
                            showMap()
                            backStack.add(Route.Game(route.gameType))
                        },
                    )
                }
            },
    )
}

@Composable
private fun GameScreen(
    gameType: GameType,
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
) {
    when (gameType) {
        GameType.ShapeMatch -> ShapeMatchGameScreen(gameInfo, onBackClick, onGameWon)
        GameType.NumberOrder -> NumberOrderGameScreen(gameInfo, onBackClick, onGameWon)
        GameType.ColorMatch -> ColorMatchGameScreen(gameInfo, onBackClick, onGameWon)
        GameType.SizeOrder -> SizeOrderGameScreen(gameInfo, onBackClick, onGameWon)
        GameType.Pattern -> PatternGameScreen(gameInfo, onBackClick, onGameWon)
        GameType.OddOneOut -> OddOneOutGameScreen(gameInfo, onBackClick, onGameWon)
        GameType.SpotDifference -> SpotDifferenceGameScreen(gameInfo, onBackClick, onGameWon)
    }
}
