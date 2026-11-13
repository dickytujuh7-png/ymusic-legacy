# GeckoView 68 — BlackBerry Q10 test

Ini APK uji mesin Gecko, **belum aplikasi YouTube**.

- Target Android Runtime: API 18 (BB10 OS 10.3)
- ABI: ARMv7
- GeckoView: 68.0.20190611143747
- Tes: membuka https://example.com/

## Build otomatis
GitHub Actions membangun APK debug dan mengunggahnya sebagai artifact bernama `gecko68-q10-debug-apk`.

APK debug ditandatangani otomatis dengan debug key. Build sukses hanya membuktikan APK berhasil dikompilasi, bukan bahwa Android Runtime BlackBerry Q10 pasti dapat menjalankannya.

## Batasan
GeckoView 68 sudah usang. Manifest merger dipaksa mengizinkan minSdk 18 untuk eksperimen; jika library memakai API yang tidak tersedia pada runtime Q10, aplikasi dapat gagal dibuka. Jangan gunakan browser uji ini untuk login atau memasukkan kata sandi.
