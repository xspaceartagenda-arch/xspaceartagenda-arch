#!/usr/bin/env python3
from __future__ import annotations

import base64
import gzip
import subprocess
import sys
from pathlib import Path


def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("usage: apply_v086_project.py <project-root>")
    root = Path(sys.argv[1]).resolve()
    if not (root / "app" / "build.gradle").exists():
        raise SystemExit("invalid Aurum project root")

    patch_dir = Path(__file__).resolve().parents[1] / "patch"
    chunks = sorted(patch_dir.glob("patch_*.b64"))
    if not chunks:
        raise SystemExit("V086_PATCH_DATA=FAIL")
    encoded = "".join(
        chunk.read_text(encoding="utf-8").strip() for chunk in chunks
    )
    patch = gzip.decompress(base64.b64decode(encoded))

    check = subprocess.run(
        ["patch", "--dry-run", "--batch", "--forward", "-p1"],
        cwd=root,
        input=patch,
    )
    if check.returncode != 0:
        raise SystemExit("V086_PATCH_CHECK=FAIL")
    applied = subprocess.run(
        ["patch", "--batch", "--forward", "-p1"],
        cwd=root,
        input=patch,
    )
    if applied.returncode != 0:
        raise SystemExit("V086_PATCH_APPLY=FAIL")
    print("V086_PATCH_APPLY=PASS")


if __name__ == "__main__":
    main()
