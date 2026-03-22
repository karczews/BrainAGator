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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = KidBlue,
    onPrimary = OnKidBlue,
    primaryContainer = KidBlueContainer,
    onPrimaryContainer = OnKidBlueContainer,
    secondary = KidOrange,
    onSecondary = OnKidOrange,
    secondaryContainer = KidOrangeContainer,
    onSecondaryContainer = OnKidOrangeContainer,
    tertiary = KidPink,
    onTertiary = OnKidPink,
    tertiaryContainer = KidPinkContainer,
    onTertiaryContainer = OnKidPinkContainer,
    error = KidError,
    onError = OnKidError,
    errorContainer = KidErrorContainer,
    onErrorContainer = OnKidErrorContainer,
    background = KidBackground,
    onBackground = OnKidBackground,
    surface = KidSurface,
    onSurface = OnKidSurface,
    surfaceVariant = KidSurfaceVariant,
    onSurfaceVariant = OnKidSurfaceVariant,
    outline = KidOutline,
    outlineVariant = KidOutlineVariant,
    inverseSurface = KidInverseSurface,
    inverseOnSurface = KidInverseOnSurface,
    inversePrimary = KidInversePrimary,
    scrim = KidScrim,
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content,
    )
}
