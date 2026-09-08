#!/usr/bin/env python3
from pathlib import Path
import sys
root = Path.cwd()
checks = {
    'version': ('app/build.gradle', '0.8.5-capture-cpe-active'),
    'heartbeat': ('app/src/main/java/com/xspaceart/radar30/ScreenCaptureService.java', 'OCR_HEARTBEAT_MS = 1400L'),
    'single_spin_guard': ('app/src/main/java/com/xspaceart/radar30/ScreenCaptureService.java', 'multiSpinCatchupAllowed ? 12 : 1'),
    'clock_filter': ('app/src/main/java/com/xspaceart/radar30/OcrHistoryParser.java', 'looksLikeClockOrUiCounter'),
    'jitter_stabilizer': ('app/src/main/java/com/xspaceart/radar30/core/ScanStabilizer.java', 'allowedMismatches'),
    'bounded_merge': ('app/src/main/java/com/xspaceart/radar30/core/ScanSynchronizer.java', 'merge(List<Integer> scan, int maxNewNumbers)'),
    'cpe_active_reason': ('app/src/main/java/com/xspaceart/radar30/lock/AurumLockEngine.java', 'CPE ATIVO:'),
    'cpe_weight': ('app/src/main/java/com/xspaceart/radar30/lock/AurumLockEngine.java', 'cpeInfluenceWeight'),
    'cpe_veto': ('app/src/main/java/com/xspaceart/radar30/lock/AurumLockPolicy.java', 'CPE ativo divergente'),
    'ui_active': ('app/src/main/java/com/xspaceart/radar30/StandaloneActivity.java', 'CPE • ATIVO'),
}
failed=[]
for name,(path,needle) in checks.items():
    p=root/path
    txt=p.read_text(encoding='utf-8') if p.exists() else ''
    if needle not in txt: failed.append(name)
if failed:
    print('V085_STATIC=FAIL ' + ','.join(failed))
    sys.exit(1)
print('V085_STATIC=PASS')
