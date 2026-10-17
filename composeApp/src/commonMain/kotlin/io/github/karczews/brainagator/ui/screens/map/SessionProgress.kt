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

package io.github.karczews.brainagator.ui.screens.map

import androidx.compose.runtime.Immutable
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo

/** Stars a map stop can hold; winning a game fills its stop. */
const val MAX_STOP_STARS = 3

/**
 * PROTOTYPE: progress through the map for the current app session only. Nothing is persisted, so a
 * restart starts the journey again.
 *
 * @property stars Stars earned per game, from 0 to [MAX_STOP_STARS].
 * @property lastWon The most recently won game, used to pick the next stop once every stop is complete.
 */
@Immutable
data class SessionProgress(
    val stars: Map<GameType, Int> = emptyMap(),
    val lastWon: GameType? = null,
) {
    fun starsFor(gameType: GameType): Int = stars[gameType] ?: 0

    fun withWin(gameType: GameType): SessionProgress = copy(stars = stars + (gameType to MAX_STOP_STARS), lastWon = gameType)

    /**
     * The stop the gator points at: the first playable game not won yet, or, once every one is won, the one after
     * the last win (so the journey loops).
     */
    fun suggestedGame(games: List<GameInfo>): GameInfo? {
        val playable = games.filterNot { it.isComingSoon }
        val afterLastWin = playable.indexOfFirst { it.gameType == lastWon } + 1
        return playable.firstOrNull { starsFor(it.gameType) == 0 }
            ?: playable.getOrNull(afterLastWin % playable.size.coerceAtLeast(1))
    }
}
