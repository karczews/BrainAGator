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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.game_odd_one_out
import brainagator.composeapp.generated.resources.subtitle_odd_one_out
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo

val OddOneOutGameInfo =
    GameInfo(
        titleRes = Res.string.game_odd_one_out,
        subtitleRes = Res.string.subtitle_odd_one_out,
        icon = Icons.AutoMirrored.Default.HelpOutline,
        gradientColors = listOf(Color(0xFF2AF598), Color(0xFF009EFD)),
        gameType = GameType.OddOneOut,
    )

@Composable
fun OddOneOutGameScreen(
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
