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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.go_back
import brainagator.composeapp.generated.resources.repeat_instruction
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.screens.GameInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenScaffold(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val tts = rememberTextToSpeech()
    val scope = rememberCoroutineScope()
    val description = stringResource(gameInfo.descriptionRes)

    // Speak game description when screen opens
    LaunchedEffect(gameInfo) {
        tts.speak(description)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(gameInfo.titleRes),
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back),
                        )
                    }
                },
                actions = {
                    RepeatAudioIconButton(
                        onClick = {
                            scope.launch {
                                tts.speak(description)
                            }
                        },
                        contentDescription = stringResource(Res.string.repeat_instruction),
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = gameInfo.gradientColors.map { it.copy(alpha = 0.15f) } + Color.White,
                        ),
                    ),
        ) {
            content(innerPadding)
        }
    }
}

@Composable
private fun RepeatAudioIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val circleColor = primaryColor.copy(alpha = 0.15f)

    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp),
        ) {
            // Circular arrow background
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .drawBehind {
                            // Draw circle
                            drawCircle(
                                color = circleColor,
                                radius = size.minDimension / 2,
                            )
                            // Draw circular arrow
                            val strokeWidth = 2.dp.toPx()
                            val radius = size.minDimension / 2 - strokeWidth
                            drawArc(
                                color = primaryColor.copy(alpha = 0.6f),
                                startAngle = -45f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth),
                                topLeft =
                                    Offset(
                                        size.width / 2 - radius,
                                        size.height / 2 - radius,
                                    ),
                                size =
                                    androidx.compose.ui.geometry.Size(
                                        radius * 2,
                                        radius * 2,
                                    ),
                            )
                            // Draw arrow head
                            val arrowSize = 6.dp.toPx()
                            val arrowX = size.width / 2 + radius * 0.7f
                            val arrowY = size.height / 2 - radius * 0.7f
                            drawLine(
                                color = primaryColor.copy(alpha = 0.6f),
                                start = Offset(arrowX - arrowSize, arrowY + arrowSize),
                                end = Offset(arrowX, arrowY),
                                strokeWidth = strokeWidth,
                            )
                            drawLine(
                                color = primaryColor.copy(alpha = 0.6f),
                                start = Offset(arrowX, arrowY + arrowSize),
                                end = Offset(arrowX, arrowY),
                                strokeWidth = strokeWidth,
                            )
                        },
            )
            // Speaker icon on top
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
