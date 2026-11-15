# Andromax Prime Firmware Analysis

Date: 2026-11-15  
Source: `Firmware+Andromax+Prime+F17A1H.zip`  
Method: offline, read-only inspection

## Safety and scope

The supplied archive was integrity-tested and its image members were extracted to a temporary analysis workspace. The sparse system image was expanded to a separate analysis copy, and the boot/recovery ramdisks were decompressed for inspection.

No original image was modified. No phone was connected. No partition was written, no security mechanism was bypassed, and no image was flashed.

## Archive integrity

- Archive size: 203,210,513 bytes.
- Archive SHA-256: `102367381ece0f259050d1eccf7461cbc3e52b5ea611295a4f96fea4a53dcf4c`.
- ZIP integrity test: PASS (no corrupt member).
- Entries: 19 total, comprising one directory and 18 image files.
- Total compressed member data: 203,207,355 bytes.
- No partition-size map or scatter XML was found in the archive. The system image does contain Android build properties.

The table distinguishes the image member's uncompressed size from the amount that member occupies inside the ZIP.

| Image | Member size (bytes) | ZIP-compressed size (bytes) |
|---|---:|---:|
| boot.img | 20,971,520 | 7,171,416 |
| cache.img | 5,533,916 | 7,423 |
| devicelog.img | 31,457,280 | 38,512 |
| fbootlogo.img | 1,048,576 | 4,347 |
| l_fixnv1.img | 1,048,576 | 31,370 |
| l_fixnv2.img | 1,048,576 | 31,370 |
| l_gdsp.img | 3,145,728 | 1,238,335 |
| l_ldsp.img | 3,145,728 | 752,873 |
| l_modem.img | 15,728,640 | 10,872,453 |
| l_runtimenv1.img | 1,048,576 | 34,254 |
| l_runtimenv2.img | 1,048,576 | 34,254 |
| logo.img | 1,048,576 | 4,347 |
| misc.img | 1,048,576 | 1,245 |
| miscdata.img | 1,048,576 | 1,654 |
| pm_sys.img | 1,048,576 | 16,973 |
| prodnv.img | 5,242,880 | 9,076 |
| recovery.img | 20,971,520 | 7,562,412 |
| system.img | 323,678,352 | 175,395,041 |

Duplicate image pairs:
- `l_fixnv1.img` and `l_fixnv2.img` are byte-identical.
- `l_runtimenv1.img` and `l_runtimenv2.img` are byte-identical.
- `logo.img` and `fbootlogo.img` are byte-identical.

These duplicates are observations, not permission to delete or rewrite device NV partitions.

## Verified Android and device properties

The extracted `/build.prop` reports:

| Property | Firmware value |
|---|---|
| Product model/name | Andromax F17A1H |
| Manufacturer / brand | Haier |
| Display build | Andromax F17A1H V5.1_M |
| Build ID | KTU84P |
| Android release | 4.4.4 |
| Android API | 19 |
| Build date | 2017-07-10 |
| Build type / tags | user / test-keys |
| CPU ABI | armeabi-v7a (32-bit ARM) |
| Board property | Haier |
| `ro.board.platform` | sc8830 |
| `ro.build.product` | sp9820a_3c10 |
| Hardware property | SP9820A-openphone_V1.1.0 |
| Display density | 120 dpi |
| Physical display properties | 54 x 96 (as reported by build.prop; not pixel resolution) |
| Modem mode list | GSM, EDGE, TD-SCDMA, WCDMA, TD-LTE, FDD-LTE |

Important identity caveat: the firmware's model is clearly F17A1H, but the ramdisk's fstab/init filenames use `sp9820a_1c10`, while `build.prop` uses `sp9820a_3c10`. A previous recovery screen reportedly showed F17ALH. These different strings remain unresolved and must be checked against the actual handset before any release image is considered.

The system build timestamp is July 2017, while the embedded kernel identifies itself as built on 2018-10-16. This date difference is not proof of incompatibility, but it is a firmware component-version skew that must be recorded and tested.

