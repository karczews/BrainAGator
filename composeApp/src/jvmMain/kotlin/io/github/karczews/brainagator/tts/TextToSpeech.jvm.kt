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
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Data class representing a voice with its language code.
 */
private data class Voice(
    val name: String,
    val languageCode: String,
)

/**
 * Desktop/JVM implementation of Text-to-Speech.
 * Uses system TTS commands with automatic language/voice matching on macOS.
 * Note: Desktop TTS support varies by OS. This is a basic implementation.
 */
class DesktopTextToSpeech : TextToSpeech {
    private val isSpeakingState = AtomicBoolean(false)
    private var currentRate = 1.0f
    private var currentPitch = 1.0f
    private val osName = System.getProperty("os.name").lowercase()
    private val isMac = osName.contains("mac")
    private val systemLanguage: String = Locale.getDefault().language
    private val cachedVoice: String? by lazy { findBestVoice() }

    /**
     * Finds the best voice for the current system language.
     * On macOS, queries available voices and matches by language code.
     */
    private fun findBestVoice(): String? {
        if (!isMac) return null

        return try {
            val process = ProcessBuilder("say", "-v", "?").start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()

            val voices = parseVoices(output)
            matchVoiceForLanguage(voices, systemLanguage)
        } catch (e: Exception) {
            Logger.e { "Failed to query voices: ${e.message}" }
            null
        }
    }

    /**
     * Parses the output of `say -v '?'` into a list of Voice objects.
     * Format: "VoiceName    lang_CODE    # Description"
     */
    private fun parseVoices(output: String): List<Voice> =
        output.lines().mapNotNull { line ->
            // Split on two or more spaces to handle multi-word voice names
            val parts = line.split(Regex("\\s{2,}")).map { it.trim() }
            if (parts.size >= 2) {
                val name = parts[0]
                val langCode = parts[1]
                Voice(name, langCode.lowercase())
            } else {
                null
            }
        }

    /**
     * Matches the system language to an available voice.
     * Returns null if no matching voice is found.
     */
    private fun matchVoiceForLanguage(
        voices: List<Voice>,
        language: String,
    ): String? {
        // First try exact match (e.g., "pl_PL" for language "pl")
        val exactMatch =
            voices.find { voice ->
                voice.languageCode.startsWith("${language}_") || voice.languageCode == language
            }
        if (exactMatch != null) return exactMatch.name

        // Fallback: try matching just the language part (e.g., "pl" in "pl_PL")
        return voices
            .find { voice ->
                voice.languageCode.split("_")[0] == language
            }?.name
    }

    override fun speak(text: String) {
        try {
            stop()

            val command =
                when {
                    isMac -> {
                        buildMacCommand(text)
                    }

                    osName.contains("win") -> {
                        buildWindowsCommand(text)
                    }

                    else -> {
                        listOf("espeak", text)
                    }
                }

            isSpeakingState.set(true)
            Thread {
                try {
                    ProcessBuilder(command).start().waitFor()
                } catch (e: Exception) {
                    Logger.w { "TTS not available on this system: ${e.message}" }
                } finally {
                    isSpeakingState.set(false)
                }
            }.start()
        } catch (e: Exception) {
            Logger.e { "TTS Error: ${e.message}" }
            isSpeakingState.set(false)
        }
    }

    /**
     * Builds the macOS say command with voice selection.
     */
    private fun buildMacCommand(text: String): List<String> {
        val voice = cachedVoice
        return if (voice != null) {
            listOf("say", "-v", voice, text)
        } else {
            listOf("say", text)
        }
    }

    /**
     * Builds the Windows PowerShell command for TTS.
     * Uses Base64 encoding to prevent command injection.
     */
    private fun buildWindowsCommand(text: String): List<String> {
        // Escape single quotes by replacing each ' with ''
        val safeText = text.replace("'", "''")
        val script =
            "Add-Type -AssemblyName System.Speech; " +
                "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('$safeText')"
        val encodedScript =
            script
                .toByteArray(Charsets.UTF_16LE)
                .let {
                    java.util.Base64
                        .getEncoder()
                        .encodeToString(it)
                }
        return listOf("powershell.exe", "-EncodedCommand", encodedScript)
    }

    override fun stop() {
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
