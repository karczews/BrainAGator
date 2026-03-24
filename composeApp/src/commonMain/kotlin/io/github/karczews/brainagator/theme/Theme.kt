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
import androidx.compose.ui.unit.dp

private val RetroMarioColorScheme = lightColorScheme(
    primary = BrickRed,
    onPrimary = White,
    primaryContainer = PalePink,
    onPrimaryContainer = DarkMaroon,
    secondary = PipeGreen,
    onSecondary = White,
    secondaryContainer = MintGreen,
    onSecondaryContainer = DarkForestGreen,
    error = CrimsonRose,
    onError = White,
    errorContainer = ErrorPink,
    onErrorContainer = ErrorMaroon,
    background = SkyBlue,
    onBackground = EarthyBrown,
    surface = SkyBlue,
    onSurface = EarthyBrown,
    surfaceVariant = WarmCream,
    onSurfaceVariant = EarthyBrown,
    surfaceContainerLow = WarmCream,
    surfaceContainerHigh = GoldenTan,
    surfaceContainerHighest = WarmCream,
    outline = WoodenBrown,
    outlineVariant = HoneyGold,
    inverseSurface = CharcoalBrown,
    inverseOnSurface = RosyBeige,
    inversePrimary = SalmonPink,
    scrim = Black,
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RetroMarioColorScheme,
        typography = getAppTypography(),
        shapes = AppShapes,
        content = content,
    )
}
