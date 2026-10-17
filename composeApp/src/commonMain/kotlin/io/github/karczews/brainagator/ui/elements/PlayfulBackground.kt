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

package io.github.karczews.brainagator.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.GameTeal

private enum class BubbleTint { Accent, Surface, Reward }

/** A decorative circle; position is a fraction of the screen, radius a fraction of its shorter side. */
private data class Bubble(
    val x: Float,
    val y: Float,
    val radius: Float,
    val tint: BubbleTint,
    val alpha: Float,
)

private val bubbles =
    listOf(
        Bubble(x = 0.04f, y = 0.07f, radius = 0.22f, tint = BubbleTint.Accent, alpha = 0.10f),
        Bubble(x = 0.97f, y = 0.16f, radius = 0.30f, tint = BubbleTint.Surface, alpha = 0.55f),
        Bubble(x = 0.93f, y = 0.62f, radius = 0.17f, tint = BubbleTint.Accent, alpha = 0.08f),
        Bubble(x = 0.06f, y = 0.52f, radius = 0.12f, tint = BubbleTint.Reward, alpha = 0.16f),
        Bubble(x = 0.30f, y = 1.00f, radius = 0.34f, tint = BubbleTint.Accent, alpha = 0.07f),
        Bubble(x = 0.80f, y = 0.92f, radius = 0.10f, tint = BubbleTint.Reward, alpha = 0.14f),
        Bubble(x = 0.24f, y = 0.30f, radius = 0.025f, tint = BubbleTint.Accent, alpha = 0.22f),
        Bubble(x = 0.72f, y = 0.40f, radius = 0.02f, tint = BubbleTint.Reward, alpha = 0.35f),
        Bubble(x = 0.42f, y = 0.86f, radius = 0.018f, tint = BubbleTint.Accent, alpha = 0.25f),
    )

private const val TOP_TINT = 0.18f
private const val BOTTOM_TINT = 0.08f

/**
 * Shared screen background: a soft wash of [accent] over the theme background with a few
 * floating bubbles. Each game passes its own accent so every screen feels related but distinct.
 */
@Composable
fun PlayfulBackground(
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.() -> Unit,
) {
    val base = MaterialTheme.colorScheme.background
    val reward = AppTheme.extendedColors.reward
    val surface = MaterialTheme.colorScheme.surface
    Box(
        modifier =
            modifier.drawWithCache {
                val gradient =
                    Brush.verticalGradient(
                        0f to lerp(base, accent, TOP_TINT),
                        0.6f to base,
                        1f to lerp(base, accent, BOTTOM_TINT),
                    )
                onDrawBehind {
                    drawRect(gradient)
                    bubbles.forEach { bubble ->
                        val tint =
                            when (bubble.tint) {
                                BubbleTint.Accent -> accent
                                BubbleTint.Surface -> surface
                                BubbleTint.Reward -> reward
                            }
                        drawCircle(
                            color = tint.copy(alpha = bubble.alpha),
                            radius = bubble.radius * size.minDimension,
                            center = Offset(bubble.x * size.width, bubble.y * size.height),
                        )
                    }
                }
            },
        content = content,
    )
}

@Preview
@Composable
private fun PlayfulBackgroundPreview() {
    AppTheme {
        PlayfulBackground(modifier = Modifier.size(360.dp, 640.dp), accent = GameTeal) {}
    }
}
