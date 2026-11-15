#!/usr/bin/env python3
"""Static source checks for the Android 4.4 keypad-first launcher prototype."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
LAUNCHER = ROOT / "launcher"
MANIFEST = LAUNCHER / "app/src/main/AndroidManifest.xml"
JAVA = LAUNCHER / "app/src/main/java/org/andromaxlite/launcher/MainActivity.java"
APP_GRADLE = LAUNCHER / "app/build.gradle"
STRINGS = LAUNCHER / "app/src/main/res/values/strings.xml"
STYLE = LAUNCHER / "app/src/main/res/values/styles.xml"
DRAWABLE = LAUNCHER / "app/src/main/res/drawable/app_tile.xml"
ANDROID = "{http://schemas.android.com/apk/res/android}"


def require(condition: bool, message: str) -> None:
    if not condition:
        raise SystemExit("Launcher source check failed: " + message)


def main() -> int:
    for path in (MANIFEST, JAVA, APP_GRADLE, STRINGS, STYLE, DRAWABLE):
        require(path.is_file(), f"missing {path.relative_to(ROOT)}")

    # Catch malformed Android XML before an Android SDK is available.
    for path in LAUNCHER.rglob("*.xml"):
        ET.parse(str(path))

    manifest = ET.parse(str(MANIFEST)).getroot()
    require(manifest.attrib.get("package") == "org.andromaxlite.launcher",
            "unexpected launcher package")
    uses_sdk = manifest.find("uses-sdk")
    require(uses_sdk is not None, "manifest must declare SDK levels")
    require(uses_sdk.attrib.get(ANDROID + "minSdkVersion") == "19",
            "minimum SDK must remain API 19")
    require(uses_sdk.attrib.get(ANDROID + "targetSdkVersion") == "19",
            "target SDK must remain API 19")

    activity = manifest.find("./application/activity")
    require(activity is not None, "launcher activity missing")
    actions = {node.attrib.get(ANDROID + "name")
               for node in activity.findall("./intent-filter/action")}
    categories = {node.attrib.get(ANDROID + "name")
                  for node in activity.findall("./intent-filter/category")}
    require("android.intent.action.MAIN" in actions, "MAIN action missing")
    require("android.intent.category.HOME" in categories, "HOME category missing")
    require("android.intent.category.DEFAULT" in categories, "DEFAULT category missing")

    permissions = manifest.findall("uses-permission")
    permission_names = {node.attrib.get(ANDROID + "name") for node in permissions}
    require("android.permission.INTERNET" not in permission_names,
            "launcher must not request network access")

    java = JAVA.read_text(encoding="utf-8")
    require("appGrid.setNumColumns(3)" in java, "launcher must use a three-column grid")
    for key in ("KEYCODE_DPAD_CENTER", "KEYCODE_ENTER", "KEYCODE_MENU",
                "KEYCODE_1", "KEYCODE_9", "KEYCODE_0"):
        require(key in java, f"physical-key mapping missing: {key}")
    require("queryIntentActivities" in java, "launcher must enumerate launchable apps")
    require("setOnItemClickListener" in java, "launcher selection handler missing")

    gradle = APP_GRADLE.read_text(encoding="utf-8")
    require("com.android.tools.build:gradle:3.0.1" in
            (LAUNCHER / "build.gradle").read_text(encoding="utf-8"),
            "legacy Android Gradle plugin declaration missing")
    require("minSdkVersion 19" in gradle, "Gradle minimum SDK must be API 19")
    require("androidx" not in gradle.lower() and "appcompat" not in gradle.lower(),
            "prototype must not add AndroidX/AppCompat dependencies")

    print("Launcher source checks passed (XML and static keypad/HOME checks only; APK not compiled).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
