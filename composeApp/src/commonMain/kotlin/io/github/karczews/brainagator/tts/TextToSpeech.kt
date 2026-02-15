package io.github.karczews.brainagator.tts

/**
 * Text-to-Speech engine interface for reading on-screen text.
 * Platform-specific implementations handle the actual TTS functionality.
 */
interface TextToSpeech {
    /**
     * Speak the given text aloud.
     * @param text The text to speak
     */
    fun speak(text: String)

    /**
     * Stop any ongoing speech immediately.
     */
    fun stop()

    /**
     * Check if currently speaking.
     * @return true if currently speaking, false otherwise
     */
    fun isSpeaking(): Boolean

    /**
     * Set speech rate (speed).
     * @param rate Value between 0.0 (slowest) and 2.0 (fastest), 1.0 is normal
     */
    fun setSpeechRate(rate: Float)

    /**
     * Set speech pitch.
     * @param pitch Value between 0.5 (lowest) and 2.0 (highest), 1.0 is normal
     */
    fun setPitch(pitch: Float)

    /**
     * Release resources when done.
     */
    fun shutdown()
}

/**
 * Factory to create platform-specific TTS instance.
 */
expect fun createTextToSpeech(): TextToSpeech
