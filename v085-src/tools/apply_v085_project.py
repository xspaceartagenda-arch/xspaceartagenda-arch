#!/usr/bin/env python3
from __future__ import annotations
import base64, gzip, subprocess, sys
from pathlib import Path


def main():
    if len(sys.argv) != 2:
        raise SystemExit('usage: apply_v085_project.py <project-root>')
    root = Path(sys.argv[1]).resolve()
    if not (root / 'app' / 'build.gradle').exists():
        raise SystemExit('invalid Aurum project root')
    patch_dir = Path(__file__).resolve().parents[1] / 'patch'
    chunks = sorted(patch_dir.glob('patch_*.b64'))
    if not chunks:
        raise SystemExit('V085_PATCH_DATA=FAIL')
    encoded = ''.join(p.read_text(encoding='utf-8').strip() for p in chunks)
    patch = gzip.decompress(base64.b64decode(encoded))
    check = subprocess.run(['git','apply','--check','--whitespace=nowarn','-'], cwd=root, input=patch)
    if check.returncode != 0:
        raise SystemExit('V085_PATCH_CHECK=FAIL')
    applied = subprocess.run(['git','apply','--whitespace=nowarn','-'], cwd=root, input=patch)
    if applied.returncode != 0:
        raise SystemExit('V085_PATCH_APPLY=FAIL')
    print('V085_PATCH_APPLY=PASS')


if __name__ == '__main__':
    main()
