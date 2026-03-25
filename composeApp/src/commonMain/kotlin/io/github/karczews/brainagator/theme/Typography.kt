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

package io.github.karczews.brainagator.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.lexend
import org.jetbrains.compose.resources.Font

private fun lexendStyle(
    fontFamily: FontFamily,
    weight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = TextUnit.Unspecified,
) = TextStyle(fontFamily = fontFamily, fontWeight = weight, fontSize = fontSize, lineHeight = lineHeight, letterSpacing = letterSpacing)

@Composable
fun getLexendFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.lexend, FontWeight.Normal),
        Font(Res.font.lexend, FontWeight.SemiBold),
        Font(Res.font.lexend, FontWeight.Bold),
        Font(Res.font.lexend, FontWeight.ExtraBold),
    )

@Composable
fun getAppTypography(): Typography {
    val lexend = getLexendFontFamily()
    return Typography(
        displayLarge = lexendStyle(lexend, FontWeight.Bold, 56.sp, 64.sp, (-0.02).sp),
        displayMedium = lexendStyle(lexend, FontWeight.Bold, 45.sp, 52.sp),
        displaySmall = lexendStyle(lexend, FontWeight.Bold, 36.sp, 44.sp),
        headlineLarge = lexendStyle(lexend, FontWeight.Bold, 32.sp, 40.sp),
        headlineMedium = lexendStyle(lexend, FontWeight.Bold, 28.sp, 36.sp),
        titleLarge = lexendStyle(lexend, FontWeight.Bold, 22.sp, 28.sp),
        titleMedium = lexendStyle(lexend, FontWeight.Bold, 16.sp, 24.sp, 0.15.sp),
        bodyLarge = lexendStyle(lexend, FontWeight.Normal, 16.sp, 24.sp, 0.5.sp),
        bodyMedium = lexendStyle(lexend, FontWeight.Normal, 14.sp, 20.sp, 0.25.sp),
        labelLarge = lexendStyle(lexend, FontWeight.Bold, 14.sp, 20.sp, 0.1.sp),
        labelMedium = lexendStyle(lexend, FontWeight.Bold, 12.sp, 16.sp, 0.5.sp),
    )
}
