package io.github.karczews.brainagator.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * Wasm/JS implementation of Text-to-Speech using the Web Speech API.
 * Similar to JS implementation but adapted for Wasm target.
 */
class WasmTextToSpeech : TextToSpeech {
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun speak(text: String) {
        try {
            stop()

            // Use js() to access Web Speech API
            val utterance = js("new SpeechSynthesisUtterance()")
            utterance.text = text
            utterance.rate = currentRate.toDouble()
            utterance.pitch = currentPitch.toDouble()
            utterance.volume = 1.0

            // Try to use default or first available voice
            val synthesis = js("window.speechSynthesis")
            val voices = synthesis.getVoices()
            if (voices != null && voices.length > 0) {
                utterance.voice = voices[0]
            }

            synthesis.speak(utterance)
        } catch (e: Exception) {
            println("TTS Error: ${e.message}")
        }
    }

    override fun stop() {
        try {
            val synthesis = js("window.speechSynthesis")
            synthesis.cancel()
        } catch (e: Exception) {
            println("TTS Stop Error: ${e.message}")
        }
    }

    override fun isSpeaking(): Boolean =
        try {
            val synthesis = js("window.speechSynthesis")
            synthesis.speaking as Boolean
        } catch (e: Exception) {
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
