# Q10 YouTube Music

Lightweight Android app shell for BlackBerry Q10 (Android Runtime 4.3 / API 18, ARMv7).

## Build target
- 720 x 720 square display
- minSdk 18, targetSdk 28
- ARMv7 (armeabi-v7a)
- GeckoView 73.0.20200217142647 with the Android-compatible SnakeYAML variant for API 18
- YouTube mobile website for search and playback

Search for a song or artist, choose a result on YouTube, and use YouTube's own player controls.

This app does not extract audio streams, remove advertisements, or bypass YouTube restrictions. Playback and background behavior remain subject to YouTube and the device runtime.

GitHub Actions builds a debug APK and uploads it as an artifact.
