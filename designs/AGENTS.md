# Design Asset Guidelines

## Image Resizing

When resizing PNG images, use the following tools with these settings:

### Recommended Tools

**1. ffmpeg (preferred for best quality)**
```bash
ffmpeg -i input.png -vf "scale=WIDTH:HEIGHT:flags=lanczos" output.png
```

**2. ImageMagick (best quality, more control)**
```bash
convert input.png -resize WIDTHxHEIGHT -filter Lanczos output.png
```

**3. macOS sips (convenient, adequate quality)**
```bash
sips -z HEIGHT WIDTH input.png --out output.png
```

### Quality Notes

- Use **Lanczos** filter for best results when downsizing — preserves sharpness and fine details
- **sips** uses bicubic interpolation which is decent but softer than Lanczos
- For aggressive downscaling ratios, the difference between Lanczos and bicubic becomes more noticeable
- Prefer **ffmpeg** or **ImageMagick** when maximum quality matters
- **sips** is acceptable for quick one-off conversions or automation scripts
