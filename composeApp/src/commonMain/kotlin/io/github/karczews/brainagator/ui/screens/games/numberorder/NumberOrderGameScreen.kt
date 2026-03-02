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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.animateItem
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.desc_number_order
import brainagator.composeapp.generated.resources.game_number_order
import brainagator.composeapp.generated.resources.number_order_feedback_negative
import brainagator.composeapp.generated.resources.number_order_feedback_positive
import brainagator.composeapp.generated.resources.number_order_instruction
import brainagator.composeapp.generated.resources.subtitle_number_order
import io.github.karczews.brainagator.Logger
import io.github.karczews.brainagator.ui.navigation.GameType
import io.github.karczews.brainagator.ui.screens.GameInfo
import io.github.karczews.brainagator.ui.screens.games.GameScreenScaffold
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

val NumberOrderGameInfo =
    GameInfo(
        titleRes = Res.string.game_number_order,
        subtitleRes = Res.string.subtitle_number_order,
        descriptionRes = Res.string.desc_number_order,
        icon = Icons.Default.Tag,
        gradientColors = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)),
        gameType = GameType.NumberOrder,
    )

@Composable
fun NumberOrderGameScreen(
    gameInfo: GameInfo,
    onBackClick: () -> Unit,
    onGameWon: () -> Unit,
) {
    fun generateNumbers(): List<Int> = (1..9).shuffled().take(5)

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
                gameNumbersInOrder.removeFirst()
                gameNumbers.remove(gameNumber)
                val position = gameNumbers.indexOfLast { it.matched } + 1
                gameNumbers.add(position, gameNumber.copy(matched = true))
                Logger.d { "New gameNumbers=$gameNumbers" }
                feedbackMessage = positiveFeedbackMessages.random()
                feedbackEventId++
            } else {
                feedbackMessage = negativeFeedbackMessages.random()
                feedbackEventId++
            }

            if (gameNumbersInOrder.isEmpty()) {
                onGameWon()
            }
        }

        LaunchedEffect(feedbackEventId) {
            feedbackMessage?.let {
                val job = tts.speak(it)
                job.join()
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.number_order_instruction),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(48.dp))

            NumberBoxesRow(
                gameNumbers = gameNumbers,
                correctNextNumber = correctNextNumber,
                onNumberClick = onNumberClick,
            )
        }
    }
}

@Composable
private fun NumberBoxesRow(
    gameNumbers: List<GameNumber>,
    correctNextNumber: Int,
    onNumberClick: (GameNumber) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            items = gameNumbers,
            key = { it.number },
        ) { gameNumber ->
            var isIncorrectSelection by remember { mutableStateOf(false) }
            LaunchedEffect(isIncorrectSelection) {
                if (isIncorrectSelection) {
                    delay(300)
                    isIncorrectSelection = false
                }
            }
            NumberBox(
                modifier = Modifier.animateItem(),
                number = gameNumber.number,
                isGreen = gameNumber.matched,
                isIncorrectSelection = isIncorrectSelection,
                onClick = {
                    if (gameNumber.number != correctNextNumber) {
                        isIncorrectSelection = true
                    }
                    onNumberClick(gameNumber)
                },
            )
        }
    }
}

@Composable
private fun NumberBox(
    modifier: Modifier,
    number: Int,
    isGreen: Boolean,
    isIncorrectSelection: Boolean,
    onClick: () -> Unit,
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isIncorrectSelection) {
        if (isIncorrectSelection) {
            shakeOffset.animateTo(
                targetValue = 1f,
                animationSpec = tween(50),
            )
            shakeOffset.animateTo(
                targetValue = -1f,
                animationSpec = tween(50),
            )
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(50),
            )
        }
    }

    val backgroundColor = if (isGreen) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surface
    val borderColor =
        if (isGreen) {
            Color(0xFF2E7D32)
        } else if (isIncorrectSelection) {
            Color.Red
        } else {
            MaterialTheme.colorScheme.outline
        }

    Box(
        modifier =
            modifier
                .size(64.dp)
                .scale(1f + shakeOffset.value * 0.1f)
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .border(
                    width = if (isIncorrectSelection) 4.dp else 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp),
                ).clickable(
                    enabled = !isGreen,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (isGreen) Color.White else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun NumberOrderGameScreenPreview() {
    NumberOrderGameScreen(
        gameInfo = NumberOrderGameInfo,
        onBackClick = {},
        onGameWon = {},
    )
}

data class GameNumber(
    val number: Int,
    val matched: Boolean,
)
