package io.github.karczews.brainagator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import brainagator.composeapp.generated.resources.Res
import brainagator.composeapp.generated.resources.compose_multiplatform
import io.github.karczews.brainagator.ui.FireworksAnimation

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var showFireworks by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fireworks demo button
            Button(
                onClick = { showFireworks = !showFireworks },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(if (showFireworks) "Stop Fireworks" else "Show Fireworks!")
            }

            // Fireworks animation
            AnimatedVisibility(visible = showFireworks) {
                FireworksAnimation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp),
                    particleCount = 150,
                    explosionCount = 0 // Infinite explosions
                )
            }
        }
    }
}