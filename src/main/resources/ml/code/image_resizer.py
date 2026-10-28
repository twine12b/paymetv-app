"""Reduce an image to a maximum height while preserving its aspect ratio.

Usage: python image_resizer.py --location /path/to/images --output-dir /path/to/output \
    --files photo1.jpg photo2.jpg
Resized images are written to the output directory with ``-resized`` appended.
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
    parser.add_argument("--location", required=True, help="Directory containing the input images.")
    parser.add_argument(
        "--file", "--files", dest="files", required=True, nargs="+",
        help="One or more image filenames inside the location directory.",
    )
    parser.add_argument(
        "--output-dir", "--output", dest="output_dir", required=True,
        help="Directory where resized images will be written.",
    )
    args = parser.parse_args()

    location_path = Path(args.location).expanduser()
    if not location_path.is_absolute():
        location_path = location_path.resolve()

    output_dir = Path(args.output_dir).expanduser()
    if not output_dir.is_absolute():
        output_dir = location_path / output_dir
    try:
        output_dir.mkdir(parents=True, exist_ok=True)
    except OSError as error:
        parser.error(f"could not create output directory {output_dir}: {error}")

    failures = 0
    for filename in args.files:
        image_path = Path(filename).expanduser()
        if not image_path.is_absolute():
            image_path = location_path / image_path
        if not image_path.is_file():
            print(f"Error: image file not found: {image_path}")
            failures += 1
            continue

        resized = None
        try:
            with Image.open(image_path) as source:
                image = ImageOps.exif_transpose(source)
                width, height = image.size

                if height <= TARGET_HEIGHT:
                    resized = image.copy()
                    print(f"{image_path.name}: already {height}px high; saving an unchanged-size copy.")
                else:
                    new_width = max(1, round(width * TARGET_HEIGHT / height))
                    resized = resize_half_steps(image, (new_width, TARGET_HEIGHT))

                output_path = output_dir / f"{image_path.stem}-resized{image_path.suffix}"
                image_format = image.format or source.format
            save_options = {}
            if image_format == "JPEG":
                save_options.update(quality=100, subsampling=0, optimize=True)
            elif image_format == "PNG":
                save_options.update(quality=100, optimize=True)
            elif image_format == "WEBP":
                save_options.update(quality=100, method=6)

            save_options["dpi"] = (dpi_val, dpi_val)
            resized.save(output_path, format=image_format, **save_options)
            print(f"Saved resized image ({resized.width}x{resized.height}) to: {output_path}")

        except (OSError, ValueError) as error:
            print(f"Error: could not resize {image_path}: {error}")
            failures += 1
        finally:
            if resized is not None:
                resized.close()

    if failures:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
