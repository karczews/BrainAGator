package io.github.karczews.brainagator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform