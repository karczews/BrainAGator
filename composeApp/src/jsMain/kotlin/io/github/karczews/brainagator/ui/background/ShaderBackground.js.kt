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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

/**
 * JS implementation of ShaderBackground.
 * Uses Skiko-compatible animated gradient rendering.
 */
@Composable
actual fun ShaderBackground(
    modifier: Modifier,
    time: Float,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .drawBehind {
                    drawAnimatedGradient(time)
                },
    )
}

/**
 * Draws an animated gradient that simulates a shader effect.
 * Creates smooth color transitions that shift over time.
 */
private fun DrawScope.drawAnimatedGradient(time: Float) {
    val width = size.width
    val height = size.height

    // Create animated colors based on time
    val color1 =
        Color(
            red = 0.5f + 0.3f * sin(time * 0.5f),
            green = 0.5f + 0.3f * sin(time * 0.7f + 1f),
            blue = 0.6f + 0.25f * sin(time * 0.4f + 2f),
        )

    val color2 =
        Color(
            red = 0.6f + 0.25f * sin(time * 0.6f + 2f),
            green = 0.4f + 0.3f * sin(time * 0.5f + 1f),
            blue = 0.7f + 0.2f * sin(time * 0.8f),
        )

    val color3 =
        Color(
            red = 0.4f + 0.2f * sin(time * 0.4f + 1f),
            green = 0.6f + 0.25f * sin(time * 0.6f),
            blue = 0.8f + 0.15f * sin(time * 0.5f + 2f),
        )

    // Create a multi-stop gradient
    val brush =
        Brush.linearGradient(
            colors = listOf(color1, color2, color3, color1),
            start = Offset(0f, 0f),
            end = Offset(width * 0.7f, height),
        )

    drawRect(brush = brush)
}
