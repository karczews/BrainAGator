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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.go_back
import brainagator.composeapp.generated.resources.ic_replay_speaker
import brainagator.composeapp.generated.resources.repeat_instruction
import io.github.karczews.brainagator.tts.TextToSpeech
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.screens.GameInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenScaffold(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues, TextToSpeech) -> Unit,
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
                    IconButton(
                        onClick = {
                            scope.launch {
                                tts.speak(description)
                            }
                        },
                        modifier = Modifier.size(60.dp),
                    ) {
                        Image(
                            modifier = Modifier.size(60.dp),
                            painter = painterResource(Res.drawable.ic_replay_speaker),
                            contentDescription = stringResource(Res.string.repeat_instruction),
                        )
                    }
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
            content(innerPadding, tts)
        }
    }
}
