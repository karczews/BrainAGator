# Basic Animated Elements

## Overview
Low-level animated container: it wraps arbitrary content in a rounded box whose border is
an animated rainbow gradient. No production code uses it yet; only its own preview calls it,
and no screenshot test covers it.

## Interface
- `@Composable AnimatedBox(modifier: Modifier = Modifier, contentAlignment: Alignment = Alignment.TopStart, content: @Composable BoxScope.() -> Unit)` -
  `Box` with a 2.dp animated rainbow radial-gradient border around a white,
  12.dp-rounded content area.
- `@Composable AnimatedBoxPreview()` - public `@Preview` that renders a sample
  `AnimatedBox`.

## Invariants
- The gradient is drawn only when the draw size is non-zero (guards an Android
  `RadialGradient` crash on zero size).
- Gradient colors come from `rainbowColors` in `ui/screens/games` (`Brushes.kt`);
  changing that list changes this border.
- Border width is the `padding(2.dp)` between the outer and inner
  `RoundedCornerShape(12.dp)` clips.
- Gradient radius uses the outer `DrawScope` size; the animated center endpoints come
  from the inner size reported by `onSizeChanged`.
- Content background is a hardcoded `Color.White`, not a theme token.

## Side Effects
- Runs an infinite `rememberInfiniteTransition` (5000 ms `FastOutSlowInEasing` tween,
  `RepeatMode.Reverse`) while in composition; the border is redrawn every frame.
- Writes the remembered `controlEdge` state on every layout size change.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
- Run: `./gradlew :composeApp:verifyRoborazziJvm --no-configuration-cache`
