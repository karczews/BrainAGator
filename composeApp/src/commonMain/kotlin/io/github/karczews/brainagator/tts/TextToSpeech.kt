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
 * Text-to-Speech engine interface for reading on-screen text.
 * Platform-specific implementations handle the actual TTS functionality.
 *
 * This interface provides a queued speaking model where multiple [speak] calls
 * are processed sequentially. Each call returns a [Job] that can be used
 * to cancel that specific utterance.
 */
interface TextToSpeech {
    /**
     * Speak the given text aloud.
     * The request is queued and will be spoken after any pending utterances.
     *
     * @param text The text to speak
     * @return A [Job] that can be used to cancel this specific utterance.
     *         Cancelling will stop the speech if it's currently playing, or
     *         remove it from the queue if not yet started.
     */
    fun speak(text: String): Job

    /**
     * Stop all speech immediately and clear the queue.
     * This cancels all pending and current utterances.
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
     * This should stop all speech and clean up any resources.
     */
    fun shutdown()
}

/**
 * Factory to create platform-specific TTS instance.
 */
expect fun createTextToSpeech(): TextToSpeech
