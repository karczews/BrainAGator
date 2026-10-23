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

package io.github.karczews.brainagator.ui.screens.games.pattern

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.runtime.Composable
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_pattern
import brainagator.composeapp.generated.resources.game_pattern
import brainagator.composeapp.generated.resources.subtitle_pattern
import io.github.karczews.brainagator.theme.GameTeal
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GamePlaceholder

val PatternGameInfo =
    GameInfo(
        titleRes = Res.string.game_pattern,
        subtitleRes = Res.string.subtitle_pattern,
        descriptionRes = Res.string.desc_pattern,
        icon = Icons.Rounded.GridView,
        accentColor = GameTeal,
        gameType = GameType.Pattern,
        isPlayable = false,
    )

@Composable
fun PatternGameScreen(
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
