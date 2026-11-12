# Wasm Text-to-Speech (`wasmJsMain` `tts`)

## Overview
Kotlin/Wasm implementation of the shared `TextToSpeech` contract, built on the browser Web
Speech API through `@JsFun` interop. `WasmTextToSpeech` extends the commonMain
`QueuedTextToSpeech` base, which owns the sequential queue, cancellation, and the stop lock; this
package only creates, starts, and cancels utterances. The shared contract and base class live in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/` (see
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/README.md`).

## Interface
- `class WasmTextToSpeech : QueuedTextToSpeech()`: the queue runs on the base default
  `Dispatchers.Default`.
- `setSpeechRate(rate)`: clamps to `0.1..2.0` and stores it.
- `setPitch(pitch)`: clamps to `0.5..2.0` and stores it.
- `performSpeak(text)` (protected): builds an utterance (rate, pitch, volume 1.0), waits for
  voices, uses voice index 0 if any, wires `onend`/`onerror` to a `CompletableDeferred`, calls
  `synthesis.speak(utterance)` inside `withSpeakStartLock`, then awaits completion.
- `performStop()` (protected): `getSpeechSynthesis().cancel()`, logging any caught `Exception`.
- `shutdown()`: `super.shutdown()`, then `performStop()`.
- `actual fun createTextToSpeech(): TextToSpeech`: returns a new `WasmTextToSpeech()`.
- `@Composable actual fun rememberTextToSpeech(): TextToSpeech`: remembers one instance and calls
  `shutdown()` from `DisposableEffect(Unit).onDispose`.
- Web Speech interop (file opts in to `ExperimentalWasmJsInterop`): `external class
  SpeechSynthesis`, `SpeechSynthesisUtterance`, `SpeechSynthesisVoice` (all `JsAny`), and
  `@JsFun` functions `getSpeechSynthesis()` (`window.speechSynthesis`),
  `createSpeechSynthesisUtterance(text)`, `getVoiceAt(voices, index)`,
  `setUtteranceCallbacks(utterance, onEnd, onError)`, `setOnVoicesChanged(synthesis, callback)`.

## Invariants
- Keep only `synthesis.speak(utterance)` inside `withSpeakStartLock { }`; voice loading and
  callback setup happen before it. The block must not suspend because `stop()` blocks on the
  same lock.
- `performSpeak` must suspend until `onend` or `onerror` fires; there is no timeout. Cancellation
  is handled by the base, which calls `performStop()` when the stop generation is unchanged.
- `onerror` completes with `Exception("TTS Error")`, which the base catches and logs.
- If no voices are loaded, `waitForVoices` waits for `voiceschanged` (no timeout) and resets the
  handler to a no-op in `finally`, including on cancellation.
- Handlers are assigned as `on*` properties, so `setOnVoicesChanged` overwrites any other
  `onvoiceschanged` handler on the page-global `speechSynthesis`.
- Requires a window with `speechSynthesis`; there is no feature detection.
- Rate and pitch are plain `var`s read when the utterance starts.
- `shutdown()` is terminal: later `speak()` calls cannot enqueue.

## Side Effects
- Calls the Web Speech API: `getVoices`, `speak` (audio output), and `cancel()`, which stops all
  speech on the page.
- Assigns `onend`/`onerror` on each new utterance (never cleared). While waiting for voices it
  sets `speechSynthesis.onvoiceschanged`, then leaves a no-op handler installed on that
  page-global object.
- Logs the full utterance text (`Logger.v`), voice count/rate/pitch and voice loading
  (`Logger.d`), and "TTS Stop Error" (`Logger.e`) when `cancel()` throws.

## Verification
- Run: `./gradlew :composeApp:wasmJsBrowserDevelopmentWebpack`
