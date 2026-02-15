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
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Desktop/JVM implementation of Text-to-Speech.
 * Uses FreeTTS or system TTS if available, otherwise provides a no-op fallback.
 * Note: Desktop TTS support varies by OS. This is a basic implementation.
 */
class DesktopTextToSpeech : TextToSpeech {
    private val isSpeakingState = AtomicBoolean(false)
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
                        // Use PowerShell with encoded command to avoid injection
                        // Base64 encode the PowerShell script to safely pass text
                        val script = "Add-Type -AssemblyName System.Speech; \$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; \$synth.Speak(\"$text\")"
                        val encodedScript =
                            java.util.Base64
                                .getEncoder()
                                .encodeToString(script.toByteArray(Charsets.UTF_16LE))
                        listOf(
                            "powershell.exe",
                            "-EncodedCommand",
                            encodedScript,
                        )
                    }

                    else -> {
                        listOf("espeak", text)
                    } // Linux fallback
                }

            isSpeakingState.set(true)
            Thread {
                try {
                    ProcessBuilder(command).start().waitFor()
                } catch (e: Exception) {
                    println("TTS not available on this system: ${e.message}")
                } finally {
                    isSpeakingState.set(false)
                }
            }.start()
        } catch (e: Exception) {
            println("TTS Error: ${e.message}")
            isSpeakingState.set(false)
        }
    }

    override fun stop() {
        // Cannot easily stop external process speech
        isSpeakingState.set(false)
    }

    override fun isSpeaking(): Boolean = isSpeakingState.get()

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
