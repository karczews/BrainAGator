package io.github.karczews.brainagator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.karczews.brainagator.ui.FireworksAnimation

import io.github.karczews.brainagator.ui.screens.GameSelectionScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var selectedGame by remember { mutableStateOf<String?>(null) }
        var showFireworks by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedGame == null) {
                GameSelectionScreen(onGameSelected = { game ->
                    selectedGame = game.title
                    showFireworks = true
                })
            } else {
                // Game Screen Placeholder
                Column(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Playing ${selectedGame}", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        selectedGame = null
                        showFireworks = false
                    }) {
                        Text("Back to Menu")
                    }
                }
            }

            // Global Fireworks animation
            AnimatedVisibility(
                visible = showFireworks,
                modifier = Modifier.fillMaxSize()
            ) {
                FireworksAnimation(
                    modifier = Modifier.fillMaxSize(),
                    particleCount = 150,
                    explosionCount = 0
                )
            }
        }
    }
}