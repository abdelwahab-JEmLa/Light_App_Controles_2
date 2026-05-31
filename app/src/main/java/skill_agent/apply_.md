# Skill - Fast Apply Changes and Launch

This skill instructs the assistant on how to compile, deploy, and launch the application with maximum speed (surpassing standard Android Studio deployment speed) by utilizing a targeted app compilation, warm Gradle daemon caches, and ADB's direct high-speed deployment stream (including fastdeploy when compatible), then automatically verifying the UI state.

---

## Trigger Phrases
- "apply_"
- "ap_"
- "fast_launch"
- "fast_l"

---

## Steps to Execute

### 1. High-Speed Targeted Compilation
Run the Gradle wrapper targeting only the `:app:assembleDebug` task, enabling complete caching, parallel execution, offline mode, and configure-on-demand:
```powershell
.\gradlew.bat :app:assembleDebug --offline --parallel --build-cache --configuration-cache --configure-on-demand
```
*Note: This avoids unnecessary tasks and uses cached configurations to start compiling within a few seconds.*

### 2. High-Speed Streamed Deployment via ADB
Deploy the newly compiled APK directly to the connected device or emulator using ADB with reinstall, test package, downgrade, and fastdeploy flags:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" install -r -t -d --fastdeploy app\build\outputs\apk\debug\app-debug.apk
```
*Note: ADB automatically optimizes the stream and uses fastdeploy deltas if supported by the device, falling back to a direct high-speed streamed install otherwise.*

### 3. Launch the Application Activity
Immediately start the launcher activity on the device:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.light_app_controles/com.example.light_app_controles.A.Main.MainActivity
```

### 4. Capture the Screen with Annotations
Take a verification screenshot with accessibility annotations:
```powershell
android screen capture -a -o screen.png
```

### 5. View and Display the Screenshot
Immediately read and display the screenshot to the user using the `view_file` tool:
- Path: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\screen.png`

### 6. Report Success
Provide the user with a confirmation of the rapid deployment along with a direct markdown link to [screen.png](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/screen.png) for verification.
