# Andromax Lite Launcher (prototype)

This is the first native Android launcher source for Andromax Lite OS.

## Target

- Android 4.4.4 / API 19 minimum.
- ARMv7 target inherited from the inspected firmware.
- Three-column application grid.
- D-pad navigation and center/Enter to launch.
- Number keys 1–9 and 0 select grid entries 1–10.
- MENU refreshes the application list.
- No AppCompat, AndroidX, support libraries, network permissions, or runtime services.

## Build

This project uses the Android Gradle Plugin 3.0.1 and Gradle 4.1 for compatibility with the Android 4.4-era SDK target. Build with JDK 8 and Android SDK platform 26 / build tools 26.0.2:

```sh
gradle assembleDebug
```

The generated APK is a development prototype only. It has not been compiled in this workspace or tested on an Andromax Prime handset. Do not install it as the system launcher until target-device validation is complete.

## Navigation

- D-pad: move focus.
- Center / Enter: launch focused application.
- 1–9: select first nine applications.
- 0: select the tenth application.
- MENU: refresh application list.
- BACK: stays on the launcher screen.

The prototype deliberately does not modify Android framework, input configuration, boot/recovery images, camera HAL, or radio partitions.
