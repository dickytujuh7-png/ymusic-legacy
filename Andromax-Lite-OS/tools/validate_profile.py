#!/usr/bin/env python3
"""Validate the Andromax Lite OS project profile without touching firmware images."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PROFILE = ROOT / "device" / "device-profile.json"


def main() -> int:
    data = json.loads(PROFILE.read_text(encoding="utf-8"))
    required = ("project", "vendor", "marketing_name", "targets", "platform", "release")
    missing = [key for key in required if key not in data]
    if missing:
        raise SystemExit("Missing top-level keys: " + ", ".join(missing))

    targets = data["targets"]
    if targets["ram_mb"] != 512:
        raise SystemExit("Unexpected RAM target")
    if targets["launcher_grid_columns"] != 3:
        raise SystemExit("Launcher grid target must remain 3 columns")
    if targets["rom_package_goal_mb"] != 200:
        raise SystemExit("Unexpected compressed ROM package size goal")
    if data["release"]["flashable"] or data["release"]["device_tested"]:
        raise SystemExit("Profile must not claim a tested flashable build before release gates pass")

    platform = data["platform"]
    for key in ("android_version", "android_api", "kernel_version", "cpu_abi"):
        if not platform.get(key):
            raise SystemExit(f"Missing firmware-verified platform field: {key}")

    print("Profile OK; firmware metadata is recorded, but hardware and release gates remain open.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
