# iOS Text-to-Speech (`iosMain` `tts`)

## Overview
iOS implementation of the shared `TextToSpeech` contract, backed by `AVSpeechSynthesizer`.
`IosTextToSpeech` extends the commonMain `QueuedTextToSpeech` base, which owns the sequential
queue, cancellation, and the stop lock; this package only starts, polls, and stops the
synthesizer. The shared contract and base class live in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/` (see
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/README.md`).

## Interface
- `class IosTextToSpeech : QueuedTextToSpeech()` (`@OptIn(ExperimentalForeignApi::class)`):
  holds one `AVSpeechSynthesizer`. The queue runs on the base default `Dispatchers.Default`.
- `setSpeechRate(rate)`: stores `(rate / 2.0f).coerceIn(0f, 1f)`, mapping the contract range
  (1.0 = normal) onto AVSpeech's `0..1` scale. Default `0.5f`.
- `setPitch(pitch)`: stores `pitch.coerceIn(0.5f, 2.0f)` as the pitch multiplier. Default `1.0f`.
- `performSpeak(text)` (protected): builds an `AVSpeechUtterance` (rate, pitch, volume 1.0),
  calls `speakUtterance` inside `withSpeakStartLock`, then polls `isSpeaking()`: every 10 ms up to
  100 times for speech to start, then every 50 ms until it stops.
- `performStop()` (protected): `stopSpeakingAtBoundary(AVSpeechBoundaryImmediate)`.
- `shutdown()`: only `super.shutdown()`; no synthesizer-specific cleanup.
- `actual fun createTextToSpeech(): TextToSpeech`: returns a new `IosTextToSpeech()`.
- `@Composable actual fun rememberTextToSpeech(): TextToSpeech`: remembers one instance and calls
  `shutdown()` from `DisposableEffect(Unit).onDispose`. No preview/inspection-mode branch.

## Invariants
- Keep only `synthesizer.speakUtterance(...)` inside `withSpeakStartLock { }`; the block must not
  suspend because `stop()` blocks on the same lock.
- `performSpeak` must suspend until speech ends. Completion is detected only by polling
  `isSpeaking()`, which reflects the whole synthesizer, not one utterance. No delegate is used.
- If speech has not started within about 1 s, `performSpeak` returns and the queue moves on.
- Cancellation is cooperative through `delay()`. Do not catch `CancellationException` here; the
  base handler calls `performStop()` when the stop generation is unchanged.
- `performStop()` runs under the base lock and must stay synchronous and non-blocking.
- `currentRate` and `currentPitch` are plain `var`s read when the utterance starts, so a change
  also affects queued utterances that have not started.
- `shutdown()` is terminal: later `speak()` calls cannot enqueue.

## Side Effects
- Creates one `AVSpeechSynthesizer` per instance; never explicitly released.
- Plays audio at volume 1.0 with no voice or language set; `AVAudioSession` is not configured.
- Stops speech immediately on `performStop()`; the Boolean result is ignored.
- Logs the full utterance text at verbose level (`TTS speaking: "<text>"`).
- `shutdown()` stops in-flight speech only indirectly, by cancelling the base scope.

## Verification
- Run from `iosApp/`: `xcodebuild build -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17' -derivedDataPath DerivedData CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO`
