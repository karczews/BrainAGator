# scripts

## Overview

Developer utility scripts that you run by hand. Nothing in the build, CI or Gradle files calls
them. There is one script at present. It takes a source PNG, generates Android density
variants (mdpi, hdpi, xhdpi, xxhdpi), and copies them into the shared Compose Multiplatform
resources at `composeApp/src/commonMain/composeResources/`.

## Interface

- `generate_density_images.sh <image_path> <mdpi_size>` (bash, `set -e`)
  - `image_path`: the source PNG, relative to the current working directory.
  - `mdpi_size`: the largest dimension in pixels at mdpi. The aspect ratio is kept.
  - It has no flags, and arguments after the second are ignored.
  - Example: `scripts/generate_density_images.sh designs/image_prototypes/gator_brain_1.png 48`
  - Calls `ffmpeg` to resize (Lanczos, `force_original_aspect_ratio=decrease`) and `bc` to work
    out the hdpi size.
  - Defines `cleanup()`, registered with `trap cleanup EXIT`.
  - Exits with status 1 and prints a message if any of these is true:
    - `ffmpeg` or `bc` is missing.
    - There are fewer than 2 arguments. It prints usage.
    - The input file does not exist.
    - The path does not end in `.png`, checked case-insensitively.
    - `mdpi_size` is not a positive integer.

## Invariants

- `ffmpeg` and `bc` must be on `PATH`.
- The input must be an existing file whose name ends in `.png`/`.PNG`. Only the name is
  checked, not the contents.
- Output sizes, as the largest dimension for an input `N`:

  | Density | Size |
  | ------- | ---- |
  | mdpi | `N` |
  | hdpi | `floor(N * 1.5)` |
  | xhdpi | `N * 2` |
  | xxhdpi | `N * 3` |

  No xxxhdpi or plain `drawable/` output is produced. Images smaller than the box are upscaled.
- The project root is the parent of the script's own directory, so the script must stay in
  `<project>/scripts/`.
- Every output keeps the source file's base name, with no density suffix.

## Side Effects

- Creates `/tmp/density_gen_<epoch-seconds>/` for intermediate files. The `EXIT` trap deletes it.
- Creates `composeApp/src/commonMain/composeResources/drawable-{mdpi,hdpi,xhdpi,xxhdpi}/` if
  any are missing.
- Writes `<filename>.png` into each of those four directories. An existing file with the same
  name is overwritten without a prompt.
- Never modifies the source image. No network access.
- Prints progress and the output paths to stdout. Error messages also go to stdout.

## Verification

- No automated tests. To check by hand, run from the repo root:
  1. Copy any real PNG to a throwaway name, for example
     `cp <some.png> /tmp/density_check.png`. The images under `designs/image_prototypes/` are
     Git LFS objects. Run `git lfs pull` first if you want to use one of them.
  2. Run `scripts/generate_density_images.sh /tmp/density_check.png 48`. The script exits with
     status 0.
  3. Confirm that `density_check.png` now exists in
     `composeApp/src/commonMain/composeResources/drawable-{mdpi,hdpi,xhdpi,xxhdpi}/`.
  4. Confirm the largest dimensions are 48, 72, 96 and 144 px. For example, run
     `sips -g pixelWidth -g pixelHeight <file>` on macOS.
  5. Confirm that `/tmp/density_gen_*` was removed.
  6. Delete the four `density_check.png` outputs.
