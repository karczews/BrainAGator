package io.github.karczews.brainagator.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

/**
 * iOS implementation of Text-to-Speech using AVSpeechSynthesizer.
 */
@OptIn(ExperimentalForeignApi::class)
class IosTextToSpeech : TextToSpeech {
    private val synthesizer = AVSpeechSynthesizer()
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun speak(text: String) {
        if (synthesizer.isSpeaking()) {
            synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundary.AVSpeechBoundaryImmediate)
        }

        val utterance = AVSpeechUtterance(string = text)

        // Get default voice for current locale
        val voice = AVSpeechSynthesisVoice.speechVoice()
        if (voice != null) {
            utterance.setVoice(voice)
        }

        // Set speech parameters
        utterance.setRate(currentRate.toDouble())
        utterance.setPitchMultiplier(currentPitch.toDouble())
        utterance.setVolume(1.0)

        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(platform.AVFAudio.AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    override fun isSpeaking(): Boolean = synthesizer.isSpeaking()

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.0f, 1.0f) // iOS uses 0.0 to 1.0 range
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
