# ROM size and component plan

Date: 2026-11-15  
Source: offline inspection of the supplied F17A1H firmware's extracted ext4 system image.

## Measurements

- The original ZIP archive: 203,210,513 bytes.
- The sparse `system.img`: 323,678,352 bytes.
- Expanded ext4 system filesystem: 629,145,600 bytes (600 MiB).
- ext4 allocated blocks at inspection time: 81,017 × 4,096 = 331,845,632 bytes (about 316.47 MiB).
- APK files in `/app` and `/priv-app`: 76 files totalling 74,673,599 bytes (about 71.21 MiB).
- `/lib/libwebviewchromium.so`: 21,343,688 bytes (about 20.35 MiB).

The 200 MB goal is treated as the **compressed release package** goal. It is not a target for the expanded filesystem or the raw sparse system image. File deletion savings cannot be translated 1:1 to the compressed update package; the rebuilt ext4/sparse image and ZIP must be measured after changes.

## Largest APKs found

| APK | Bytes | Approx. MiB | Initial treatment |
|---|---:|---:|---|
| `/app/WhatsApp.apk` | 36,258,577 | 34.58 | Optional bundled user app candidate; retain unless the user explicitly wants it removed and dependency checks pass |
| `/priv-app/MySmartfren.apk` | 5,223,968 | 4.98 | Carrier app; optional only after checking account/service usage |
| `/app/Gallery2.apk` | 3,686,441 | 3.51 | Keep initially; media picker/gallery dependencies may exist |
| `/priv-app/SprdVoWifiService.apk` | 3,191,465 | 3.04 | Preserve until VoWiFi/radio dependency is proven unnecessary |
| `/app/IQQI_9Key.apk` | 2,953,922 | 2.82 | Preserve until a keypad-compatible text input method is validated |
| `/priv-app/Mms.apk` | 2,927,253 | 2.79 | Preserve; SMS/MMS function |
| `/priv-app/Settings.apk` | 2,173,949 | 2.07 | Preserve; core Settings UI |
| `/priv-app/ContactsProvider.apk` | 2,016,666 | 1.92 | Preserve; contacts data provider |
| `/app/DeskClock.apk` | 1,851,511 | 1.77 | Optional candidate after alarm/clock checks |
| `/app/FMPlayer.apk` | 1,668,979 | 1.59 | Optional candidate if FM radio is not required |
| `/priv-app/SimpleHome.apk` | 1,466,692 | 1.40 | Stock HOME app; keep until Andromax Lite Launcher is installed and selected successfully |
| `/app/Camera2.apk` | 1,350,175 | 1.29 | Preserve while diagnosing camera stability |
| `/priv-app/TeleService.apk` | 1,346,607 | 1.28 | Preserve; telephony service |
| `/app/Browser.apk` | 1,128,327 | 1.08 | May be optional only after Andromax Web/WebView use cases and all dependent intents are checked |
| `/priv-app/Dialer.apk` | 1,039,147 | 0.99 | Preserve; call UI |
| `/priv-app/SystemUI.apk` | 963,488 | 0.92 | Preserve; core system UI |

## Camera HAL dependency preservation list

ELF dynamic dependencies read from `/lib/hw/camera.sc8830.so` include:

- Android platform: `libutils.so`, `libmemoryheapion.so`, `libcamera_client.so`, `libcutils.so`, `libhardware.so`, `libcamera_metadata.so`, `libui.so`, `libbinder.so`.
- Sensor/image tuning: `libae.so`, `libawb.so`, `libaf.so`, `libev.so`.
- Vendor features: `libts_face_beautify_hal.so`, `libsprd_easy_hdr.so`.
- Runtime: `libdl.so`, `libc.so`, `libstdc++.so`, `libm.so`.

These are direct ELF dependencies reported by readelf. Transitive dependencies and runtime-loaded libraries may exist beyond this list. Keep the HAL and all its dependencies untouched while collecting camera logs and reproducing the hang. Removing one of these libraries could break Camera2 or other camera clients.

## System components to preserve in the initial ROM branch

Keep all of the following until a complete replacement and regression test exists:
- Telephony: Dialer, TeleService, Mms, TelephonyProvider, CarrierConfig and modem/DSP firmware.
- User data: Contacts, ContactsProvider, SettingsProvider, DownloadProvider.
- Platform UI: SystemUI, Keyguard, Settings, InputDevices, PackageInstaller.
- Connectivity: Bluetooth, Wi-Fi components, VoWiFi service, network configuration.
- Camera: Camera2, camera HAL, camera client/service libraries and every listed vendor dependency.
- Key input: existing SCI keypad layout files and keyboard/input method components.
- Web rendering: `libwebviewchromium.so` and related WebView framework components. A separate browser APK is not the same thing as Android WebView.

## Staged size reduction plan

1. Keep a byte-for-byte stock image backup and checksums.
2. Build the launcher as a separate APK and validate it before replacing `SimpleHome.apk`.
3. Inventory app manifests, shared UIDs, providers, services, receivers, permissions, odex files and framework references before considering any app removal.
4. Start with optional preinstalled apps that are not needed by the owner (for example bundled third-party apps), and measure rebuilt sparse-image and archive sizes after each isolated change.
5. Do not remove carrier, modem, DSP, NV, telephony, camera, input, WebView, SystemUI, or Settings components to chase the package-size target.
6. Keep a clear rollback image and stop if any dependency or boot behavior is unclear.

## Current decision

No stock APK has been deleted. No system partition image has been changed. This document is an optimization plan based on static file inventory, not a tested removal list.
