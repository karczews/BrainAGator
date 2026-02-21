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

/**
 * Text-to-Speech engine interface for reading on-screen text.
 * Platform-specific implementations handle the actual TTS functionality.
 */
interface TextToSpeech {
    /**
     * Speak the given text aloud.
     * @param text The text to speak
     */
    fun speak(text: String)

    /**
     * Stop any ongoing speech immediately.
     */
    fun stop()

    /**
     * Check if currently speaking.
     * @return true if currently speaking, false otherwise
     */
    fun isSpeaking(): Boolean

    /**
     * Set speech rate (speed).
     * @param rate Value between 0.0 (slowest) and 2.0 (fastest), 1.0 is normal
     */
    fun setSpeechRate(rate: Float)

    /**
     * Set speech pitch.
     * @param pitch Value between 0.5 (lowest) and 2.0 (highest), 1.0 is normal
     */
    fun setPitch(pitch: Float)

    /**
     * Release resources when done.
     */
    fun shutdown()
}

/**
 * Dummy TTS implementation for Compose Preview that does nothing.
 * Used when LocalInspectionMode.current is true (in preview).
 */
class DummyTextToSpeech : TextToSpeech {
    override fun speak(text: String) {
        // No-op in preview mode
    }

    override fun stop() {
        // No-op in preview mode
    }

    override fun isSpeaking(): Boolean = false

    override fun setSpeechRate(rate: Float) {
        // No-op in preview mode
    }

    override fun setPitch(pitch: Float) {
        // No-op in preview mode
    }

    override fun shutdown() {
        // No-op in preview mode
    }
}

/**
 * Factory to create platform-specific TTS instance.
 */
expect fun createTextToSpeech(): TextToSpeech
