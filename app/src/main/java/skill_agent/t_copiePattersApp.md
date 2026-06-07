# Skill - Copy Coding Patterns to Light App (t_copiePattersApp)

This skill instructs the assistant on how to automatically copy, adapt, and port coding patterns, feature implementations, and fixes from the `ClientJetPack` codebase to the local `Light_App_Controles` (light app) project codebase. It guides the assistant on adjusting package imports, database calls, parameters, and layout structures to fit the light app's requirements.

---

## Trigger Phrases
- "t_copiePattersApp"
- "t>copiePattersApp"
- "copiePattersApp"
- "t_c_client"

---

## Steps to Execute

### 1. Identify Coding Patterns in the Client Codebase
- Search for the reference feature, fix, or `TODO` comment in the `ClientJetPack` project (`D:\AndroidStudioProjects\ClientJetPack`).
- Identify the target file(s) or directory containing the code and examine how the feature/logic was implemented.

### 2. Copy the Files/Folders in Bulk (Fast & Efficient)
- Instead of manual file edits, use PowerShell's `Copy-Item` to copy entire directories or files recursively from `ClientJetPack` to the appropriate location in `Light_App_Controles`:
  ```powershell
  Copy-Item -Path "D:\AndroidStudioProjects\ClientJetPack\app\src\main\java\<source_path>" -Destination "app\src\main\java\<dest_path>" -Recurse -Force
  ```

### 3. Clean Up Unused Copied Files
- Review the copied folders and delete any files or directories containing scripts that are not used by the imported components.
- Specifically delete scripts that depend on Koin repositories, wifi transfers, or client-specific databases that are not part of the light app to prevent compiler errors:
  ```powershell
  Remove-Item -Path "app\src\main\java\<dest_path>\<unused_folder>" -Recurse -Force
  ```

### 4. Verify Library Dependencies
- Check if the imported code relies on libraries not yet configured in the light app's [build.gradle.kts](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/build.gradle.kts) (e.g. iText PDF libraries: `implementation("com.itextpdf:itext7-core:7.2.5")` and `implementation("com.itextpdf:html2pdf:4.0.5")`).
- Add the necessary dependencies to the `dependencies` block of the local [build.gradle.kts](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/build.gradle.kts) if missing.

### 5. Adapt Code to Local Architecture
- Open the ported files and adjust the code to match the local architecture:
  - **Koin Injections**: Replace Koin injections (e.g. `org.koin.compose.koinInject`) with local data-binding, such as retrieving values directly from the view model's state (e.g., replacing `repo13TarificationInfos.datasValue` with `uiState.list_M13TarificationInfos`).
  - **WiFi Controls**: Remove references to WiFi data transfers or connection actions (e.g. `viewModel.disconnect()`) if not supported by the local light app.
  - **Imports**: Make sure import packages point to local namespaces (like `com.example.light_app_controles.R`).

### 6. Verify Local Compilation
- Run a compilation check in the local `Light_App_Controles` project to verify the changes:
  ```powershell
  .\gradlew.bat compileDebugKotlin --offline --parallel --build-cache --configuration-cache
  ```
- Parse any compilation errors and correct them immediately.

### 7. Clean up TODOs in BOTH Codebases
- Once the local build compiles successfully, locate the file containing the ported `TODO` in the `ClientJetPack` codebase (`D:\AndroidStudioProjects\ClientJetPack`) and delete the `TODO` comment (along with any helper pointer comments) using code edit tools.
- Delete the matching `TODO` in the local `Light_App_Controles` codebase as well.

### 8. Report Success and Display Diffs
- Provide the user with clickable links to the modified files in both projects.
- Display a detailed git-style diff showing all modified files at the very end of your explanations.
