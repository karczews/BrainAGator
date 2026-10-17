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

package io.github.karczews.brainagator.ui.screens.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.karczews.brainagator.theme.Dimens

private const val SIDE_PANEL_FRACTION = 0.4f

/**
 * Standard game layout: [instruction] on top, the [board] filling the middle and [progress] at the bottom.
 *
 * In landscape the instruction and progress move to a side column so the board keeps the full (short) height.
 * The column takes 40% of the width, narrowing further only as far as the board needs to keep [boardMinWidth]
 * (the instruction card drops its mascot when narrow, so its text stays readable). If that would leave the
 * column narrower than [Dimens.sidePanelMinWidth], everything stacks instead.
 *
 * [board] receives whether it sits beside the side column and the modifier that sizes it.
 */
@Composable
fun GameBoardLayout(
    instruction: @Composable () -> Unit,
    progress: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    boardMinWidth: Dp = Dimens.boardMinWidth,
    board: @Composable (isLandscape: Boolean, boardModifier: Modifier) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val sidePanelWidth = minOf(maxWidth * SIDE_PANEL_FRACTION, maxWidth - Dimens.spaceMd - boardMinWidth)
        val useSidePanel = maxWidth > maxHeight && sidePanelWidth >= Dimens.sidePanelMinWidth
        if (useSidePanel) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)) {
                Column(
                    modifier = Modifier.width(sidePanelWidth).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd, Alignment.CenterVertically),
                ) {
                    instruction()
                    progress()
                }
                board(true, Modifier.weight(1f).fillMaxHeight())
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)) {
                instruction()
                board(false, Modifier.weight(1f).fillMaxWidth())
                progress()
            }
        }
    }
}
