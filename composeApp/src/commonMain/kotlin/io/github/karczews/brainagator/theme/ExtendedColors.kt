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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Game-specific color roles that Material3's [androidx.compose.material3.ColorScheme] has no slot for.
 * Read them through [AppTheme.extendedColors].
 */
@Immutable
data class ExtendedColors(
    /** Content (text, icons) drawn on a game's accent color. */
    val onAccent: Color,
    val success: Color,
    val onSuccess: Color,
    val reward: Color,
    /** Outline around reward-colored shapes; yellow alone is too faint on white. */
    val onReward: Color,
)

internal val LightExtendedColors =
    ExtendedColors(
        onAccent = White,
        success = GatorGreen,
        onSuccess = White,
        reward = SunnyYellow,
        onReward = HoneyBrown,
    )

internal val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }
