@echo off
setlocal
title Setup WSL dan Ubuntu

:: Minta hak Administrator secara otomatis
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Meminta izin Administrator...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Process -FilePath '%~f0' -Verb RunAs"
    exit /b
)

set "STAGE=%~dp0wsl_setup_stage.txt"

echo.
echo ==========================================
echo       PERSIAPAN WSL UNTUK WINDOWS 10
echo ==========================================
echo.

if not exist "%STAGE%" goto ENABLE

echo Komponen Windows sudah pernah diaktifkan.
echo Sekarang mencoba memasang Ubuntu...
echo.

wsl --install -d Ubuntu --web-download
if %errorlevel% neq 0 (
    echo.
    echo Instalasi melalui web-download tidak berhasil.
    echo Mencoba perintah instalasi standar...
    wsl --install -d Ubuntu
)

echo.
echo Jika Ubuntu berhasil terpasang, jendela Ubuntu akan meminta
echo nama pengguna Linux dan kata sandi.
echo Saat mengetik kata sandi, huruf memang tidak terlihat.
echo.
pause
exit /b

:ENABLE
echo Mengaktifkan Windows Subsystem for Linux...
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
set "RC=%errorlevel%"
if "%RC%"=="0" goto ENABLE_VMP
if "%RC%"=="3010" goto ENABLE_VMP
goto FAILED

:ENABLE_VMP
echo.
echo Mengaktifkan Virtual Machine Platform...
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
set "RC=%errorlevel%"
if "%RC%"=="0" goto FEATURES_DONE
if "%RC%"=="3010" goto FEATURES_DONE
goto FAILED

:FEATURES_DONE
echo.
echo Kedua komponen WSL berhasil diaktifkan.
echo Windows perlu di-restart sebelum pemasangan Ubuntu.
echo. > "%STAGE%"
echo.
choice /C YN /M "Restart komputer sekarang"
if errorlevel 2 goto LATER
shutdown /r /t 10
echo Komputer akan restart dalam 10 detik.
pause
exit /b

:LATER
echo.
echo Baik. Restart Windows secara manual nanti.
echo Setelah restart, klik dua kali file ini lagi.
pause
exit /b

:FAILED
echo.
echo Ada komponen yang gagal diaktifkan.
echo Catat seluruh pesan error di atas lalu kirimkan fotonya.
pause
exit /b
