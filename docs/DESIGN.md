# Design System Strategy: The Pixel-Plush Editorial

## 1. Overview & Creative North Star
This design system translates the raw, high-contrast energy of classic 8-bit adventures into a sophisticated, premium digital experience. The Creative North Star is **"Retro-Futurist Play."** 

We are not merely copying pixel art; we are elevating it. By pairing the sharp, aggressive corners of the 8-bit era (0px roundedness) with the soft, friendly geometry of Lexend typography, we create a visual tension that feels both nostalgic and avant-garde. The layout philosophy breaks the "standard grid" by using intentional asymmetry—think of elements as floating "power-up" blocks or "pipes" emerging from the ground—creating an editorial flow that rewards exploration.

## 2. Colors
Our palette is a high-fidelity tribute to iconic gaming landscapes, utilizing deep earthy tones and vibrant action hues to guide the user's eye.

*   **Primary (`#bb000a`):** The "Brick Red." Reserved for critical actions and brand-heavy moments.
*   **Secondary (`#006b1c`):** The "Pipe Green." Used for success states, secondary navigation, and growth-oriented components.
*   **Surface Hierarchy:** We utilize a "Subterranean Depth" model.
    *   **Background (`#fff5ec`):** The canvas, a warm parchment that prevents the high-contrast colors from feeling sterile.
    *   **Surface-Container-Low (`#ffeedc`) to Highest (`#ffd6a1`):** These define our "floating platforms." 
*   **The "No-Line" Rule:** Sectioning must never be achieved with 1px lines. Use the transition from `surface` to `surface-container-low` to define content boundaries.
*   **Glass & Gradient Rule:** For "Overworld" navigation or floating HUD elements, use a semi-transparent `surface-container-low` with a 20px backdrop blur. Apply a subtle linear gradient from `primary` to `primary-container` on hero CTAs to give them a "glowing" 16-bit sprite effect.

## 3. Typography
We use **Lexend** exclusively. Its hyper-legible, rounded forms act as the necessary counterweight to our sharp-edged UI containers.

*   **Display (Lg/Md/Sm):** These are our "Boss Battle" moments. Use `display-lg` (3.5rem) with tight letter-spacing (-0.02em) for hero headlines.
*   **Headline & Title:** Use these to label "World Zones." The contrast between the bold `headline-lg` and the minimal `body-md` creates an authoritative, editorial feel.
*   **Body (Lg/Md/Sm):** Set in `on-surface` (`#422a00`). The earthy brown text on the warm background ensures maximum readability without the harshness of pure black.
*   **Labels:** Use `label-md` in uppercase for a "HUD" feel, especially in buttons and navigation headers.

## 4. Elevation & Depth
In this design system, we reject the rounded "soft UI" trend. Depth is achieved through **Tonal Layering** and **Pixel Shadows.**

*   **The Layering Principle:** Avoid traditional shadows where possible. Instead, "stack" your blocks. A `surface-container-highest` card sitting on a `surface` background provides all the hierarchy needed.
*   **Ambient Shadows:** When an element must "jump" (like a modal or floating action button), use an extra-diffused shadow: `0 20px 40px rgba(66, 42, 0, 0.08)`. The use of the `on-surface` brown tint in the shadow keeps the light feeling natural.
*   **The "Ghost Border" Fallback:** If a container needs more definition, use a 2px "Ghost Border" using `outline-variant` (`#d7a459`) at 15% opacity. 
*   **Hard Edges:** Every container, button, and input must maintain a **0px border-radius**. This is non-negotiable; the "roundedness" comes from the typography, while the "structure" remains classic and rigid.

## 5. Components

### Buttons
*   **Primary:** Background `primary`, text `on-primary`. 0px radius. Use a 4px bottom "inset" shadow of `primary-dim` to simulate a tactile, pressable block.
*   **Secondary:** Background `secondary`, text `on-secondary`.
*   **Tertiary:** No background. Use `label-md` styling with a `primary` color.

### Input Fields
*   **Style:** Flat `surface-container-highest` background with a 2px bottom border of `outline`.
*   **States:** On focus, the bottom border shifts to `primary`. Errors use `error` (`#b41340`) for both text and border.

### Chips
*   **Action Chips:** Small rectangular blocks using `surface-container-high`. No rounding. Label set in `label-sm`.

### Cards & Lists
*   **Rule:** No dividers. Use `8` (2rem) spacing from the scale to separate list items.
*   **Cards:** Use `surface-container-low` with a 0px radius. For "Featured" content, use a background shift to `surface-container-highest` or a subtle `primary-container` tint.

### Navigation (The HUD)
*   **The HUD Bar:** A top-screen floating bar using glassmorphism (surface color @ 80% opacity + blur). 

## 6. Do's and Don'ts

### Do
*   **Do** use asymmetrical layouts. Place a large `display-lg` heading on the left and offset the body text to the right to mimic a level's "starting line."
*   **Do** embrace the earthy brown (`on-surface`) for all text. It is softer and more premium than black.
*   **Do** use large blocks of `secondary` (Pipe Green) to highlight "safe" zones or successful user journeys.

### Don't
*   **Don't** ever use a border-radius. Even a 2px radius destroys the "8-bit" editorial aesthetic.
*   **Don't** use 1px grey borders. If a section needs a break, use a `surface` color shift or a large vertical gap from the spacing scale (e.g., `12` or `16`).
*   **Don't** clutter the screen. Classic games used negative space (the "sky") to let the hero breathe; do the same for your content.