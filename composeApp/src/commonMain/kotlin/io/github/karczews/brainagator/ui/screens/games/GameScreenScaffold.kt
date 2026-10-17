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

import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.go_back
import brainagator.composeapp.generated.resources.repeat_instruction
import io.github.karczews.brainagator.Logger
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.darken
import io.github.karczews.brainagator.tts.TextToSpeech
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.elements.StarProgress
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileIconButton
import io.github.karczews.brainagator.ui.screens.GameInfo
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.stringResource

private const val TITLE_DARKEN_FRACTION = 0.2f

/** How far a game has got: [current] of [total] stars earned. */
@Immutable
data class GameProgress(
    val current: Int,
    val total: Int,
)

/**
 * Common frame for every game: the game's [PlayfulBackground], a top bar with back and
 * "repeat instructions" buttons, and spoken instructions when the screen opens.
 *
 * PROTOTYPE (adventure map): when the game reports [progress], the top bar shows it as stars instead of the
 * game's title, so the board below gets the room a separate progress card used to take.
 */
@Composable
fun GameScreenScaffold(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    progress: GameProgress? = null,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues, TextToSpeech) -> Unit,
) {
    val tts = rememberTextToSpeech()
    val description = stringResource(gameInfo.descriptionRes)

    // Speak the game description when the screen opens and again on every help tap. A counter keys the
    // effect (not the text), so a tap restarts the description even while it is still playing.
    var speakCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(speakCount) {
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
            topBar = {
                GameTopBar(
                    gameInfo = gameInfo,
                    progress = progress,
                    onBackClick = onBackClick,
                    onHelpClick = { speakCount++ },
                )
            },
            bottomBar = bottomBar,
        ) { innerPadding ->
            content(innerPadding, tts)
        }
    }
}

@Composable
private fun GameTopBar(
    gameInfo: GameInfo,
    progress: GameProgress?,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TactileIconButton(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(Res.string.go_back),
            onClick = onBackClick,
            colors = TactileDefaults.surfaceColors(content = gameInfo.accentColor),
        )
        val middleModifier =
            Modifier
                .weight(1f)
                .padding(horizontal = Dimens.spaceSm)
        if (progress != null) {
            TopBarProgress(gameInfo = gameInfo, progress = progress, modifier = middleModifier)
        } else {
            GameTitle(gameInfo = gameInfo, modifier = middleModifier)
        }
        // The help button is the accent-colored one: replaying instructions matters most for non-readers.
        TactileIconButton(
            icon = Icons.Rounded.QuestionMark,
            contentDescription = stringResource(Res.string.repeat_instruction),
            onClick = onHelpClick,
            colors = TactileDefaults.colors(gameInfo.accentColor),
        )
    }
}

/** Progress stars in the middle of the top bar; the (unread) title stays available to screen readers. */
@Composable
private fun TopBarProgress(
    gameInfo: GameInfo,
    progress: GameProgress,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(gameInfo.titleRes)
    Box(
        modifier =
            modifier.semantics {
                heading()
                contentDescription = title
            },
        contentAlignment = Alignment.Center,
    ) {
        StarProgress(current = progress.current, total = progress.total)
    }
}

@Composable
private fun GameTitle(
    gameInfo: GameInfo,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(gameInfo.titleRes),
            color = gameInfo.accentColor.darken(TITLE_DARKEN_FRACTION),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            // Long titles (e.g. in Polish) shrink to fit between the buttons instead of being cut off.
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = AutoSizeMin.title,
                    maxFontSize = MaterialTheme.typography.headlineSmall.fontSize,
                ),
        )
    }
}
