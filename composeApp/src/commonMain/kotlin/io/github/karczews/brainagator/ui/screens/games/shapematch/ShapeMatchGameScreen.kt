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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_shape_match
import brainagator.composeapp.generated.resources.game_shape_match
import brainagator.composeapp.generated.resources.shape_match_progress
import brainagator.composeapp.generated.resources.shape_match_select_instruction
import brainagator.composeapp.generated.resources.subtitle_shape_match
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GameScreenScaffold
import org.jetbrains.compose.resources.stringResource

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
    maxShapes: Int = 5,
    maxColors: Int = 5,
    numbersOfItemsToSelect: Int = 12,
) {
    // Function to generate random shape-color pairs
    fun generateShapeColorPairs(): List<Pair<GameShape, GameColor>> {
        val shapes = gameShapes.shuffled().take(maxShapes.coerceIn(1, gameShapes.size))
        val colors = gameColors.shuffled().take(maxColors.coerceIn(1, gameColors.size))

        val gamesList = mutableListOf<Pair<GameShape, GameColor>>()

        repeat(numbersOfItemsToSelect) {
            var game = shapes.random() to colors.random()
            while (game in gamesList) {
                game = shapes.random() to colors.random()
            }
            gamesList.add(game)
        }

        return gamesList
    }

    var shapeColorPairs by remember { mutableStateOf(generateShapeColorPairs()) }
    var correctCount by remember { mutableIntStateOf(0) }
    val totalIterations = 5

    val currentTarget = remember(shapeColorPairs) { shapeColorPairs.random() }

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
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Instruction text
            currentTarget.let { target ->
                val (targetShape, targetColor) = target
                val instructionText =
                    stringResource(
                        Res.string.shape_match_select_instruction,
                        stringResource(targetColor.nameRes),
                        stringResource(targetShape.nameRes),
                    )
                LaunchedEffect(instructionText) {
                    tts.speak(instructionText)
                }
                Text(
                    text = instructionText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Display 10 colored shapes in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(shapeColorPairs) { (shape, color) ->
                    ColoredShapeButton(
                        shape = shape,
                        color = color.color,
                        onClick = { handleShapeClick(shape, color) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 500)
@Composable
private fun GameProgress(
    correctCount: Int = 0,
    totalCount: Int = 5,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(Res.string.shape_match_progress, correctCount, totalCount),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ColoredShapeButton(
    shape: GameShape,
    color: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .aspectRatio(1f)
                .clip(shape.shape)
                .clickable(onClick = onClick)
                .background(color)
                .border(2.dp, MaterialTheme.colorScheme.outline, shape.shape),
    )
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
