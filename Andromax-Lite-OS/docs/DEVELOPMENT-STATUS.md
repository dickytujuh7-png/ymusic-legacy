# Development status

Last updated: 2026-11-15

## Completed in this workspace

- [x] Dedicated project directory created in the existing GitHub repository.
- [x] Project goals and current unknowns documented.
- [x] Initial device profile created with unverified fields explicitly marked.
- [x] Added a read-only firmware ZIP inventory tool that records SHA-256, ZIP integrity, entry names/sizes, and basic header signatures without extracting or modifying images.
- [x] Added a documented Windows procedure for generating a reproducible firmware inventory.
- [x] Added GitHub Actions checks for project-profile validation and Python syntax.

## Not completed

- [ ] Run the inventory tool against the original firmware archive and preserve its report/checksum.
- [ ] Confirm exact product name, board/platform, SoC, Android version, kernel, and partition map from the actual firmware.
- [ ] Decode and inspect the vendor boot image format safely.
- [ ] Establish a reproducible host build environment.
- [ ] Build and test the keypad-first 3-column launcher on target-compatible Android.
- [ ] Profile Chromium 68 memory use and define a lean browser configuration.
- [ ] Diagnose camera service/HAL instability using logs and repeatable tests.
- [ ] Produce and verify a system image below the 200 MB goal.
- [ ] Verify boot, recovery, and rollback procedure on the exact device.

## Immediate blockers

The original firmware ZIP is not currently mounted in the active build workspace, so its contents cannot yet be inspected or hashed in this session. The earlier reported 203.21 MB archive size and EXT4/system image details are not treated as verified until the archive is re-read and the inventory report is generated.

The boot image reportedly uses a Spreadtrum-specific wrapper rather than a standard Android boot image. A generic Android repacker must not be assumed to work. Device identifiers also conflict between the firmware filename (F17A1H) and a recovery display (F17ALH), so they must be reconciled before building.

The development PC is Windows 10 with 4 GB RAM and less than 50 GB free disk space. Tooling choices must be conservative; a full Android source build is unlikely to fit comfortably without careful space planning. The immediate stage is firmware inspection and a minimal, reproducible device-specific build path—not an assumed full AOSP rebuild.

## Release gate

A release candidate must not be called flashable until all of the following are documented:

- exact target identity and partition compatibility;
- successful build with recorded tool versions and checksums;
- image format and size validation;
- boot/recovery integrity checks;
- keypad navigation tests;
- camera stability tests;
- memory and storage measurements;
- tested recovery/rollback path.

No release artifact exists yet. Do not flash unverified images.
