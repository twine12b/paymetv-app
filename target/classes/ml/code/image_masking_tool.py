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
        "--location", required=True,
        help="Directory containing the input file.",
    )
    parser.add_argument(
        "--file", required=True,
        help="Input filename inside the location directory.",
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

    # --- resolve input ---
    location_path = Path(args.location)
    if not location_path.is_absolute():
        location_path = location_path.resolve()

    input_path = location_path / args.file
    if not input_path.exists() or not input_path.is_file():
        print(f"Error: input file not found: {input_path}", file=sys.stderr)
        sys.exit(2)

    # --- ensure output directory exists ---
    masks_out_dir = location_path / "masks"
    try:
        masks_out_dir.mkdir(parents=True, exist_ok=True)
    except OSError as e:
        print(f"Error: could not create output directory '{masks_out_dir}': {e}", file=sys.stderr)
        sys.exit(4)

    mask_output_path = masks_out_dir / f"{input_path.stem}.png"

    # --- load image (preserve alpha channel when present) ---
    print(f"Loading input image: {input_path}")
    image = cv2.imread(str(input_path), cv2.IMREAD_UNCHANGED)
    if image is None:
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

    # --- select non-transparent pixels ---
    if image.ndim == 3 and image.shape[2] == 4:
        alpha_channel = image[:, :, 3]
        mask = (alpha_channel > 0).astype(np.uint8) * 255
        selected_alpha = alpha_channel
    else:
        mask = np.full(image.shape[:2], 255, dtype=np.uint8)
        selected_alpha = np.full(image.shape[:2], 255, dtype=np.uint8)

    # --- build RGBA output: green for selected pixels, transparent background ---
    colour_mask = np.zeros((image.shape[0], image.shape[1], 4), dtype=np.uint8)
    colour_mask[:, :, 1] = np.where(mask > 0, 255, 0).astype(np.uint8)
    colour_mask[:, :, 3] = np.where(mask > 0, selected_alpha, 0).astype(np.uint8)

    # --- save single-colour output ---
    try:
        if not cv2.imwrite(str(mask_output_path), colour_mask):
            raise IOError("cv2.imwrite returned False (check path and codec support)")
    except Exception as e:
        print(f"Error: failed to write single-colour output to '{mask_output_path}': {e}", file=sys.stderr)
        sys.exit(6)
    print("Successful")


if __name__ == "__main__":
    main()
