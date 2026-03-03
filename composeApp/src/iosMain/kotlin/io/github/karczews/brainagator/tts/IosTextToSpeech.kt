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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.karczews.brainagator.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

/**
 * iOS implementation of Text-to-Speech using AVSpeechSynthesizer.
 * Implements queued speaking where multiple requests are processed sequentially.
 */
@OptIn(ExperimentalForeignApi::class)
class IosTextToSpeech : QueuedTextToSpeech() {
    private val synthesizer = AVSpeechSynthesizer()
    private var currentRate = 0.5f
    private var currentPitch = 1.0f

    override fun setSpeechRate(rate: Float) {
        currentRate = (rate / 2.0f).coerceIn(0.0f, 1.0f)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
    }

    override suspend fun performSpeak(text: String) {
        Logger.v { "TTS speaking: \"$text\"" }

        val utterance = AVSpeechUtterance(string = text)
        utterance.setRate(currentRate)
        utterance.setPitchMultiplier(currentPitch)
        utterance.setVolume(1.0f)

        synthesizer.speakUtterance(utterance)

        // Wait for the synthesizer to actually start speaking
        // isSpeaking() returns false immediately after speakUtterance() due to async nature
        // We poll with a small delay to detect when speech actually begins
        var attempts = 0
        while (!synthesizer.isSpeaking() && attempts < 100) {
            delay(10)
            attempts++
        }

        // Now wait for speech to complete
        while (synthesizer.isSpeaking()) {
            delay(50)
        }
    }

    override fun performStop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    override fun shutdown() {
        super.shutdown()
        // AVSpeechSynthesizer doesn't need explicit cleanup
    }
}

/**
 * Creates platform-specific TTS instance for iOS.
 */
actual fun createTextToSpeech(): TextToSpeech = IosTextToSpeech()

/**
 * Composable remember function for TTS in iOS.
 * Automatically handles lifecycle and cleanup.
 */
@Composable
actual fun rememberTextToSpeech(): TextToSpeech {
    val tts = remember { createTextToSpeech() }

    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }

    return tts
}
