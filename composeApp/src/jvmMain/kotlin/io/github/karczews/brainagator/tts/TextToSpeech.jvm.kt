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
import androidx.compose.ui.platform.LocalInspectionMode
import io.github.karczews.brainagator.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

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
 */
class DesktopTextToSpeech : QueuedTextToSpeech() {
    private var currentRate = 1.0f
    private var currentPitch = 1.0f
    private val osName = System.getProperty("os.name").lowercase()
    private val isMac = osName.contains("mac")
    private val systemLanguage: String = Locale.getDefault().language
    private val cachedVoice: String? by lazy { findBestVoice() }
    private var currentProcess: Process? = null

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
            Logger.e(e) { "Failed to query voices" }
            null
        }
    }

    /**
     * Parses the output of `say -v '?'` into a list of Voice objects.
     */
    private fun parseVoices(output: String): List<Voice> =
        output.lines().mapNotNull { line ->
            val parts = line.split("""\s{2,}""".toRegex()).map { it.trim() }
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
     */
    private fun matchVoiceForLanguage(
        voices: List<Voice>,
        language: String,
    ): String? {
        val exactMatch =
            voices.find { voice ->
                voice.languageCode.startsWith("${language}_") || voice.languageCode == language
            }
        if (exactMatch != null) return exactMatch.name

        return voices
            .find { voice ->
                voice.languageCode.split("_")[0] == language
            }?.name
    }

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.1f, 2.0f)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
    }

    override suspend fun performSpeak(text: String) {
        Logger.v { "TTS speaking: \"$text\"" }

        withContext(Dispatchers.IO) {
            try {
                val command = buildCommand(text)
                Logger.d { "TTS executing: $command" }

                val process = ProcessBuilder(command).start()
                currentProcess = process

                try {
                    process.waitFor()
                    Logger.v { "TTS completed: \"$text\"" }
                } finally {
                    currentProcess = null
                }
            } catch (e: Exception) {
                Logger.e(e) { "TTS not available on this system" }
                throw e
            }
        }
    }

    /**
     * Builds the system command for speaking text.
     */
    private fun buildCommand(text: String): List<String> =
        when {
            isMac -> {
                val voice = cachedVoice
                if (voice != null) {
                    listOf("say", "-v", voice, text)
                } else {
                    listOf("say", text)
                }
            }

            osName.contains("win") -> {
                buildWindowsCommand(text)
            }

            else -> {
                listOf("espeak", text)
            }
        }

    /**
     * Builds the Windows PowerShell command for TTS.
     */
    private fun buildWindowsCommand(text: String): List<String> {
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

    override fun performStop() {
        currentProcess?.destroy()
        currentProcess = null
    }

    override fun shutdown() {
        super.shutdown()
        currentProcess?.destroy()
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
    if (LocalInspectionMode.current) {
        return remember { DummyTextToSpeech() }
    }

    val tts = remember { createTextToSpeech() }

    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }

    return tts
}
