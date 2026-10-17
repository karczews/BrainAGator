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

package io.github.karczews.brainagator.ui.screens.games.numberorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_number_order
import brainagator.composeapp.generated.resources.game_number_order
import brainagator.composeapp.generated.resources.number_order_feedback_negative
import brainagator.composeapp.generated.resources.number_order_feedback_positive
import brainagator.composeapp.generated.resources.number_order_instruction
import brainagator.composeapp.generated.resources.subtitle_number_order
import io.github.karczews.brainagator.Logger
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.theme.Dimens
import io.github.karczews.brainagator.theme.GameOcean
import io.github.karczews.brainagator.tts.TextToSpeech
import io.github.karczews.brainagator.tts.speakNow
import io.github.karczews.brainagator.ui.elements.GatorSpeech
import io.github.karczews.brainagator.ui.elements.TactileColors
import io.github.karczews.brainagator.ui.elements.TactileDefaults
import io.github.karczews.brainagator.ui.elements.TactileSurface
import io.github.karczews.brainagator.ui.elements.rememberShakeState
import io.github.karczews.brainagator.ui.elements.shake
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GameBoardLayout
import io.github.karczews.brainagator.ui.screens.games.GameScreenScaffold
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

private const val NUMBER_TILES_PER_ROW = 3

val NumberOrderGameInfo =
    GameInfo(
        titleRes = Res.string.game_number_order,
        subtitleRes = Res.string.subtitle_number_order,
        descriptionRes = Res.string.desc_number_order,
        icon = Icons.Rounded.Tag,
        accentColor = GameOcean,
        gameType = GameType.NumberOrder,
    )

/**
 * Main game screen for the Number Order game.
 *
 * Presents a sequence of shuffled numbers that the user must click in ascending order.
 * The game generates 5 random numbers from 1-9 and requires the player to tap them
 * from smallest to largest. Correct selections are highlighted in green, and
 * speech feedback is provided for both correct and incorrect answers.
 *
 * @param gameInfo Information about the game (title, subtitle, icon)
 * @param onBackClick Callback when the back button is pressed
 * @param onGameWon Callback when all numbers are correctly ordered
 */
@Composable
fun NumberOrderGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
    random: Random = Random.Default,
) {
    // Generates a list of 5 unique random numbers between 1 and 9.
    fun generateNumbers(): List<Int> = (1..9).shuffled(random).take(5)

    val generatedNumbers = remember { generateNumbers() }

    val gameNumbersInOrder = remember(generatedNumbers) { generatedNumbers.sorted().toMutableStateList() }
    val gameNumbers = remember(generatedNumbers) { generatedNumbers.map { GameNumber(it, false) }.toMutableStateList() }
    val correctNextNumber by remember { derivedStateOf { gameNumbersInOrder.firstOrNull() ?: 0 } }

    val positiveFeedbackMessages =
        stringArrayResource(Res.array.number_order_feedback_positive)
    val negativeFeedbackMessages =
        stringArrayResource(Res.array.number_order_feedback_negative)

    GameScreenScaffold(
        gameInfo = gameInfo,
        onBackClick = onBackClick,
    ) { innerPadding, tts ->

        var feedbackMessage by remember { mutableStateOf<String?>(null) }
        var feedbackEventId by remember { mutableStateOf(0) }

        val onNumberClick = { gameNumber: GameNumber ->
            Logger.d { "Number clicked: ${gameNumber.number}, gameNumbersInOrder=$gameNumbersInOrder" }
            val nextNumber = gameNumbersInOrder.firstOrNull()
            if (nextNumber != null && gameNumber.number == nextNumber) {
                gameNumbersInOrder.removeAt(0)
                gameNumbers.remove(gameNumber)
                val position = gameNumbers.indexOfLast { it.matched } + 1
                gameNumbers.add(position, gameNumber.copy(matched = true))
                Logger.d { "New gameNumbers=$gameNumbers" }
                feedbackMessage = positiveFeedbackMessages.random(random)
                feedbackEventId++
            } else {
                feedbackMessage = negativeFeedbackMessages.random(random)
                feedbackEventId++
            }

            if (gameNumbersInOrder.isEmpty()) {
                onGameWon()
            }
        }

        LaunchedEffect(feedbackEventId) {
            feedbackMessage?.let {
                val job = tts.speak(it)
                try {
                    job.join()
                } finally {
                    job.cancel()
                }
            }
        }
        NumberOrderBoard(
            gameNumbers = gameNumbers,
            correctNextNumber = correctNextNumber,
            onNumberClick = onNumberClick,
            tts = tts,
            accentColor = gameInfo.accentColor,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        )
    }
}

/**
 * Layout of the Number Order game on the shared [GameBoardLayout]: big number tiles, three to a row, with the
 * speaking gator below them. On short landscape screens the gator moves beside the board (or everything
 * stacks), so the tiles always stay on screen.
 */
