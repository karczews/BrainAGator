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

package io.github.karczews.brainagator.ui.screens.games.shapematch

import androidx.compose.ui.graphics.Color

internal data class GameColor(
    val color: Color,
    val name: String,
)

internal val gameColors =
    listOf(
        GameColor(Color(0xFFE53935), "Red"),
        GameColor(Color(0xFF1E88E5), "Blue"),
        GameColor(Color(0xFFFDD835), "Yellow"),
        GameColor(Color(0xFF43A047), "Green"),
        GameColor(Color(0xFFFB8C00), "Orange"),
        GameColor(Color(0xFF8E24AA), "Purple"),
        GameColor(Color(0xFFF06292), "Pink"),
        GameColor(Color(0xFF00ACC1), "Cyan"),
    )
