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

package io.github.karczews.brainagator.ui.elements

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens

private val StarSize = 40.dp
private const val EMPTY_STAR_SCALE = 0.8f

/**
 * Progress shown as a row of stars that pop in as they are earned, so children who cannot
 * read numbers yet can still see how far they are.
 */
@Composable
fun StarProgress(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val earnedColor = AppTheme.extendedColors.reward
    val emptyColor = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier =
            modifier.semantics(mergeDescendants = true) {
                progressBarRangeInfo =
                    ProgressBarRangeInfo(
                        current = current.toFloat(),
                        range = 0f..total.toFloat(),
                        steps = (total - 1).coerceAtLeast(0),
                    )
            },
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val isEarned = index < current
            val scale by animateFloatAsState(
                targetValue = if (isEarned) 1f else EMPTY_STAR_SCALE,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMediumLow),
                label = "starScale",
            )
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = if (isEarned) earnedColor else emptyColor,
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .size(StarSize)
                        .scale(scale),
            )
        }
    }
}

@Preview
@Composable
private fun StarProgressPreview() {
    AppTheme {
        PlayCard {
            StarProgress(current = 3, total = 5)
        }
    }
}
