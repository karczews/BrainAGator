package io.github.karczews.brainagator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.karczews.brainagator.App
import io.github.karczews.brainagator.theme.BrainagatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrainagatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    App()
                }
            }
        }
    }
}


