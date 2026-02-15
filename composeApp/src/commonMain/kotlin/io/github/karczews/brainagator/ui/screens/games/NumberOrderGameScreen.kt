package io.github.karczews.brainagator.ui.screens.games

import androidx.compose.runtime.Composable
import io.github.karczews.brainagator.ui.screens.GameInfo

@Composable
fun NumberOrderGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
) {
    GamePlaceholder(
        gameInfo = gameInfo,
        onBackClick = onBackClick,
    )
}
