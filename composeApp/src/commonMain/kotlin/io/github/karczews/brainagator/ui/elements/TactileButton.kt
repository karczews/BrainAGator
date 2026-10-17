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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.AutoSizeMin
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GameOcean

/** A pill-shaped [TactileSurface] with a label and an optional leading icon. */
@Composable
fun TactileButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    colors: TactileColors = TactileDefaults.primaryColors(),
) {
    TactileSurface(
        modifier = modifier.heightIn(min = Dimens.touchTarget),
        colors = colors,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(Dimens.buttonIcon))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                // Long labels (e.g. in Polish) shrink to stay on one line next to the icon.
                autoSize =
                    TextAutoSize.StepBased(
                        minFontSize = AutoSizeMin.label,
                        maxFontSize = MaterialTheme.typography.titleMedium.fontSize,
                    ),
            )
        }
    }
}

/** A round [TactileSurface] holding a single icon, e.g. back or help. */
@Composable
fun TactileIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TactileColors = TactileDefaults.surfaceColors(),
) {
    TactileSurface(
        // Extra height for the lip keeps the face itself a perfect circle.
        modifier = modifier.size(width = Dimens.iconButton, height = Dimens.iconButton + Dimens.lipHeight),
        colors = colors,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(Dimens.buttonIcon))
    }
}

@Preview
@Composable
private fun TactileButtonPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(Dimens.spaceMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd),
        ) {
            TactileButton(text = "Back to Main Screen", icon = Icons.Rounded.Home, onClick = {})
            TactileIconButton(
                icon = Icons.Rounded.QuestionMark,
                contentDescription = null,
                onClick = {},
                colors = TactileDefaults.colors(GameOcean),
            )
        }
    }
}
