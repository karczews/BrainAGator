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

/**
 * JVM/Desktop implementation of Platform.
 */
actual class Platform actual constructor() {
    actual val name: String = "Java ${System.getProperty("java.version")}"
}

/**
 * Desktop runs are the dev loop, so this stays true (system properties do not propagate from the IDE).
 * Packaged native distributions inherit this value.
 */
actual val isDebugBuild: Boolean = true
