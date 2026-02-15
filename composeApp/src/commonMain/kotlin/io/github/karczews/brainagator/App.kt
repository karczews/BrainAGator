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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
    MaterialTheme {
        // Navigation 3 back stack - simple mutable list
        val backStack: MutableList<Route> = remember { mutableStateListOf(Route.GameSelection) }

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
                            )
                        }
                        entry<Route.Game> { route ->
                            val gameInfo =
                                gameInfoMap[route.gameType]
                                    ?: error("Unknown game type: ${route.gameType}")
                            when (route.gameType) {
                                GameType.ShapeMatch -> {
                                    ShapeMatchGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }

                                GameType.NumberOrder -> {
                                    NumberOrderGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }

                                GameType.ColorMatch -> {
                                    ColorMatchGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }

                                GameType.SizeOrder -> {
                                    SizeOrderGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }

                                GameType.Pattern -> {
                                    PatternGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }

                                GameType.OddOneOut -> {
                                    OddOneOutGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }

                                GameType.SpotDifference -> {
                                    SpotDifferenceGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            backStack.removeLastOrNull()
                                        },
                                        onGameWon = {
                                            backStack.add(Route.GameWon)
                                        },
                                    )
                                }
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
