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

import androidx.compose.runtime.compositionLocalOf

/**
 * Platform information with platform-specific implementation.
 */
expect class Platform() {
    val name: String
}

/**
 * Build-time flag indicating if this is a debug build.
 * Should be true in development/debug builds and false in release builds.
 */
expect val isDebugBuild: Boolean

/**
 * Whether debug affordances (coming-soon placeholders, test buttons, build labels) are enabled.
 *
 * Defaults to the platform [isDebugBuild] flag. KMP library modules have no build variants, so on
 * Android the app module (which does) overrides this at its entry point with its own build type.
 */
val LocalIsDebugBuild = compositionLocalOf { isDebugBuild }
