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

package io.github.karczews.brainagator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.karczews.brainagator.theme.AppTheme
import io.github.karczews.brainagator.ui.screens.GameSelectionScreen
import io.github.karczews.brainagator.ui.screens.GameWonScreen
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.colormatch.ColorMatchGameScreen
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameInfo
import io.github.karczews.brainagator.ui.screens.games.numberorder.NumberOrderGameScreen
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameInfo
import io.github.karczews.brainagator.ui.screens.games.shapematch.ShapeMatchGameScreen
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.random.Random
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DesktopScreenshotTest {
    private val roborazziOptions =
        RoborazziOptions(
            compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01F),
        )

    // Portrait 400x860dp at density 2 (800x1720px window).
    private fun capture(content: @Composable () -> Unit) {
        runDesktopComposeUiTest(width = VIEWPORT_WIDTH_PX, height = VIEWPORT_HEIGHT_PX) {
            setContent {
                CompositionLocalProvider(LocalInspectionMode provides true) {
                    AppTheme {
                        content()
                    }
                }
            }
            mainClock.autoAdvance = false
            mainClock.advanceTimeBy(1000L)
            onRoot().captureRoboImage(
                roborazziOptions = roborazziOptions,
            )
        }
    }

    @Test
    fun gameSelection() {
        capture {
            SharedTransitionLayout {
                AnimatedContent(targetState = "preview") {
                    GameSelectionScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@AnimatedContent,
                        shouldSpeakWelcome = false,
                    )
                }
            }
        }
    }

    @Test
    fun gameWon() {
        capture {
            GameWonScreen(
                onBackToMainClick = {},
                showFireworks = false,
            )
        }
    }

    @Test
    fun shapeMatch() {
        capture {
            ShapeMatchGameScreen(
                gameInfo = ShapeMatchGameInfo,
                onBackClick = {},
                onGameWon = {},
                random = Random(0),
            )
        }
    }

    @Test
    fun numberOrder() {
        capture {
            NumberOrderGameScreen(
                gameInfo = NumberOrderGameInfo,
                onBackClick = {},
                onGameWon = {},
                random = Random(0),
            )
        }
    }

    @Test
    fun colorMatch() {
        capture {
            ColorMatchGameScreen(
                gameInfo = ColorMatchGameInfo,
                onBackClick = {},
                onGameWon = {},
            )
        }
    }

    private companion object {
        const val VIEWPORT_WIDTH_PX = 800
        const val VIEWPORT_HEIGHT_PX = 1720
    }
}
