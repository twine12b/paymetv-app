#!/usr/bin/env python3
"""Copy the default dataset structure, then move a user's foreground files."""

import argparse
import shutil
from pathlib import Path


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Move an active user's foreground files into a destination directory."
    )
    parser.add_argument("active_user", help="User directory under /data")
    parser.add_argument("mv_to", type=Path, help="Directory to move the files into")
    args = parser.parse_args()

    script_directory = Path(__file__).resolve().parent
    default_structure = script_directory / "default_ml_structure"
    if not default_structure.is_dir():
        # In this project the default structure is stored under src/main/resources.
        default_structure = script_directory / "src/main/resources/default_ml_structure"

    source = Path("/data") / args.active_user / "datasets/input/foreground/cars/000624"
    datasets_directory = Path("/data") / args.active_user / "datasets"
    destination = args.mv_to

    if not default_structure.is_dir():
        parser.error(f"default_ml_structure directory not found next to the script: {default_structure}")

    if not source.is_dir():
        parser.error(f"source directory does not exist: {source}")

    shutil.copytree(default_structure, datasets_directory, dirs_exist_ok=True)

    destination.mkdir(parents=True, exist_ok=True)
    files = [path for path in source.iterdir() if path.is_file()]

    collisions = [path.name for path in files if (destination / path.name).exists()]
    if collisions:
        parser.error("destination already contains: " + ", ".join(collisions))

    for path in files:
        shutil.move(str(path), str(destination / path.name))

    print(f"Moved {len(files)} file(s) from {source} to {destination}")


if __name__ == "__main__":
    main()
