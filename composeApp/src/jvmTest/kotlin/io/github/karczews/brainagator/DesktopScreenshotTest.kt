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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.Density
import com.dropbox.differ.SimpleImageComparator
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
    private val lenientComparator = SimpleImageComparator(maxDistance = 0.01F)
    private val defaultRoborazziOptions =
        RoborazziOptions(
            compareOptions =
                RoborazziOptions.CompareOptions(
                    changeThreshold = 0.025F,
                    imageComparator = lenientComparator,
                ),
        )

    // Placeholder screen carries a color-emoji glyph plus large display text:
    // cross-OS rasterization moves ~4.3% of pixels, so it gets extra headroom.
    private val colorMatchRoborazziOptions =
        RoborazziOptions(
            compareOptions =
                RoborazziOptions.CompareOptions(
                    changeThreshold = 0.06F,
                    imageComparator = lenientComparator,
                ),
        )

    // Portrait 400x860dp: 800x1720px window at density 2. The runner defaults
    // to 1x (800x1720dp), so the density override is what pins the viewport.
    private fun capture(
        roborazziOptions: RoborazziOptions = defaultRoborazziOptions,
        widthPx: Int = VIEWPORT_WIDTH_PX,
        heightPx: Int = VIEWPORT_HEIGHT_PX,
        content: @Composable () -> Unit,
    ) {
        runDesktopComposeUiTest(width = widthPx, height = heightPx) {
            setContent {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalDensity provides Density(2f),
                ) {
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
            GameSelectionContent()
        }
    }

    // Small phone (360x640dp): the size at which every game must still be reachable at a glance.
    @Test
    fun gameSelectionSmallPhone() {
        capture(widthPx = SMALL_PHONE_WIDTH_PX, heightPx = SMALL_PHONE_HEIGHT_PX) {
            GameSelectionContent()
        }
    }

    @Composable
    private fun GameSelectionContent() {
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

    @Test
    fun gameWon() {
        capture {
            GameWonScreen(
                onPlayAgainClick = {},
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
                // Random(0) happens to deal 2, 5, 6, 7, 8, which looks already solved.
                random = Random(1),
            )
        }
    }

    @Test
    fun colorMatch() {
        capture(roborazziOptions = colorMatchRoborazziOptions) {
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
        const val SMALL_PHONE_WIDTH_PX = 720
        const val SMALL_PHONE_HEIGHT_PX = 1280
    }
}
