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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GameGrape
import io.github.karczews.brainagator.theme.darken
import io.github.karczews.brainagator.theme.lighten

private const val HIGHLIGHT_FRACTION = 0.12f

/** Colors of a [TactileSurface]: the top [face], the [content] drawn on it and the darker [lip] beneath. */
@Immutable
data class TactileColors(
    val face: Color,
    val content: Color,
    val lip: Color,
)

object TactileDefaults {
    private const val LIP_DARKEN_FRACTION = 0.25f

    /** A colored block, e.g. a game tile or an accent button. The lip is a darker shade of [face]. */
    @Composable
    fun colors(
        face: Color,
        content: Color = AppTheme.extendedColors.onAccent,
    ): TactileColors = TactileColors(face = face, content = content, lip = face.darken(LIP_DARKEN_FRACTION))

    /** A neutral white block, e.g. an answer tile or a secondary button. */
    @Composable
    fun surfaceColors(content: Color = MaterialTheme.colorScheme.onSurface): TactileColors =
        TactileColors(
            face = MaterialTheme.colorScheme.surface,
            content = content,
            lip = MaterialTheme.colorScheme.outlineVariant,
        )

    /** The main call to action on a screen. */
    @Composable
    fun primaryColors(): TactileColors = colors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)

    /** An answer the child got right. */
    @Composable
    fun successColors(): TactileColors = colors(AppTheme.extendedColors.success, AppTheme.extendedColors.onSuccess)

    /** An answer the child got wrong; soft rather than alarming. */
    @Composable
    fun wrongColors(): TactileColors =
        TactileColors(
            face = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer,
            lip = MaterialTheme.colorScheme.error,
        )
}

/**
 * The basic building block of the app: a chunky, rounded block standing on a darker "lip",
 * like a toy button. When [onClick] is set, the face sinks into the lip while pressed.
 * Anything with a lip is tappable; non-interactive content should use [PlayCard] instead.
 *
 * Size modifiers apply to the whole block including the lip ([Dimens.lipHeight]).
 */
@Composable
fun TactileSurface(
    modifier: Modifier = Modifier,
    colors: TactileColors = TactileDefaults.surfaceColors(),
    shape: Shape = MaterialTheme.shapes.large,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressDepth by animateDpAsState(
        targetValue = if (isPressed) Dimens.lipHeight else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "tactilePressDepth",
    )
    val clickModifier =
        if (onClick != null) {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
        } else {
            Modifier
        }

    Box(modifier = modifier.then(clickModifier), propagateMinConstraints = true) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = Dimens.lipHeight)
                .background(colors.lip, shape),
        )
        Box(
            modifier =
                Modifier
                    .padding(bottom = Dimens.lipHeight)
                    .offset { IntOffset(0, pressDepth.roundToPx()) }
                    .clip(shape)
                    // A slightly lighter top edge gives the face a soft, candy-like sheen.
                    .background(Brush.verticalGradient(listOf(colors.face.lighten(HIGHLIGHT_FRACTION), colors.face))),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides colors.content) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun TactileSurfacePreview() {
    AppTheme {
        TactileSurface(
            modifier = Modifier.size(120.dp),
            colors = TactileDefaults.colors(GameGrape),
            onClick = {},
        ) {
            Text("Tap!", style = MaterialTheme.typography.titleLarge)
        }
    }
}
