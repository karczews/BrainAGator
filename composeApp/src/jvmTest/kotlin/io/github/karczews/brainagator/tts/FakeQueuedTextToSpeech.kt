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

package io.github.karczews.brainagator.tts

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Test implementation of QueuedTextToSpeech.
 *
 * [performSpeak] suspends until [releaseSpeak] is called (or it is cancelled),
 * allowing tests to control timing precisely.
 */
class FakeQueuedTextToSpeech(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : QueuedTextToSpeech(dispatcher) {
    val spokenTexts = mutableListOf<String>()
    val stopCount get() = _stopCount
    private var _stopCount = 0

    /** Deferred used to block the current [performSpeak] call. */
    private val speakGate = atomic<CompletableDeferred<Unit>?>(null)

    /** Completes the currently suspended [performSpeak], allowing it to return. */
    fun releaseSpeak() {
        speakGate.value?.complete(Unit)
    }

    override suspend fun performSpeak(text: String) {
        val gate = CompletableDeferred<Unit>()
        speakGate.value = gate
        gate.await()
        spokenTexts.add(text)
    }

    override fun performStop() {
        _stopCount++
    }

    override fun setSpeechRate(rate: Float) = Unit

    override fun setPitch(pitch: Float) = Unit
}
