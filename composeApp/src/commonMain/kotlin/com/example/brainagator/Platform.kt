package com.example.brainagator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform