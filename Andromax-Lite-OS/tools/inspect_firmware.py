#!/usr/bin/env python3
"""Read-only inventory and checksum tool for the Andromax stock firmware ZIP.

This tool never extracts, modifies, repacks, or flashes firmware contents.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import sys
import zipfile
from pathlib import Path

CHUNK = 1024 * 1024
SIGNATURES = {
    b"ANDROID!": "Android boot image header",
    b"\x3a\xff\x26\xed": "Android sparse image magic",
    b"\x7fELF": "ELF executable/shared object",
    b"PK\x03\x04": "ZIP archive",
}


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(CHUNK), b""):
            digest.update(chunk)
    return digest.hexdigest()


def identify_header(data: bytes) -> list[str]:
    found = [label for magic, label in SIGNATURES.items() if data.startswith(magic)]
    # EXT4 superblock magic is at byte offset 0x438 from the start of a filesystem.
    if len(data) >= 0x43A and data[0x438:0x43A] == b"\x53\xef":
        found.append("EXT-family filesystem superblock magic at offset 0x438")
    return found or ["No recognized signature at inspected offset"]


def inspect(path: Path) -> dict:
    if not path.is_file():
        raise FileNotFoundError(path)
    report = {
        "archive": str(path),
        "archive_size_bytes": path.stat().st_size,
        "archive_sha256": sha256_file(path),
        "entries": [],
        "total_uncompressed_bytes": 0,
        "zip_test": "not_run",
    }
    with zipfile.ZipFile(path, "r") as archive:
        bad_member = archive.testzip()
        report["zip_test"] = "passed" if bad_member is None else f"failed: {bad_member}"
        for info in archive.infolist():
            entry = {
                "name": info.filename,
                "compressed_bytes": info.compress_size,
                "uncompressed_bytes": info.file_size,
                "is_directory": info.is_dir(),
            }
            report["total_uncompressed_bytes"] += info.file_size
            if not info.is_dir() and info.file_size:
                with archive.open(info, "r") as member:
                    header = member.read(4096)
                entry["header_identification"] = identify_header(header)
            report["entries"].append(entry)
    return report


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("firmware_zip", type=Path, help="Path to the original firmware ZIP")
    parser.add_argument("--json", action="store_true", help="Print machine-readable JSON")
    args = parser.parse_args()
    try:
        result = inspect(args.firmware_zip)
    except (OSError, zipfile.BadZipFile, RuntimeError) as error:
        print(f"Inspection failed: {error}", file=sys.stderr)
        return 2
    if args.json:
        print(json.dumps(result, indent=2))
    else:
        print(f"Archive: {result['archive']}")
        print(f"Size: {result['archive_size_bytes']} bytes")
        print(f"SHA-256: {result['archive_sha256']}")
        print(f"ZIP integrity test: {result['zip_test']}")
        print(f"Total uncompressed size: {result['total_uncompressed_bytes']} bytes")
        print("\nEntries:")
        for item in result["entries"]:
            print(f"- {item['name']} ({item['uncompressed_bytes']} bytes)")
            for label in item.get("header_identification", []):
                print(f"    {label}")
    return 0 if result["zip_test"] == "passed" else 3


if __name__ == "__main__":
    raise SystemExit(main())
