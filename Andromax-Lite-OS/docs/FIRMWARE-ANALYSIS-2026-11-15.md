# Firmware Analysis — 2026-11-15

## Scope and safety

Source archive: `Firmware+Andromax+Prime+F17A1H.zip`  
Archive size: 203,210,513 bytes.

This is an offline, read-only inspection. The archive was integrity-tested and its image files were extracted into a temporary analysis workspace. No phone was connected, no partition was written, and no image was flashed or modified.

## Archive integrity and contents

- ZIP integrity test: PASS (no corrupt member reported).
- Entries: 19 total (one directory and 18 image files).
- Important correction: the ZIP contains 18 image files, not 19.
- The archive contains no obvious scatter/partition XML or Android build metadata in the listed members.

| Image | Stored bytes | Initial identification |
|---|---:|---|
| boot.img | 20,971,520 | Starts with `SPRD-SECUREFLAG` wrapper |
| cache.img | 5,533,916 | Android sparse image; expands to 157,286,400 bytes |
| devicelog.img | 31,457,280 | Unknown/vendor data; starts with zero bytes |
| fbootlogo.img | 1,048,576 | BMP-like header |
| l_fixnv1.img | 1,048,576 | NV data; starts with `VN` |
| l_fixnv2.img | 1,048,576 | NV data; byte-identical to l_fixnv1.img |
| l_gdsp.img | 3,145,728 | Starts with `SPRD-SECUREFLAG` wrapper |
| l_ldsp.img | 3,145,728 | Starts with `SPRD-SECUREFLAG` wrapper |
| l_modem.img | 15,728,640 | Starts with `SPRD-SECUREFLAG` wrapper |
| l_runtimenv1.img | 1,048,576 | NV data; starts with `VN` |
| l_runtimenv2.img | 1,048,576 | NV data; byte-identical to l_runtimenv1.img |
| logo.img | 1,048,576 | BMP-like header; byte-identical to fbootlogo.img |
| misc.img | 1,048,576 | Unknown/vendor data; starts with zero bytes |
| miscdata.img | 1,048,576 | Unknown/vendor data; starts with ASCII-like marker |
| pm_sys.img | 1,048,576 | Starts with `SPRD-SECUREFLAG` wrapper |
| prodnv.img | 5,242,880 | Unknown/vendor data; starts with zero bytes |
| recovery.img | 20,971,520 | Starts with `SPRD-SECUREFLAG` wrapper |
| system.img | 323,678,352 | Android sparse image; expands to 629,145,600 bytes |

## Sparse image details

Both `system.img` and `cache.img` use Android sparse image format v1.0:
- Block size: 4096 bytes.
- system.img: 153,600 blocks, 1,375 chunks, expanded size 629,145,600 bytes (600 MiB).
- cache.img: 38,400 blocks, 16 chunks, expanded size 157,286,400 bytes (150 MiB).

The system image was expanded in the temporary workspace using a read-only sparse-image decoder. The result is identified as an ext4 filesystem. The ext4 superblock reports:
- 38,400 inodes.
- 153,600 filesystem blocks.
- 4 KiB block size.
- ext4 magic `0xEF53`.

The expanded image is an analysis copy only; the original ZIP and sparse image remain unchanged.

## Cryptographic hashes (SHA-256)

| Image | SHA-256 |
|---|---|
| boot.img | `f134795c3e61fdec2375eca92c72f05137652248b544a4b016e080d77d23d0c3` |
| cache.img | `829a0b204c89eee942ff163940787f70d96190cf32b31ff26b09fee00b5cb3be` |
| devicelog.img | `6eb082ce07fe3c1b41e6eb9d5536b4bac2dec6788d1a09b752239b86d3edc4e7` |
| fbootlogo.img | `2f0e7b956b36ee8743af05b576940ca7a735ef982a5b345b5811a2a1162e4de9` |
| l_fixnv1.img | `bfaf073a6d68ef4826b714b81b519917e38b148919f3d5f6fb43f38630f97038` |
| l_fixnv2.img | `bfaf073a6d68ef4826b714b81b519917e38b148919f3d5f6fb43f38630f97038` |
| l_gdsp.img | `569d6d8e3d223fa1ad1db50f31e7e916d3968ab44b3a67e24e4dee1b3c931b6b` |
| l_ldsp.img | `719c4ecd8e64ba7b304638b759afc6efa73a22999ecff09dcaac5c7156343eec` |
| l_modem.img | `47807382a49b3064cb6e41f18e2d93684936c50bae88ebd3fa2bfed01847387e` |
| l_runtimenv1.img | `3ed85c7b14317c60878b918e280ad4ae1c4a535d04991c7f3f2722d1c7e069ad` |
| l_runtimenv2.img | `3ed85c7b14317c60878b918e280ad4ae1c4a535d04991c7f3f2722d1c7e069ad` |
| logo.img | `2f0e7b956b36ee8743af05b576940ca7a735ef982a5b345b5811a2a1162e4de9` |
| misc.img | `30e14955ebf1352266dc2ff8067e68104607e750abb9d3b36582b8af909fcb58` |
| miscdata.img | `53badcf6a89c6a4222f962fd2a7d3a985aa2e7aa23a4eaf94a3d01eb5a4d3be4` |
| pm_sys.img | `f424041a48686271524eb2c98202c4ab82f47a7b1ab786d9f0cb1fff58b7d0b1` |
| prodnv.img | `d3da68f067feeaccbb6e83a8ad8ef4904dd91d279d511524eb36a70e7ebae7b6` |
| recovery.img | `41c505a98844ba67c8cdc19bd0ad112c3ad445608e3f96ec90e8834d77db1bfd` |
| system.img | `9961784bb57db72df14decc5123af77d3ba7bfff9ce835eda7ced12c6a619532` |

## Findings and unresolved questions

1. The archive contains a 600 MiB expanded ext4 system filesystem. The 323.7 MB figure is the compressed sparse file size, not the expanded filesystem size.
2. Boot and recovery are not plain Android boot images at byte zero; both begin with the vendor marker `SPRD-SECUREFLAG`. The exact wrapper structure and signing requirements remain unverified. Repacking these images without the correct vendor signing process may produce images the boot chain rejects.
3. Several modem/DSP/PM images also begin with the same vendor marker. These must be preserved unless their formats and dependencies are understood.
4. Duplicate image pairs were found: l_fixnv1/l_fixnv2 and l_runtimenv1/l_runtimenv2 are byte-identical; logo/fbootlogo are byte-identical.
5. The archive alone does not yet establish the Android release, SoC, board/codename, display resolution, partition map, or the F17A1H vs F17ALH identity conflict.
6. The original 600 MiB system filesystem cannot be assumed to fit a 200 MB target ROM. A ROM's flash image size and runtime storage requirements must be measured separately, and the requested 200 MB goal needs feasibility analysis.

## Next analysis steps

- Parse the ext4 filesystem tree and locate `build.prop`, `default.prop`, framework manifests, native libraries, and vendor configuration.
- Inspect the SPRD wrapper metadata and identify whether the boot/recovery payloads can be safely unpacked without modifying or re-signing them.
- Identify Android version, build fingerprint, architecture, device/board identifiers, and display properties from system properties.
- Establish the actual partition layout and image-size limits from matching device metadata or a verified device dump.
- Only after those facts are known, plan minimal system changes and a reversible test strategy.

## Release status

This report is not a ROM build. No flashable image has been produced or tested on hardware.
