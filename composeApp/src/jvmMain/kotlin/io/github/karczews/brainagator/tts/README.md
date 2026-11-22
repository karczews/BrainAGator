# Desktop Text-to-Speech (`jvmMain` `tts`)

## Overview
Desktop (JVM) implementation of the shared `TextToSpeech` contract. `DesktopTextToSpeech`
extends the commonMain `QueuedTextToSpeech` base, which owns the sequential queue, cancellation,
and the stop lock; this package speaks each utterance by spawning an OS speech process. The
shared contract and base class live in
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/` (see
`composeApp/src/commonMain/kotlin/io/github/karczews/brainagator/tts/README.md`).

## Interface
- `class DesktopTextToSpeech : QueuedTextToSpeech()`: no-arg constructor; the queue runs on the
  base default `Dispatchers.Default`.
- `setSpeechRate(rate)` / `setPitch(pitch)`: clamp to `0.1..2.0` / `0.5..2.0` and store the value.
  The stored values are not passed to any command, so they do not change output.
- `performSpeak(text)` (protected): on `Dispatchers.IO`, builds the OS command, starts the process
  inside `withSpeakStartLock`, then waits with `runInterruptible { process.waitFor() }`.
- `performStop()` (protected): `currentProcess.getAndSet(null)?.destroy()`.
- `shutdown()`: `super.shutdown()`, then destroys the current process the same way.
- `actual fun createTextToSpeech(): TextToSpeech`: returns a new `DesktopTextToSpeech()`.
- `@Composable actual fun rememberTextToSpeech(): TextToSpeech`: returns a remembered
  `DummyTextToSpeech` in inspection (preview) mode; otherwise remembers one instance and calls
  `shutdown()` from `DisposableEffect(Unit).onDispose`.

## Invariants
- The OS is detected once from `os.name`: contains "mac", then contains "win", else espeak.
- Keep only `ProcessBuilder(command).start()` inside `withSpeakStartLock { }`; the block must not
  suspend because `stop()` blocks on the same lock.
- Keep the blocking `waitFor()` inside `runInterruptible` so cancellation interrupts it.
- The inner `finally` always calls `process.destroy()` and then
  `currentProcess.compareAndSet(process, null)`, so it never clears a newer process.
- `performStop()` and `shutdown()` clear the process reference with an atomic `getAndSet(null)`,
  never read-then-null; the inner `finally` uses `compareAndSet(process, null)` (above).
- `CancellationException` is logged at debug and rethrown; other exceptions are logged and
  rethrown, and the base logs them again.
- On macOS the voice is looked up once (`by lazy`) on the first speak; a `null` result is cached
  and never retried.
- `Dispatchers.IO` is hardcoded in `performSpeak` (not injected).

## Side Effects
- macOS: runs `say -v ?` once per instance to list voices, then `say -v <voice> <text>` (or
  `say <text>` when no voice matches the system language or the voice query failed).
- Windows: runs `powershell.exe -EncodedCommand <base64>` using `System.Speech`
  `SpeechSynthesizer.Speak`, with `'` escaped as `''`.
- Other OSes: runs `espeak <text>`.
- Audio is produced by the spawned process; its stdout/stderr are not read.
- Stops speech with `Process.destroy()` (not `destroyForcibly`).
- Logs the utterance text, command and completion (`Logger.v`/`Logger.d`), cancellations
  (`Logger.d`), and
  failures (`Logger.e`: "TTS not available on this system", "Failed to query voices").

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
- Desktop run: `./gradlew :composeApp:run`
