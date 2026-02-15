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

@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.karczews.brainagator.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.karczews.brainagator.Logger

/**
 * Wasm/JS implementation of Text-to-Speech using the Web Speech API.
 * Uses external interfaces for proper Kotlin/Wasm JS interop.
 */
class WasmTextToSpeech : TextToSpeech {
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun speak(text: String) {
        try {
            stop()

            val synthesis = getSpeechSynthesis()
            val utterance = createSpeechSynthesisUtterance(text)

            utterance.rate = currentRate.toDouble()
            utterance.pitch = currentPitch.toDouble()
            utterance.volume = 1.0

            // Try to use default or first available voice
            val voices = synthesis.getVoices()
            if (voices.length > 0) {
                utterance.voice = getVoiceAt(voices, 0)
            }

            synthesis.speak(utterance)
        } catch (e: Exception) {
            Logger.e { "TTS Error: ${e.message}" }
        }
    }

    override fun stop() {
        try {
            getSpeechSynthesis().cancel()
        } catch (e: Exception) {
            Logger.e { "TTS Stop Error: ${e.message}" }
        }
    }

    override fun isSpeaking(): Boolean =
        try {
            getSpeechSynthesis().speaking
        } catch (e: Exception) {
            Logger.w { "TTS isSpeaking check failed: ${e.message}" }
            false
        }

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.1f, 2.0f)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
    }

    override fun shutdown() {
        stop()
    }
}

/**
 * Creates platform-specific TTS instance for Wasm/JS.
 */
actual fun createTextToSpeech(): TextToSpeech = WasmTextToSpeech()

/**
 * Composable remember function for TTS in Wasm.
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

// External declarations for Web Speech API

external class SpeechSynthesis : JsAny {
    fun speak(utterance: SpeechSynthesisUtterance)

    fun cancel()

    fun getVoices(): JsArray<SpeechSynthesisVoice>

    val speaking: Boolean
}

external class SpeechSynthesisUtterance : JsAny {
    constructor(text: String)

    var text: String
    var rate: Double
    var pitch: Double
    var volume: Double
    var voice: SpeechSynthesisVoice?
}

external class SpeechSynthesisVoice : JsAny

@JsFun("() => { return window.speechSynthesis; }")
external fun getSpeechSynthesis(): SpeechSynthesis

@JsFun("(text) => { return new SpeechSynthesisUtterance(text); }")
external fun createSpeechSynthesisUtterance(text: String): SpeechSynthesisUtterance

@JsFun("(voices, index) => { return voices[index]; }")
external fun getVoiceAt(
    voices: JsArray<SpeechSynthesisVoice>,
    index: Int,
): SpeechSynthesisVoice?
