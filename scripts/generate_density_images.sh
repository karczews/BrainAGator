#!/bin/bash

# Android Density Image Generator
# Generates mdpi, hdpi, xhdpi, and xxhdpi versions of a PNG image
#
# Usage: ./generate_density_images.sh <image_path> <mdpi_size>
# Example: ./generate_density_images.sh designs/image_prototypes/gator_brain_1.png 48

set -e

# Check if ffmpeg is available
if ! command -v ffmpeg &> /dev/null; then
    echo "Error: ffmpeg is not installed. Please install ffmpeg to use this script."
    echo "  macOS: brew install ffmpeg"
    echo "  Ubuntu/Debian: sudo apt-get install ffmpeg"
    exit 1
fi

# Check if bc is available
if ! command -v bc &> /dev/null; then
    echo "Error: bc is not installed. Please install bc to use this script."
    echo "  macOS: brew install bc"
    echo "  Ubuntu/Debian: sudo apt-get install bc"
    exit 1
fi

# Check arguments
if [ $# -lt 2 ]; then
    echo "Usage: $0 <image_path> <mdpi_size>"
    echo ""
    echo "Arguments:"
    echo "  image_path    Path to the source PNG image (e.g., designs/image_prototypes/gator_brain_1.png)"
    echo "  mdpi_size     Maximum dimension in pixels for mdpi (e.g., 48). Aspect ratio preserved."
    echo ""
    echo "Example:"
    echo "  $0 designs/image_prototypes/gator_brain_1.png 48"
    exit 1
fi

INPUT_IMAGE="$1"
MDPI_SIZE="$2"

# Validate input image
if [ ! -f "$INPUT_IMAGE" ]; then
    echo "Error: Input image not found: $INPUT_IMAGE"
    exit 1
fi

# Check if it's a PNG file (case-insensitive)
if ! echo "$INPUT_IMAGE" | grep -qi "\.png$"; then
    echo "Error: Input file must be a PNG image. Got: $INPUT_IMAGE"
    exit 1
fi

# Validate mdpi_size is a positive integer
if ! [[ "$MDPI_SIZE" =~ ^[0-9]+$ ]] || [ "$MDPI_SIZE" -le 0 ]; then
    echo "Error: mdpi_size must be a positive integer. Got: $MDPI_SIZE"
    exit 1
fi

# Calculate sizes for each density (max dimension)
MDPI_MAX=$((MDPI_SIZE))
HDPI_MAX=$(echo "scale=0; $MDPI_MAX * 1.5 / 1" | bc)
XHDPI_MAX=$((MDPI_MAX * 2))
XXHDPI_MAX=$((MDPI_MAX * 3))

# Extract filename
FILENAME=$(basename "$INPUT_IMAGE")

# Get project root (assuming script is in scripts/ directory)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Define directories
TEMP_DIR="/tmp/density_gen_$(date +%s)"
RESOURCE_DIR="$PROJECT_ROOT/composeApp/src/commonMain/composeResources"

# Cleanup temp directory on exit (success or failure)
cleanup() {
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
    fi
}
trap cleanup EXIT

MDPI_DIR="$RESOURCE_DIR/drawable-mdpi"
HDPI_DIR="$RESOURCE_DIR/drawable-hdpi"
XHDPI_DIR="$RESOURCE_DIR/drawable-xhdpi"
XXHDPI_DIR="$RESOURCE_DIR/drawable-xxhdpi"

# Create temp directory
mkdir -p "$TEMP_DIR"

# Create resource directories if they don't exist
mkdir -p "$MDPI_DIR" "$HDPI_DIR" "$XHDPI_DIR" "$XXHDPI_DIR"

echo "Generating Android density images..."
echo "Source: $INPUT_IMAGE"
echo "Filename: $FILENAME"
echo ""
echo "Target max dimensions (aspect ratio preserved):"
echo "  mdpi:   ${MDPI_MAX}px"
echo "  hdpi:   ${HDPI_MAX}px"
echo "  xhdpi:  ${XHDPI_MAX}px"
echo "  xxhdpi: ${XXHDPI_MAX}px"
echo ""

# Generate mdpi version (preserves aspect ratio, fits within max dimension)
ffmpeg -y -loglevel warning -i "$INPUT_IMAGE" -vf "scale=${MDPI_MAX}:${MDPI_MAX}:force_original_aspect_ratio=decrease:flags=lanczos" "$TEMP_DIR/${FILENAME}"

echo "Created: mdpi (max ${MDPI_MAX}px)"

# Generate hdpi version (preserves aspect ratio, fits within max dimension)
ffmpeg -y -loglevel warning -i "$INPUT_IMAGE" -vf "scale=${HDPI_MAX}:${HDPI_MAX}:force_original_aspect_ratio=decrease:flags=lanczos" "$TEMP_DIR/hdpi_${FILENAME}"

echo "Created: hdpi (max ${HDPI_MAX}px)"

# Generate xhdpi version (preserves aspect ratio, fits within max dimension)
ffmpeg -y -loglevel warning -i "$INPUT_IMAGE" -vf "scale=${XHDPI_MAX}:${XHDPI_MAX}:force_original_aspect_ratio=decrease:flags=lanczos" "$TEMP_DIR/xhdpi_${FILENAME}"

echo "Created: xhdpi (max ${XHDPI_MAX}px)"

# Generate xxhdpi version (preserves aspect ratio, fits within max dimension)
ffmpeg -y -loglevel warning -i "$INPUT_IMAGE" -vf "scale=${XXHDPI_MAX}:${XXHDPI_MAX}:force_original_aspect_ratio=decrease:flags=lanczos" "$TEMP_DIR/xxhdpi_${FILENAME}"

echo "Created: xxhdpi (max ${XXHDPI_MAX}px)"
echo ""

# Copy files to destination directories
cp "$TEMP_DIR/${FILENAME}" "$MDPI_DIR/${FILENAME}"
echo "Copied to: drawable-mdpi/${FILENAME}"

cp "$TEMP_DIR/hdpi_${FILENAME}" "$HDPI_DIR/${FILENAME}"
echo "Copied to: drawable-hdpi/${FILENAME}"

cp "$TEMP_DIR/xhdpi_${FILENAME}" "$XHDPI_DIR/${FILENAME}"
echo "Copied to: drawable-xhdpi/${FILENAME}"

cp "$TEMP_DIR/xxhdpi_${FILENAME}" "$XXHDPI_DIR/${FILENAME}"
echo "Copied to: drawable-xxhdpi/${FILENAME}"

# Cleanup handled by trap

echo ""
echo "Done! Images copied to composeResources drawable folders."
echo ""
echo "Output locations:"
echo "  - $MDPI_DIR/$FILENAME"
echo "  - $HDPI_DIR/$FILENAME"
echo "  - $XHDPI_DIR/$FILENAME"
echo "  - $XXHDPI_DIR/$FILENAME"
