# Skill - Click Android Studio Run Button (click_as_Run)

This skill instructs the assistant on how to focus the Android Studio window and trigger the "Run" shortcut (`Shift + F10`) to build and launch the application on the connected device or emulator (without triggering the "Debug" button).

---

## Trigger Phrases
- "click_as_Run"
- "click_run"
- "r_"

---

## Steps to Execute

### 1. Execute Window Activation and Keypress Simulation
Run the following PowerShell command to locate the `studio64` process, activate/focus its window, wait for focus, and send `Shift + F10` (the standard Android Studio Run shortcut):

```powershell
powershell -Command "Add-Type -AssemblyName System.Windows.Forms; $wshell = New-Object -ComObject wscript.shell; $app = Get-Process | Where-Object {\$_.ProcessName -like '*studio*'} | Select-Object -First 1; if (\$app) { \$activated = \$wshell.AppActivate(\$app.Id); if (-not \$activated) { \$activated = \$wshell.AppActivate('studio64') }; if (\$activated) { Start-Sleep -Milliseconds 400; [System.Windows.Forms.SendKeys]::SendWait('+{F10}'); Write-Output 'Successfully focused Android Studio and triggered Run (Shift+F10).' } else { Write-Output 'Found Android Studio process but could not bring the window to the foreground (AppActivate returned False).' } } else { Write-Output 'Android Studio process not found.' }"
```

### 2. Report Success
- Print a clear message to the user confirming whether Android Studio was successfully focused and the key combination sent.
