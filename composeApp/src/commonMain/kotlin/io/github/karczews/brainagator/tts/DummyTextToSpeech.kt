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

import kotlinx.coroutines.Job

/**
 * Dummy TTS implementation for Compose Preview that does nothing.
 * Used when LocalInspectionMode.current is true (in preview).
 * Implements TextToSpeech directly to avoid any dependency on
 * QueuedTextToSpeech (and its atomicfu usage) in the preview classloader.
 */
class DummyTextToSpeech : TextToSpeech {
    override fun speak(text: String): Job = Job().also { it.complete() }

    override fun stop() {
        // No-op for dummy
    }

    override fun setSpeechRate(rate: Float) {
        // No-op for dummy
    }

    override fun setPitch(pitch: Float) {
        // No-op for dummy
    }

    override fun shutdown() {
        // No-op for dummy
    }
}
