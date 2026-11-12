# JS Text-to-Speech (`jsMain` `tts`)

## Overview
Kotlin/JS implementation of the shared `TextToSpeech` contract, built on the browser Web Speech
API. `JsTextToSpeech` extends the commonMain `QueuedTextToSpeech` base, which owns the sequential
queue, cancellation, and the stop lock; this package only creates, starts, and cancels
utterances. The shared contract and base class live in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/` (see
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/README.md`). The Wasm
counterpart is in `composeApp/src/wasmJsMain/kotlin/io/github/karczews/brainagator/tts/`.

## Interface
- `class JsTextToSpeech : QueuedTextToSpeech()`: the queue runs on the base default
  `Dispatchers.Default`.
- `setSpeechRate(rate)`: clamps to `0.1..2.0` and stores it.
- `setPitch(pitch)`: clamps to `0.5..2.0` and stores it.
- `performSpeak(text)` (protected): builds an utterance (rate, pitch, volume 1.0, `voices[0]`
  if any voices exist), sets `onend`/`onerror` to complete a `CompletableDeferred`, calls
  `synthesis.speak(utterance)` inside `withSpeakStartLock`, then awaits completion.
- `performStop()` (protected): `getSpeechSynthesis().cancel()`, logging any caught `Exception`.
- `shutdown()`: `super.shutdown()`, then `performStop()`.
- `actual fun createTextToSpeech(): TextToSpeech`: returns a new `JsTextToSpeech()`.
- `@Composable actual fun rememberTextToSpeech(): TextToSpeech`: remembers one instance and calls
  `shutdown()` from `DisposableEffect(Unit).onDispose`.
- Web Speech externals: `external class SpeechSynthesis`, `external class
  SpeechSynthesisUtterance(text)`, `external class SpeechSynthesisVoice`,
  `external fun getSpeechSynthesis()`, `external fun createSpeechSynthesisUtterance(text)`.

## Invariants
- Keep only `synthesis.speak(utterance)` inside `withSpeakStartLock { }`; the block must not
  suspend because `stop()` blocks on the same lock.
- `performSpeak` must suspend until `onend` or `onerror` fires; there is no timeout. Cancellation
  is handled by the base, which calls `performStop()` when the stop generation is unchanged.
- `onerror` completes with `Exception("TTS Error")`, which the base catches and logs.
- `getSpeechSynthesis` and `createSpeechSynthesisUtterance` are plain top-level `external fun`s
  with no `@JsFun`/`js()` body, so they bind to JS globals of those names. Nothing in this
  repository defines those globals (the Wasm target binds the same names with `@JsFun`).
- Voice selection is `voices[0]` of whatever is already loaded; no `voiceschanged` wait.
- Rate and pitch are plain `var`s read when the utterance starts.
- `shutdown()` is terminal: later `speak()` calls cannot enqueue.

## Side Effects
- Calls the Web Speech API: `getVoices`, `speak` (audio output) and `cancel()`, which stops all
  speech on the page.
- Assigns `onend`/`onerror` properties on each new utterance; they are never cleared.
- Logs the full utterance text (`Logger.v`), voice count/rate/pitch (`Logger.d`), and
  "TTS Stop Error" (`Logger.e`) when `cancel()` throws.

## Verification
- Manual smoke check only (no automated verification task exists for the JS target):
  `./gradlew :composeApp:jsBrowserDevelopmentRun`
