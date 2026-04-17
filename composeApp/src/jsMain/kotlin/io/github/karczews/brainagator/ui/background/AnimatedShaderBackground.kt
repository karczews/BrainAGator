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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberInfiniteTransition
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.mod

@Composable
fun AnimatedShaderBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shader_animation")
    val animValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat() * 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shader_time",
    )

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedGradientBackground(animValue)
        content()
    }
}

@Composable
private fun AnimatedGradientBackground(time: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = maxOf(centerX, centerY) * 1.5f

        val hue1 = (time * 20f).mod(360f)
        val hue2 = (hue1 + 60f).mod(360f)
        val hue3 = (hue1 + 120f).mod(360f)
        val hue4 = (hue1 + 180f).mod(360f)

        val brush = Brush.radialGradient(
            colors = listOf(
                Color.hsv(hue1, 0.6f, 0.9f),
                Color.hsv(hue2, 0.5f, 0.8f),
                Color.hsv(hue3, 0.4f, 0.7f),
                Color.hsv(hue4, 0.3f, 0.6f),
            ),
            center = Offset(centerX, centerY),
            radius = radius,
        )
        drawRect(brush = brush)
    }
}