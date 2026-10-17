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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.gator_kid_in_suit_1
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GameTangerine
import io.github.karczews.brainagator.theme.darken
import org.jetbrains.compose.resources.painterResource

private const val ACCENT_TEXT_DARKEN_FRACTION = 0.2f
private const val AVATAR_BACKDROP_ALPHA = 0.14f

/** Below this card width the mascot is dropped: it would leave the text less than ~120dp. */
private val MascotMinCardWidth = 200.dp

/**
 * A flat, rounded card for content the child reads but does not tap (instructions, progress).
 * It deliberately has no lip, so it never looks like a button next to [TactileSurface]s.
 */
@Composable
fun PlayCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                .padding(Dimens.spaceMd),
        content = content,
    )
}

/**
 * A [PlayCard] in which the gator mascot "says" [text], with an optional small [label] above it.
 * In narrow spaces the (decorative) mascot is left out so the text keeps room to stay readable.
 */
@Composable
fun InstructionCard(
    text: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    PlayCard(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints {
            val showMascot = maxWidth >= MascotMinCardWidth
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showMascot) {
                    InstructionMascot(accent = accent)
                }
                InstructionText(text = text, label = label, accent = accent)
            }
        }
    }
}

@Composable
private fun InstructionMascot(accent: Color) {
    Box(
        modifier =
            Modifier
                .size(64.dp)
                .background(accent.copy(alpha = AVATAR_BACKDROP_ALPHA), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.gator_kid_in_suit_1),
            contentDescription = null,
            modifier = Modifier.size(52.dp),
        )
    }
}

@Composable
private fun InstructionText(
    text: String,
    label: String?,
    accent: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs)) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = accent.darken(ACCENT_TEXT_DARKEN_FRACTION),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun InstructionCardPreview() {
    AppTheme {
        InstructionCard(
            text = "Select a Yellow Diamond",
            label = "CURRENT GOAL",
            accent = GameTangerine,
            modifier = Modifier.padding(Dimens.spaceMd),
        )
    }
}
