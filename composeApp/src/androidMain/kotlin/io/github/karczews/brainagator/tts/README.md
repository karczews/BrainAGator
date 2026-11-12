# Android Text-to-Speech (`androidMain` `tts`)

## Overview
Android implementation of the shared `TextToSpeech` contract, backed by the platform
`android.speech.tts.TextToSpeech` engine. `AndroidTextToSpeech` extends the commonMain
`QueuedTextToSpeech` base, which owns the sequential queue, cancellation, and the stop lock; this
package only starts and stops the engine. The shared contract and base class live in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/` (see
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/README.md`).

## Interface
- `class AndroidTextToSpeech(context: Context) : QueuedTextToSpeech()`: builds the engine from
  `context.applicationContext`. The queue runs on the base default `Dispatchers.Default`.
- `setSpeechRate(rate)`: clamps to `0.1..2.0`, stores it, and applies it to the engine only once
  initialized.
- `setPitch(pitch)`: clamps to `0.5..2.0`, stores it, and applies it to the engine only once
  initialized.
- `performSpeak(text)` (protected): awaits engine init if needed, sets a new
  `UtteranceProgressListener`, calls `speak(text, QUEUE_FLUSH, null, <random UUID>)` inside
  `withSpeakStartLock`, then suspends until `onDone` or `onError`.
- `performStop()` (protected): `tts?.stop()`.
- `shutdown()`: `super.shutdown()`, then `tts?.shutdown()`, then nulls the engine and clears
  `isInitialized`.
- `actual fun createTextToSpeech(): TextToSpeech`: always throws `IllegalStateException`. Android
  needs a `Context`; use the overload or `rememberTextToSpeech()`.
- `fun createTextToSpeech(context: Context): TextToSpeech`: Android-only overload returning
  `AndroidTextToSpeech(context)`.
- `@Composable actual fun rememberTextToSpeech(): TextToSpeech`: returns a remembered
  `DummyTextToSpeech` in inspection (preview) mode; otherwise remembers
  `createTextToSpeech(LocalContext.current)` and calls `shutdown()` from
  `DisposableEffect(Unit).onDispose`.

## Invariants
- Keep only the non-suspending engine `speak(...)` call inside `withSpeakStartLock { }`. The base
  holds the stop lock for the whole block and `stop()` blocks on it.
- `performSpeak` must suspend until the utterance finishes. The base queue relies on this to keep
  speech sequential. Completion comes only from `onDone`, `onError`, or coroutine cancellation;
  there is no timeout and `onStop` is not overridden.
- Engine init is asynchronous. Rate and pitch set before init are buffered and applied on init
  success together with `Locale.getDefault()`. The language is set only at init.
- If init fails, `initDeferred` stays failed, so every later `performSpeak` rethrows
  "TTS initialization failed" (logged by the base). There is no retry.
- `QUEUE_FLUSH` is used for every utterance; ordering comes from the base queue, not the engine.
- The listener ignores `utteranceId` and is replaced on each `performSpeak`, not cleared after
  `await()`.
- `tts`, `isInitialized`, `currentRate`, and `currentPitch` are plain `var`s (no atomics).
- `shutdown()` is terminal: later `speak()` calls cannot enqueue. Callers of
  `createTextToSpeech(context)` must call `shutdown()` themselves.

## Side Effects
- Creates and binds an Android TTS engine at construction and releases it in `shutdown()`.
- Plays audio through the engine; `performStop()` stops it.
- Sets engine language, rate, and pitch on init success, and rate/pitch on later setter calls.
- Registers one `UtteranceProgressListener` per utterance.
- No logging in this package; the base logs speak errors (`Logger.e`) and closed-queue warnings
  (`Logger.w`).

## Verification
- Run: `./gradlew :composeApp:compileAndroidMain` (compile check; this source set has no unit tests)
