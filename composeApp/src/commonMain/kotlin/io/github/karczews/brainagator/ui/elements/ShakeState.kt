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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val SHAKE_STEP_MILLIS = 50
private val ShakeKeyframes = listOf(-1f, 1f, -0.7f, 0.7f, -0.35f, 0f)

/**
 * Drives the shared "wrong answer" wiggle. Call [shake] from a coroutine and apply [Modifier.shake]
 * to the element; use [isShaking] to tint it while the wiggle plays.
 */
@Stable
class ShakeState {
    private val offset = Animatable(0f)
    private var activeShakes by mutableIntStateOf(0)

    /** Current horizontal displacement, from -1 to 1. */
    internal val displacement: Float get() = offset.value

    val isShaking: Boolean get() = activeShakes > 0

    suspend fun shake() {
        activeShakes++
        try {
            offset.snapTo(0f)
            ShakeKeyframes.forEach { target -> offset.animateTo(target, tween(SHAKE_STEP_MILLIS)) }
        } finally {
            activeShakes--
        }
    }
}

@Composable
fun rememberShakeState(): ShakeState = remember { ShakeState() }

/** Moves the element sideways by up to [distance] while [state] is shaking. */
fun Modifier.shake(
    state: ShakeState,
    distance: Dp = 10.dp,
): Modifier = graphicsLayer { translationX = state.displacement * distance.toPx() }
