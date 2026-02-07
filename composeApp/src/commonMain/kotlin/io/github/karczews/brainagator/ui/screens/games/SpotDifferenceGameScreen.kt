package io.github.karczews.brainagator.ui.screens.games

import androidx.compose.runtime.Composable
import io.github.karczews.brainagator.ui.screens.GameInfo

@Composable
fun SpotDifferenceGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit
) {
    GamePlaceholder(
        gameInfo = gameInfo,
        onBackClick = onBackClick
    )
}
