package io.github.karczews.brainagator.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * Desktop/JVM implementation of Text-to-Speech.
 * Uses FreeTTS or system TTS if available, otherwise provides a no-op fallback.
 * Note: Desktop TTS support varies by OS. This is a basic implementation.
 */
class DesktopTextToSpeech : TextToSpeech {
    private var isSpeakingState = false
    private var currentRate = 1.0f
    private var currentPitch = 1.0f

    override fun speak(text: String) {
        // Desktop implementation - attempts to use system TTS via Runtime.exec
        // This is a basic implementation. For production, consider using:
        // - FreeTTS library
        // - System-specific commands (say on macOS, espeak on Linux)
        try {
            stop()

            val osName = System.getProperty("os.name").lowercase()
            val command =
                when {
                    osName.contains("mac") -> {
                        listOf("say", text)
                    }

                    osName.contains(
                        "win",
                    ) -> {
                        listOf(
                            "powershell.exe",
                            "Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('$text')",
                        )
                    }

                    else -> {
                        listOf("espeak", text)
                    } // Linux fallback
                }

            isSpeakingState = true
            Thread {
                try {
                    ProcessBuilder(command).start().waitFor()
                } catch (e: Exception) {
                    println("TTS not available on this system: ${e.message}")
                } finally {
                    isSpeakingState = false
                }
            }.start()
        } catch (e: Exception) {
            println("TTS Error: ${e.message}")
            isSpeakingState = false
        }
    }

    override fun stop() {
        // Cannot easily stop external process speech
        isSpeakingState = false
    }

    override fun isSpeaking(): Boolean = isSpeakingState

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
 * Creates platform-specific TTS instance for Desktop/JVM.
 */
actual fun createTextToSpeech(): TextToSpeech = DesktopTextToSpeech()

/**
 * Composable remember function for TTS in Desktop.
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
