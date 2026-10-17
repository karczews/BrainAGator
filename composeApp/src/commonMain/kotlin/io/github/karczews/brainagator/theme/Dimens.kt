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

import androidx.compose.ui.unit.dp

/** Spacing and sizing tokens shared by all screens. */
object Dimens {
    val spaceXs = 4.dp
    val spaceSm = 8.dp
    val spaceMd = 16.dp
    val spaceLg = 24.dp
    val spaceXl = 32.dp

    /** Horizontal padding between screen edges and content. */
    val screenPadding = 20.dp

    /** Height of the darker "lip" under tactile elements; the face sinks by this much when pressed. */
    val lipHeight = 6.dp

    /** Minimum size of anything a child should tap; larger than the 48dp adult guideline. */
    val touchTarget = 64.dp

    /** Size of round icon buttons (back, help); at least [touchTarget]. */
    val iconButton = 64.dp

    /** Size of icons inside buttons. */
    val buttonIcon = 28.dp

    // Game tiles on the home screen
    val gameTileMinWidth = 160.dp
    val gameTileHeight = 184.dp
    val gameTilePadding = 12.dp

    // Answer tiles inside games
    val answerTileGap = 12.dp
    val answerTileMinHeight = 100.dp
    val answerTileMaxSize = 76.dp

    /** White circle holding a game's icon on its tile. */
    val iconBadge = 56.dp
    val iconBadgeIcon = 32.dp

    /** Large accent circle holding a game's icon on its placeholder screen. */
    val heroBadge = 120.dp
    val heroBadgeIcon = 64.dp

    // Mascot and illustrations
    val headerMascot = 88.dp
    val illustrationHeight = 110.dp
    val illustrationMaxHeight = 128.dp
    val rewardStar = 80.dp

    // Adventure map (PROTOTYPE): round stops along a trail, sized from the space available

    /** Largest face of a map stop; smaller screens shrink stops down to [touchTarget]. */
    val mapStopMaxFace = 88.dp

    /** Widest box (face plus label) a map stop takes, so labels stay close to their stop on wide screens. */
    val mapStopMaxWidth = 140.dp
    val mapStopStar = 14.dp
    val mapStopLabelHeight = 16.dp

    /** Below this portrait screen height the stops would overlap, so the home screen scrolls instead. */
    val mapScreenMinHeight = 560.dp

    /** Height of the map when the home screen scrolls. */
    val mapScrollHeight = 460.dp
    val mapTrailDot = 5.dp
    val mapTrailDotGap = 14.dp

    /** Narrowest landscape side column (instruction and progress) before a game board stacks instead. */
    val sidePanelMinWidth = 120.dp

    /** Narrowest board worth placing beside the side column: three 48dp tiles and their gaps. */
    val boardMinWidth = 168.dp

    // Width caps that keep content readable on wide screens
    val contentMaxWidth = 480.dp
    val buttonMaxWidth = 420.dp
}
