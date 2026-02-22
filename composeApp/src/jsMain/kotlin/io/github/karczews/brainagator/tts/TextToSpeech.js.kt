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
import kotlinx.coroutines.CompletableDeferred

/**
 * JS implementation of Text-to-Speech using the Web Speech API.
 * Implements queued speaking where multiple requests are processed sequentially.
 */
class JsTextToSpeech : QueuedTextToSpeech() {
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.1f, 2.0f)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
    }

    override suspend fun performSpeak(text: String) {
        Logger.v { "TTS speaking: \"$text\"" }

        val completable = CompletableDeferred<Unit>()

        val synthesis = getSpeechSynthesis()
        val utterance = createSpeechSynthesisUtterance(text)

        utterance.rate = currentRate.toDouble()
        utterance.pitch = currentPitch.toDouble()
        utterance.volume = 1.0

        // Try to use default or first available voice
        val voices = synthesis.getVoices()
        val voiceCount = voices.length
        Logger.d { "TTS voices: $voiceCount, rate: $currentRate, pitch: $currentPitch" }
        if (voiceCount > 0) {
            utterance.voice = voices[0]
        }

        // Set up completion handlers
        utterance.onend = {
            completable.complete(Unit)
        }
        utterance.onerror = {
            completable.completeExceptionally(Exception("TTS Error"))
        }

        synthesis.speak(utterance)

        completable.await()
    }

    override fun performStop() {
        try {
            getSpeechSynthesis().cancel()
        } catch (e: Exception) {
            Logger.e(e) { "TTS Stop Error" }
        }
    }

    override fun shutdown() {
        super.shutdown()
        performStop()
    }
}

/**
 * Creates platform-specific TTS instance for JS.
 */
actual fun createTextToSpeech(): TextToSpeech = JsTextToSpeech()

/**
 * Composable remember function for TTS in JS.
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

// External declarations for Web Speech API - using native JS interop

external class SpeechSynthesis {
    fun speak(utterance: SpeechSynthesisUtterance)

    fun cancel()

    fun getVoices(): JsArray<SpeechSynthesisVoice>

    val speaking: Boolean
}

external class SpeechSynthesisUtterance {
    constructor(text: String)

    var text: String
    var rate: Double
    var pitch: Double
    var volume: Double
    var voice: SpeechSynthesisVoice?
    var onend: (() -> Unit)?
    var onerror: (() -> Unit)?
}

external class SpeechSynthesisVoice

external fun getSpeechSynthesis(): SpeechSynthesis

external fun createSpeechSynthesisUtterance(text: String): SpeechSynthesisUtterance
