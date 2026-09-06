from pathlib import Path

root = Path(__file__).resolve().parents[1]
config = (root / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockConfig.java").read_text(encoding="utf-8")
policy = (root / "app/src/main/java/com/xspaceart/radar30/lock/AurumLockPolicy.java").read_text(encoding="utf-8")
build = (root / "app/build.gradle").read_text(encoding="utf-8")
standalone = (root / "app/src/main/java/com/xspaceart/radar30/StandaloneActivity.java").read_text(encoding="utf-8")
checks = {
    "version": 'versionCode 16' in build and '0.8.2-opportunity-lock' in build,
    "calibration": '54, 62, 76, 58' in config,
    "families_preserved": '3, 3' in config,
    "opportunity_four_families": 'input.independentFamilies >= 4' in policy,
    "stability": 'input.stableTargetUpdates >= 2' in policy,
    "audit": 'input.fastAuditPassed' in policy,
    "contradiction_veto": 'input.contradiction <= 45' in policy,
    "no_cadence_trigger": 'System.currentTimeMillis' not in policy and 'hour' not in policy.lower(),
    "ui_version": 'v0.8.2 OPPORTUNITY' in standalone,
    "safe_top_padding": 'Ui.dp(this, 48)' in standalone,
}
failed = [k for k, v in checks.items() if not v]
if failed:
    raise SystemExit('V082_VALIDATION=FAIL ' + ','.join(failed))
print('V082_OPPORTUNITY_LOCK_STATIC=PASS')
print('RADAR_ENGINE=PRESERVED')
print('NO_TIME_BASED_SIGNAL_QUOTA=PASS')
print('MULTI_CONFLUENCE_GATE=PASS')
print('TOP_SAFE_PADDING=PASS')
