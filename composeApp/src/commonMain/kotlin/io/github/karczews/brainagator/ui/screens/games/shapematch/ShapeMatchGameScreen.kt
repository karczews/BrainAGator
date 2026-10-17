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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_shape_match
import brainagator.composeapp.generated.resources.game_shape_match
import brainagator.composeapp.generated.resources.shape_match_current_goal
import brainagator.composeapp.generated.resources.shape_match_select_instruction
import brainagator.composeapp.generated.resources.subtitle_shape_match
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GameGrape
import io.github.karczews.brainagator.ui.elements.InstructionCard
import io.github.karczews.brainagator.ui.elements.PlayCard
import io.github.karczews.brainagator.ui.elements.StarProgress
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileSurface
import io.github.karczews.brainagator.ui.elements.rememberShakeState
import io.github.karczews.brainagator.ui.elements.shake
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GameBoardLayout
import io.github.karczews.brainagator.ui.screens.games.GameScreenScaffold
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val TILE_ASPECT_RATIO = 0.85f
private val TileSpacing = 12.dp
private val MinTileHeight = 100.dp
private const val SHAPE_FILL_FRACTION = 0.85f

/**
 * Game information for the Shape Match game.
 */
val ShapeMatchGameInfo =
    GameInfo(
        titleRes = Res.string.game_shape_match,
        subtitleRes = Res.string.subtitle_shape_match,
        descriptionRes = Res.string.desc_shape_match,
        icon = Icons.Rounded.Category,
        accentColor = GameGrape,
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
    val totalIterations = 5

    val currentTarget =
        remember(shapeColorPairs) {
            shapeColorPairs.randomOrNull()
                ?: throw IllegalStateException(
                    "shapeColorPairs is empty - this should not happen when numbersOfItemsToSelect >= 1",
                )
        }

    fun handleCorrectSelection() {
        correctCount++
        if (correctCount >= totalIterations) {
            onGameWon()
        } else {
            // Regenerate shapes and colors
            shapeColorPairs = generateShapeColorPairs()
        }
    }

    GameScreenScaffold(
        gameInfo = gameInfo,
        onBackClick = onBackClick,
    ) { innerPadding, tts ->
        val (targetShape, targetColor) = currentTarget
        val instructionText =
            stringResource(
                Res.string.shape_match_select_instruction,
                stringResource(targetColor.nameRes),
                stringResource(targetShape.nameRes),
            )

        LaunchedEffect(instructionText) {
            tts.speak(instructionText)
        }

        GameBoardLayout(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
            instruction = {
                InstructionCard(
                    text = instructionText,
                    label = stringResource(Res.string.shape_match_current_goal),
                    accent = gameInfo.accentColor,
                )
            },
            progress = { GameProgress(correctCount, totalIterations) },
        ) { isLandscape, boardModifier ->
            ShapeGrid(
                shapeColorPairs = shapeColorPairs,
                currentTarget = currentTarget,
                isLandscape = isLandscape,
                onCorrectClick = ::handleCorrectSelection,
                modifier = boardModifier,
            )
        }
    }
}

/**
 * Grid of shape tiles. In portrait, tiles shrink so that all of them fit without scrolling
 * (down to [MinTileHeight]); in landscape they fill two rows.
 */
@Composable
private fun ShapeGrid(
    shapeColorPairs: List<Pair<GameShape, GameColor>>,
    currentTarget: Pair<GameShape, GameColor>,
    isLandscape: Boolean,
    onCorrectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = Arrangement.spacedBy(TileSpacing, Alignment.CenterHorizontally)
    val verticalSpacing = Arrangement.spacedBy(TileSpacing, Alignment.CenterVertically)
    BoxWithConstraints(modifier = modifier) {
        if (isLandscape) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = spacing,
                verticalArrangement = verticalSpacing,
            ) {
                items(shapeColorPairs) { pair ->
                    ColoredShapeButton(
                        shape = pair.first,
                        color = pair.second,
                        isCorrect = pair == currentTarget,
                        onCorrectClick = onCorrectClick,
                        modifier = Modifier.aspectRatio(TILE_ASPECT_RATIO),
                    )
                }
            }
        } else {
            val rows = (shapeColorPairs.size + 1) / 2
            val cellWidth = (maxWidth - TileSpacing) / 2
            val fittingHeight = (maxHeight - TileSpacing * (rows - 1)) / rows
            val tileHeight = minOf(fittingHeight, cellWidth / TILE_ASPECT_RATIO).coerceAtLeast(MinTileHeight)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = spacing,
                verticalArrangement = verticalSpacing,
            ) {
                items(shapeColorPairs) { pair ->
                    ColoredShapeButton(
                        shape = pair.first,
                        color = pair.second,
                        isCorrect = pair == currentTarget,
                        onCorrectClick = onCorrectClick,
                        modifier = Modifier.height(tileHeight),
                    )
                }
            }
        }
    }
}

/**
 * Displays the game progress as stars earned out of total iterations.
 *
 * @param correctCount Number of correct selections made by the user
 * @param totalCount Total number of iterations required to complete the game
 */
@Composable
private fun GameProgress(
    correctCount: Int,
    totalCount: Int,
) {
    PlayCard(modifier = Modifier.fillMaxWidth()) {
        StarProgress(
            current = correctCount,
            total = totalCount,
            modifier = Modifier.fillMaxWidth(),
        )
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
 * A tactile tile that displays a shape filled with a specific color.
 * Tapping the wrong tile makes it wiggle; tapping the right one reports success.
 *
 * @param shape The shape to display
 * @param color The color to fill the shape with
 * @param isCorrect Whether this tile is the current goal
 * @param onCorrectClick Callback invoked when the correct tile is tapped
 * @param modifier Sizing provided by the grid
 */
@Composable
private fun ColoredShapeButton(
    shape: GameShape,
    color: GameColor,
    isCorrect: Boolean,
    onCorrectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shapeName = stringResource(shape.nameRes).uppercase()
    val colorName = stringResource(color.nameRes)
    val buttonDescription = "$colorName $shapeName"
    val shakeState = rememberShakeState()
    val scope = rememberCoroutineScope()

    TactileSurface(
        modifier =
            modifier
                .shake(shakeState)
                .semantics {
                    contentDescription = buttonDescription
                },
        colors = if (shakeState.isShaking) TactileDefaults.wrongColors() else TactileDefaults.surfaceColors(),
        onClick = {
            if (isCorrect) {
                onCorrectClick()
            } else {
                scope.launch { shakeState.shake() }
            }
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(Dimens.spaceSm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
        ) {
            // Soft inner well with the shape drawn as large as the well allows
            BoxWithConstraints(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .padding(6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(minOf(maxWidth, maxHeight) * SHAPE_FILL_FRACTION)
                            .clip(shape.shape)
                            .background(color.color),
                )
            }
            Text(
                text = shapeName,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
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
