# FN External Panel V1

Android overlay + Shizuku permission/status integration. AIMBOT/ESP are **local settings panels only**; no game data acquisition, aiming, or player rendering is implemented. No root required.

## Build using only a phone
1. Open Actions → Build Android APK → Run workflow.
2. Download artifact `FNExternalPanel-debug`, extract ZIP, install `app-debug.apk`.
3. Install and start Shizuku separately; grant the app permission in Shizuku. Grant Android overlay permission.
4. Open floating panel. Long-press its title to close it, or close from main app.

The workflow uses Gradle 8.9; no local wrapper required.

Build has not been run or verified.