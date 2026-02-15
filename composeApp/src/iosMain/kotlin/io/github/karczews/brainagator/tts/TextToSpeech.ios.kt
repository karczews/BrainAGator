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
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

/**
 * iOS implementation of Text-to-Speech using AVSpeechSynthesizer.
 */
@OptIn(ExperimentalForeignApi::class)
class IosTextToSpeech : TextToSpeech {
    private val synthesizer = AVSpeechSynthesizer()
    private var currentRate = 0.5f // iOS uses 0.0 to 1.0 range, default is 0.5
    private var currentPitch = 1.0f

    override fun speak(text: String) {
        Logger.v { "TTS speak called: \"$text\"" }
        if (synthesizer.isSpeaking()) {
            Logger.d { "TTS stopping previous speech" }
            synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundary.AVSpeechBoundaryImmediate)
        }

        val utterance = AVSpeechUtterance(string = text)

        // Set speech parameters - iOS uses Float, not Double
        utterance.setRate(currentRate)
        utterance.setPitchMultiplier(currentPitch)
        utterance.setVolume(1.0f)

        Logger.d { "TTS configured: rate=$currentRate, pitch=$currentPitch" }
        Logger.d { "TTS starting speech synthesis" }
        synthesizer.speakUtterance(utterance)
        Logger.v { "TTS speak completed: \"$text\"" }
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    override fun isSpeaking(): Boolean = synthesizer.isSpeaking()

    override fun setSpeechRate(rate: Float) {
        // Map external 0.1-2.0 range to iOS 0.0-1.0 range
        currentRate = (rate / 2.0f).coerceIn(0.0f, 1.0f)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
    }

    override fun shutdown() {
        stop()
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
