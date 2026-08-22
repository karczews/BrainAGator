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

package io.github.karczews.brainagator.ui.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

/**
 * An animated shader background that automatically handles time animation.
 * Uses AGSL on Android and Skiko-compatible rendering on other platforms.
 *
 * @param modifier The modifier to apply to this composable
 * @param durationMillis The duration of one complete animation cycle in milliseconds
 */
@Composable
fun AnimatedShaderBackground(
    modifier: Modifier = Modifier,
    durationMillis: Int = 15000,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shader_time")
    val animatedTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f * 2f, // 2 full cycles (4*PI)
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "time",
    )

    ShaderBackground(
        modifier = modifier,
        time = animatedTime,
    )
}