## System image and ext4 filesystem

`system.img` is Android sparse-image format v1.0:
- Sparse block size: 4,096 bytes.
- Sparse block count: 153,600.
- Chunk count: 1,375.
- Expanded filesystem size: 629,145,600 bytes (600 MiB).

The expanded analysis copy is ext4. Its superblock reports:
- Inodes: 38,400; free inodes: 36,963.
- Blocks: 153,600; free blocks: 72,583.
- Block size: 4,096 bytes.
- Allocated/used blocks: 81,017, approximately 316.47 MiB.
- Free blocks: approximately 283.53 MiB.
- ext4 magic: `0xEF53`.

The sparse image member is 323,678,352 bytes before outer ZIP compression and occupies 175,395,041 bytes inside the original ZIP. These are three different measurements: expanded filesystem, sparse-image file, and compressed archive member.

Top-level file-size inventory (rounded):
- `/lib`: 90.04 MiB.
- `/app`: 61.91 MiB.
- `/framework`: 33.73 MiB.
- `/priv-app`: 31.52 MiB.
- `/usr`: 18.83 MiB.
- `/fonts`: 17.34 MiB.
- `/media`: 14.34 MiB.
- `/bin`: 10.22 MiB.
- `/etc`: 8.49 MiB.

There are 76 APK files with a combined file size of approximately 71.21 MiB. The largest single APK is `/app/WhatsApp.apk` at approximately 34.58 MiB. Other larger packages include MySmartfren, Gallery2, SprdVoWifiService, and IQQI_9Key. This identifies possible packaging/optional-app candidates; it does not mean any should be removed before dependency and user-function testing.

The system includes `/lib/libwebviewchromium.so` (approximately 20.35 MiB) and `/app/Browser.apk`. These are distinct from the planned Andromax Web application. Do not remove the WebView library merely because a separate browser app is planned; Android components may depend on it.

## Boot and recovery inspection

Both `boot.img` and `recovery.img`:
- Start with the vendor marker `SPRD-SECUREFLAG` at byte offset 0.
- Contain an Android boot-image magic `ANDROID!` at byte offset 1,024.
- Use a legacy Android boot header layout with 2,048-byte pages.
- Contain a 9,065,824-byte ARM kernel payload.
- Contain different gzip-compressed ramdisks.
- Contain the same kernel payload; its SHA-256 is `4c4d8a514cc90bad164d15be12904a173f2956f10b11830e0e3b20d98729f6fd`.

Kernel identity:
- Linux version: `3.10.65-svn183239`.
- Architecture: ARM 32-bit.
- Compiler string: GCC 4.7.
- Kernel build timestamp: 2018-10-16 13:57:50 CST.

The boot ramdisk decompresses to a CPIO newc archive of 7,612,928 bytes. The recovery ramdisk decompresses to 8,146,688 bytes. The boot ramdisk contains `init`, `init.sc8830.rc`, `init.sp9820a_1c10.rc`, `fstab.sp9820a_1c10`, SELinux policy/context files, and vendor kernel modules.

The recovery fstab maps the boot/recovery and several modem/DSP/PM partitions with `secureboot=vlr:/uboot`. Therefore a generic Android boot-image repack must not be assumed to produce a bootable image. The exact vendor wrapper/authentication process remains unknown; boot and recovery should be preserved untouched in early system/launcher development.

## Partition and radio observations

The boot fstab uses the eMMC by-name path:
`/dev/block/platform/sdio_emmc/by-name/`

It names at least system, userdata, cache, prodnv, devicelog, boot, recovery, and multiple modem/DSP/NV partitions. The fstab establishes names and mount behavior, not partition capacities. A true partition-size map is still missing.

The system properties report `ro.modem.l.enable=1` and `ro.radio.modemtype=l`, with LTE modem devices and NV settings. The L-modem path appears active in this build. Do not drop modem/DSP images or alter NV data as a size-saving shortcut.

## Physical-keypad evidence

