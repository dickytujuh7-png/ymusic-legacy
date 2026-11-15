#!/usr/bin/env python3
"""Estimate a ZIP release payload from selected stock-firmware members.

Read-only: this tool never extracts, edits, repacks, or flashes firmware.
The estimate uses each source member's compressed size and adds a configurable
metadata reserve. It is a budget check, not proof of installer compatibility.
"""
from __future__ import annotations

import argparse
import json
import sys
import zipfile
from pathlib import Path

DEFAULT_MEMBERS = (
    "system.img",
    "boot.img",
    "l_modem.img",
    "l_gdsp.img",
    "l_ldsp.img",
)
MIB = 1024 * 1024


def estimate(archive_path: Path, members: list[str], limit_mib: float,
             reserve_mib: float) -> dict:
    if limit_mib <= 0 or reserve_mib < 0:
        raise ValueError("limit must be positive and reserve cannot be negative")
    with zipfile.ZipFile(archive_path, "r") as archive:
        infos = {item.filename: item for item in archive.infolist() if not item.is_dir()}
        missing = [name for name in members if name not in infos]
        if missing:
            raise ValueError("missing requested firmware members: " + ", ".join(missing))
        selected = [
            {
                "name": name,
                "source_compressed_bytes": infos[name].compress_size,
                "source_uncompressed_bytes": infos[name].file_size,
                "compression": infos[name].compress_type,
            }
            for name in members
        ]
        payload = sum(item["source_compressed_bytes"] for item in selected)
        reserve = round(reserve_mib * MIB)
        estimated = payload + reserve
        limit = round(limit_mib * MIB)
        return {
            "source_archive": str(archive_path),
            "selected_members": selected,
            "payload_compressed_bytes": payload,
            "metadata_reserve_bytes": reserve,
            "estimated_package_bytes": estimated,
            "limit_bytes": limit,
            "headroom_bytes": limit - estimated,
            "within_budget_estimate": estimated <= limit,
            "warning": (
                "Estimate only. Actual repacking, installer metadata, signatures, "
                "compression settings, and device installation compatibility are "
                "not validated. This does not authorize flashing."
            ),
        }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("firmware_zip", type=Path)
    parser.add_argument("--include", action="append", dest="members",
                        help="Firmware member to include; repeat for multiple members")
    parser.add_argument("--limit-mb", type=float, default=200.0,
                        help="Decimal MB package limit (default: 200)")
    parser.add_argument("--reserve-mb", type=float, default=2.0,
                        help="Reserve for installer/metadata (default: 2)")
    parser.add_argument("--json", action="store_true")
    args = parser.parse_args()
    try:
        result = estimate(args.firmware_zip, args.members or list(DEFAULT_MEMBERS),
                          args.limit_mb, args.reserve_mb)
    except (OSError, zipfile.BadZipFile, RuntimeError, ValueError) as error:
        print(f"Estimate failed: {error}", file=sys.stderr)
        return 2
    if args.json:
        print(json.dumps(result, indent=2))
    else:
        print(f"Selected compressed payload: {result['payload_compressed_bytes']:,} bytes")
        print(f"Metadata reserve: {result['metadata_reserve_bytes']:,} bytes")
        print(f"Estimated package: {result['estimated_package_bytes']:,} bytes")
        print(f"Budget: {result['limit_bytes']:,} bytes")
        print(f"Headroom: {result['headroom_bytes']:,} bytes")
        print("Within estimated budget: " +
              ("YES" if result["within_budget_estimate"] else "NO"))
        print(result["warning"])
    return 0 if result["within_budget_estimate"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
