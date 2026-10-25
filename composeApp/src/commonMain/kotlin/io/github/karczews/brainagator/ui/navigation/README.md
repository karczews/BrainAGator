# Navigation Routes

## Overview
Type-safe navigation keys for the Navigation 3 back stack. `Routes.kt` holds only
declarations: the game identifier enum and the sealed route hierarchy that
`AppNavigation` (in `App.kt`, package root) maps to screens.

## Interface
- `@Serializable enum class GameType` - game identifiers, in order: `ShapeMatch`,
  `NumberOrder`, `ColorMatch`, `SizeOrder`, `Pattern`, `OddOneOut`, `SpotDifference`.
- `@Serializable sealed interface Route` - root of the route hierarchy:
  - `data object Route.GameSelection` - game selection (home) screen, no arguments.
  - `data class Route.Game(gameType: GameType)` - play the given game.
  - `data class Route.GameWon(gameType: GameType)` - win screen; carries `gameType` so
    "play again" knows which game to restart.

## Invariants
- Every route type and `GameType` is `@Serializable`; new routes must be too, and implement
  `Route`. The KDoc says this is for back stack persistence, but the current back stack
  (`AppNavigation` in `App.kt`) is a plain `remember`ed list that is never serialized.
- `Route` is sealed, so `when` over it is exhaustive; adding a subtype requires handling
  it wherever routes are mapped to screens.
- `Game` and `GameWon` hold a single immutable `gameType` with no default.
- Adding a `GameType` entry requires a matching game package and `GameInfo` under
  `ui/screens/games`, an entry in the `games` list (`ui/screens/GameSelectionScreen.kt`), and a
  branch in the `when` in `AppNavigation`. Without the `games` entry, `AppNavigation` throws.

## Side Effects
- None. Pure data declarations.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
