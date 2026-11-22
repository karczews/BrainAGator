# UI Elements

## Overview
Reusable building blocks for the app's tactile, kid-friendly look. Screens are composed
from these elements instead of styling Material components per screen (see
`docs/DESIGN.md`). Colors come from `MaterialTheme.colorScheme` and
`AppTheme.extendedColors`; sizes mostly from `Dimens` (a few file-private constants such as
`StarSize`, `BubbleBorder`, `BubbleCorner`, `BadgeBorder` and `FocusRingWidth` are local dp
literals).

## Interface
- `@Composable TactileSurface(modifier = Modifier, colors: TactileColors = TactileDefaults.surfaceColors(), shape = MaterialTheme.shapes.large, onClick: (() -> Unit)? = null, enabled = true, content: BoxScope.() -> Unit)` -
  rounded block on a darker lip; the face sinks into the lip when pressed.
- `@Immutable data class TactileColors(face, content, lip: Color)`.
- `object TactileDefaults` - `@Composable` presets: `colors(face, content)`,
  `surfaceColors(content)`, `primaryColors()`, `successColors()`, `wrongColors()`.
- `@Composable TactileButton(text: String, onClick: () -> Unit, modifier = Modifier, icon: ImageVector? = null, colors = TactileDefaults.primaryColors())` -
  pill-shaped labelled button.
- `@Composable TactileIconButton(icon: ImageVector, contentDescription: String?, onClick: () -> Unit, modifier = Modifier, colors = TactileDefaults.surfaceColors())` -
  round icon button.
- `@Stable class ShakeState` - `isShaking: Boolean`, `suspend fun shake()`;
  `@Composable rememberShakeState()`; `Modifier.shake(state: ShakeState, distance: Dp = 10.dp)` -
  shared wrong-answer wiggle.
- `@Composable InstructionHeader(text: String, onGatorClick: () -> Unit, modifier = Modifier, accent = colorScheme.primary)` -
  gator avatar plus speech bubble with the current instruction.
- `@Composable GatorAvatarButton(onClick: () -> Unit, modifier = Modifier, accent = colorScheme.primary)` -
  tappable gator with speaker badge; shared element across screens.
- `@Composable SpeechBubble(text: String, modifier = Modifier, tail: BubbleTail = BubbleTail.Start, textStyle = typography.titleMedium, singleLine = false)`
  and `enum class BubbleTail { Start, Bottom }`.
- `@Composable StarProgress(current: Int, total: Int, modifier = Modifier)` - row of
  stars, the first `current` earned.
- `@Composable PlayCard(modifier = Modifier, content: ColumnScope.() -> Unit)` - flat,
  non-interactive card.
- `@Composable PlayfulBackground(modifier = Modifier, accent = colorScheme.primary, content: BoxScope.() -> Unit)` -
  accent-tinted gradient with decorative circles.
- Subpackage: [Basic animated elements](./basic/README.md) - `AnimatedBox` with an
  animated rainbow border.

## Invariants
- `TactileSurface` is clickable only when `onClick != null` (role `Button`, no ripple);
  press depth animates up to `Dimens.lipHeight`, and size modifiers include the lip.
- `enabled` only gates the click; there is no disabled visual. `TactileButton` and
  `TactileIconButton` do not expose `enabled`.
- Keyboard focus shows a 3.dp border in `colors.content` instead of a ripple.
- `TactileDefaults.colors` derives the lip as `face.darken(0.25f)`; success colors come
  from `AppTheme.extendedColors`; wrong colors use `errorContainer` / `onErrorContainer` for
  face and content and `colorScheme.error` for the lip.
- `TactileButton` height is at least `Dimens.touchTarget`; `TactileIconButton` is
  `Dimens.iconButton` wide and `iconButton + lipHeight` tall so the face stays circular.
- `ShakeState` counts concurrent shakes; `isShaking` is true while any is running. A
  cancelled shake snaps back to 0 (under `NonCancellable`) unless a newer shake is still
  active.
- `GatorAvatarButton` uses shared-element key `speaking-gator` and is a no-op transition
  when `LocalSharedTransitionContext` is null (previews, tests); its accessible label is
  `Res.string.repeat_instruction`.
- `StarProgress` exposes progress semantics (`ProgressBarRangeInfo`); `current` is not
  clamped to `total`.
- No hardcoded user-facing strings in production composables; text is caller-supplied or
  from resources.

## Side Effects
- `TactileSurface`: spring animation of press depth from a remembered interaction source;
  invokes the caller's `onClick`.
- `ShakeState.shake()`: suspends ~300 ms running an `Animatable` keyframe wiggle and
  mutates snapshot state; `Modifier.shake` reads it in `graphicsLayer` (no recomposition).
- `StarProgress`: spring scale animation per star.
- `GatorAvatarButton`: loads the gator drawable and runs a `sharedElement` transition
  when a `SharedTransitionContext` is provided.
- `InstructionHeader` only forwards `onGatorClick`; it makes no TTS call itself.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache --tests "*.ShakeStateTest"`
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
