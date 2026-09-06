from pathlib import Path
import shutil
import sys

root = Path(sys.argv[1]).resolve()
workspace = Path(__file__).resolve().parents[2]
staged = workspace / "v082-src"

copies = {
    staged / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockConfig.java":
        root / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockConfig.java",
    staged / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockPolicy.java":
        root / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockPolicy.java",
    staged / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockFormatter.java":
        root / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockFormatter.java",
    staged / "tools/AurumLockCoreTest.java": root / "tools/AurumLockCoreTest.java",
    staged / "tools/v082_opportunity_lock_validation.py":
        root / "tools/v082_opportunity_lock_validation.py",
}
for src, dst in copies.items():
    if not src.exists():
        raise SystemExit(f"missing staged source: {src}")
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)

build = root / "app/build.gradle"
text = build.read_text(encoding="utf-8")
repls = [
    ("versionCode 15", "versionCode 16"),
    ('versionName "0.8.1-premium-ui"', 'versionName "0.8.2-opportunity-lock"'),
]
for old, new in repls:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"build replacement count for {old!r}: {count}")
    text = text.replace(old, new, 1)
build.write_text(text, encoding="utf-8")

standalone = root / "app/src/main/java/com/xspaceart/radar30/StandaloneActivity.java"
text = standalone.read_text(encoding="utf-8")
repls = [
    ('"v0.8.1 PREMIUM"', '"v0.8.2 OPPORTUNITY"'),
    (
        "root.setPadding(Ui.dp(this, 14), Ui.dp(this, 12), Ui.dp(this, 14), Ui.dp(this, 28));",
        "root.setPadding(Ui.dp(this, 14), Ui.dp(this, 48), Ui.dp(this, 14), Ui.dp(this, 28));",
    ),
]
for old, new in repls:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"standalone replacement count for {old!r}: {count}")
    text = text.replace(old, new, 1)
standalone.write_text(text, encoding="utf-8")

print("APPLY_V082_PROJECT=PASS")
print("LOCK_POLICY=OPPORTUNITY_V2")
print("TOP_SAFE_PADDING=48DP")
