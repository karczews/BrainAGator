package io.github.karczews.brainagator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.karczews.brainagator.ui.FireworksAnimation
import io.github.karczews.brainagator.ui.navigation.Route
import io.github.karczews.brainagator.ui.screens.GameSelectionScreen
import io.github.karczews.brainagator.ui.screens.games
import io.github.karczews.brainagator.ui.screens.games.*

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Navigation 3 back stack - simple mutable list
        val backStack: MutableList<Route> = remember { mutableStateListOf(Route.GameSelection) }
        var showFireworks by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            // NavDisplay manages the mapping from routes to content
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { route ->
                    when (route) {
                        is Route.GameSelection -> NavEntry(route) {
                            GameSelectionScreen(
                                onGameSelected = { game ->
                                    showFireworks = true
                                    backStack.add(Route.Game(game.title))
                                }
                            )
                        }
                        is Route.Game -> {
                            val gameInfo = games.first { it.title == route.gameTitle }
                            NavEntry(route) {
                                when (route.gameTitle) {
                                    "Shape Match" -> ShapeMatchGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    "Number Order" -> NumberOrderGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    "Color Match" -> ColorMatchGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    "Size Order" -> SizeOrderGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    "Pattern Game" -> PatternGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    "Odd One Out" -> OddOneOutGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    "Spot Difference" -> SpotDifferenceGameScreen(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                    else -> GamePlaceholder(
                                        gameInfo = gameInfo,
                                        onBackClick = {
                                            showFireworks = false
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )

            // Global Fireworks animation overlay
            AnimatedVisibility(
                visible = showFireworks,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                FireworksAnimation(
                    modifier = Modifier.fillMaxSize(),
                    particleCount = 150,
                    explosionCount = 3,
                    onAnimationComplete = {
                        showFireworks = false
                    }
                )
            }
        }
    }
}
