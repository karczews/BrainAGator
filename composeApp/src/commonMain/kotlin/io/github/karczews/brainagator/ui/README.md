# UI

## Overview
All shared Compose UI: navigation keys, reusable tactile elements, and the screens
(home picker, games, win screen). The one file at this level is the fireworks overlay
used by the win screen. Navigation wiring (`AppNavigation`) lives one level up in
`App.kt`.

## Interface
- `@Composable FireworksAnimation(modifier: Modifier = Modifier, particleCount: Int = 120, explosionCount: Int = 0, colors: List<Color> = <8 hardcoded colors>, onAnimationComplete: (() -> Unit)? = null)` -
  canvas fireworks overlay; `explosionCount = 0` runs until it leaves composition.
- Subpackages:
  - [Elements](./elements/README.md) - tactile building blocks (`TactileSurface`,
    `TactileButton`, `ShakeState`, `InstructionHeader`, `StarProgress`, ...).
  - [Navigation](./navigation/README.md) - `GameType` and the serializable `Route` hierarchy.
  - [Screens](./screens/README.md) - `GameInfo`, `games`, `GameSelectionScreen`,
    `GameWonScreen`, and the `games/` packages.

## Invariants
- `explosionCount == 0` means infinite explosions and `onAnimationComplete` is never
  called; for `explosionCount > 0` the callback fires once, 2000 ms after the last spawn.
- Fireworks use the unseeded `kotlin.random.Random` default instance, so output is
  non-deterministic; screenshot tests pass `showFireworks = false` to `GameWonScreen`.
- `colors` must be non-empty (`colors.random()` per explosion); the default palette is
  hardcoded, not theme tokens.
- The fireworks `LaunchedEffect` is keyed only on `explosionCount`; changing other
  parameters while running does not restart it.
- Across subpackages, colors and sizes mostly come from `theme` (`MaterialTheme.colorScheme`,
  `AppTheme.extendedColors`, `Dimens`) and user-facing strings from Compose resources. Known
  exceptions: the fireworks, `rainbowColors` and Shape Match tile palettes are hardcoded colors;
  some element sizes are file-private dp literals (`StarSize`, `BubbleBorder`, `BubbleCorner`,
  `AnimatedBox` corners); the home logo spans and build-info label are hardcoded text.

## Side Effects
- `FireworksAnimation` runs a spawn loop (`delay` 300-800 ms between explosions) and a
  1500 ms `Animatable` per explosion, mutating snapshot state and redrawing every frame
  while active.
- Subpackages speak through TTS, run animations, and invoke navigation callbacks (see
  their READMEs).

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
