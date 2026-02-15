package io.github.karczews.brainagator.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Creates a TextToSpeech instance that is remembered across recompositions.
 * This is the common entry point - platform-specific implementations
 * provide the actual functionality.
 *
 * Usage:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val tts = rememberTextToSpeech()
 *
 *     Button(onClick = { tts.speak("Hello World") }) {
 *         Text("Speak")
 *     }
 * }
 * ```
 */
@Composable
expect fun rememberTextToSpeech(): TextToSpeech

/**
 * Utility composable that automatically speaks text when the content changes.
 * Useful for reading dynamic content like game titles or descriptions.
 *
 * @param text The text to speak when it changes
 * @param speakOnChange If true, automatically speaks when text changes (default: true)
 * @param stopOnDispose If true, stops speech when composable is disposed (default: true)
 */
@Composable
fun SpeakOnContentChange(
    text: String,
    speakOnChange: Boolean = true,
    stopOnDispose: Boolean = true,
) {
    val tts = rememberTextToSpeech()

    if (speakOnChange && text.isNotBlank()) {
        LaunchedEffect(text) {
            tts.speak(text)
        }
    }

    if (stopOnDispose) {
        androidx.compose.runtime.DisposableEffect(Unit) {
            onDispose {
                tts.stop()
            }
        }
    }
}

/**
 * Data class to hold speakable content.
 * Useful for screens with multiple speakable elements.
 */
data class SpeakableContent(
    val title: String = "",
    val description: String = "",
    val instructions: String = "",
) {
    fun toSpeechText(): String =
        buildString {
            if (title.isNotBlank()) append("$title. ")
            if (description.isNotBlank()) append("$description. ")
            if (instructions.isNotBlank()) append(instructions)
        }.trim()
}

/**
 * Speaks all content from a SpeakableContent object.
 *
 * @param content The content to speak
 * @param speakOnChange If true, automatically speaks when content changes (default: true)
 */
@Composable
fun SpeakContent(
    content: SpeakableContent,
    speakOnChange: Boolean = true,
) {
    SpeakOnContentChange(
        text = content.toSpeechText(),
        speakOnChange = speakOnChange,
    )
}
