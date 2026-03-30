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

/**
 * Creates a TextToSpeech instance that is remembered across recompositions.
 * This is the common entry point - platform-specific implementations
 * provide the actual functionality.
 *
 * The TTS implementation provides internal queuing - multiple speak() calls
 * are processed sequentially. Each call returns a Job that can be
 * used to cancel that specific utterance.
 *
 * Usage:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val tts = rememberTextToSpeech()
 *
 *     // Speak with cancellation support
 *     val job = tts.speak("Hello World")
 *     // ... later, to cancel this specific utterance:
 *     job.cancel()
 *
 *     // Or simply speak without caring about cancellation
 *     Button(onClick = { tts.speak("Hello") }) {
 *         Text("Speak")
 *     }
 * }
 * ```
 */
@Composable
expect fun rememberTextToSpeech(): TextToSpeech
