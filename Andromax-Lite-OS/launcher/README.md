# Andromax Lite Launcher (prototype)

This is the first native Android launcher source for Andromax Lite OS.

## Target

- Android 4.4.4 / API 19 minimum.
- ARMv7 target inherited from the inspected firmware.
- Three-column application grid.
- D-pad navigation and center/Enter to launch.
- Number keys 1–9 and 0 select grid entries 1–10.
- MENU refreshes the application list.
- Returning from another app refreshes launch targets while preserving the selected app when it still exists.
- No AppCompat, AndroidX, support libraries, network permissions, or runtime services.

## Build

This project uses the Android Gradle Plugin 3.0.1 and Gradle 4.1 for compatibility with the Android 4.4-era SDK target. Build with JDK 8 and Android SDK platform 26 / build tools 26.0.2:

```sh
gradle assembleDebug
```

The APK was compiled successfully in GitHub Actions and its ZIP structure was checked. It is a development/debug prototype only; it has not been installed or tested on an Andromax Prime handset. Do not select it as the permanent system launcher until target-device validation and a stock-HOME rollback path are complete.

## Navigation

- D-pad: move focus.
- Center / Enter: launch focused application.
- 1–9: select first nine applications.
- 0: select the tenth application.
- MENU: refresh application list.
- BACK: stays on the launcher screen.

The prototype deliberately does not modify Android framework, input configuration, boot/recovery images, camera HAL, or radio partitions.

The first CI build produced a 12,706-byte APK with SHA-256 `40d5ee182e1ee6efbd1aae44045f9ac85f8c34c2905ba0cbaff7c84828e34032`. The launcher now refreshes on resume and restores the previously selected app where possible. The change needs a fresh CI build and remains untested on physical hardware.
