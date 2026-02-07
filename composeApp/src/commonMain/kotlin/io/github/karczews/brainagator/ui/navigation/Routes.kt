package io.github.karczews.brainagator.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for Navigation 3
 * All routes are serializable for back stack persistence
 */
@Serializable
sealed interface Route {
    @Serializable
    data object GameSelection : Route

    @Serializable
    data class Game(val gameTitle: String) : Route
}
