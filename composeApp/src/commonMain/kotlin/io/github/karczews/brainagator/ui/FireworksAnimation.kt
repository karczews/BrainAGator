package io.github.karczews.brainagator.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Fireworks explosion animation with multiple particles.
 *
 * @param modifier Modifier for the composable
 * @param particleCount Number of particles per explosion
 * @param explosionCount Number of explosions to trigger (0 for infinite)
 * @param colors List of colors for the fireworks
 * @param onAnimationComplete Callback when all explosions complete (only called if explosionCount > 0)
 */
@Composable
fun FireworksAnimation(
    modifier: Modifier = Modifier,
    particleCount: Int = 120,
    explosionCount: Int = 0,
    colors: List<Color> = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Cyan
        Color(0xFFFFE66D), // Yellow
        Color(0xFF95E1D3), // Mint
        Color(0xFFF38181), // Pink
        Color(0xFFAA96DA), // Purple
        Color(0xFFFCBAD3), // Light Pink
        Color(0xFFFFD93D), // Gold
    ),
    onAnimationComplete: (() -> Unit)? = null
) {
    val explosions = remember { mutableStateListOf<Explosion>() }
    var explosionIndex by remember { mutableIntStateOf(0) }
    val infiniteExplosions = explosionCount == 0

    // Trigger explosions
    LaunchedEffect(explosionCount) {
        while (infiniteExplosions || explosionIndex < explosionCount) {
            delay(Random.nextLong(300, 800))
            val centerX = Random.nextFloat() * 0.6f + 0.2f // 20% to 80% of width
            val centerY = Random.nextFloat() * 0.5f + 0.1f // 10% to 60% of height
            val color = colors.random()
            explosions.add(
                Explosion(
                    center = Offset(centerX, centerY),
                    color = color,
                    particleCount = particleCount
                )
            )
            explosionIndex++
        }
        // Wait for all explosions to finish before calling complete
        if (!infiniteExplosions) {
            delay(2000)
            onAnimationComplete?.invoke()
        }
    }

    // Clean up finished explosions
    LaunchedEffect(explosions.size) {
        if (explosions.isNotEmpty()) {
            delay(2000)
            explosions.removeAll { it.isFinished }
        }
    }

    Box(modifier = modifier) {
        explosions.forEach { explosion ->
            ExplosionParticles(
                explosion = explosion,
                onFinished = { explosion.isFinished = true }
            )
        }
    }
}

private data class Explosion(
    val center: Offset,
    val color: Color,
    val particleCount: Int,
    var isFinished: Boolean = false
)

@Composable
private fun ExplosionParticles(
    explosion: Explosion,
    onFinished: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fireworks")
    
    // Animation progress from 0 to 1
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutQuart),
        ),
        label = "progress"
    )

    println("progress: $progress")

    // Generate particles with random velocities
    val particles = remember(explosion) {
        List(explosion.particleCount) {
            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val velocity = Random.nextFloat() * 0.3f + 0.1f
            Particle(
                angle = angle,
                velocity = velocity,
                size = Random.nextFloat() * 4f + 2f
            )
        }
    }

    // Mark as finished after one cycle
    LaunchedEffect(progress) {
        if (progress > 0.95f) {
            delay(100)
            onFinished()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = explosion.center.x * size.width
        val centerY = explosion.center.y * size.height

        particles.forEach { particle ->
            drawParticle(
                centerX = centerX,
                centerY = centerY,
                particle = particle,
                progress = progress,
                color = explosion.color
            )
        }
    }
}

private data class Particle(
    val angle: Float,
    val velocity: Float,
    val size: Float
)

private fun DrawScope.drawParticle(
    centerX: Float,
    centerY: Float,
    particle: Particle,
    progress: Float,
    color: Color
) {
    // Calculate position with gravity effect
    val distance = particle.velocity * progress * size.minDimension * 0.5f
    val x = centerX + cos(particle.angle) * distance
    val y = centerY + sin(particle.angle) * distance + progress * progress * 100f // Gravity

    // Fade out and shrink
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val currentSize = particle.size * (1f - progress * 0.5f)

    // Draw particle
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = currentSize,
        center = Offset(x, y)
    )

    // Draw trail for larger particles
    if (particle.size > 4f && progress > 0.1f) {
        val trailProgress = (progress - 0.1f).coerceIn(0f, 1f)
        val trailDistance = particle.velocity * trailProgress * size.minDimension * 0.5f
        val trailX = centerX + cos(particle.angle) * trailDistance
        val trailY = centerY + sin(particle.angle) * trailDistance + trailProgress * trailProgress * 100f

        drawLine(
            color = color.copy(alpha = alpha * 0.5f),
            start = Offset(x, y),
            end = Offset(trailX, trailY),
            strokeWidth = currentSize * 0.5f
        )
    }
}

// Custom easing for more explosive feel
private val EaseOutQuart: Easing = Easing { fraction ->
    1f - (1f - fraction) * (1f - fraction) * (1f - fraction) * (1f - fraction)
}