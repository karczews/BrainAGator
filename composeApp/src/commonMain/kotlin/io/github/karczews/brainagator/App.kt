package io.github.karczews.brainagator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.karczews.brainagator.ui.FireworksAnimation
import io.github.karczews.brainagator.ui.navigation.Screen
import io.github.karczews.brainagator.ui.navigation.rememberNavController
import io.github.karczews.brainagator.ui.screens.GameSelectionScreen
import io.github.karczews.brainagator.ui.screens.games.*

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        var showFireworks by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            // Main content with crossfade animation between screens
            AnimatedContent(
                targetState = navController.currentScreen.value,
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    is Screen.GameSelection -> {
                        GameSelectionScreen(
                            onGameSelected = { game ->
                                showFireworks = true
                                navController.navigateTo(Screen.Game(game))
                            }
                        )
                    }
                    is Screen.Game -> {
                        val gameInfo = screen.gameInfo
                        when (gameInfo.title) {
                            "Shape Match" -> ShapeMatchGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            "Number Order" -> NumberOrderGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            "Color Match" -> ColorMatchGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            "Size Order" -> SizeOrderGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            "Pattern Game" -> PatternGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            "Odd One Out" -> OddOneOutGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            "Spot Difference" -> SpotDifferenceGameScreen(
                                gameInfo = gameInfo,
                                onBackClick = {
                                    showFireworks = false
                                    navController.navigateBack()
                                }
                            )
                            else -> {
                                // Fallback for unknown games
                                GamePlaceholder(
                                    gameInfo = gameInfo,
                                    onBackClick = {
                                        showFireworks = false
                                        navController.navigateBack()
                                    }
                                )
                            }
                        }
                    }
                }
            }

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

@Composable
fun AnimatedContent(
    targetState: Screen,
    modifier: Modifier = Modifier,
    content: @Composable (Screen) -> Unit
) {
    androidx.compose.animation.AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            val isForward = initialState is Screen.GameSelection && targetState is Screen.Game
            if (isForward) {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> width } + fadeOut()
            }
        }
    ) { screen ->
        content(screen)
    }
}
