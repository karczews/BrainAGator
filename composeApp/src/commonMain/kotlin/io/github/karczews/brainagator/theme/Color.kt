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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// --- "Gator Jungle" palette: bright, friendly hues that keep white text readable ---
// Colors carrying white text reach at least 5:1 on white (4.5:1 under the tactile highlight).

// Primary: Gator Green (the mascot)
val GatorGreen = Color(0xFF1F7E37)
val LeafMint = Color(0xFFD4F5D6)
val DeepLeaf = Color(0xFF0B4F21)

// Secondary: Sunny Yellow (stars and rewards; pair with dark text)
val SunnyYellow = Color(0xFFFFC233)
val ButterCream = Color(0xFFFFEDB8)
val HoneyBrown = Color(0xFF5C3D00)

// Tertiary: Sky Blue
val SkyBlue = Color(0xFF1770BF)
val SkyMist = Color(0xFFD8ECFF)
val DeepSky = Color(0xFF00325E)

// Error: soft coral instead of an alarming red
val CoralRed = Color(0xFFE04A3C)
val CoralMist = Color(0xFFFFDDD8)
val DeepCoral = Color(0xFF5F0F08)

// Neutrals: warm cream canvas, plum ink instead of pure black
val Ink = Color(0xFF2B2541)
val InkSoft = Color(0xFF5E5873)
val Cream = Color(0xFFFFF8EC)
val White = Color(0xFFFFFFFF)
val Oat = Color(0xFFFFF1DC)
val Biscuit = Color(0xFFFCE6C6)
val Sand = Color(0xFFEADFCB)
val Pebble = Color(0xFF9E9073)
val Black = Color(0xFF000000)

// Logo
val LogoGrape = Color(0xFF7A52E8)
val LogoBerry = Color(0xFFD9407E)

// Game accents: one identity color per game, used for its tile, top bar and highlights.
// Kept clear of the feedback colors (error coral, success green, reward yellow), and each one a different
// color word a child knows (purple, blue, pink, orange, teal, brown, fuchsia) so no two games look alike.
val GameGrape = Color(0xFF7A52E8)
val GameOcean = Color(0xFF1770BF)
val GameBerry = Color(0xFFCF296C)
val GameTangerine = Color(0xFFB74F0C)
val GameTeal = Color(0xFF0C7C75)
val GameCocoa = Color(0xFF7A4A2A)
val GameFuchsia = Color(0xFFB0109F)

/** Returns this color mixed with black; used for tactile "lips" and readable accent text. */
fun Color.darken(fraction: Float): Color = lerp(this, Black, fraction)

/** Returns this color mixed with white; used for soft tints and highlights. */
fun Color.lighten(fraction: Float): Color = lerp(this, White, fraction)
