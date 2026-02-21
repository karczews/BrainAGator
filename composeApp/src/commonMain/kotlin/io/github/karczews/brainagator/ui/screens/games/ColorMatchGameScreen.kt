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

package io.github.karczews.brainagator.ui.screens.games

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.game_color_match
import brainagator.composeapp.generated.resources.subtitle_color_match
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo

val ColorMatchGameInfo =
    GameInfo(
        titleRes = Res.string.game_color_match,
        subtitleRes = Res.string.subtitle_color_match,
        icon = Icons.Default.Palette,
        gradientColors = listOf(Color(0xFFFA709A), Color(0xFFFEE140)),
        gameType = GameType.ColorMatch,
    )

@Composable
fun ColorMatchGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
) {
    GamePlaceholder(
        gameInfo = gameInfo,
        onBackClick = onBackClick,
        onGameWon = onGameWon,
    )
}
