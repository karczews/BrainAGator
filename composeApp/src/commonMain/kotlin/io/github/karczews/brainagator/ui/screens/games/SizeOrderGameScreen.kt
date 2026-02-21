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
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_size_order
import brainagator.composeapp.generated.resources.game_size_order
import brainagator.composeapp.generated.resources.subtitle_size_order
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo

val SizeOrderGameInfo =
    GameInfo(
        titleRes = Res.string.game_size_order,
        subtitleRes = Res.string.subtitle_size_order,
        descriptionRes = Res.string.desc_size_order,
        icon = Icons.Default.SwapVert,
        gradientColors = listOf(Color(0xFFF093FB), Color(0xFFF5576C)),
        gameType = GameType.SizeOrder,
    )

@Composable
fun SizeOrderGameScreen(
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
