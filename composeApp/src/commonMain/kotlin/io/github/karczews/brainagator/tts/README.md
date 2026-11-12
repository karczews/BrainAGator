# Text-to-Speech

## Overview
Common text-to-speech abstraction. Defines the `TextToSpeech` contract, a queueing base
class that serializes utterances for platform engines, `expect` factories implemented
per platform, and a no-op implementation for Compose previews.

Platform `actual`s (outside this package):
`composeApp/src/androidMain/kotlin/io/github/karczews/brainagator/tts/AndroidTextToSpeech.kt`,
`composeApp/src/iosMain/kotlin/io/github/karczews/brainagator/tts/IosTextToSpeech.kt`,
`composeApp/src/jvmMain/kotlin/io/github/karczews/brainagator/tts/DesktopTextToSpeech.kt`,
`composeApp/src/jsMain/kotlin/io/github/karczews/brainagator/tts/JsTextToSpeech.kt`,
`composeApp/src/wasmJsMain/kotlin/io/github/karczews/brainagator/tts/WasmTextToSpeech.kt`.

## Interface
- `interface TextToSpeech`:
  - `speak(text: String): Job` - enqueue after pending utterances; cancelling the `Job`
    stops playback or removes it from the queue.
  - `stop()` - stop current speech and clear the queue.
  - `setSpeechRate(rate: Float)` (documented 0.0..2.0) and `setPitch(pitch: Float)`
    (documented 0.5..2.0); ranges are not enforced here.
  - `shutdown()` - release resources and stop all speech.
- `fun TextToSpeech.speakNow(text: String): Job` - `stop()` then `speak(text)`, so the
  text plays immediately.
- `expect fun createTextToSpeech(): TextToSpeech` - platform factory.
- `@Composable expect fun rememberTextToSpeech(): TextToSpeech` - Compose entry point.
- `abstract class QueuedTextToSpeech(dispatcher: CoroutineDispatcher = Dispatchers.Default) : TextToSpeech` -
  implements `speak` / `stop` / `shutdown`; subclasses provide:
  - `protected abstract suspend fun performSpeak(text: String)` - should return only when
    speech finishes.
  - `protected abstract fun performStop()`.
  - `setSpeechRate` / `setPitch`.
  - Helpers: `protected suspend fun checkSpeakNotSuperseded()` and
    `protected suspend fun <T> withSpeakStartLock(block: () -> T): T`.
- `class DummyTextToSpeech : TextToSpeech` - no-op for previews; `speak` returns an
  already-completed `Job`.

## Invariants
- Utterances run one at a time in FIFO order: a single processor coroutine joins each
  queued job before taking the next. Jobs are launched `CoroutineStart.LAZY`, so a job
  cancelled while queued never calls `performSpeak`.
- `speak()` and `stop()` synchronize on one `atomicfu` lock. `stop()` bumps a stop
  generation, cancels queued and current jobs, calls `performStop()`, and restarts the
  processor.
- Each utterance carries the generation at enqueue time; `checkSpeakNotSuperseded()` and
  `withSpeakStartLock` throw `CancellationException` if `stop()` ran since then.
- Subclasses must wrap the engine's start-speaking call in `withSpeakStartLock`; its block
  is non-suspending because `stop()` blocks on the same lock.
- On cancellation of a running utterance, `performStop()` runs only if no newer `stop()`
  happened; the `CancellationException` is rethrown.
- Non-cancellation exceptions from `performSpeak` are logged and swallowed; the queue
  continues.
- Scope is `SupervisorJob + dispatcher + CoroutineName("TTS-Queue")`; inject the dispatcher
  in tests.
- If the buffered channel rejects a job (full or closed after `shutdown()`), the returned
  `Job` is cancelled.
- `DummyTextToSpeech` implements the interface directly so previews never load
  `QueuedTextToSpeech` / atomicfu.
- No `@Volatile` in common code: `currentJob` and `stopGeneration` are `atomicfu` atomics;
  `queueProcessor` is a plain `var`, set once from `init` and otherwise reassigned only inside
  `stop()` under the lock.

## Side Effects
- Constructing a `QueuedTextToSpeech` launches a long-lived queue-processor coroutine.
- Each `speak()` launches a lazy coroutine and enqueues it.
- Platform speech happens through subclasses' `performSpeak` / `performStop`.
- `Logger.e` on speak errors, `Logger.w` when enqueueing fails.
- `shutdown()` cancels the channel and the scope.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache --tests "*.QueuedTextToSpeechTest"`
