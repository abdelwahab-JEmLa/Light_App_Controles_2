---
name: build_apk_au_playe_store_phone
description: Compile and deploy the Tahfid Quran application APK to the connected phone's Playe_Store/app_tahfid/ storage folder. Trigger this when the user says "build_PS", "build_apk_au_playe_store_phone", "build_tahfid", "b_tahfid", "bt_", or asks to build and push the tahfid APK to the phone.
---

# Skill - Build APK and Deploy to Playe Store Phone (Tahfid App)

This skill instructs the assistant on how to compile the application and deploy it to the connected phone's SD Card specifically under the `Playe_Store/app_tahfid/<VERSION>/` directory when working on the Tahfid Quran version of the application.

---

## Trigger Phrases
- "build_PS"
- "build_apk_au_playe_store_phone"
- "build_tahfid"
- "b_tahfid"
- "bt_"

---

## Steps to Execute

### 1. Verify Active Branch / Application Type
Verify that you are on the `app_tahfid_quran` branch or that the project is configured for the Tahfid app:
```powershell
git branch --show-current
```

### 2. Read the Application Version Name
Extract the current `versionName` value from [app/build.gradle.kts](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/build.gradle.kts) (e.g. `1.13.6`).

### 3. Compile the Application
Run the Gradle wrapper script `gradlew.bat` in the project root directory to compile and assemble the debug APK:
```powershell
.\gradlew.bat assembleDebug --offline --parallel --build-cache --configuration-cache
```

### 4. Create Export Directory on Desktop
Create the export directory on the local Desktop under the `app_tahfid` subfolder structure:
```powershell
New-Item -ItemType Directory -Force -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>"
```
*(Replace `<VERSION>` with the version name read in step 2).*

### 5. Copy APK to Destination
Copy the built APK `app-debug.apk` to the newly created local folder:
```powershell
Copy-Item -Path "app\build\outputs\apk\debug\app-debug.apk" -Destination "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\app-debug.apk" -Force
```

### 6. Create Phone Storage Directory & Deploy via ADB
Create the destination folder structure on the phone and push the compiled APK directly to the phone's SD Card storage using the ADB tool:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell mkdir -p /sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/<VERSION>/
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" push "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\<VERSION>\app-debug.apk" "/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/<VERSION>/"
```

### 7. Report Success
Provide the user with a detailed summary showing:
- **Branche Active** : The active Git branch verified in step 1.
- **Version Détectée** : The versionName read from `build.gradle.kts`.
- **Chemin de l'export local** : Clickable link to the local folder on Desktop.
- **Chemin de déploiement SD Card (APK)** : The destination path on the Android device (`/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/<VERSION>/app-debug.apk`).
- **Confirmation de transfert** : Confirmation that compilation succeeded and the APK was pushed to the phone.
