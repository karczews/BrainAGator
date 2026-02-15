package io.github.karczews.brainagator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.karczews.brainagator.tts.rememberTextToSpeech
import io.github.karczews.brainagator.ui.navigation.GameType

data class GameInfo(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val gameType: GameType,
)

val games =
    listOf(
        GameInfo(
            title = "Shape Match",
            subtitle = "Match shapes to colors!",
            icon = Icons.Default.Category,
            gradientColors = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)),
            gameType = GameType.ShapeMatch,
        ),
        GameInfo(
            title = "Number Order",
            subtitle = "Put numbers in order!",
            icon = Icons.Default.Tag,
            gradientColors = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)),
            gameType = GameType.NumberOrder,
        ),
        GameInfo(
            title = "Color Match",
            subtitle = "Find matching colors!",
            icon = Icons.Default.Palette,
            gradientColors = listOf(Color(0xFFFA709A), Color(0xFFFEE140)), // Pink to Orange
            gameType = GameType.ColorMatch,
        ),
        GameInfo(
            title = "Size Order",
            subtitle = "Order by size!",
            icon = Icons.Default.SwapVert,
            gradientColors = listOf(Color(0xFFF093FB), Color(0xFFF5576C)),
            gameType = GameType.SizeOrder,
        ),
        GameInfo(
            title = "Pattern Game",
            subtitle = "Complete the pattern!",
            icon = Icons.Default.GridView,
            gradientColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
            gameType = GameType.Pattern,
        ),
        GameInfo(
            title = "Odd One Out",
            subtitle = "Find what's different!",
            icon = Icons.Default.HelpOutline,
            gradientColors = listOf(Color(0xFF2AF598), Color(0xFF009EFD)),
            gameType = GameType.OddOneOut,
        ),
        GameInfo(
            title = "Spot Difference",
            subtitle = "Find the difference!",
            icon = Icons.Default.Search,
            gradientColors = listOf(Color(0xFF43E97B), Color(0xFF38F9D7)),
            gameType = GameType.SpotDifference,
        ),
    )

@Composable
fun GameSelectionScreen(onGameSelected: (GameInfo) -> Unit = {}) {
    val tts = rememberTextToSpeech()

    // Speak welcome message when screen loads
    androidx.compose.runtime.LaunchedEffect(Unit) {
        tts.speak("Welcome to Brainagator! Select a game to play.")
    }
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color(0xFFFFFDE7), // Very Light Yellow
                                Color(0xFFFFF8E1), // Cream
                                Color(0xFFF8BBD0).copy(alpha = 0.3f), // Very Light Pink
                            ),
                    ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF9C27B0))) {
                                append("Brain")
                            }
                            withStyle(SpanStyle(color = Color(0xFFEC407A))) {
                                append("agator")
                            }
                        },
                    style =
                        MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                        ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐊", fontSize = 32.sp)
            }

            Text(
                text = "Let's Learn & Play!",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        color = Color.DarkGray.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                    ),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
            )

            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val chunkedGames = games.chunked(2)
                chunkedGames.forEach { rowGames ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (rowGames.size == 1) Arrangement.Center else Arrangement.spacedBy(
                            16.dp
                        ),
                    ) {
                        rowGames.forEach { game ->
                            Box(
                                modifier = if (rowGames.size == 1) Modifier.fillMaxWidth(0.5f) else Modifier.weight(
                                    1f
                                )
                            ) {
                                GameCard(
                                    game = game,
                                    onClick = {
                                        tts.speak("${game.title}. ${game.subtitle}")
                                        onGameSelected(game)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Balloon at the bottom
            Text(
                text = "🎈",
                fontSize = 48.sp,
                modifier =
                    Modifier
                        .padding(bottom = 32.dp)
                        .offset(y = 10.dp),
            )
        }
    }
}

@Composable
fun GameCard(
    game: GameInfo,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(game.gradientColors))
                    .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = game.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = game.subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}
