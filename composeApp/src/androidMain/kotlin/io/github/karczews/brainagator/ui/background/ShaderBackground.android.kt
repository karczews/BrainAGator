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

package io.github.karczews.brainagator.ui.background

import android.graphics.RuntimeShader
import android.os.Build
import android.util.AndroidRuntimeException
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.karczews.brainagator.Logger

/**
 * AGSL shader code for a simple animated gradient.
 * Creates a smooth color gradient that shifts over time.
 */
private const val AGSL_SHADER_CODE = """
uniform float2 resolution;
uniform float time;

vec4 main(vec2 coord) {
    float2 uv = coord / resolution;
    
    // Create animated gradient colors
    float r = 0.5 + 0.3 * sin(time * 0.5 + uv.x * 2.0 + uv.y);
    float g = 0.5 + 0.3 * sin(time * 0.7 + uv.x * 1.5 + uv.y * 2.0);
    float b = 0.6 + 0.25 * sin(time * 0.4 + uv.x * 2.5);
    
    // Add some diagonal movement
    float diagonal = sin(uv.x * 3.14159 + uv.y * 3.14159 + time * 0.3);
    r += diagonal * 0.1;
    g += diagonal * 0.08;
    
    return vec4(r, g, b, 1.0);
}
"""

/**
 * Android implementation of ShaderBackground using AGSL RuntimeShader.
 * Only available on Android 13+ (API 33+).
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
actual fun ShaderBackground(
    modifier: Modifier,
    time: Float,
) {
    val shaderBrush = createShaderBrush(time)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .drawBehind {
                    if (shaderBrush != null) {
                        drawRect(brush = shaderBrush)
                    } else {
                        drawFallbackGradient()
                    }
                },
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun createShaderBrush(time: Float): ShaderBrush? {
    return try {
        val shader = RuntimeShader(AGSL_SHADER_CODE)
        object : ShaderBrush() {
            override fun createShader(size: Size): android.graphics.Shader {
                shader.setFloatUniform("resolution", size.width, size.height)
                shader.setFloatUniform("time", time)
                return shader
            }
        }
    } catch (e: AndroidRuntimeException) {
        Logger.e(e) { "Failed to create RuntimeShader" }
        null
    } catch (e: IllegalArgumentException) {
        Logger.e(e) { "Invalid AGSL shader code" }
        null
    }
}

private fun DrawScope.drawFallbackGradient() {
    // Fallback gradient for older Android versions or shader errors
    val fallbackBrush =
        Brush.linearGradient(
            colors =
                listOf(
                    Color(0xFF87CEEB),
                    Color(0xFFE0B0FF),
                    Color(0xFFFFB6C1),
                ),
        )
    drawRect(brush = fallbackBrush)
}
