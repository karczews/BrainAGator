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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.gator_kid_in_suit_1
import brainagator.composeapp.generated.resources.repeat_instruction
import io.github.karczews.brainagator.LocalSharedTransitionContext
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GameOcean
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val BadgeBorder = 2.dp
private const val SHARED_GATOR_KEY = "speaking-gator"

/**
 * The gator "talking": a round, pressable gator next to a [SpeechBubble] with [text].
 * Pressing the gator makes it say the text again, so this replaces both the instruction card
 * and the old "?" help button. A small speaker badge hints that the gator makes sound.
 */
@Composable
fun GatorSpeech(
    text: String,
    onGatorClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GatorAvatarButton(onClick = onGatorClick, accent = accent)
        // At least as tall as the gator, so one- and two-line sentences give the same height.
        SpeechBubble(
            text = text,
            modifier =
                Modifier
                    .weight(1f)
                    .heightIn(min = Dimens.gatorAvatar + Dimens.lipHeight),
        )
    }
}

/**
 * The gator in a round tactile button with a speaker badge; [onClick] should make it speak.
 * It is a shared element, so the same gator travels from the home screen into the game.
 */
@Composable
fun GatorAvatarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    val description = stringResource(Res.string.repeat_instruction)
    Box(
        modifier =
            modifier
                .sharedGator()
                .size(width = Dimens.gatorAvatar, height = Dimens.gatorAvatar + Dimens.lipHeight),
    ) {
        TactileSurface(
            modifier =
                Modifier
                    .matchParentSize()
                    .semantics { contentDescription = description },
            colors = TactileDefaults.surfaceColors(content = accent),
            shape = CircleShape,
            onClick = onClick,
        ) {
            Image(
                painter = painterResource(Res.drawable.gator_kid_in_suit_1),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(top = Dimens.spaceXs)
                        .size(Dimens.gatorAvatarImage),
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Dimens.lipHeight)
                    .size(Dimens.speakerBadge)
                    .background(accent, CircleShape)
                    .border(BadgeBorder, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = null,
                tint = AppTheme.extendedColors.onAccent,
                modifier = Modifier.size(Dimens.speakerBadgeIcon),
            )
        }
    }
}

/** Matches the gator across screens when a [LocalSharedTransitionContext] is provided (previews and tests have none). */
@Composable
private fun Modifier.sharedGator(): Modifier {
    val context = LocalSharedTransitionContext.current ?: return this
    return with(context.sharedTransitionScope) {
        this@sharedGator.sharedElement(
            sharedContentState = rememberSharedContentState(key = SHARED_GATOR_KEY),
            animatedVisibilityScope = context.animatedContentScope,
        )
    }
}

@Preview
@Composable
private fun GatorSpeechPreview() {
    AppTheme {
        GatorSpeech(
            text = "Select a Yellow Diamond",
            onGatorClick = {},
            accent = GameOcean,
            modifier = Modifier.padding(Dimens.spaceMd),
        )
    }
}
