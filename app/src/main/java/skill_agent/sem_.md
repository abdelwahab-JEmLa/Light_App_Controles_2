# Skill - Real-Time Semantics Inspector (sem_)

This skill instructs the assistant on how to automatically detect any dynamic developer TODO comment containing `sem_` or `filter` (e.g., `//TODO: sem_ $variable` or `//TODO: filter $variable`) next to any layout container (Column, LazyColumn, Box, Row, etc.), automatically inject the corresponding custom semantics block directly onto the outer container (such as the Box or Column), remove the comment, and **immediately dump the active Android UI hierarchy using ADB uiautomator dump without rebuilding the app**, then extract and display the semantics information to the user in near real-time.

---

## Trigger Phrases
- "sem_"
- "sem_d"
- "Todo: sem_"
- "Todo: filter"

---

## Steps to Execute

### 1. Locate Dynamic Semantics TODO Comments
Search the codebase (`app/src/main/java`) for any dynamic semantics/filter comments using the `grep_search` tool:
- Query: `TODO: sem_` or `TODO: filter` (case-insensitive)
- Extract the file name, line number, and targeted variable/filter expression.

### 2. Inject Semantics Modifier
Inspect the surrounding lines of the TODO comment:
- Locate the adjacent Jetpack Compose layout container (e.g., `Column`, `LazyColumn`, `Box`, `Row`, etc.).
- Inject a Jetpack Compose `.semantics` modifier setting the variable as a custom semantics property.
- **Critical Placement:** If a `Box` or layout container surrounds the code where the `TODO` is written, prefer injecting the `.semantics` modifier directly into that outer layout container (like `Box` or `Column`), rather than inner elements, to keep semantics clean at the root of the component:
  ```kotlin
  Box(modifier = Modifier.semantics(mergeDescendants = true) {
      set(value = $variable, key = SemanticsPropertyKey("$variable"))
  }) {
  ```
- Remove the triggering `TODO` comment from the source file.

### 3. Dump and Pull UI Hierarchy via ADB (No Rebuild)
Do NOT run any Gradle compilation or build tasks. Directly capture the current active screen hierarchy from the device:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell uiautomator dump /sdcard/window_dump.xml
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /sdcard/window_dump.xml
```

### 4. Parse and Display the Semantics Data
Read the pulled `window_dump.xml` using the `view_file` tool. Parse the node hierarchy corresponding to the Compose container and display the extracted semantics data structure to the user.
