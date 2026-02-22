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

import io.github.karczews.brainagator.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Abstract base class for TextToSpeech implementations that provides
 * internal queuing of speak requests using a simple job queue pattern.
 *
 * Subclasses must implement [performSpeak] to actually speak text,
 * and [performStop] to stop current speech.
 */
abstract class QueuedTextToSpeech : TextToSpeech {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("TTS-Queue"))
    private val queue = Channel<Job>(Channel.UNLIMITED)
    private val isSpeakingFlag = AtomicBoolean(false)
    private var queueProcessor: Job? = null

    init {
        startQueueProcessor()
    }

    private fun startQueueProcessor() {
        queueProcessor?.cancel()
        queueProcessor =
            scope.launch {
                for (job in queue) {
                    isSpeakingFlag.set(true)
                    job.join()
                    isSpeakingFlag.set(false)
                }
            }
    }

    override fun speak(text: String): Job {
        if (queue.isClosedForSend) {
            Logger.w { "TTS queue closed, restarting..." }
            startQueueProcessor()
        }

        val job =
            scope.launch(start = CoroutineStart.LAZY) {
                try {
                    performSpeak(text)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e(e) { "TTS Error speaking: $text" }
                }
            }

        val result = queue.trySend(job)
        if (result.isFailure) {
            Logger.w { "TTS queue closed, cannot enqueue: $text" }
            job.cancel()
        }
        return job
    }

    override fun stop() {
        performStop()
        isSpeakingFlag.set(false)
    }

    override fun shutdown() {
        queue.cancel()
        scope.cancel()
    }

    /**
     * Platform-specific implementation to actually speak text.
     * This is called sequentially for each queued utterance.
     * Should block until speaking is complete.
     *
     * @param text The text to speak
     */
    protected abstract suspend fun performSpeak(text: String)

    /**
     * Platform-specific implementation to stop current speech.
     * This should interrupt any ongoing speech immediately.
     */
    protected abstract fun performStop()
}
