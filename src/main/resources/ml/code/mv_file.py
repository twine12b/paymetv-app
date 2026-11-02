#!/usr/bin/env python3
"""Run the ML pod file setup script for a user category."""

import argparse
import subprocess
from pathlib import Path


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Copy the default dataset structure and prepare a user category."
    )
    parser.add_argument("--active_user", required=True, help="User directory name under /data")
    parser.add_argument("--category", required=True, help="Category directory name")
    parser.add_argument("--sub_cat", required=True, help="Subcategory directory name")
    args = parser.parse_args()

    mv_script = Path(__file__).resolve().with_name("mv_file.sh")
    if not mv_script.is_file():
        parser.error(f"file setup script not found: {mv_script}")

    subprocess.run(
        ["bash", str(mv_script), args.active_user, args.category, args.sub_cat],
        check=True,
    )


if __name__ == "__main__":
    main()
