#!/usr/bin/env python3
from pathlib import Path
import sys


ROOT = Path(sys.argv[1] if len(sys.argv) > 1 else Path.cwd()).resolve()


def read(relative: str) -> str:
    return (ROOT / relative).read_text(encoding="utf-8")


def require(condition: bool, marker: str) -> None:
    if not condition:
        raise SystemExit(f"V086_{marker}=FAIL")
    print(f"V086_{marker}=PASS")


def main() -> None:
    build = read("app/build.gradle")
    service = read(
        "app/src/main/java/com/xspaceart/radar30/ScreenCaptureService.java"
    )
    standalone = read(
        "app/src/main/java/com/xspaceart/radar30/StandaloneActivity.java"
    )
    main_activity = read(
        "app/src/main/java/com/xspaceart/radar30/MainActivity.java"
    )
    integrated = read(
        "app/src/main/java/com/xspaceart/radar30/integrated/IntegratedActivity.java"
    )

    require(
        "versionCode 20" in build
        and 'versionName "0.8.6-android16-capture-stability"' in build,
        "VERSION",
    )

    command_start = service.index("public int onStartCommand")
    command_end = service.index("public IBinder onBind", command_start)
    command = service[command_start:command_end]
    fresh_grant = command.index(
        "resultCode != Activity.RESULT_OK || resultData == null"
    )
    promotion = command.index('ensureForeground("Preparando leitura")')
    require(
        command.count('ensureForeground("Preparando leitura")') == 1
        and promotion > fresh_grant
        and command.index("ACTION_RESET.equals(action)") < promotion
        and command.index("ACTION_STOP.equals(action)") < promotion,
        "FOREGROUND_ORDER",
    )
    require(
        'catch (SecurityException error)' in command
        and '"Autorização de captura expirada"' in command,
        "EXPIRED_GRANT_FALLBACK",
    )
    require(
        "private void updateNotification(String text) {\n"
        "        if (mediaProjection == null) return;" in service,
        "IDLE_NOTIFICATION_GUARD",
    )
    require(
        "private ByteBuffer packedFrameBuffer;" in service
        and "ByteBuffer source = plane.getBuffer().duplicate();" in service
        and "packed.put(rowPixels);" in service
        and "padded.copyPixelsFromBuffer" not in service,
        "SAFE_FRAME_COPY",
    )

    activities = standalone + main_activity + integrated
    require(
        activities.count("startForegroundService(service);") == 3
        and activities.count("EXTRA_RESULT_DATA") >= 3,
        "CONSENT_STARTS_ONLY",
    )
    require(
        "startService(service);" in standalone
        and "startService(intent);" in main_activity
        and "startService(mode);" in integrated,
        "CONTROL_COMMANDS",
    )

    refresh_start = standalone.index("private void refreshState()")
    refresh_end = standalone.index(
        "private void refreshSessionSummaryAsync()", refresh_start
    )
    refresh = standalone[refresh_start:refresh_end]
    require(
        "SessionLedger.sessionSummary" not in refresh
        and "sessionSummaryExecutor.execute" in standalone
        and "STATE_REFRESH_DEBOUNCE_MS" in standalone,
        "UI_THREAD_LEDGER",
    )

    print("V086_ANDROID16_STABILITY=PASS")


if __name__ == "__main__":
    main()
