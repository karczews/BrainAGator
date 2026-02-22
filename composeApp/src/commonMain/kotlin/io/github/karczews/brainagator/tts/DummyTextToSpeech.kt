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

import kotlinx.coroutines.delay

/**
 * Dummy TTS implementation for Compose Preview that does nothing.
 * Used when LocalInspectionMode.current is true (in preview).
 */
class DummyTextToSpeech : QueuedTextToSpeech() {
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.1f, 2.0f)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
    }

    override suspend fun performSpeak(text: String) {
        // Simulate speech duration based on text length
        val duration = (text.length * 50L / currentRate).toLong()
        delay(duration.coerceIn(100, 2000))
    }

    override fun performStop() {
        // No-op for dummy
    }
}
