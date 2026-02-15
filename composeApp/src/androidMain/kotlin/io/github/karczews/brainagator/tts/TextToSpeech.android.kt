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

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import java.util.UUID

/**
 * Android implementation of Text-to-Speech using Android's native TextToSpeech API.
 */
class AndroidTextToSpeech(
    context: Context,
) : io.github.karczews.brainagator.tts.TextToSpeech {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    init {
        tts =
            TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isInitialized = true
                    tts?.apply {
                        language = Locale.getDefault()
                        setSpeechRate(currentRate)
                        setPitch(currentPitch)
                    }
                    pendingText?.let { speak(it) }
                    pendingText = null
                }
            }
    }

    override fun speak(text: String) {
        if (!isInitialized) {
            pendingText = text
            return
        }

        tts?.let { engine ->
            if (engine.isSpeaking) {
                engine.stop()
            }

            val utteranceId = UUID.randomUUID().toString()
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    override fun stop() {
        tts?.stop()
    }

    override fun isSpeaking(): Boolean = tts?.isSpeaking ?: false

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.1f, 2.0f)
        if (isInitialized) {
            tts?.setSpeechRate(currentRate)
        }
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
        if (isInitialized) {
            tts?.setPitch(currentPitch)
        }
    }

    override fun shutdown() {
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}

/**
 * Creates platform-specific TTS instance for Android.
 * Note: This requires a Context, so it should be called from a @Composable.
 */
actual fun createTextToSpeech(): io.github.karczews.brainagator.tts.TextToSpeech =
    throw IllegalStateException(
        "Use createTextToSpeech(context) instead from Android code, " +
            "or use rememberTextToSpeech() composable",
    )

/**
 * Creates TTS with Context (for use in Android-specific code).
 */
fun createTextToSpeech(context: Context): io.github.karczews.brainagator.tts.TextToSpeech = AndroidTextToSpeech(context)

/**
 * Composable remember function for TTS in Android.
 * Automatically handles lifecycle and cleanup.
 */
@Composable
actual fun rememberTextToSpeech(): io.github.karczews.brainagator.tts.TextToSpeech {
    val context = LocalContext.current
    val tts = androidx.compose.runtime.remember { createTextToSpeech(context) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }

    return tts
}
