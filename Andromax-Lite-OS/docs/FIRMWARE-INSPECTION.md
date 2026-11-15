# Stock firmware inspection procedure

## Purpose

Create a reproducible, read-only inventory of the supplied
`Firmware+Andromax+Prime+F17A1H.zip` before attempting any modification.

## Run on Windows

1. Install Python 3 if it is not already available.
2. Open Command Prompt in the folder containing the firmware ZIP.
3. Run:

```bat
python inspect_firmware.py "Firmware+Andromax+Prime+F17A1H.zip" --json > firmware-inventory.json
```

Place `inspect_firmware.py` beside the ZIP, or provide its full path.

## What the tool records

- SHA-256 of the original archive.
- ZIP integrity test result.
- Every entry's path, compressed size, and uncompressed size.
- A limited header-signature check for likely boot images, Android sparse images,
  ELF files, ZIPs, and EXT-family filesystem superblocks.

The tool reads archive members but does not extract them to disk, alter the archive,
repack images, or perform any device operation.

## Important limitations

A header signature is only a clue, not proof of a partition format or device
compatibility. In particular, a vendor-specific Spreadtrum boot wrapper may not
use the standard Android boot-image header. Partition names, sizes, device
identifiers, Android version, kernel, and boot/recovery compatibility must be
verified independently before any image is built.

Do not flash an image based only on this report. Keep the original firmware
unchanged as the recovery source.