@Composable
private fun NumberOrderBoard(
    gameNumbers: List<GameNumber>,
    correctNextNumber: Int,
    onNumberClick: (GameNumber) -> Unit,
    tts: TextToSpeech,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val instructionText = stringResource(Res.string.number_order_instruction)
    GameBoardLayout(
        modifier = modifier.padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
        boardMinWidth = Dimens.touchTarget * NUMBER_TILES_PER_ROW + Dimens.answerTileGap * (NUMBER_TILES_PER_ROW - 1),
        instruction = {
            GatorSpeech(
                text = instructionText,
                onGatorClick = { tts.speakNow(instructionText) },
                accent = accentColor,
            )
        },
        progress = {},
    ) { _, boardModifier ->
        NumberTilesGrid(
            gameNumbers = gameNumbers,
            correctNextNumber = correctNextNumber,
            accentColor = accentColor,
            onNumberClick = onNumberClick,
            modifier = boardModifier,
        )
    }
}

/**
 * Number tiles, three to a row and as big as the board allows (up to [Dimens.numberTileMaxSize]); a shorter last
 * row is centered. Matched numbers turn green and slide to the front, in order. Wrong taps wiggle.
 *
 * The grid has two columns per tile, so the centered last row is just a one-column spacer in front of it.
 */
@Composable
private fun NumberTilesGrid(
    gameNumbers: List<GameNumber>,
    correctNextNumber: Int,
    accentColor: Color,
    onNumberClick: (GameNumber) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val gap = Dimens.answerTileGap
        val rows = (gameNumbers.size + NUMBER_TILES_PER_ROW - 1) / NUMBER_TILES_PER_ROW
        val tileSize =
            minOf(
                (maxWidth - gap * (NUMBER_TILES_PER_ROW - 1)) / NUMBER_TILES_PER_ROW,
                (maxHeight - gap * (rows - 1)) / rows.coerceAtLeast(1),
                Dimens.numberTileMaxSize,
            ).coerceAtLeast(Dimens.touchTarget)
        val lastRowStart = (rows - 1) * NUMBER_TILES_PER_ROW
        val lastRowCount = gameNumbers.size - lastRowStart
        LazyVerticalGrid(
            columns = GridCells.Fixed(NUMBER_TILES_PER_ROW * 2),
            modifier = Modifier.width(tileSize * NUMBER_TILES_PER_ROW + gap * (NUMBER_TILES_PER_ROW - 1)),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalArrangement = Arrangement.spacedBy(gap),
            userScrollEnabled = false,
        ) {
            gameNumbers.forEachIndexed { index, gameNumber ->
                if (index == lastRowStart && lastRowCount < NUMBER_TILES_PER_ROW) {
                    item(key = "centering-gap", span = { GridItemSpan(NUMBER_TILES_PER_ROW - lastRowCount) }) {}
                }
                item(key = gameNumber.number, span = { GridItemSpan(2) }) {
                    NumberTile(
                        gameNumber = gameNumber,
                        isNext = gameNumber.number == correctNextNumber,
                        accentColor = accentColor,
                        onClick = { onNumberClick(gameNumber) },
                        modifier = Modifier.animateItem().size(tileSize),
                    )
                }
            }
        }
    }
}

/** One number tile: accent number on white while open, green once matched, coral and wiggling when wrong. */
@Composable
private fun NumberTile(
    gameNumber: GameNumber,
    isNext: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shakeState = rememberShakeState()
    val scope = rememberCoroutineScope()
    NumberBox(
        modifier = modifier.shake(shakeState),
        number = gameNumber.number,
        colors =
            when {
                gameNumber.matched -> TactileDefaults.successColors()
                shakeState.isShaking -> TactileDefaults.wrongColors()
                else -> TactileDefaults.surfaceColors(content = accentColor)
            },
        enabled = !gameNumber.matched,
        onClick = {
            if (!isNext) {
                scope.launch { shakeState.shake() }
            }
            onClick()
        },
    )
}

/**
 * A single number tile in the Number Order game.
 *
 * @param modifier Modifier for layout and styling
 * @param number The number to display in the box
 * @param colors Tile colors reflecting its state (to tap, matched, wrong)
 * @param enabled Whether the tile can still be tapped
 * @param onClick Callback invoked when the box is tapped
 */
@Composable
private fun NumberBox(
    modifier: Modifier,
    number: Int,
    colors: TactileColors,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    TactileSurface(
        modifier = modifier,
        colors = colors,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.displayMedium,
        )
    }
}

@Preview
@Composable
private fun NumberOrderGameScreenPreview() {
    AppTheme {
        NumberOrderGameScreen(
            gameInfo = NumberOrderGameInfo,
            onBackClick = {},
            onGameWon = {},
        )
    }
}

/**
 * Represents a number in the Number Order game.
 *
 * @property number The numeric value to display (1-9)
 * @property matched Whether this number has been correctly selected in sequence
 */
data class GameNumber(
    val number: Int,
    val matched: Boolean,
)
