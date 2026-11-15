#!/usr/bin/env python3
"""Validate the project profile without touching firmware images."""
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
    if targets["system_image_goal_mb"] != 200:
        raise SystemExit("Unexpected system image size goal")
    if data["release"]["flashable"] or data["release"]["device_tested"]:
        raise SystemExit("Initial project profile must not claim a tested flashable build")

    print("Profile OK; device/platform details remain explicitly unverified.")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
