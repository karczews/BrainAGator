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

package io.github.karczews.brainagator.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.back_to_main
import brainagator.composeapp.generated.resources.gator_win_1
import brainagator.composeapp.generated.resources.play_again
import brainagator.composeapp.generated.resources.star_1
import brainagator.composeapp.generated.resources.win_speech
import io.github.karczews.brainagator.Logger
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.FireworksAnimation
import io.github.karczews.brainagator.ui.elements.BubbleTail
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.elements.SpeechBubble
import io.github.karczews.brainagator.ui.elements.TactileColors
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileSurface
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The gator celebrates and says so out loud. Nothing to read: the two ways on are icon-only round buttons,
 * play again (the main one) and home.
 */
@Composable
fun GameWonScreen(
    onPlayAgainClick: () -> Unit,
    onBackToMainClick: () -> Unit,
    showFireworks: Boolean = true,
) {
    val tts = rememberTextToSpeech()
    val winSpeech = stringResource(Res.string.win_speech)
    LaunchedEffect(Unit) {
        val job = tts.speak(winSpeech)
        try {
            job.join()
        } catch (e: CancellationException) {
            Logger.d { "Cancelled win speech" }
            job.cancel()
            throw e
        }
    }

    PlayfulBackground(modifier = Modifier.fillMaxSize(), accent = AppTheme.extendedColors.reward) {
        // Screenshot tests turn the fireworks off: their particles are random.
        if (showFireworks) {
            FireworksAnimation(
                modifier = Modifier.fillMaxSize(),
                particleCount = 120,
                explosionCount = 0,
            )
        }

        WinLayout(winSpeech = winSpeech, onPlayAgainClick = onPlayAgainClick, onBackToMainClick = onBackToMainClick)
    }
}

@Composable
private fun WinLayout(
    winSpeech: String,
    onPlayAgainClick: () -> Unit,
    onBackToMainClick: () -> Unit,
) {
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(Dimens.screenPadding),
    ) {
        if (maxWidth > maxHeight) {
            // Landscape: gator beside the bubble and buttons, so short screens keep the buttons visible.
            // Scrollable as a fallback: on very short phones the column can still exceed the height.
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLg, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BouncingGator(modifier = Modifier.weight(1f, fill = false))
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterVertically),
                ) {
                    WinBubble(text = winSpeech, tail = BubbleTail.Start)
                    WinStars()
                    WinActions(onPlayAgainClick = onPlayAgainClick, onBackToMainClick = onBackToMainClick)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterVertically),
            ) {
                WinBubble(text = winSpeech)
                // Shrinks on short screens instead of pushing the buttons off-screen.
                BouncingGator(modifier = Modifier.weight(1f, fill = false))
                WinStars()
                WinActions(onPlayAgainClick = onPlayAgainClick, onBackToMainClick = onBackToMainClick)
            }
        }
    }
}

@Composable
private fun WinBubble(
    text: String,
    tail: BubbleTail = BubbleTail.Bottom,
) {
    SpeechBubble(
        text = text,
        tail = tail,
        textStyle = MaterialTheme.typography.headlineMedium,
        singleLine = true,
        modifier = Modifier.widthIn(max = Dimens.buttonMaxWidth),
    )
}

@Composable
private fun WinActions(
    onPlayAgainClick: () -> Unit,
    onBackToMainClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WinActionButton(
            icon = Icons.Rounded.Home,
            contentDescription = stringResource(Res.string.back_to_main),
            colors = TactileDefaults.surfaceColors(content = MaterialTheme.colorScheme.primary),
            onClick = onBackToMainClick,
        )
        WinActionButton(
            icon = Icons.Rounded.Replay,
            contentDescription = stringResource(Res.string.play_again),
            colors = TactileDefaults.primaryColors(),
            onClick = onPlayAgainClick,
        )
    }
}

/** A large round tactile button with a single icon; bigger than [io.github.karczews.brainagator.ui.elements.TactileIconButton]. */
@Composable
private fun WinActionButton(
    icon: ImageVector,
    contentDescription: String,
    colors: TactileColors,
    onClick: () -> Unit,
) {
    TactileSurface(
        modifier =
            Modifier
                .size(width = Dimens.winAction, height = Dimens.winAction + Dimens.lipHeight)
                .semantics { this.contentDescription = contentDescription },
        colors = colors,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(Dimens.winActionIcon))
    }
}

@Composable
private fun BouncingGator(modifier: Modifier = Modifier) {
    val scale by rememberInfiniteTransition(label = "trophy_animation").animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "scale",
    )
    Image(
        modifier = modifier.scale(scale),
        painter = painterResource(Res.drawable.gator_win_1),
        contentDescription = null,
    )
}

@Composable
private fun WinStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "star_animation")
    Row {
        listOf(750, 550, 650).forEachIndexed { index, duration ->
            val rotation by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(duration, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "star_rotation_$index",
            )
            Image(
                modifier =
                    Modifier
                        .size(Dimens.rewardStar)
                        .padding(horizontal = Dimens.spaceXs)
                        .rotate(rotation),
                painter = painterResource(Res.drawable.star_1),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun GameWonScreenPreview() {
    AppTheme {
        GameWonScreen(onPlayAgainClick = {}, onBackToMainClick = {})
    }
}
