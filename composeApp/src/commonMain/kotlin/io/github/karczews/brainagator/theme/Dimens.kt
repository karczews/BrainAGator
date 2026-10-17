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

    /** Size of round icon buttons (back, help). */
    val iconButton = 56.dp

    /** Size of icons inside buttons. */
    val buttonIcon = 28.dp
}
