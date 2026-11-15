# Development status

Last updated: 2026-11-15

## Completed in this workspace

- [x] Dedicated project directory created in the existing GitHub repository.
- [x] Project goals and current unknowns documented.
- [x] Initial device profile created with unverified fields explicitly marked.

## Not completed

- [ ] Reproducible inventory and checksum of the original firmware archive.
- [ ] Confirm exact product name, board/platform, SoC, Android version, kernel, and partition map.
- [ ] Decode and inspect the vendor boot image format safely.
- [ ] Establish a reproducible host build environment.
- [ ] Build and test the keypad-first 3-column launcher.
- [ ] Profile Chromium 68 memory use and define a lean browser configuration.
- [ ] Diagnose camera service/HAL instability using logs and repeatable tests.
- [ ] Produce and verify a system image below the 200 MB goal.
- [ ] Verify boot, recovery, and rollback procedure on the exact device.

## Immediate technical blockers

The boot image reportedly uses a Spreadtrum-specific wrapper rather than a standard Android boot image. A generic Android repacker must not be assumed to work. Device identifiers also conflict between the firmware filename (F17A1H) and a recovery display (F17ALH), so they must be reconciled before building.

The development PC is Windows 10 with 4 GB RAM and limited free disk space. Tooling choices should be conservative; a Linux environment may be needed for image inspection/build steps, but this must be checked against available disk space first.

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

No release artifact exists yet.
