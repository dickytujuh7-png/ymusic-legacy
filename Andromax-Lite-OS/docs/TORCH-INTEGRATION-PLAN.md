# Torch integration plan — Andromax Prime F17A1H

Status: required feature; implementation is not yet verified.

## Compatibility constraints

The inspected firmware is Android 4.4.4 / API 19. The public Android `CameraManager.setTorchMode()` API is an API 23-era interface and must not be used as the assumed control path on this device. The torch may be controlled through the vendor camera stack, a vendor service, or a kernel LED/flash interface. The exact mechanism has not yet been established.

## Safe discovery sequence

1. Keep the stock camera APK, camera HAL (`camera.sc8830.so`), native dependencies, kernel modules, init scripts, permissions, and SELinux policy unchanged.
2. Inspect the stock firmware's camera/torch application code and resources for flashlight intents, vendor service names, JNI entry points, and documented device interfaces.
3. Inspect the extracted firmware's init/property configuration and exported native symbols for evidence of the actual vendor torch path.
4. On the exact handset, collect read-only diagnostic information while using the stock flashlight UI. Record Android build fingerprint, kernel, available LED class devices, relevant service names, and logcat messages. Do not write to guessed sysfs nodes.
5. Confirm whether the stock torch works independently of the camera preview and whether camera use releases the flash resource correctly.
6. Only after the control path and permissions are proven, implement a small API-19-compatible keypad-accessible toggle that uses that verified vendor interface. Include explicit OFF handling, error reporting, and lifecycle cleanup.
7. Test repeated ON/OFF, screen sleep/wake, incoming call, camera launch/exit, low battery, and reboot. Verify the flash is OFF after the app exits or an error occurs.

## Do not do

- Do not guess paths such as `/sys/class/leds/torch/brightness`.
- Do not write arbitrary values to sysfs or procfs.
- Do not replace or strip the camera HAL/dependencies to make the ROM smaller.
- Do not claim torch support until tested on the target hardware.

## Acceptance criteria

- Torch can be toggled with physical keypad only.
- UI always reflects actual torch state or reports an unknown/error state.
- Flash turns OFF reliably on exit, app crash recovery, and reboot.
- Camera still starts and records/photos normally after torch use.
- No persistent wakelock or background service is required unless the verified vendor implementation proves it necessary.
