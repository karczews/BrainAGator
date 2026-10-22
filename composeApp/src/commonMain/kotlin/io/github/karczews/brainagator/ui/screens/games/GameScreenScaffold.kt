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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.go_back
import io.github.karczews.brainagator.Logger
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.tts.TextToSpeech
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileIconButton
import io.github.karczews.brainagator.ui.screens.GameInfo
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.stringResource

/**
 * Common frame for every game: the game's [PlayfulBackground], a back button and the game description
 * spoken when the screen opens. There is no title and no help button: each game's speaking gator
 * ([io.github.karczews.brainagator.ui.elements.InstructionHeader]) shows the instruction and says it again when pressed.
 */
@Composable
fun GameScreenScaffold(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues, TextToSpeech) -> Unit,
) {
    val tts = rememberTextToSpeech()
    val description = stringResource(gameInfo.descriptionRes)

    LaunchedEffect(Unit) {
        val job = tts.speak(description)
        try {
            job.join()
        } catch (e: CancellationException) {
            Logger.d { "Cancelled text-to-speech job for game: ${gameInfo.titleRes}" }
            job.cancel()
            throw e
        }
    }

    PlayfulBackground(modifier = Modifier.fillMaxSize(), accent = gameInfo.accentColor) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { GameTopBar(gameInfo = gameInfo, onBackClick = onBackClick) },
        ) { innerPadding ->
            content(innerPadding, tts)
        }
    }
}

@Composable
private fun GameTopBar(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
    ) {
        TactileIconButton(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(Res.string.go_back),
            onClick = onBackClick,
            colors = TactileDefaults.surfaceColors(content = gameInfo.accentColor),
        )
    }
}
