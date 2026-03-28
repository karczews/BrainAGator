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

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QueuedTextToSpeechTest {
    // ------------------------------------------------------------------
    // speak() — basic enqueue and execution
    // ------------------------------------------------------------------

    @Test
    fun speakExecutesPerformSpeak() =
        runTest {
            // given
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            // when
            val job = tts.speak("hello")
            delay(50)
            tts.releaseSpeak()
            job.join()

            // then
            assertEquals(listOf("hello"), tts.spokenTexts)
            tts.shutdown()
        }

    @Test
    fun speakReturnsActiveJobBeforeCompletion() =
        runTest {
            // given
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            // when
            val job = tts.speak("hello")
            delay(50)

            // then — job is still running (blocked in performSpeak)
            assertFalse(job.isCompleted)

            tts.releaseSpeak()
            job.join()
            tts.shutdown()
        }

    @Test
    fun speakReturnsCompletedJobAfterSpeaking() =
        runTest {
            // given
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            // when
            val job = tts.speak("hello")
            delay(50)
            tts.releaseSpeak()
            job.join()

            // then
            assertTrue(job.isCompleted)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // speak() — sequential ordering
    // ------------------------------------------------------------------

    @Test
    fun multipleSpeaksAreExecutedSequentially() =
        runTest {
            // given
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            // when
            val job1 = tts.speak("first")
            val job2 = tts.speak("second")
            val job3 = tts.speak("third")

            delay(50)
            tts.releaseSpeak() // releases "first"
            job1.join()

            delay(50)
            tts.releaseSpeak() // releases "second"
            job2.join()

            delay(50)
            tts.releaseSpeak() // releases "third"
            job3.join()

            // then
            assertEquals(listOf("first", "second", "third"), tts.spokenTexts)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // stop() — cancels submitted (queued) speak before it starts
    // ------------------------------------------------------------------

    @Test
    fun stopCancelsPendingQueuedJob() =
        runTest {
            // given — first speak blocks the queue processor; second is queued but not yet started
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            val job1 = tts.speak("first")
            delay(50)
            val job2 = tts.speak("second")

            // when
            tts.stop()
            tts.releaseSpeak()
            delay(50)

            // then — job2 was cancelled before it ever ran
            assertTrue(job2.isCancelled, "Queued job should be cancelled by stop()")
            assertFalse(tts.spokenTexts.contains("second"), "second should not have been spoken")
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // stop() — cancels the currently executing speak
    // ------------------------------------------------------------------

    @Test
    fun stopCancelsCurrentlyExecutingJob() =
        runTest {
            // given — queue processor is inside performSpeak, blocking on the gate
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            val job = tts.speak("hello")
            delay(50)

            // when
            tts.stop()
            delay(50)

            // then
            assertTrue(job.isCancelled, "Currently running job should be cancelled by stop()")
            assertTrue(tts.stopCount >= 1, "performStop should have been called")
            tts.shutdown()
        }

    @Test
    fun stopCallsPerformStop() =
        runTest {
            // given
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            tts.speak("hello")
            delay(50)

            // when
            tts.stop()
            delay(50)

            // then
            assertTrue(tts.stopCount >= 1)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // stop() — cancelling individual job returned by speak()
    // ------------------------------------------------------------------

    @Test
    fun cancellingReturnedJobStopsCurrentSpeech() =
        runTest {
            // given — inside performSpeak now
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            val job = tts.speak("hello")
            delay(50)

            // when
            job.cancel()
            job.join() // wait for cancellation to propagate
            delay(50)

            // then — performStop is called on CancellationException inside QueuedTextToSpeech
            assertTrue(job.isCancelled)
            assertTrue(tts.stopCount >= 1, "performStop should be called when job is cancelled")
            tts.shutdown()
        }

    @Test
    fun cancellingQueuedJobPreventsItFromSpeaking() =
        runTest {
            // given — block the queue with first speak, enqueue second
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            val job1 = tts.speak("first")
            delay(50)
            val job2 = tts.speak("second")

            // when — cancel the queued job immediately, then release the first
            job2.cancel()
            tts.releaseSpeak()
            job1.join()
            delay(50)

            // then
            assertFalse(tts.spokenTexts.contains("second"), "Cancelled queued job must not be spoken")
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // Job interruption — repeated start/cancel cycles
    // ------------------------------------------------------------------

    @Test
    fun repeatedSpeakCancelCycles() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            delay(50)
            job1.cancel()
            job1.join()
            delay(50)

            val job2 = tts.speak("B")
            delay(50)
            job2.cancel()
            job2.join()
            delay(50)

            val job3 = tts.speak("C")
            delay(50)
            tts.releaseSpeak()
            job3.join()

            assertEquals(listOf("C"), tts.spokenTexts)
            assertTrue(job1.isCancelled)
            assertTrue(job2.isCancelled)
            assertTrue(job3.isCompleted)
            assertEquals(2, tts.stopCount)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // Job interruption — selective cancellation of queued jobs
    // ------------------------------------------------------------------

    @Test
    fun cancelMiddleQueuedJobSkipsIt() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            delay(50)
            val job2 = tts.speak("B")
            val job3 = tts.speak("C")

            job2.cancel()

            tts.releaseSpeak()
            job1.join()
            delay(50)
            tts.releaseSpeak()
            job3.join()

            assertEquals(listOf("A", "C"), tts.spokenTexts)
            assertTrue(job2.isCancelled)
            tts.shutdown()
        }

    @Test
    fun cancelFirstAndLastOfThreeQueuedJobs() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            delay(50)
            val job2 = tts.speak("B")
            val job3 = tts.speak("C")

            job1.cancel()
            job3.cancel()

            delay(50)
            tts.releaseSpeak()
            job2.join()

            assertEquals(listOf("B"), tts.spokenTexts)
            assertTrue(job1.isCancelled)
            assertTrue(job3.isCancelled)
            assertFalse(job2.isCancelled)
            tts.shutdown()
        }

    @Test
    fun cancellingCurrentJobAllowsRemainingQueuedJobsToExecute() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            delay(50)
            val job2 = tts.speak("B")
            val job3 = tts.speak("C")

            job1.cancel()
            delay(50)
            tts.releaseSpeak()
            job2.join()
            delay(50)
            tts.releaseSpeak()
            job3.join()

            assertEquals(listOf("B", "C"), tts.spokenTexts)
            assertTrue(job1.isCancelled)
            assertFalse(job2.isCancelled)
            assertFalse(job3.isCancelled)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // stop() — clears all pending and current jobs
    // ------------------------------------------------------------------

    @Test
    fun stopCancelsAllPendingAndCurrentJobs() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            delay(50)
            val job2 = tts.speak("B")
            val job3 = tts.speak("C")
            val job4 = tts.speak("D")

            tts.stop()
            delay(50)

            assertTrue(job1.isCancelled)
            assertTrue(job2.isCancelled)
            assertTrue(job3.isCancelled)
            assertTrue(job4.isCancelled)
            assertEquals(emptyList<String>(), tts.spokenTexts)
            tts.shutdown()
        }

    @Test
    fun speakAfterStopResumesQueueProcessing() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            delay(50)
            tts.stop()
            delay(50)

            assertTrue(job1.isCancelled)

            val job2 = tts.speak("B")
            delay(50)
            tts.releaseSpeak()
            job2.join()

            assertEquals(listOf("B"), tts.spokenTexts)
            assertFalse(job2.isCancelled)
            tts.shutdown()
        }

    @Test
    fun stopWhenIdleIsHarmless() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            delay(50)

            tts.stop()

            assertEquals(1, tts.stopCount)
            assertEquals(emptyList<String>(), tts.spokenTexts)

            val job = tts.speak("after idle stop")
            delay(50)
            tts.releaseSpeak()
            job.join()

            assertEquals(listOf("after idle stop"), tts.spokenTexts)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // Job interruption — rapid interleaving
    // ------------------------------------------------------------------

    @Test
    fun rapidSpeakAndCancelOnlyLastCompletes() =
        runTest {
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))

            val job1 = tts.speak("A")
            val job2 = tts.speak("B")
            val job3 = tts.speak("C")

            job1.cancel()
            job2.cancel()
            delay(50)
            tts.releaseSpeak()
            job3.join()

            assertEquals(listOf("C"), tts.spokenTexts)
            assertTrue(job1.isCancelled)
            assertTrue(job2.isCancelled)
            assertTrue(job3.isCompleted)
            tts.shutdown()
        }

    // ------------------------------------------------------------------
    // shutdown()
    // ------------------------------------------------------------------

    @Test
    fun shutdownPreventsNewSpeakFromExecuting() =
        runTest {
            // given
            val tts = FakeQueuedTextToSpeech(StandardTestDispatcher(testScheduler))
            tts.shutdown()
            delay(50)

            // when — queue channel is closed; speak() should handle gracefully
            val job = tts.speak("after shutdown")
            delay(50)

            // then
            assertTrue(job.isCancelled || job.isCompleted)
            assertTrue(tts.spokenTexts.isEmpty())
        }
}
