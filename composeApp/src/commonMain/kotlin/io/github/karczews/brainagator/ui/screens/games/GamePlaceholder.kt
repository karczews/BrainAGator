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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.game_coming_soon
import brainagator.composeapp.generated.resources.test_trigger_win
import io.github.karczews.brainagator.LocalIsDebugBuild
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.tts.speakNow
import io.github.karczews.brainagator.ui.elements.InstructionHeader
import io.github.karczews.brainagator.ui.elements.TactileButton
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.screens.GameInfo
import org.jetbrains.compose.resources.stringResource

@Composable
fun GamePlaceholder(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: (() -> Unit)? = null,
) {
    val comingSoon = stringResource(Res.string.game_coming_soon)
    GameScreenScaffold(
        gameInfo = gameInfo,
        onBackClick = onBackClick,
        entrySpeech = comingSoon,
    ) { innerPadding, tts ->
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            // Centered when it fits, scrollable when it doesn't (e.g. landscape phones).
            Column(
                modifier =
                    Modifier
                        .widthIn(max = Dimens.contentMaxWidth)
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                        .padding(horizontal = Dimens.screenPadding, vertical = Dimens.spaceMd),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceLg, Alignment.CenterVertically),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(Dimens.heroBadge)
                            .background(gameInfo.accentColor, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = gameInfo.icon,
                        contentDescription = null,
                        tint = AppTheme.extendedColors.onAccent,
                        modifier = Modifier.size(Dimens.heroBadgeIcon),
                    )
                }
                InstructionHeader(
                    text = comingSoon,
                    onGatorClick = { tts.speakNow(comingSoon) },
                    accent = gameInfo.accentColor,
                )

                if (LocalIsDebugBuild.current && onGameWon != null) {
                    TactileButton(
                        text = stringResource(Res.string.test_trigger_win),
                        onClick = onGameWon,
                        icon = Icons.Rounded.EmojiEvents,
                        colors = TactileDefaults.colors(gameInfo.accentColor),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
