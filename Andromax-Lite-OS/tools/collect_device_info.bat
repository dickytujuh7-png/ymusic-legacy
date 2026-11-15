@echo off
setlocal EnableExtensions
title Andromax Lite OS - Read-only device collector

set "ADB=adb"
where adb >nul 2>&1
if errorlevel 1 (
  echo ADB tidak ditemukan.
  echo Pasang Android Platform-Tools dan tambahkan folder platform-tools ke PATH,
  echo atau jalankan file ini dari folder platform-tools.
  pause
  exit /b 2
)

set "OUT=%~dp0Andromax-F17A1H-Diagnostic"
if not exist "%OUT%" mkdir "%OUT%"

echo Memeriksa perangkat...
adb start-server
adb get-state >nul 2>&1
if errorlevel 1 (
  echo Perangkat belum terdeteksi atau belum memberi izin USB debugging.
  adb devices
  echo.
  echo Buka kunci ponsel dan pilih Allow pada dialog USB debugging.
  pause
  exit /b 3
)

echo Mengumpulkan properti sistem yang tidak memuat nomor seri...
adb shell getprop | findstr /i /v "serialno serialnumber imei meid deviceid" > "%OUT%\getprop-redacted.txt"
adb shell cat /proc/cpuinfo > "%OUT%\cpuinfo.txt" 2>&1
adb shell cat /proc/meminfo > "%OUT%\meminfo.txt" 2>&1
adb shell cat /proc/partitions > "%OUT%\partitions.txt" 2>&1
adb shell mount > "%OUT%\mounts.txt" 2>&1
adb shell df > "%OUT%\filesystem-space.txt" 2>&1
adb shell ls -l /dev/block/platform/sdio_emmc/by-name > "%OUT%\emmc-by-name.txt" 2>&1
adb shell "cat /fstab.* /etc/recovery.fstab 2>/dev/null" > "%OUT%\fstab.txt" 2>&1
adb shell lsmod > "%OUT%\kernel-modules.txt" 2>&1
adb shell dumpsys sensorservice > "%OUT%\sensors.txt" 2>&1
adb shell dumpsys media.camera > "%OUT%\camera-service.txt" 2>&1

echo.
echo Selesai. Hasil tersimpan di:
echo %OUT%
echo.
echo Periksa dan hapus sendiri informasi pribadi jika ada sebelum membagikan folder ini.
echo Pengumpulan ini hanya membaca data; tidak mengubah partisi atau firmware.
pause
