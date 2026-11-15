# Andromax F17A1H read-only diagnostic collector

This Windows batch file collects device properties and hardware/partition inventory over ADB. It does **not** write to the phone, change settings, unlock the bootloader, or flash firmware.

## Run

1. Install Google's Android Platform-Tools and enable USB debugging on the phone.
2. Connect the phone, unlock it, and approve the computer's USB debugging prompt.
3. Put `collect_device_info.bat` in the Platform-Tools folder (or ensure `adb.exe` is on PATH).
4. Double-click the batch file.
5. Review the generated `Andromax-F17A1H-Diagnostic` folder before sharing it.

The collector excludes common serial/IMEI property lines from `getprop`. Other diagnostic outputs may still contain identifying or network information; review them before sharing. The output is for analysis only and does not establish that a ROM is safe to flash.
