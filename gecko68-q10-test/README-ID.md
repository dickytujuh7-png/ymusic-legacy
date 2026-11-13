# YouTube Q10 — GeckoView 68

Aplikasi Android APK ringan untuk eksperimen BlackBerry Q10 / BB10 OS 10.3.

## Dasar teknis
- GeckoView 68.0.20190611143747 (ARMv7)
- Android API minimum 18, target 18
- Antarmuka gelap sederhana yang disesuaikan untuk layar Q10
- Kolom pencarian, Beranda, Muat ulang, Kembali, dan Lanjut
- Membuka situs mobile YouTube di dalam GeckoView

## Build
GitHub Actions workflow: `.github/workflows/gecko68-q10-build.yml`

Artifact: `gecko68-q10-debug-apk`

## Catatan kompatibilitas
APK harus diuji langsung pada Android Runtime BlackBerry Q10. Keberhasilan kompilasi tidak otomatis membuktikan semua halaman YouTube atau pemutaran media didukung GeckoView 68. YouTube dapat mengubah persyaratan situsnya; bila halaman modern tidak kompatibel, pencarian atau pemutaran dapat gagal. Versi ini adalah fondasi aplikasi ringan, belum menjamin mode audio-only atau pemutaran latar belakang.
