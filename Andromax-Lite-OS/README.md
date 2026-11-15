# Andromax Lite OS

Project workspace for a lightweight, physical-keypad-first Android ROM concept for the Smartfren Andromax Prime (firmware package identifies F17A1H).

## Target requirements

- Target device: Smartfren Andromax Prime, firmware package name F17A1H.
- Memory target: 512 MB RAM.
- ROM-size goal: under 200 MB (not yet measured or achieved).
- Launcher: 3-column application grid inspired by feature-phone interfaces.
- Input: all system navigation and included apps must be usable with the physical keypad.
- Browser: Andromax Web, based on the tested Chromium 68 baseline, with memory use reduced where feasible.
- Camera: investigate and fix intermittent hangs before considering the build stable.

## Current status

**This is an initial source/documentation workspace, not a flashable ROM.** No ROM image has been built, boot-tested, or validated on the device. Do not flash anything from this directory.

The original firmware and the launcher foundation archive are retained separately in the conversation files. They are not copied into this repository in this initial commit.

## Device identity and platform facts

- Firmware archive: `Firmware+Andromax+Prime+F17A1H.zip`.
- A previous inspection reported EXT4 for the system image and a vendor-specific Spreadtrum boot image wrapper. These details still require reproducible verification from the original package.
- Recovery has also displayed F17ALH; the exact product/device identifiers and partition map must be confirmed from the firmware before any image is produced.
- Android version, SoC, kernel configuration, boot image format, partition sizes, and signing requirements are **unverified** in this workspace.

## Development principles

1. Preserve a known-good stock firmware backup and document its checksums before modifying anything.
2. Identify the exact device, board, partition layout, boot image format, and recovery behavior from the supplied firmware.
3. Make changes incrementally and keep stock/vendor components unless removal is proven safe.
4. Prefer measured size and memory results over assumptions.
5. Require static checks, image integrity checks, and a documented recovery path before any device testing.
6. Never label an image as flashable until it has passed device-specific validation.

## Planned work

See [docs/DEVELOPMENT-STATUS.md](docs/DEVELOPMENT-STATUS.md) and [device/device-profile.json](device/device-profile.json).
