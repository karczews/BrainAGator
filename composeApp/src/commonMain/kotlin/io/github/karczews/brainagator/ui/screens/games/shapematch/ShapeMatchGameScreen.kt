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

package io.github.karczews.brainagator.ui.screens.games.shapematch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_shape_match
import brainagator.composeapp.generated.resources.game_shape_match
import brainagator.composeapp.generated.resources.subtitle_shape_match
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GameScreenScaffold

private enum class GameShape {
    CIRCLE,
    SQUARE,
    TRIANGLE,
    STAR,
}

private data class GameColor(
    val color: Color,
    val name: String,
)

private val gameColors =
    listOf(
        GameColor(Color(0xFFE53935), "Red"),
        GameColor(Color(0xFF1E88E5), "Blue"),
        GameColor(Color(0xFFFDD835), "Yellow"),
        GameColor(Color(0xFF43A047), "Green"),
        GameColor(Color(0xFFFB8C00), "Orange"),
    )

private class StarShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val outerRadius = size.width / 2f
                val innerRadius = outerRadius * 0.4f
                val points = 5
                val angleStep = 360f / points

                moveTo(centerX, centerY - outerRadius)
                for (i in 0 until points * 2) {
                    val radius = if (i % 2 == 0) outerRadius else innerRadius
                    val angle = kotlin.math.PI * (i * angleStep / 2 - 90) / 180
                    val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
                    val y = centerY + (radius * kotlin.math.sin(angle)).toFloat()
                    lineTo(x, y)
                }
                close()
            }
        return Outline.Generic(path)
    }
}

val ShapeMatchGameInfo =
    GameInfo(
        titleRes = Res.string.game_shape_match,
        subtitleRes = Res.string.subtitle_shape_match,
        descriptionRes = Res.string.desc_shape_match,
        icon = Icons.Default.Category,
        gradientColors = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)),
        gameType = GameType.ShapeMatch,
    )

@Composable
fun ShapeMatchGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
) {
    var selectedShape by remember { mutableStateOf<GameShape?>(null) }
    var selectedColor by remember { mutableStateOf<GameColor?>(null) }

    GameScreenScaffold(gameInfo, onBackClick) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Select a shape",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                GameShape.entries.forEach { shape ->
                    ShapeButton(
                        shape = shape,
                        isSelected = selectedShape == shape,
                        onClick = { selectedShape = shape },
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Select a color",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(gameColors) { gameColor ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ColorButton(
                            gameColor = gameColor,
                            isSelected = selectedColor == gameColor,
                            onClick = { selectedColor = gameColor },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            selectedShape?.let { shape ->
                selectedColor?.let { color ->
                    Text(
                        text = "You selected: ${shape.name.lowercase()} + ${color.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShapeButton(
    shape: GameShape,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        }

    val borderWidth = if (isSelected) 4.dp else 2.dp
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier =
            Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .background(surfaceColor),
        contentAlignment = Alignment.Center,
    ) {
        when (shape) {
            GameShape.CIRCLE -> {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(onSurfaceColor, CircleShape),
                )
            }

            GameShape.SQUARE -> {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(onSurfaceColor),
                )
            }

            GameShape.TRIANGLE -> {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(surfaceColor)
                            .drawBehind {
                                val trianglePath =
                                    Path().apply {
                                        val width = size.width
                                        val height = size.height
                                        moveTo(width / 2f, 0f)
                                        lineTo(width, height)
                                        lineTo(0f, height)
                                        close()
                                    }
                                drawPath(trianglePath, onSurfaceColor)
                            },
                )
            }

            GameShape.STAR -> {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(StarShape())
                            .background(onSurfaceColor),
                )
            }
        }
    }
}

@Composable
private fun ColorButton(
    gameColor: GameColor,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        }

    val borderWidth = if (isSelected) 4.dp else 2.dp

    Box(
        modifier =
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .background(gameColor.color),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier =
                    Modifier
                        .size(20.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
            )
        }
    }
}

@Preview
@Composable
private fun ShapeMatchGameScreenPreview() {
    ShapeMatchGameScreen(
        gameInfo = ShapeMatchGameInfo,
        onBackClick = {},
        onGameWon = {},
    )
}
