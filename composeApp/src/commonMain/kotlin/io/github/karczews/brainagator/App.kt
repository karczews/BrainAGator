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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.navigation.Route
import io.github.karczews.brainagator.ui.screens.GameSelectionScreen
import io.github.karczews.brainagator.ui.screens.GameWonScreen
import io.github.karczews.brainagator.ui.screens.games
import io.github.karczews.brainagator.ui.screens.games.ColorMatchGameScreen
import io.github.karczews.brainagator.ui.screens.games.NumberOrderGameScreen
import io.github.karczews.brainagator.ui.screens.games.OddOneOutGameScreen
import io.github.karczews.brainagator.ui.screens.games.PatternGameScreen
import io.github.karczews.brainagator.ui.screens.games.ShapeMatchGameScreen
import io.github.karczews.brainagator.ui.screens.games.SizeOrderGameScreen
import io.github.karczews.brainagator.ui.screens.games.SpotDifferenceGameScreen

@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        Logger.i { "App composable started" }
    }
    MaterialTheme {
        // Navigation 3 back stack - simple mutable list
        val backStack: MutableList<Route> = remember { mutableStateListOf(Route.GameSelection) }

        // Track if welcome message has been spoken - persists across navigation
        var hasSpokenWelcome by rememberSaveable { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            // Create a lookup map for GameInfo by GameType
            val gameInfoMap = remember { games.associateBy { it.gameType } }

            // NavDisplay manages the mapping from routes to content using entryProvider DSL
            NavDisplay(
                backStack = backStack,
                transitionSpec = {
                    // Slide in from right with fade when entering
                    (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> -width / 2 } + fadeOut())
                },
                popTransitionSpec = {
                    // Slide in from left with fade when popping back
                    (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> width } + fadeOut())
                },
                entryProvider =
                    entryProvider {
                        entry<Route.GameSelection> {
                            GameSelectionScreen(
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
                            val onGameWon: () -> Unit = { backStack.add(Route.GameWon) }

                            when (route.gameType) {
                                GameType.ShapeMatch -> ShapeMatchGameScreen(gameInfo, onBackClick, onGameWon)
                                GameType.NumberOrder -> NumberOrderGameScreen(gameInfo, onBackClick, onGameWon)
                                GameType.ColorMatch -> ColorMatchGameScreen(gameInfo, onBackClick, onGameWon)
                                GameType.SizeOrder -> SizeOrderGameScreen(gameInfo, onBackClick, onGameWon)
                                GameType.Pattern -> PatternGameScreen(gameInfo, onBackClick, onGameWon)
                                GameType.OddOneOut -> OddOneOutGameScreen(gameInfo, onBackClick, onGameWon)
                                GameType.SpotDifference -> SpotDifferenceGameScreen(gameInfo, onBackClick, onGameWon)
                            }
                        }
                        entry<Route.GameWon> {
                            GameWonScreen(
                                onBackToMainClick = {
                                    backStack.clear()
                                    backStack.add(Route.GameSelection)
                                },
                            )
                        }
                    },
            )
        }
    }
}
