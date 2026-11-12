# Number Order Game

## Overview
Playable "tap the numbers from smallest to largest" game. Five distinct numbers are
dealt as tactile tiles; tapping the next number in ascending order marks it matched
and moves it to the front, tapping any other tile shakes it. Spoken feedback and the
instruction prompt go through the scaffold-provided TTS.

## Interface
- `NumberOrderGameInfo: GameInfo` - catalog metadata: `Res.string.game_number_order` /
  `subtitle_number_order` / `desc_number_order`, icon `Icons.Rounded.Tag`, accent
  `GameOcean`, `GameType.NumberOrder` (playable; `isPlayable` left at its default).
- `@Composable NumberOrderGameScreen(gameInfo: GameInfo, onBackClick: () -> Unit, onGameWon: () -> Unit, random: Random = Random.Default)` -
  game screen built on `GameScreenScaffold`.
- `data class GameNumber(number: Int, matched: Boolean)` - tile model.

## Invariants
- Numbers are `(1..9).shuffled(random).take(5)`, so they are unique; grid item keys rely
  on that uniqueness.
- The deal is `remember`ed without a key: a new `random` argument does not re-deal.
  All randomness (deal and feedback phrase choice) uses the injected `random`, so a seeded
  `Random` is deterministic (the screenshot test uses `Random(1)`).
- A tap is correct only when `number == gameNumbersInOrder.firstOrNull()`; the correct
  number is then removed from the ordered list and the tile is re-inserted as
  `matched = true` after the last matched tile.
- `onGameWon()` is called when the ordered list becomes empty.
- Matched tiles are disabled; a tile shakes (with `TactileDefaults.wrongColors()`) when it
  is not the next expected number.
- Grid: 3 tiles per row (`GridCells.Fixed(6)`, each tile spans 2), a short last row is
  centered with a spacer; tile size is clamped between `Dimens.touchTarget` and
  `Dimens.numberTileMaxSize`.
- All text comes from resources: `number_order_instruction`,
  `number_order_feedback_positive` / `_negative` string arrays.
- `feedbackEventId` increments on every tap so repeated identical feedback is still spoken.

## Side Effects
- Mutates snapshot state lists (`gameNumbersInOrder`, `gameNumbers`) and feedback state
  on every tap.
- `LaunchedEffect(feedbackEventId)` speaks the feedback via `tts.speak`, joins the job and
  cancels it in `finally` when the effect restarts or leaves composition.
- Gator avatar click calls `tts.speakNow(instructionText)` (interrupts queued speech).
- Wrong taps launch `shakeState.shake()` in a `rememberCoroutineScope`.
- `Logger.d` logs each tap and the reordered list.
- Calls `onGameWon()` on completion; `onBackClick` is forwarded to `GameScreenScaffold`.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache --tests "*.DesktopScreenshotTest"`
  (covers the `numberOrder` golden)
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
