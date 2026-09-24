from rembg import remove
from pathlib import Path
import argparse
import sys
from io import BytesIO
from PIL import Image

def find_candidate_file(base_dir: Path, candidates):
    """Search for a candidate input file in several likely locations.

    Order of checks:
    - base_dir / candidate
    - base_dir / 'image_in' / candidate
    - base_dir.parent / candidate
    - base_dir.parent / 'image_in' / candidate
    - fallback: glob search for files with common extensions whose name contains a keyword
    """
    # direct checks
    locations = [
        base_dir,
        base_dir / "../image_in/image_in",
        base_dir.parent,
        base_dir.parent / "image_in",
    ]

    for loc in locations:
        if not loc.exists():
            continue
        for name in candidates:
            p = loc / name
            if p.exists():
                return p

    # Fallback: search for any file with common image_in extensions that matches a keyword
    keywords = ["image_in", "handbag", "photo", "pic"]
    exts = ["jpg", "jpeg", "png", "webp"]
    # search base_dir and base_dir.parent and their image_in subfolders
    search_dirs = [base_dir, base_dir / "image_in", base_dir.parent, base_dir.parent / "image_in"]
    seen = set()
    for d in search_dirs:
        if not d.exists():
            continue
        for ext in exts:
            for p in d.glob(f"**/*.{ext}"):
                name_lower = p.name.lower()
                if p in seen:
                    continue
                seen.add(p)
                if any(k in name_lower for k in keywords):
                    return p
    # If nothing matched keywords, return the first image_in file found
    for d in search_dirs:
        if not d.exists():
            continue
        for ext in exts:
            for p in d.glob(f"**/*.{ext}"):
                return p

    return None


def main():
    parser = argparse.ArgumentParser(description="Remove background from an input image using rembg")
    parser.add_argument("--location", required=True, help="Directory containing the input file.")
    parser.add_argument("--file", required=True, help="Input filename inside the location directory.")
    args = parser.parse_args()

    location_path = Path(args.location)
    if not location_path.is_absolute():
        location_path = location_path.resolve()

    input_path = location_path / args.file
    if not input_path.exists() or not input_path.is_file():
        print(f"Error: input file not found: {input_path}", file=sys.stderr)
        sys.exit(2)

    removed_dir = location_path / "output"
    removed_dir.mkdir(parents=True, exist_ok=True)
    output_path = removed_dir / f"{input_path.stem}.png"

    print(f"Loading input image_in: {input_path}")
    try:
        input_bytes = input_path.read_bytes()
    except Exception as e:
        print(f"Failed to read input file: {e}", file=sys.stderr)
        sys.exit(4)

    print("Removing background (this may take a few seconds)...")
    try:
        result_bytes = remove(input_bytes)
    except Exception as e:
        print(f"rembg.remove() failed: {e}", file=sys.stderr)
        sys.exit(5)

    try:
        # Force PNG with alpha channel so the background is transparent.
        result_image = Image.open(BytesIO(result_bytes)).convert("RGBA")
        result_image.save(output_path, format="PNG")
    except Exception as e:
        print(f"Failed to write output file: {e}", file=sys.stderr)
        sys.exit(6)

    print("Success")


if __name__ == '__main__':
    main()
