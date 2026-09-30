"""Reduce an image to a maximum height while preserving its aspect ratio.

Usage: python image_resizer.py --location /path/to/images --file photo.jpg
The resized image is written beside the input with ``-resized`` appended.
"""

import argparse
from pathlib import Path

from PIL import Image, ImageFilter, ImageOps


# Set the desired output height here, in pixels.
TARGET_HEIGHT = 600
# Photoshop's Preserve Details 2.0 is an enlargement algorithm. For reduction,
# Photoshop recommends Bicubic Sharper; these settings approximate that workflow.
SHARPEN_RADIUS = 0.5
SHARPEN_PERCENT = 120
SHARPEN_THRESHOLD = 3
dpi_val = 4800


def resize_half_steps(image: Image.Image, target_size: tuple[int, int]) -> Image.Image:
    """Downscale in steps of no more than 2x per pass to reduce aliasing."""
    w, h = image.size
    tw, th = target_size
    if h <= th and w <= tw:
        return image

    # Pre-blur to suppress moiré when the scale factor is large
    scale = h / th
    if scale > 3:
        image = image.filter(ImageFilter.GaussianBlur(radius=scale * 0.4))

    # Halve in steps until within 2x of the target
    while h > th * 2 or w > tw * 2:
        h = max(th, h // 2)
        w = max(tw, w // 2)
        image = image.resize((w, h), Image.Resampling.LANCZOS)

    resized = image.resize((tw, th), Image.Resampling.BICUBIC)
    return resized.filter(
        ImageFilter.UnsharpMask(
            radius=SHARPEN_RADIUS,
            percent=SHARPEN_PERCENT,
            threshold=SHARPEN_THRESHOLD,
        )
    )


def main() -> None:
    parser = argparse.ArgumentParser(
        description=f"Reduce an image to {TARGET_HEIGHT} pixels high while preserving its aspect ratio."
    )
    parser.add_argument("--location", required=True, help="Directory containing the image.")
    parser.add_argument("--file", required=True, help="Image filename inside the directory.")
    args = parser.parse_args()

    image_path = Path(args.location).expanduser() / args.file
    if not image_path.is_file():
        parser.error(f"image file not found: {image_path}")

    try:
        with Image.open(image_path) as source:
            image = ImageOps.exif_transpose(source)
            width, height = image.size

            if height <= TARGET_HEIGHT:
                resized = image.copy()
                print(f"Image is already {height}px high; saving an unchanged-size copy.")
            else:
                new_width = max(1, round(width * TARGET_HEIGHT / height))
                resized = resize_half_steps(image, (new_width, TARGET_HEIGHT))

            output_path = image_path.with_name(f"{image_path.stem}-resized{image_path.suffix}")
            save_options = {}
            if image.format == "JPEG":
                save_options.update(quality=100, subsampling=0, optimize=True)
            elif image.format == "PNG":
                save_options.update(quality=100, optimize=True)
            elif image.format == "WEBP":
                save_options.update(quality=100, method=6)

            save_options["dpi"] = (dpi_val, dpi_val)
            resized.save(output_path, format=image.format, **save_options)

    except (OSError, ValueError) as error:
        parser.error(f"could not resize image: {error}")

    print(f"Saved resized image ({resized.width}x{resized.height}) to: {output_path}")


if __name__ == "__main__":
    main()
