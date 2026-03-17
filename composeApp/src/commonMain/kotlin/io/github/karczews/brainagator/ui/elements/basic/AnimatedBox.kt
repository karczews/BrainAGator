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

package io.github.karczews.brainagator.ui.elements.basic

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.karczews.brainagator.ui.screens.games.rainbowColors

@Composable
fun AnimatedBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "borderRotation")
    var controlEdge by remember { mutableStateOf(Offset.Zero) }
    val center by transition.animateValue(
        initialValue = controlEdge.copy(x = 0f),
        targetValue = controlEdge,
        typeConverter = Offset.VectorConverter,
        animationSpec =
            infiniteRepeatable(
                animation = tween(5000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
    )
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .drawWithCache {
                    onDrawBehind {
                        drawRect(Brush.radialGradient(rainbowColors, center = center, radius = controlEdge.x))
                    }
                }.padding(2.dp)
                .onSizeChanged { size ->
                    controlEdge = Offset(size.width.toFloat(), size.height.toFloat())
                }.clip(RoundedCornerShape(12.dp))
                .background(Color.White),
        contentAlignment = contentAlignment,
        content = content,
    )
}

@Preview(
    backgroundColor = 0xDDDDDD,
    locale = "en",
    showBackground = true,
)
@Composable
fun AnimatedBoxPreview() {
    AnimatedBox(
        modifier = Modifier,
    ) {
        Text("Animated box", Modifier.padding(20.dp))
    }
}
