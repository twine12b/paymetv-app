from rembg import remove
from pathlib import Path
import argparse
import sys

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
    parser = argparse.ArgumentParser(description="Remove background from an image_in using rembg")
    parser.add_argument("--input", "-i", help="Input image_in path (optional). If omitted, the script searches for common filenames.")
    parser.add_argument("--output", "-o", help="Output path (optional). Defaults to input name with .png suffix)")
    args = parser.parse_args()

    base = Path(__file__).parent

    # If user provided an input path, prefer that (and resolve relative to the script dir)
    if args.input:
        input_path = Path(args.input)
        if not input_path.is_absolute():
            input_path = base / input_path
        if not input_path.exists():
            print(f"Error: input file not found: {input_path}", file=sys.stderr)
            sys.exit(2)
    else:
        # Common candidate filenames present in this repo
        candidates = [
            "image1.jpg",
            "image1.jpeg",
            "image1.png",
            "image1.webp",
            "handbag_image_original.png",
            "handbag_image1.jpeg",
            "handbag_image1.jpg",
            "handbag_image1.png",
        ]
        found = find_candidate_file(base, candidates)
        if not found:
            print("Error: no input image_in found. Tried common filenames in the script folder and sibling image_in/ folders and did a fallback search.", file=sys.stderr)
            print("Provide --input /path/to/image_in to specify a file.")
            sys.exit(3)
        input_path = found

    # Ensure outputs go into the project's image_out directory (sibling of ml/code)
    images_out_dir = base.parent / "image_out"
    images_out_dir.mkdir(parents=True, exist_ok=True)

    if args.output:
        output_path = Path(args.output)
        # If the user provided a relative filename, place it inside image_out
        if not output_path.is_absolute():
            output_path = images_out_dir / output_path
    else:
        # Default output filename: same base name as input, but .png, placed in image_out
        output_path = images_out_dir / input_path.with_suffix('.png').name

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
        output_path.write_bytes(result_bytes)
    except Exception as e:
        print(f"Failed to write output file: {e}", file=sys.stderr)
        sys.exit(6)

    print(f"Background removed and saved to {output_path}")


if __name__ == '__main__':
    main()
