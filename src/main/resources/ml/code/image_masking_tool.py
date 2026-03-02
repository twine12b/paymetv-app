"""
image_masking_tool.py
---------------------
Create an HSV colour-range mask for image segmentation / manipulation tasks.

Usage (defaults – auto-discovers the first image in image_in/):
    python image_masking_tool.py

Specify your own HSV range to isolate a particular colour:
    python image_masking_tool.py --lower-hsv 0 120 70 --upper-hsv 10 255 255

Fully explicit:
    python image_masking_tool.py -i ../image_in/handbag_image1.jpeg \
                                  -o handbag_masked.png \
                                  -m handbag_mask.png \
                                  --hsv-output handbag_masked_hsv.png \
                                  --lower-hsv 0 0 0 --upper-hsv 179 255 255
"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

import cv2
import matplotlib.pyplot as plt
import numpy as np


# ---------------------------------------------------------------------------
# File-discovery helper (mirrors background_removal_tool.py)
# ---------------------------------------------------------------------------

def find_candidate_file(base_dir: Path, candidates: list) -> Path | None:
    """Search for a candidate input file across several likely locations.

    Search order:
      1. base_dir itself
      2. base_dir/../image_in  (sibling image_in/ of the code/ folder)
      3. base_dir.parent       (ml/ root)
      4. base_dir.parent/image_in
    Falls back to a glob search for common image extensions; returns the first
    file whose name contains a recognisable keyword, or the very first image
    file found if no keyword matches.
    """
    locations = [
        base_dir,
        base_dir / "../image_in",
        base_dir.parent,
        base_dir.parent / "image_in",
    ]

    # --- direct name checks ---
    for loc in locations:
        if not loc.exists():
            continue
        for name in candidates:
            p = loc / name
            if p.exists():
                return p

    # --- keyword-based glob fallback ---
    keywords = ["image", "handbag", "photo", "pic"]
    exts = ["jpg", "jpeg", "png", "webp"]
    search_dirs = [
        base_dir,
        base_dir / "image_in",
        base_dir.parent,
        base_dir.parent / "image_in",
    ]
    seen: set[Path] = set()
    for d in search_dirs:
        if not d.exists():
            continue
        for ext in exts:
            for p in d.glob(f"**/*.{ext}"):
                if p in seen:
                    continue
                seen.add(p)
                if any(k in p.name.lower() for k in keywords):
                    return p

    # --- absolute last resort: first image file found ---
    for d in search_dirs:
        if not d.exists():
            continue
        for ext in exts:
            for p in d.glob(f"**/*.{ext}"):
                return p

    return None


# ---------------------------------------------------------------------------
# Core masking logic
# ---------------------------------------------------------------------------

def create_hsv_mask(
    bgr_image: np.ndarray,
    lower_hsv: np.ndarray,
    upper_hsv: np.ndarray,
) -> tuple[np.ndarray, np.ndarray, np.ndarray]:
    """Create a binary mask by thresholding an image in HSV colour space.

    Steps:
      1. Convert BGR → HSV so hue, saturation and value are independent axes.
      2. cv2.inRange() produces a binary mask: 255 where pixels fall inside
         [lower_hsv, upper_hsv], 0 elsewhere.
      3. MORPH_OPEN  (erode → dilate) removes small noise blobs at the edges.
      4. MORPH_CLOSE (dilate → erode) fills small holes inside the mask.
      5. cv2.bitwise_and() applies the mask to the original BGR image so only
         the selected region is kept; everything else becomes black (0).
      6. cv2.bitwise_and() applies the same mask to the HSV image, preserving
         the raw hue/saturation/value channels in the kept region.

    Args:
        bgr_image:  Input image in BGR format (as returned by cv2.imread).
        lower_hsv:  Lower HSV bound, shape (3,), dtype uint8.
        upper_hsv:  Upper HSV bound, shape (3,), dtype uint8.

    Returns:
        mask:         Binary mask  (uint8, values 0 or 255).
        masked_image: BGR image with the mask applied.
        masked_hsv:   HSV image with the same mask applied; useful for
                      inspecting raw hue/saturation/value within the
                      segmented region.
    """
    # --- Step 1: colour-space conversion ---
    hsv = cv2.cvtColor(bgr_image, cv2.COLOR_BGR2HSV)

    # --- Step 2: threshold on the specified HSV range ---
    mask = cv2.inRange(hsv, lower_hsv, upper_hsv)

    # --- Steps 3 & 4: morphological cleanup ---
    # An elliptical kernel gives smoother mask edges than a rectangular one.
    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
    # Open: removes isolated noise pixels smaller than the kernel
    mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel)
    # Close: fills small gaps / holes inside the masked region
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)

    # --- Step 5: apply the mask to the BGR image ---
    masked_image = cv2.bitwise_and(bgr_image, bgr_image, mask=mask)

    # --- Step 6: apply the same mask to the HSV image ---
    # Zeroed-out pixels outside the mask make it easy to inspect the raw
    # hue / saturation / value values of only the segmented region.
    masked_hsv = cv2.bitwise_and(hsv, hsv, mask=mask)

    return mask, masked_image, masked_hsv


# ---------------------------------------------------------------------------
# CLI entry-point
# ---------------------------------------------------------------------------

def main() -> None:
    parser = argparse.ArgumentParser(
        description="Create an HSV colour-range mask for image segmentation using OpenCV",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "--input", "-i",
        help="Input image path (optional). Auto-discovered if omitted.",
    )
    parser.add_argument(
        "--output", "-o",
        help="Output path for the masked image. Defaults to '<stem>_masked.png' in image_out/.",
    )
    parser.add_argument(
        "--mask-output", "-m",
        dest="mask_output",
        help="Output path for the binary mask. Defaults to '<stem>_mask.png' in image_out/.",
    )
    parser.add_argument(
        "--hsv-output",
        dest="hsv_output",
        help="Output path for the masked HSV image. Defaults to '<stem>_masked_hsv.png' in image_out/.",
    )
    parser.add_argument(
        "--lower-hsv", nargs=3, type=int, metavar=("H", "S", "V"),
        default=[0, 0, 0],
        help="Lower HSV bound (default: 0 0 0  → keeps the entire image).",
    )
    parser.add_argument(
        "--upper-hsv", nargs=3, type=int, metavar=("H", "S", "V"),
        default=[179, 255, 255],
        help="Upper HSV bound (default: 179 255 255 → keeps the entire image).",
    )
    args = parser.parse_args()

    base = Path(__file__).resolve().parent

    # --- resolve input ---
    if args.input:
        input_path = Path(args.input)
        if not input_path.is_absolute():
            input_path = base / input_path
        if not input_path.exists():
            print(f"Error: input file not found: {input_path}", file=sys.stderr)
            sys.exit(2)
    else:
        candidates = [
            "image1.jpg", "image1.jpeg", "image1.png", "image1.webp",
            "handbag_image_original.png",
            "handbag_image1.jpeg", "handbag_image1.jpg", "handbag_image1.png",
        ]
        input_path = find_candidate_file(base, candidates)
        if input_path is None:
            print(
                "Error: no input image found. Pass --input /path/to/image to specify one.",
                file=sys.stderr,
            )
            sys.exit(3)

    # --- ensure output directory exists ---
    images_out_dir = base.parent / "image_out"
    try:
        images_out_dir.mkdir(parents=True, exist_ok=True)
    except OSError as e:
        print(f"Error: could not create output directory '{images_out_dir}': {e}", file=sys.stderr)
        sys.exit(4)

    stem = input_path.stem

    # --- resolve masked-image output path ---
    if args.output:
        output_path = Path(args.output)
        if not output_path.is_absolute():
            output_path = images_out_dir / output_path
    else:
        output_path = images_out_dir / f"{stem}_masked.png"

    # --- resolve binary-mask output path ---
    if args.mask_output:
        mask_output_path = Path(args.mask_output)
        if not mask_output_path.is_absolute():
            mask_output_path = images_out_dir / mask_output_path
    else:
        mask_output_path = images_out_dir / f"{stem}_mask.png"

    # --- resolve masked HSV output path ---
    if args.hsv_output:
        hsv_output_path = Path(args.hsv_output)
        if not hsv_output_path.is_absolute():
            hsv_output_path = images_out_dir / hsv_output_path
    else:
        hsv_output_path = images_out_dir / f"{stem}_masked_hsv.png"

    # --- load image ---
    print(f"Loading input image: {input_path}")
    bgr = cv2.imread(str(input_path))
    if bgr is None:
        print(
            f"Error: cv2.imread could not load '{input_path}'. "
            "The file may be corrupted or in an unsupported format.",
            file=sys.stderr,
        )
        sys.exit(5)

    # --- build HSV bounds ---
    lower_hsv = np.array(args.lower_hsv, dtype=np.uint8)
    upper_hsv = np.array(args.upper_hsv, dtype=np.uint8)
    print(f"Masking HSV range: lower={lower_hsv.tolist()}, upper={upper_hsv.tolist()}")

    # --- run masking ---
    mask, masked_image, masked_hsv = create_hsv_mask(bgr, lower_hsv, upper_hsv)

    # --- save binary mask ---
    try:
        if not cv2.imwrite(str(mask_output_path), mask):
            raise IOError("cv2.imwrite returned False (check path and codec support)")
    except Exception as e:
        print(f"Error: failed to write mask to '{mask_output_path}': {e}", file=sys.stderr)
        sys.exit(6)
    print(f"Binary mask saved to:  {mask_output_path}")

    # --- save masked image ---
    try:
        if not cv2.imwrite(str(output_path), masked_image):
            raise IOError("cv2.imwrite returned False (check path and codec support)")
    except Exception as e:
        print(f"Error: failed to write masked image to '{output_path}': {e}", file=sys.stderr)
        sys.exit(7)
    print(f"Masked image saved to:     {output_path}")

    # --- save masked HSV image ---
    # The HSV channels are stored as-is (H: 0-179, S: 0-255, V: 0-255).
    # Pixels outside the mask are zeroed, so only the segmented region
    # retains its hue/saturation/value data.
    try:
        if not cv2.imwrite(str(hsv_output_path), masked_hsv):
            raise IOError("cv2.imwrite returned False (check path and codec support)")
    except Exception as e:
        print(f"Error: failed to write masked HSV image to '{hsv_output_path}': {e}", file=sys.stderr)
        sys.exit(8)
    print(f"Masked HSV image saved to: {hsv_output_path}")


if __name__ == "__main__":
    main()
