# Skill - PC Specialist (skill_pc)

This skill instructs the assistant on how to act as a PC Hardware & System Specialist, executing deep diagnostics on the host Windows machine to gather OS details, CPU load, RAM usage, storage space, network status, and top running processes.

---

## Trigger Phrases
- `skill_pc`
- `pc_spec`
- `pc_status`
- `diagnose_pc`

---

## Steps to Execute

### 1. Execute System Diagnostics
Run the following PowerShell command to collect comprehensive PC performance, hardware, and network metrics:

```powershell
powershell -Command "
$os = Get-CimInstance Win32_OperatingSystem;
$cpu = Get-CimInstance Win32_Processor;
$cs = Get-CimInstance Win32_ComputerSystem;
$disks = Get-Volume | Where-Object DriveLetter;
$ip = Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '127.*' -and $_.InterfaceAlias -notlike '*Loopback*' };
$procsCPU = Get-Process | Sort-Object CPU -Descending | Select-Object -First 5;
$procsRAM = Get-Process | Sort-Object WorkingSet64 -Descending | Select-Object -First 5;

Write-Output '=== OS INFO ===';
Write-Output ('OS: ' + $os.Caption + ' (' + $os.OSArchitecture + ')');
Write-Output ('Version: ' + $os.Version);
Write-Output ('Uptime: ' + ([DateTime]::Now - $os.LastBootUpTime).ToString('d\.hh\:mm\:ss'));

Write-Output '=== CPU & RAM ===';
Write-Output ('CPU: ' + $cpu.Name);
Write-Output ('Cores: ' + $cpu.NumberOfCores + ' Cores / ' + $cpu.NumberOfLogicalProcessors + ' Threads');
Write-Output ('Total RAM: ' + [Math]::Round($os.TotalVisibleMemorySize / 1MB, 2) + ' GB');
Write-Output ('Free RAM: ' + [Math]::Round($os.FreePhysicalMemory / 1MB, 2) + ' GB (' + [Math]::Round(($os.FreePhysicalMemory / $os.TotalVisibleMemorySize) * 100, 1) + '% free)');

Write-Output '=== STORAGE ===';
$disks | ForEach-Object {
    $size = [Math]::Round($_.Size / 1GB, 2);
    $free = [Math]::Round($_.SizeRemaining / 1GB, 2);
    $used = $size - $free;
    $pct = [Math]::Round(($used / $size) * 100, 1);
    Write-Output ('Drive ' + $_.DriveLetter + ': [' + $_.FileSystemLabel + '] ' + $used + 'GB / ' + $size + 'GB used (' + $pct + '%)');
};

Write-Output '=== NETWORK ===';
$ip | ForEach-Object {
    Write-Output ($_.InterfaceAlias + ': ' + $_.IPAddress);
};

Write-Output '=== TOP CPU PROCESSES ===';
$procsCPU | ForEach-Object {
    Write-Output ($_.Name + ' (PID: ' + $_.Id + ') - CPU: ' + [Math]::Round($_.CPU, 1));
};

Write-Output '=== TOP RAM PROCESSES ===';
$procsRAM | ForEach-Object {
    Write-Output ($_.Name + ' (PID: ' + $_.Id + ') - RAM: ' + [Math]::Round($_.WorkingSet64 / 1MB, 1) + ' MB');
};
"
```

### 2. Format and Display Report
Parse the output of the command and display a beautifully formatted Markdown report with sections, tables, and system health status.

---

## 🔗 Direct Links
* 🖥 [PC Specialist Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/skill_pc.md)
* ⚙️ [PC Help Page (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/hw_.md)
