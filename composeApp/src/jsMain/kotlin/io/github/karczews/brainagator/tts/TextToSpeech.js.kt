package io.github.karczews.brainagator.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * Web/JS implementation of Text-to-Speech using the Web Speech API.
 * Supports modern browsers that implement the SpeechSynthesis interface.
 */
class WebTextToSpeech : TextToSpeech {
    private val synthesis: dynamic = js("window.speechSynthesis")
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun speak(text: String) {
        try {
            stop()

            val utterance = js("new SpeechSynthesisUtterance()")
            utterance.text = text
            utterance.rate = currentRate.toDouble()
            utterance.pitch = currentPitch.toDouble()
            utterance.volume = 1.0

            // Try to use default or first available voice
            val voices = synthesis.getVoices()
            if (voices != null && voices.length > 0) {
                utterance.voice = voices[0]
            }

            synthesis.speak(utterance)
        } catch (e: Exception) {
            console.error("TTS Error: ${e.message}")
        }
    }

    override fun stop() {
        try {
            synthesis.cancel()
        } catch (e: Exception) {
            console.error("TTS Stop Error: ${e.message}")
        }
    }

    override fun isSpeaking(): Boolean =
        try {
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
 * Creates platform-specific TTS instance for Web/JS.
 */
actual fun createTextToSpeech(): TextToSpeech = WebTextToSpeech()

/**
 * Composable remember function for TTS in Web.
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
