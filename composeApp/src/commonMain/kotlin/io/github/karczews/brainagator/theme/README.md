# Theme and Design Tokens

## Overview
The app's Material3 theme and design tokens: color palette, extended color roles,
spacing/size scale, Lexend typography, and shapes. Screens and elements read these
tokens instead of hardcoding values. Design rules live in `docs/DESIGN.md`.

## Interface
- `@Composable AppTheme(content: @Composable () -> Unit)` - wraps content in
  `MaterialTheme` (color scheme, typography, shapes) and provides `LocalExtendedColors`.
- `object AppTheme { val extendedColors: ExtendedColors }` - `@Composable` accessor for
  extended color roles.
- `@Immutable data class ExtendedColors(onAccent, success, onSuccess, reward, onReward: Color)` -
  game color roles with no Material3 slot.
- `internal val LightExtendedColors`, `internal val LocalExtendedColors`
  (`staticCompositionLocalOf`, defaults to `LightExtendedColors`).
- `Color.kt` - public `Color` constants: primary `GatorGreen`/`LeafMint`/`DeepLeaf`,
  secondary `SunnyYellow`/`ButterCream`/`HoneyBrown`, tertiary `SkyBlue`/`SkyMist`/`DeepSky`,
  error `CoralRed`/`CoralMist`/`DeepCoral`, neutrals (`Ink`, `InkSoft`, `Cream`, `White`,
  `Oat`, `Biscuit`, `Sand`, `Pebble`, `Black`), logo (`LogoGrape`, `LogoBerry`), and one
  accent per game (`GameGrape`, `GameOcean`, `GameBerry`, `GameTangerine`, `GameTeal`,
  `GameIndigo`, `GamePlum`).
- `fun Color.darken(fraction: Float)` / `fun Color.lighten(fraction: Float)` - lerp toward
  `Black` / `White`.
- `object Dimens` - spacing scale (`spaceXs`..`spaceXl`), `screenPadding`, `lipHeight`,
  `touchTarget`, icon-button, tile (including `numberTileMaxSize`), badge, mascot,
  speaking-gator and speech-bubble, pager, win-screen action, and layout min/max-width tokens.
- `@Composable getLexendFontFamily(): FontFamily` and `@Composable getAppTypography(): Typography`.
- `object AutoSizeMin` - minimum auto-size text sizes: `display` 24sp, `title` 16sp,
  `label` 14sp, `caption` 10sp.

## Invariants
- Light-only: `AppTheme` always uses one `lightColorScheme` (no dark branch) and provides
  `LightExtendedColors`.
- Reading `AppTheme.extendedColors` outside `AppTheme` returns the light defaults (static
  local with a default value).
- User-visible colors should flow through `MaterialTheme.colorScheme` or
  `AppTheme.extendedColors`; new custom roles belong in `ExtendedColors`.
- Spacing and sizes come from `Dimens`; values are hardcoded dp literals.
- All 15 typography styles use Lexend (one variable-font resource registered at Normal,
  SemiBold, Bold and ExtraBold).
- Shapes: rounded corners 8/12/20/28/36 dp for extraSmall..extraLarge.
- `getAppTypography` / `getLexendFontFamily` are `@Composable` and rebuild on each call.

## Side Effects
- `AppTheme` provides composition locals (`MaterialTheme`, `LocalExtendedColors`) to
  its subtree.
- `getLexendFontFamily` loads the `Res.font.lexend` Compose resource.

## Verification
- Run: `./gradlew :composeApp:jvmTest --no-configuration-cache`
