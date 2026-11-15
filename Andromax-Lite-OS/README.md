# Andromax Lite OS

Project workspace for a lightweight, physical-keypad-first Android system for Smartfren Andromax Prime (the supplied firmware identifies the model as Haier Andromax F17A1H).

## Target requirements

- Target device: Smartfren Andromax Prime / Haier Andromax F17A1H firmware.
- Memory target: 512 MB RAM.
- Release package goal: under 200 MB compressed (not the expanded /system partition).
- Launcher: 3-column application grid inspired by feature-phone interfaces.
- Input: all system navigation and included apps should be usable with the physical keypad, without touchscreen dependency.
- Browser: Andromax Web, based on the user's tested Chromium 68 baseline, with memory use reduced where feasible.
- Camera: investigate and fix intermittent hangs before considering the build stable.

## Current status

**This repository is still a firmware-analysis and source-workspace project, not a flashable ROM.** The supplied firmware has now been inspected read-only. A first Android 4.4 HOME launcher prototype now compiles into a 12,706-byte debug APK in GitHub Actions. It has not been installed or tested on the handset. No system image has been modified and no release ROM ZIP exists.

See [launcher/README.md](launcher/README.md) for the launcher source and build notes, [docs/FIRMWARE-ANALYSIS-2026-11-15.md](docs/FIRMWARE-ANALYSIS-2026-11-15.md) for the inspection report, and [docs/DEVELOPMENT-STATUS.md](docs/DEVELOPMENT-STATUS.md) for the release gates.

## First launcher build

- Build: successful in GitHub Actions (Android Gradle Plugin 3.0.1, Gradle 4.1, JDK 8, Android API 26 build tools).
- APK: 12,706 bytes; SHA-256 `40d5ee182e1ee6efbd1aae44045f9ac85f8c34c2905ba0cbaff7c84828e34032`.
- This is a development/debug APK only. It is not a full ROM, has not been installed on-device, and must not be treated as a validated system launcher.

## Firmware facts established

- Android 4.4.4 / API 19, ARMv7 (armeabi-v7a).
- Firmware model: Andromax F17A1H; a previously reported recovery display of F17ALH remains unresolved.
- Kernel: 3.10.65-svn183239 (32-bit ARM).
- System image: ext4 sparse image, 600 MiB expanded and about 316.47 MiB of allocated filesystem blocks.
- Boot and recovery have a vendor `SPRD-SECUREFLAG` preheader. Recovery fstab references VLR secure boot for boot/recovery and several modem/DSP partitions.
- Physical keypad layout files are present and map numeric keys, D-pad, center, menu, back, call, home, camera, star, and pound.
- Camera stack uses the vendor `camera.sc8830.so` HAL; its dependencies must be preserved until the freeze is diagnosed.
- Some firmware identifiers differ: system uses `sp9820a_3c10`, while ramdisk fstab/init filenames use `sp9820a_1c10`. The system build timestamp is also older than the kernel build timestamp. These require device-level confirmation.

## ROM size note

The original firmware archive is 203,210,513 bytes. A selected set of existing compressed image members (system, boot, active L-modem, and L-DSP/G-DSP) totals about 195.43 MB before package metadata or modifications. This is only a feasibility estimate. The raw sparse system image is 323.68 MB, and its expanded ext4 filesystem is 600 MiB. Do not confuse archive size, sparse-image size, and expanded partition size.

## Development principles

1. Preserve untouched stock images and record their checksums.
2. Resolve exact device identity and partition capacities before creating a release image.
3. Preserve vendor boot/security and radio components unless their format and installation path are fully understood.
4. Compile and test the launcher separately before changing framework or init services.
5. Keep keypad navigation as a required test for each screen.
6. Preserve the stock camera HAL and dependencies until device logs identify a specific fault.
7. Measure package size and RAM use rather than assuming improvements.
8. Never label an image flashable until it has passed device-specific validation.
