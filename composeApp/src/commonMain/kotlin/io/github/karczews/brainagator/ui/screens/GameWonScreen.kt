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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.back_to_main
import brainagator.composeapp.generated.resources.congratulations
import brainagator.composeapp.generated.resources.gator_win_1
import brainagator.composeapp.generated.resources.star_1
import brainagator.composeapp.generated.resources.you_won
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.ui.FireworksAnimation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameWonScreen(onBackToMainClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "trophy_animation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "scale",
    )

    val rotations =
        listOf(750, 550, 650).mapIndexed { index, duration ->
            infiniteTransition
                .animateFloat(
                    initialValue = -10f,
                    targetValue = 10f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(duration, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "star_rotation_$index",
                ).value
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color(0xFFFFFDE7),
                                Color(0xFFFFF8E1),
                                Color(0xFFE8F5E9),
                            ),
                    ),
                ),
    ) {
        // Fireworks animation overlay
        FireworksAnimation(
            modifier = Modifier.fillMaxSize(),
            particleCount = 120,
            explosionCount = 0,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                modifier = Modifier.scale(scale),
                painter = painterResource(Res.drawable.gator_win_1),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Congratulations text
            Text(
                text = stringResource(Res.string.congratulations),
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                    ),
                color = Color(0xFF2E7D32),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.you_won),
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                color = Color(0xFF388E3C),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                repeat(3) { index ->
                    Image(
                        modifier =
                            Modifier
                                .size(80.dp)
                                .padding(horizontal = 4.dp)
                                .rotate(rotations[index]),
                        painter = painterResource(Res.drawable.star_1),
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Back to main button
            Button(
                onClick = onBackToMainClick,
                modifier =
                    Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth(1f)
                        .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.back_to_main),
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun GameWonScreenPreview() {
    AppTheme {
        GameWonScreen(onBackToMainClick = {})
    }
}
