from rembg import new_session, remove
from pathlib import Path
import argparse
import sys
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
    parser = argparse.ArgumentParser(description="Remove backgrounds from images using rembg")
    parser.add_argument("--location", required=True, help="Directory containing the input file.")
    parser.add_argument(
        "--file", "--files", dest="files", required=True, nargs="+",
        help="One or more input filenames inside the location directory.",
    )
    parser.add_argument(
        "--output-dir", "--output", dest="output_dir", required=True,
        help="Directory where output PNG files will be written.",
    )
    args = parser.parse_args()

    location_path = Path(args.location)
    if not location_path.is_absolute():
        location_path = location_path.resolve()

    removed_dir = Path(args.output_dir)
    if not removed_dir.is_absolute():
        removed_dir = location_path / removed_dir
    try:
        removed_dir.mkdir(parents=True, exist_ok=True)
    except Exception as e:
        print(f"Failed to create output directory {removed_dir}: {e}", file=sys.stderr)
        sys.exit(3)

    try:
        # Load the model once and reuse it for every image in this run.
        session = new_session("u2netp")
    except Exception as e:
        print(f"Failed to initialize rembg u2netp model: {e}", file=sys.stderr)
        sys.exit(5)

    failures = 0
    for filename in args.files:
        input_path = Path(filename)
        if not input_path.is_absolute():
            input_path = location_path / input_path
        if not input_path.is_file():
            print(f"Error: input file not found: {input_path}", file=sys.stderr)
            failures += 1
            continue

        output_path = removed_dir / f"{input_path.stem}.png"
        input_image = None
        result_image = None
        try:
            print(f"Loading input image: {input_path}")
            with Image.open(input_path) as source_image:
                input_image = source_image.convert("RGB")

            print(f"Removing background: {input_path.name}")
            result_image = remove(input_image, session=session)
            result_image.convert("RGBA").save(output_path, format="PNG")
            print(f"Success: {output_path}")
        except Exception as e:
            print(f"Failed to process {input_path}: {e}", file=sys.stderr)
            failures += 1
        finally:
            if input_image is not None:
                input_image.close()
            if result_image is not None:
                result_image.close()

    if failures:
        sys.exit(1)


if __name__ == '__main__':
    main()
