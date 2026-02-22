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
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.color_blue
import brainagator.composeapp.generated.resources.color_green
import brainagator.composeapp.generated.resources.color_orange
import brainagator.composeapp.generated.resources.color_pink
import brainagator.composeapp.generated.resources.color_purple
import brainagator.composeapp.generated.resources.color_red
import brainagator.composeapp.generated.resources.color_turquoise
import brainagator.composeapp.generated.resources.color_yellow
import org.jetbrains.compose.resources.StringResource

internal data class GameColor(
    val color: Color,
    val nameRes: StringResource,
)

internal val gameColors =
    listOf(
        GameColor(Color(0xFFE53935), Res.string.color_red),
        GameColor(Color(0xFF1E88E5), Res.string.color_blue),
        GameColor(Color(0xFFFDD835), Res.string.color_yellow),
        GameColor(Color(0xFF43A047), Res.string.color_green),
        GameColor(Color(0xFFFB8C00), Res.string.color_orange),
        GameColor(Color(0xFF8E24AA), Res.string.color_purple),
        GameColor(Color(0xFFF06292), Res.string.color_pink),
        GameColor(Color(0xFF40E0D0), Res.string.color_turquoise),
    )
