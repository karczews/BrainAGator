package io.github.karczews.brainagator.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe game identifiers
 */
@Serializable
enum class GameType {
    ShapeMatch,
    NumberOrder,
    ColorMatch,
    SizeOrder,
    Pattern,
    OddOneOut,
    SpotDifference
}

/**
 * Navigation routes for Navigation 3
 * All routes are serializable for back stack persistence
 */
@Serializable
sealed interface Route {
    @Serializable
    data object GameSelection : Route

    @Serializable
    data class Game(val gameType: GameType) : Route
}
