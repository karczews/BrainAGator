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
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.CompletableDeferred
import java.util.Locale
import java.util.UUID
import android.speech.tts.TextToSpeech as AndroidTextToSpeechAPI

/**
 * Android implementation of Text-to-Speech using Android's native TextToSpeech API.
 * Implements queued speaking where multiple requests are processed sequentially.
 */
class AndroidTextToSpeech(
    context: Context,
) : QueuedTextToSpeech() {
    private var tts: AndroidTextToSpeechAPI? = null
    private var isInitialized = false
    private val initDeferred = CompletableDeferred<Unit>()
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    init {
        tts =
            AndroidTextToSpeechAPI(context.applicationContext) { status ->
                if (status == AndroidTextToSpeechAPI.SUCCESS) {
                    isInitialized = true
                    tts?.apply {
                        language = Locale.getDefault()
                        setSpeechRate(currentRate)
                        setPitch(currentPitch)
                    }
                    initDeferred.complete(Unit)
                } else {
                    initDeferred.completeExceptionally(Exception("TTS initialization failed"))
                }
            }
    }

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

    override suspend fun performSpeak(text: String) {
        // Wait for initialization if not yet initialized
        if (!isInitialized) {
            initDeferred.await()
        }

        val completable = CompletableDeferred<Unit>()

        tts?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Speech started
                }

                override fun onDone(utteranceId: String?) {
                    completable.complete(Unit)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    completable.completeExceptionally(
                        Exception("TTS Error for utterance: $utteranceId"),
                    )
                }
            },
        )

        val utteranceId = UUID.randomUUID().toString()
        tts?.speak(text, AndroidTextToSpeechAPI.QUEUE_FLUSH, null, utteranceId)
        completable.await()
    }

    override fun performStop() {
        tts?.stop()
    }

    override fun shutdown() {
        super.shutdown()
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
    // Return dummy implementation for Compose Preview
    if (LocalInspectionMode.current) {
        return androidx.compose.runtime.remember<io.github.karczews.brainagator.tts.TextToSpeech> { DummyTextToSpeech() }
    }

    val context = LocalContext.current
    val tts = androidx.compose.runtime.remember<io.github.karczews.brainagator.tts.TextToSpeech> { createTextToSpeech(context) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }

    return tts
}
