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

package io.github.karczews.brainagator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.app_tagline
import brainagator.composeapp.generated.resources.game_color_match
import brainagator.composeapp.generated.resources.game_number_order
import brainagator.composeapp.generated.resources.game_odd_one_out
import brainagator.composeapp.generated.resources.game_pattern
import brainagator.composeapp.generated.resources.game_shape_match
import brainagator.composeapp.generated.resources.game_size_order
import brainagator.composeapp.generated.resources.game_spot_difference
import brainagator.composeapp.generated.resources.subtitle_color_match
import brainagator.composeapp.generated.resources.subtitle_number_order
import brainagator.composeapp.generated.resources.subtitle_odd_one_out
import brainagator.composeapp.generated.resources.subtitle_pattern
import brainagator.composeapp.generated.resources.subtitle_shape_match
import brainagator.composeapp.generated.resources.subtitle_size_order
import brainagator.composeapp.generated.resources.subtitle_spot_difference
import brainagator.composeapp.generated.resources.welcome_message
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.navigation.GameType
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class GameInfo(
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val gameType: GameType,
)

val games =
    listOf(
        GameInfo(
            titleRes = Res.string.game_shape_match,
            subtitleRes = Res.string.subtitle_shape_match,
            icon = Icons.Default.Category,
            gradientColors = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)),
            gameType = GameType.ShapeMatch,
        ),
        GameInfo(
            titleRes = Res.string.game_number_order,
            subtitleRes = Res.string.subtitle_number_order,
            icon = Icons.Default.Tag,
            gradientColors = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)),
            gameType = GameType.NumberOrder,
        ),
        GameInfo(
            titleRes = Res.string.game_color_match,
            subtitleRes = Res.string.subtitle_color_match,
            icon = Icons.Default.Palette,
            gradientColors = listOf(Color(0xFFFA709A), Color(0xFFFEE140)),
            gameType = GameType.ColorMatch,
        ),
        GameInfo(
            titleRes = Res.string.game_size_order,
            subtitleRes = Res.string.subtitle_size_order,
            icon = Icons.Default.SwapVert,
            gradientColors = listOf(Color(0xFFF093FB), Color(0xFFF5576C)),
            gameType = GameType.SizeOrder,
        ),
        GameInfo(
            titleRes = Res.string.game_pattern,
            subtitleRes = Res.string.subtitle_pattern,
            icon = Icons.Default.GridView,
            gradientColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
            gameType = GameType.Pattern,
        ),
        GameInfo(
            titleRes = Res.string.game_odd_one_out,
            subtitleRes = Res.string.subtitle_odd_one_out,
            icon = Icons.Default.HelpOutline,
            gradientColors = listOf(Color(0xFF2AF598), Color(0xFF009EFD)),
            gameType = GameType.OddOneOut,
        ),
        GameInfo(
            titleRes = Res.string.game_spot_difference,
            subtitleRes = Res.string.subtitle_spot_difference,
            icon = Icons.Default.Search,
            gradientColors = listOf(Color(0xFF43E97B), Color(0xFF38F9D7)),
            gameType = GameType.SpotDifference,
        ),
    )

@Composable
fun GameSelectionScreen(
    onGameSelected: (GameInfo) -> Unit = {},
    onWelcomeSpoken: () -> Unit = {},
    shouldSpeakWelcome: Boolean = true,
) {
    val tts = rememberTextToSpeech()
    val welcomeText = stringResource(Res.string.welcome_message)

    // Speak welcome message only once per app session
    LaunchedEffect(Unit) {
        if (shouldSpeakWelcome) {
            tts.speak(welcomeText)
            onWelcomeSpoken()
        }
    }
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                // Very Light Yellow
                                Color(0xFFFFFDE7),
                                // Cream
                                Color(0xFFFFF8E1),
                                // Very Light Pink
                                Color(0xFFF8BBD0).copy(alpha = 0.3f),
                            ),
                    ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF9C27B0))) {
                                append("Brain")
                            }
                            withStyle(SpanStyle(color = Color(0xFFEC407A))) {
                                append("agator")
                            }
                        },
                    style =
                        MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                        ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐊", fontSize = 32.sp)
            }

            Text(
                text = stringResource(Res.string.app_tagline),
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        color = Color.DarkGray.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                    ),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
            )

            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val chunkedGames = games.chunked(2)
                chunkedGames.forEach { rowGames ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (rowGames.size == 1) {
                                Arrangement.Center
                            } else {
                                Arrangement.spacedBy(
                                    16.dp,
                                )
                            },
                    ) {
                        rowGames.forEach { game ->
                            Box(
                                modifier =
                                    if (rowGames.size == 1) {
                                        Modifier.fillMaxWidth(0.5f)
                                    } else {
                                        Modifier.weight(
                                            1f,
                                        )
                                    },
                            ) {
                                GameCard(
                                    game = game,
                                    onClick = { title, subtitle ->
                                        tts.speak("$title. $subtitle")
                                        onGameSelected(game)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Balloon at the bottom
            Text(
                text = "🎈",
                fontSize = 48.sp,
                modifier =
                    Modifier
                        .padding(bottom = 32.dp)
                        .offset(y = 10.dp),
            )
        }
    }
}

@Composable
fun GameCard(
    game: GameInfo,
    onClick: (String, String) -> Unit,
) {
    val title = stringResource(game.titleRes)
    val subtitle = stringResource(game.subtitleRes)

    Card(
        onClick = { onClick(title, subtitle) },
        shape = RoundedCornerShape(24.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(game.gradientColors))
                    .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}
