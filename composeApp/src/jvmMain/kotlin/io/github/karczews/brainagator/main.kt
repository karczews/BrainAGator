package io.github.karczews.brainagator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    // Platform-specific entry point for JVM/Desktop
    // Similar structure to webMain/main.kt is intentional for multiplatform consistency
    Window(
        onCloseRequest = ::exitApplication,
        title = "brainagator",
    ) {
        App()
    }
}