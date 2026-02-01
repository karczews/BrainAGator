package io.github.karczews.brainagator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

data class GameInfo(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradientColors: List<Color>
)

val games = listOf(
    GameInfo(
        "Shape Match",
        "Match shapes to colors!",
        Icons.Default.Category,
        listOf(Color(0xFFB06AB3), Color(0xFF4568DC))
    ),
    GameInfo(
        "Number Order",
        "Put numbers in order!",
        Icons.Default.Tag,
        listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
    ),
    GameInfo(
        "Color Match",
        "Find matching colors!",
        Icons.Default.Palette,
        listOf(Color(0xFFFA709A), Color(0xFFFEE140)) // Pink to Orange
    ),
    GameInfo(
        "Size Order",
        "Order by size!",
        Icons.Default.SwapVert,
        listOf(Color(0xFFF093FB), Color(0xFFF5576C))
    ),
    GameInfo(
        "Pattern Game",
        "Complete the pattern!",
        Icons.Default.GridView,
        listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    ),
    GameInfo(
        "Odd One Out",
        "Find what's different!",
        Icons.Default.HelpOutline,
        listOf(Color(0xFF2AF598), Color(0xFF009EFD))
    ),
    GameInfo(
        "Spot Difference",
        "Find the difference!",
        Icons.Default.Search,
        listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
    )
)

@Composable
fun GameSelectionScreen(onGameSelected: (GameInfo) -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7), // Very Light Yellow
                        Color(0xFFFFF8E1), // Cream
                        Color(0xFFF8BBD0).copy(alpha = 0.3f)  // Very Light Pink
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF9C27B0))) {
                            append("Brain")
                        }
                        withStyle(SpanStyle(color = Color(0xFFEC407A))) {
                            append("agator")
                        }
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐊", fontSize = 32.sp)
            }
            
            Text(
                text = "Let's Learn & Play!",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val chunkedGames = games.chunked(2)
                chunkedGames.forEach { rowGames ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (rowGames.size == 1) Arrangement.Center else Arrangement.spacedBy(16.dp)
                    ) {
                        rowGames.forEach { game ->
                            Box(modifier = if (rowGames.size == 1) Modifier.fillMaxWidth(0.5f) else Modifier.weight(1f)) {
                                GameCard(game = game, onClick = { onGameSelected(game) })
                            }
                        }
                    }
                }
            }
            
            // Balloon at the bottom
            Text(
                text = "🎈",
                fontSize = 48.sp,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .offset(y = 10.dp)
            )
        }
    }
}

@Composable
fun GameCard(game: GameInfo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(game.gradientColors))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = game.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = game.subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
