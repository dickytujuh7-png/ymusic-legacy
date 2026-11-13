# YMusic Q10

A compact YouTube music discovery interface designed for BlackBerry Q10 / Android Runtime 4.3 (API 18), ARMv7.

## Design
- Square-screen friendly dark interface with red accent
- Native search field and quick-search chips
- Embedded YouTube mobile site for browsing and selecting videos
- BlackBerry native Back key navigates WebView history
- No GeckoView or external UI libraries; uses Android's built-in WebView

## Compatibility target
- minSdk 18, targetSdk 28
- ARMv7 / armeabi-v7a
- Java 8 source compatibility
- Android SDK 30 build tools

## Playback behavior
Video selection and playback are handled by YouTube's own mobile website/player. This project does not extract audio streams, bypass ads, or remove platform restrictions. Background playback, modern YouTube page compatibility, and fullscreen behavior must be tested on the actual Q10 runtime; they are not guaranteed.

GitHub Actions builds a debug APK and uploads it as an artifact.