The system contains dedicated keypad layouts:
- `/usr/keylayout/sci-keypad.kl`
- `/usr/keylayout/sci-keypad-ext.kl`
- `/usr/keylayout/sprd-eic-keys.kl`
- `/usr/keylayout/sprd-gpio-keys.kl`

The SCI keypad layout maps the number keys 0–9, DPAD up/down/left/right/center, MENU, BACK, CALL, HOME, CAMERA, STAR, POUND, POWER, and volume keys. The build also has `qemu.hw.mainkeys=1` and `ro.product.barphone=true`.

This is strong evidence that the stock system was configured for physical-key operation. The new launcher must preserve those Android key events and provide focus/selection behavior for every screen. It does not prove that every third-party app is keypad-friendly.

## Low-memory and performance settings

The stock build already enables several low-memory settings:
- `ro.config.low_ram=true`
- `ro.config.zram.support=true`
- `ro.config.ksm.support=true`
- Dalvik heap start: 5 MiB.
- Dalvik growth limit: 48 MiB.
- Dalvik hard heap limit: 128 MiB.
- `ro.board_ram_size=mid`.

The project target remains 512 MiB RAM. These settings are useful baseline evidence, but real memory use must be measured on the device. Do not arbitrarily reduce heap sizes or disable Android services without runtime tests.

## Camera stack observations

The firmware includes:
- `/app/Camera2.apk` (approximately 1.29 MiB).
- `/lib/hw/camera.sc8830.so` (9,160,488 bytes, ARM ELF32).
- `/lib/libcameraservice.so`, `libcamera_client.so`, and `libcamera_metadata.so`.
- Camera HAL dependencies including vendor AE/AWB/AF/EV libraries, `libmemoryheapion.so`, face-beautify HAL, and SPRD HDR library.

The boot ramdisk sets `camera.disable_zsl_mode=1`. The system properties also disable several camera extras, including ZSL, HDR, burst, and other optional features. This suggests the base firmware already avoids some heavier camera modes. It does not identify the cause of intermittent camera hangs. Preserve the complete camera HAL/dependency set until device logcat/dmesg and repeatable camera tests identify a specific fault.

## ROM size assessment

The original ZIP is approximately 203.21 MB (decimal), slightly above the 200 MB package target. The target must be interpreted as a compressed release-package size, not the expanded `/system` filesystem size.

Compressed member-size estimates from the original archive:
- `system.img` alone: 175,395,041 bytes.
- `system.img` + `boot.img`: 182,566,457 bytes.
- Adding `l_modem.img`: 193,438,910 bytes.
- Adding `l_gdsp.img` and `l_ldsp.img`: 195,430,118 bytes.

This makes a sub-200 MB package look possible only with a carefully selected update payload and limited headroom. These are archive-member size sums, not a newly built ROM, and they exclude installer metadata and any changed compression behavior. Including the recovery image as well would push this particular subset above 200 MB. A system-only update package could be smaller, but its recovery compatibility and signing/installation path are unverified.

If the requirement instead means the raw sparse `system.img` file itself must be below 200 MB, the current 323.68 MB image does not meet it. The 600 MiB expanded system partition is a separate limit and cannot be reduced without verified partition capacity and a tested migration plan.

## Next engineering steps

1. Resolve the device/platform identifier discrepancy (F17A1H vs reported F17ALH; `sp9820a_3c10` vs `sp9820a_1c10`) and obtain exact partition capacities.
2. Preserve the current stock boot, recovery, modem, DSP, and NV images as read-only references.
3. Build the keypad-first 3-column launcher as a separate Android application before changing system services.
4. Identify the existing home app/default HOME intent and test key navigation on the target.
5. Profile the stock camera using actual device logs before changing any camera component.
6. Make a package-size plan that separates optional preinstalled apps from radio, telephony, camera, input, and system dependencies.
7. Establish an authenticated, reversible install/restore process before any device test.

## Release status

This is a firmware-analysis report, not a ROM build. No Android Lite OS launcher has been compiled, no system image has been modified, no flashable package exists, and nothing has been tested on hardware.
