package io.github.karczews.brainagator

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // ExperimentalComposeUiApi is required for ComposeViewport web support
    // This API is experimental but necessary for web platform integration
    ComposeViewport {
        App()
    }
}