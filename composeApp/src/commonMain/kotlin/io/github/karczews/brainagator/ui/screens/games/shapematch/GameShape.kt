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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.shape_circle
import brainagator.composeapp.generated.resources.shape_diamond
import brainagator.composeapp.generated.resources.shape_heart
import brainagator.composeapp.generated.resources.shape_square
import brainagator.composeapp.generated.resources.shape_star
import brainagator.composeapp.generated.resources.shape_triangle
import org.jetbrains.compose.resources.StringResource

internal data class GameShape(
    val shape: Shape,
    val nameRes: StringResource,
)

internal val gameShapes =
    listOf(
        GameShape(CircleShape, Res.string.shape_circle),
        GameShape(SquareShape(), Res.string.shape_square),
        GameShape(TriangleShape(), Res.string.shape_triangle),
        GameShape(StarShape(), Res.string.shape_star),
        GameShape(DiamondShape(), Res.string.shape_diamond),
        GameShape(HeartShape(), Res.string.shape_heart),
    )

class SquareShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        return Outline.Generic(path)
    }
}

class TriangleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                val width = size.width
                val height = size.height

                moveTo(width / 2f, 0f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
        return Outline.Generic(path)
    }
}

class StarShape : Shape {
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

class DiamondShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                val centerX = size.width / 2f
                val centerY = size.height / 2f

                moveTo(centerX, 0f)
                lineTo(size.width, centerY)
                lineTo(centerX, size.height)
                lineTo(0f, centerY)
                close()
            }
        return Outline.Generic(path)
    }
}

class HeartShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                val width = size.width
                val height = size.height

                // Start at bottom point (sharp angle)
                moveTo(width / 2f, height)

                // Right side curve going up to right lobe
                cubicTo(
                    width * 0.75f,
                    height * 0.6f,
                    width * 0.85f,
                    height * 0.35f,
                    width * 0.85f,
                    height * 0.25f,
                )
                // Curve to top of right lobe
                cubicTo(
                    width * 0.85f,
                    height * 0.05f,
                    width * 0.65f,
                    height * 0.05f,
                    width * 0.55f,
                    height * 0.2f,
                )

                // Center dip
                lineTo(width / 2f, height * 0.3f)

                // Top of left lobe
                lineTo(width * 0.45f, height * 0.2f)
                // Curve down to left lobe
                cubicTo(
                    width * 0.35f,
                    height * 0.05f,
                    width * 0.15f,
                    height * 0.05f,
                    width * 0.15f,
                    height * 0.25f,
                )
                // Curve down left side
                cubicTo(
                    width * 0.15f,
                    height * 0.35f,
                    width * 0.25f,
                    height * 0.6f,
                    width / 2f,
                    height,
                )

                close()
            }
        return Outline.Generic(path)
    }
}
