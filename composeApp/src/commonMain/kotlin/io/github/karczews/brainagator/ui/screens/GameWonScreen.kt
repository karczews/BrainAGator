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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.back_to_main
import brainagator.composeapp.generated.resources.congratulations
import brainagator.composeapp.generated.resources.gator_win_1
import brainagator.composeapp.generated.resources.star_1
import brainagator.composeapp.generated.resources.you_won
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.ui.FireworksAnimation
import io.github.karczews.brainagator.ui.elements.PlayfulBackground
import io.github.karczews.brainagator.ui.elements.TactileButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameWonScreen(
    onBackToMainClick: () -> Unit,
    showFireworks: Boolean = true,
) {
    PlayfulBackground(modifier = Modifier.fillMaxSize(), accent = AppTheme.extendedColors.reward) {
        // Screenshot tests turn the fireworks off: their particles are random.
        if (showFireworks) {
            FireworksAnimation(
                modifier = Modifier.fillMaxSize(),
                particleCount = 120,
                explosionCount = 0,
            )
        }

        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(Dimens.screenPadding),
        ) {
            if (maxWidth > maxHeight) {
                // Landscape: gator beside the message, so short screens keep both large and the button visible.
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLg, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BouncingGator(modifier = Modifier.weight(1f, fill = false))
                    WinMessage(onBackToMainClick = onBackToMainClick, modifier = Modifier.weight(1f))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterVertically),
                ) {
                    // Shrinks on short screens instead of pushing the button off-screen.
                    BouncingGator(modifier = Modifier.weight(1f, fill = false))
                    WinMessage(onBackToMainClick = onBackToMainClick)
                }
            }
        }
    }
}

/** Title, stars and the way home; scrolls only if a screen is too short to show it all. */
@Composable
private fun WinMessage(
    onBackToMainClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
    ) {
        Text(
            text = stringResource(Res.string.congratulations),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            // Shrinks in narrow columns instead of breaking the word.
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = AutoSizeMin.display,
                    maxFontSize = MaterialTheme.typography.displaySmall.fontSize,
                ),
        )
        Text(
            text = stringResource(Res.string.you_won),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        WinStars()
        TactileButton(
            text = stringResource(Res.string.back_to_main),
            onClick = onBackToMainClick,
            icon = Icons.Rounded.Home,
            modifier =
                Modifier
                    .padding(top = Dimens.spaceSm)
                    .widthIn(max = Dimens.buttonMaxWidth)
                    .fillMaxWidth(),
        )
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
        GameWonScreen(onBackToMainClick = {})
    }
}
