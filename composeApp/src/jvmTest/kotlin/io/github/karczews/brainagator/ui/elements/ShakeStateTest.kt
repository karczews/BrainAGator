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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ShakeStateTest {
    /** A frame clock on the test scheduler's virtual time: one 16ms frame per `delay`, no real waiting. */
    private fun TestScope.frameClock(): MonotonicFrameClock =
        object : MonotonicFrameClock {
            override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
                delay(FRAME_MILLIS)
                return onFrame(testScheduler.currentTime * NANOS_PER_MILLI)
            }
        }

    @Test
    fun cancelledShakeSettlesBackInPlace() =
        runTest {
            withContext(StandardTestDispatcher(testScheduler) + frameClock()) {
                // given
                val state = ShakeState()
                val shake = launch { state.shake() }
                awaitMidWiggle(state)

                // when
                shake.cancelAndJoin()

                // then
                assertEquals(0f, state.displacement)
                assertFalse(state.isShaking)
            }
        }

    @Test
    fun newerShakeIsNotInterruptedByTheOneItReplaces() =
        runTest {
            withContext(StandardTestDispatcher(testScheduler) + frameClock()) {
                // given
                val state = ShakeState()
                launch { state.shake() }
                awaitMidWiggle(state)

                // when
                val newer = launch { state.shake() }
                newer.join()

                // then
                assertFalse(newer.isCancelled, "the replaced shake's cleanup must not cancel the newer one")
                assertEquals(0f, state.displacement)
                assertFalse(state.isShaking)
            }
        }

    private suspend fun awaitMidWiggle(state: ShakeState) =
        withTimeout(TIMEOUT_MILLIS) {
            while (state.displacement == 0f) delay(FRAME_MILLIS)
        }

    private companion object {
        const val FRAME_MILLIS = 16L
        const val NANOS_PER_MILLI = 1_000_000L
        const val TIMEOUT_MILLIS = 5_000L
    }
}
