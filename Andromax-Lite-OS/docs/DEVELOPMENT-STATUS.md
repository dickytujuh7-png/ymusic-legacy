# Development status

Last updated: 2026-11-15

## Completed in this workspace

- [x] Dedicated project directory and target requirements documented.
- [x] Device profile updated from direct, read-only firmware evidence.
- [x] Firmware ZIP integrity test completed successfully; archive SHA-256 recorded.
- [x] All 18 image members inventoried with both uncompressed and ZIP-compressed sizes.
- [x] Android sparse system image expanded to a separate analysis copy and inspected as ext4.
- [x] Android release, API level, product model, ARM ABI, kernel version, and boot header details identified.
- [x] Boot and recovery ramdisks decompressed read-only; fstab and initialization files inspected.
- [x] Physical keypad layout mappings inspected.
- [x] Camera HAL and its principal native dependencies inventoried.
- [x] Low-memory settings and compressed ROM-package size estimates documented.
- [x] First native Android 4.4 HOME launcher prototype added: three-column app grid, D-pad/center/Enter navigation, numeric shortcuts, MENU refresh, and BACK handling.
- [x] Launcher uses platform widgets only; no AppCompat/AndroidX or network permission.
- [x] Static launcher checks added for Android XML, HOME intent, API level, grid size, and keypad mappings.
- [x] GitHub Actions updated to run device-profile, launcher-source, and Python syntax checks.

## Verified firmware findings

- Firmware product model: Haier Andromax F17A1H.
- Android 4.4.4 / API 19; ARMv7 (armeabi-v7a).
- Kernel: 3.10.65-svn183239, 32-bit ARM.
- System image: ext4, 600 MiB expanded; approximately 316.47 MiB of filesystem blocks allocated.
- Boot/recovery: vendor `SPRD-SECUREFLAG` preheader followed by an Android boot image header; recovery fstab marks boot/recovery and modem/DSP partitions with `secureboot=vlr:/uboot`.
- Keypad: SCI layout maps number keys, D-pad, center, menu, back, call, home, camera, star, and pound.
- Camera: vendor `camera.sc8830.so` HAL and dependencies are present; ZSL is already disabled in default properties.
- The 200 MB objective is now interpreted as compressed release-package size, not the expanded /system partition.

## Not completed

- [ ] Resolve platform strings `sp9820a_3c10` vs `sp9820a_1c10` and the previously reported F17ALH recovery label.
- [ ] Obtain exact partition capacities; fstab provides partition names/mounts only.
- [ ] Establish the vendor secure-boot wrapper/signing and supported update-package process.
- [ ] Compile the launcher APK with the legacy Android SDK toolchain and review compiler/lint output.
- [ ] Compare the existing stock HOME app and test HOME selection, focus movement, and application launch on target hardware.
- [ ] Build Andromax Web integration and measure its memory use on-device.
- [ ] Diagnose the camera freeze with real logcat/dmesg data and repeatable device tests.
- [ ] Build the modified system image and a release package below 200 MB.
- [ ] Verify boot, calls, SMS, mobile data, Wi-Fi, Bluetooth, keypad navigation, camera stability, and rollback on the exact device.

## Immediate blockers

The archive can now be analyzed offline, but static firmware inspection cannot reveal the exact physical partition capacities or prove that the handset currently displaying F17ALH accepts this F17A1H package. The system build properties and ramdisk target contain different internal platform strings (`sp9820a_3c10` and `sp9820a_1c10`). The system build date is July 2017 while the kernel build date is October 2018; compatibility must be checked rather than assumed.

Boot/recovery images contain a vendor preheader, and the recovery fstab refers to VLR secure boot. A generic boot repacker is not a safe release method. The precise vendor signing/installation process is not yet known.

The user's development PC is Windows 10 with 4 GB RAM and less than 50 GB free disk space. The analysis so far has been done in a temporary workspace, not by taking control of that PC. There has been no device connection, no system-image modification, no APK compilation yet, and no flash test. The launcher source is a prototype, not a tested APK.

## ROM-size interpretation

The original firmware ZIP is 203,210,513 bytes. A compressed member subset consisting of system, boot, active L-modem, and L-DSP/G-DSP images totals 195,430,118 bytes before package metadata or changes. This suggests a sub-200 MB release package may be possible with a carefully chosen payload, but it is only an estimate. It is not proof of a package that installs safely. The raw sparse system image itself is 323,678,352 bytes and the expanded filesystem is 600 MiB.

## Release gate

A release candidate must not be called flashable until all of the following are documented:

- exact device identity and partition capacity compatibility;
- vendor-accepted boot/recovery/update mechanism;
- reproducible build with tool versions and checksums;
- image format, size, and filesystem validation;
- successful recovery/rollback procedure;
- keypad navigation tests;
- camera stability tests;
- memory measurements;
- calls, SMS, mobile data, Wi-Fi, and Bluetooth tests.

No release artifact exists yet. Do not flash unverified images.
