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

package io.github.karczews.brainagator.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe game identifiers
 */
@Serializable
enum class GameType {
    ShapeMatch,
    NumberOrder,
    ColorMatch,
    SizeOrder,
    Pattern,
    OddOneOut,
    SpotDifference,
}

/**
 * Navigation routes for Navigation 3
 * All routes are serializable for back stack persistence
 */
@Serializable
sealed interface Route {
    @Serializable
    data object GameSelection : Route

    @Serializable
    data class Game(
        val gameType: GameType,
    ) : Route

    @Serializable
    data object GameWon : Route
}
