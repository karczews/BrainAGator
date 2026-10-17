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

import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ShakeStateTest {
    /** Advances 16ms of animation time per frame, one frame per millisecond of real time. */
    private val frameClock =
        object : MonotonicFrameClock {
            private var frameTimeNanos = 0L

            override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
                delay(1)
                frameTimeNanos += FRAME_NANOS
                return onFrame(frameTimeNanos)
            }
        }

    @Test
    fun cancelledShakeSettlesBackInPlace() =
        runBlocking {
            withContext(frameClock) {
                val state = ShakeState()
                val shake = launch { state.shake() }
                awaitMidWiggle(state)

                shake.cancelAndJoin()

                assertEquals(0f, state.displacement)
                assertFalse(state.isShaking)
            }
        }

    @Test
    fun newerShakeIsNotInterruptedByTheOneItReplaces() =
        runBlocking {
            withContext(frameClock) {
                val state = ShakeState()
                launch { state.shake() }
                awaitMidWiggle(state)

                val newer = launch { state.shake() }
                newer.join()

                assertFalse(newer.isCancelled, "the replaced shake's cleanup must not cancel the newer one")
                assertEquals(0f, state.displacement)
                assertFalse(state.isShaking)
            }
        }

    private suspend fun awaitMidWiggle(state: ShakeState) =
        withTimeout(TIMEOUT_MILLIS) {
            while (state.displacement == 0f) delay(1)
        }

    private companion object {
        const val FRAME_NANOS = 16_000_000L
        const val TIMEOUT_MILLIS = 5_000L
    }
}
