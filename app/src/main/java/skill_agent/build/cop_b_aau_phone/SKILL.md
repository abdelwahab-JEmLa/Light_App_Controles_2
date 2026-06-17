---
name: cop_b_aau_phone
description: Copy/push the latest compiled build (from Desktop playe_store/app_tahfid directory) directly to the phone via ADB without recompiling. Trigger this when the user says "cop_b_aau_phone", "colle_b", "push_last_build", or "cop_b_phone".
---

# Skill - Copy Last Build to Phone (Tahfid App)

This skill describes how to deploy the latest compiled build and ZIP archive from the Desktop to the connected phone via ADB without recompiling the project. Use this when the device was disconnected during a build or when you want to deploy a previously compiled build.

---

## Trigger Phrases
- "cop_b_aau_phone"
- "colle_b"
- "push_last_build"
- "cop_b_phone"

---

## Steps to Execute

### 1. Identify the Latest Compiled Version
Find the most recent directory inside `C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\`.
Run this PowerShell command to automatically find the latest version name:
```powershell
$latestDir = Get-ChildItem -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid" -Directory | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $latestDir) {
    Write-Error "No build folders found in C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\"
    exit 1
}
$version = $latestDir.Name
Write-Output "Latest version found: $version"
```

### 2. Verify ADB Device Connection
Check if the phone is connected and authorized:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices
```
If no device is listed as `device` (e.g. it shows `unauthorized` or is empty), report it to the user.

### 3. Deploy/Push to Phone Storage
Create the directory on the phone and push the ZIP archive and the raw folder:
```powershell
$version = (Get-ChildItem -Path "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid" -Directory | Sort-Object LastWriteTime -Descending | Select-Object -First 1).Name

& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell mkdir -p /sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/$version/
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" push "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\$version.zip" "/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/"
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" push "C:\Users\Abou Mohamed\Desktop\Playe_Store\app_tahfid\$version\" "/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/$version"
```

### 4. Report Success
Provide a detailed summary:
- **Version Déployée** : The detected version name (e.g., `1.13.8.06_17.16_34`).
- **Chemin Source Local** : [Link to Desktop folder](file:///C:/Users/Abou%20Mohamed/Desktop/Playe_Store/app_tahfid/<VERSION>)
- **Chemin Destination SD Card** : `/sdcard/Abdelwahab_jeMla.com/Playe_Store/app_tahfid/<VERSION>/app-debug.apk`
- **Statut de Transfert** : Confirmation that files were successfully transferred via ADB.
