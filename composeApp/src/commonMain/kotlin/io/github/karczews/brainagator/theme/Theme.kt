/*
 * Copyright 2026 Krzysztof Karczewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.karczews.brainagator.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

private val GatorJungleColorScheme =
    lightColorScheme(
        primary = GatorGreen,
        onPrimary = White,
        primaryContainer = LeafMint,
        onPrimaryContainer = DeepLeaf,
        inversePrimary = LeafMint,
        secondary = SunnyYellow,
        onSecondary = HoneyBrown,
        secondaryContainer = ButterCream,
        onSecondaryContainer = HoneyBrown,
        tertiary = SkyBlue,
        onTertiary = White,
        tertiaryContainer = SkyMist,
        onTertiaryContainer = DeepSky,
        error = CoralRed,
        onError = White,
        errorContainer = CoralMist,
        onErrorContainer = DeepCoral,
        background = Cream,
        onBackground = Ink,
        surface = White,
        onSurface = Ink,
        surfaceVariant = Oat,
        onSurfaceVariant = InkSoft,
        surfaceContainerLowest = White,
        surfaceContainerLow = Cream,
        surfaceContainer = Oat,
        surfaceContainerHigh = Oat,
        surfaceContainerHighest = Biscuit,
        inverseSurface = Ink,
        inverseOnSurface = Cream,
        outline = Pebble,
        outlineVariant = Sand,
        scrim = Black,
    )

// Generous, friendly corners everywhere; pills and circles come from CircleShape.
private val AppShapes =
    Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(28.dp),
        extraLarge = RoundedCornerShape(36.dp),
    )

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalExtendedColors provides LightExtendedColors) {
        MaterialTheme(
            colorScheme = GatorJungleColorScheme,
            typography = getAppTypography(),
            shapes = AppShapes,
            content = content,
        )
    }
}

/** Accessors for theme values that live outside [MaterialTheme]. */
object AppTheme {
    val extendedColors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}
