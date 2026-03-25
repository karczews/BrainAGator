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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_shape_match
import brainagator.composeapp.generated.resources.game_shape_match
import brainagator.composeapp.generated.resources.shape_match_select_instruction
import brainagator.composeapp.generated.resources.subtitle_shape_match
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GameScreenScaffold
import org.jetbrains.compose.resources.stringResource

/**
 * Game information for the Shape Match game.
 */
val ShapeMatchGameInfo =
    GameInfo(
        titleRes = Res.string.game_shape_match,
        subtitleRes = Res.string.subtitle_shape_match,
        descriptionRes = Res.string.desc_shape_match,
        icon = Icons.Default.Category,
        gradientColors = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)),
        gameType = GameType.ShapeMatch,
    )

/**
 * Main screen for the Shape Match game where users find and select
 * colored shapes matching the given instruction.
 *
 * @param gameInfo Information about the game for display in the scaffold
 * @param onBackClick Callback invoked when user wants to navigate back
 * @param onGameWon Callback invoked when user completes all iterations
 * @param maxShapes Maximum number of different shapes to use (default: 5)
 * @param maxColors Maximum number of different colors to use (default: 5)
 * @param numbersOfItemsToSelect Number of shape-color pairs to display (default: 6)
 */
@Composable
fun ShapeMatchGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
    maxShapes: Int = 5,
    maxColors: Int = 5,
    numbersOfItemsToSelect: Int = 6,
) {
    // Validate input parameter
    require(numbersOfItemsToSelect >= 1) {
        "numbersOfItemsToSelect must be at least 1, got $numbersOfItemsToSelect"
    }

    // Function to generate random shape-color pairs
    fun generateShapeColorPairs(): List<Pair<GameShape, GameColor>> {
        val shapes = gameShapes.shuffled().take(maxShapes.coerceIn(1, gameShapes.size))
        val colors = gameColors.shuffled().take(maxColors.coerceIn(1, gameColors.size))

        // Calculate maximum unique combinations possible
        val maxUnique = shapes.size * colors.size
        val desiredCount = numbersOfItemsToSelect.coerceAtMost(maxUnique)

        // Build all possible pairs, shuffle, and take desired count
        val allPairs = shapes.flatMap { shape -> colors.map { color -> shape to color } }
        return allPairs.shuffled().take(desiredCount)
    }

    var shapeColorPairs by remember { mutableStateOf(generateShapeColorPairs()) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isIncorrectSelection by remember { mutableStateOf(false) }
    val totalIterations = 5

    val currentTarget =
        remember(shapeColorPairs) {
            shapeColorPairs.randomOrNull()
                ?: throw IllegalStateException(
                    "shapeColorPairs is empty - this should not happen when numbersOfItemsToSelect >= 1",
                )
        }

    // Flash color for incorrect selection
    val flashBorderColor = if (isIncorrectSelection) MaterialTheme.colorScheme.error else Color.Transparent

    // Reset incorrect selection after animation
    LaunchedEffect(isIncorrectSelection) {
        if (isIncorrectSelection) {
            kotlinx.coroutines.delay(300)
            isIncorrectSelection = false
        }
    }

    fun handleShapeClick(
        clickedShape: GameShape,
        clickedColor: GameColor,
    ) {
        if (clickedShape to clickedColor == currentTarget) {
            // Correct - increment count and regenerate
            correctCount++
            if (correctCount >= totalIterations) {
                onGameWon()
            } else {
                // Regenerate shapes and colors
                shapeColorPairs = generateShapeColorPairs()
            }
        } else {
            // Incorrect - trigger flash effect
            isIncorrectSelection = true
        }
    }

    GameScreenScaffold(
        gameInfo = gameInfo,
        onBackClick = onBackClick,
        bottomBar = {
            GameProgress(correctCount, totalIterations)
        },
    ) { innerPadding, tts ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Instruction Sign
            val (targetShape, targetColor) = currentTarget
            val instructionText =
                stringResource(
                    Res.string.shape_match_select_instruction,
                    stringResource(targetColor.nameRes),
                    stringResource(targetShape.nameRes),
                )
            val targetShapeName = stringResource(targetShape.nameRes)

            LaunchedEffect(instructionText) {
                tts.speak(instructionText)
            }

            // Sign board with wooden post
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .border(4.dp, MaterialTheme.colorScheme.outline)
                            .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = "CURRENT GOAL",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = instructionText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                // Wooden post under the sign
                Box(
                    modifier =
                        Modifier
                            .width(20.dp)
                            .height(44.dp)
                            .background(Color(0xFF8B5E3C)),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Flash bar for incorrect selection
            if (isIncorrectSelection) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(flashBorderColor),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Display colored shapes in a grid - adapts to orientation
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                val isLandscape = maxWidth > maxHeight

                if (isLandscape) {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    ) {
                        items(shapeColorPairs) { (shape, color) ->
                            ColoredShapeButton(
                                shape = shape,
                                color = color,
                                onClick = { handleShapeClick(shape, color) },
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    ) {
                        items(shapeColorPairs) { (shape, color) ->
                            ColoredShapeButton(
                                shape = shape,
                                color = color,
                                onClick = { handleShapeClick(shape, color) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays the game progress indicator showing current progress out of total iterations.
 *
 * @param correctCount Number of correct selections made by the user
 * @param totalCount Total number of iterations required to complete the game
 */
@Composable
private fun GameProgress(
    correctCount: Int,
    totalCount: Int,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .border(4.dp, MaterialTheme.colorScheme.outline)
                .padding(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PROGRESS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.outline),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(fraction = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

/**
 * Preview wrapper for GameProgress composable.
 */
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 500)
@Composable
private fun GameProgressPreview() {
    AppTheme {
        GameProgress(correctCount = 3, totalCount = 5)
    }
}

/**
 * A colored shape button that displays a shape filled with a specific color.
 * The shape is drawn large within the card to be clearly visible.
 *
 * @param shape The shape to display
 * @param color The color to fill the shape with
 * @param onClick Callback invoked when the shape is clicked
 */
@Composable
private fun ColoredShapeButton(
    shape: GameShape,
    color: GameColor,
    onClick: () -> Unit,
) {
    val shapeName = stringResource(shape.nameRes).uppercase()
    val colorName = stringResource(color.nameRes)
    val buttonDescription = "$colorName $shapeName"

    Column(
        modifier =
            Modifier
                .aspectRatio(0.85f)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .border(4.dp, MaterialTheme.colorScheme.outline)
                .clickable(onClick = onClick)
                .semantics {
                    contentDescription = buttonDescription
                }.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // White inner box with shape drawn large
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(2.dp, MaterialTheme.colorScheme.outline)
                    .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(0.7f)
                        .aspectRatio(1f)
                        .clip(shape.shape)
                        .background(color.color),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = shapeName,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Preview of the ShapeMatchGameScreen for design-time visualization.
 */
@Preview
@Composable
private fun ShapeMatchGameScreenPreview() {
    AppTheme {
        ShapeMatchGameScreen(
            gameInfo = ShapeMatchGameInfo,
            onBackClick = {},
            onGameWon = {},
        )
    }
}
