---
name: copy_skill
description: Use this skill to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. Trigger this whenever the user asks to copy files to the clipboard, trigger `c_`, `cl_`, `cc_`, `cop_last`, `cop_`, `ca_`, `dc_`, or explicitly mentions backing up files from clipboard or copying files to a prompt.
---

# Skill - Copy to Clipboard & Backup (copy_skill)

This skill instructs the assistant on how to automatically copy, bundle, and format all sibling files and subdirectories of a specified reference package. It handles both direct copying to the Windows Clipboard and bundling into a text backup file (`hist_copie.md`).

## Active Reference Package

The current default reference package is set below:
- **Reference Directory**: `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens`

## Last Copied Files

The following files were targeted during the last execution:
- `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens\MainScreen.kt`

## Steps to Execute

### 1. Identify Target Directory & Files
- **Case A: Default Trigger**: If the user triggers `cl_`, `cc_`, `cop_last`, `c_`, or `ca_` without a path, first check the **actual Windows Clipboard** using PowerShell (`Get-Clipboard -Format FileDropList`). If the user manually copied files in Windows/Android Studio, use those files as the targets. If no files are in the clipboard, fallback to reading the **Last Copied Files** section above.
- **Case B: Dynamic Request**: If a path is provided with `cop_` or `c_`, scan that directory recursively for `.kt` files. Overwrite the **Active Reference Package** and **Last Copied Files** sections in this file (`SKILL.md`).

### 2. Action: Copy to Clipboard (`cl_`, `cc_`, `cop_last`, `cop_`)
- To ensure maximum execution speed, run the ultra-fast native Python scripts included in this skill's `scripts` directory instead of PowerShell `Set-Clipboard`:
  ```bash
  # Pour copier des fichiers (remplace Set-Clipboard -Path) :
  python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\Agents\copy_skill\scripts\copy_files.py" "<file_path_1>" "<file_path_2>" ...
  
  # Pour copier du texte/des lignes :
  python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\Agents\copy_skill\scripts\copy_lines.py" "Texte à copier"
  ```

### 3. Action: Manage Backup File (`c_`, `ca_`, `dc_`)
These triggers interact with the `app\src\main\java\skill_agent\copy_clipboard\hist_copie.md` file without touching the clipboard.
- **Trigger `c_` (Delete & Recreate)**: Delete the existing `hist_copie.md` file in `skill_agent\copy_clipboard\`, then recreate a new one containing **ONLY** the clickable Markdown links to the targeted files (e.g., `### 🔗 [Filename.kt](file:///path...)`). Do not include the source code.
- **Trigger `ca_` (Append Backup)**: Append only the clickable Markdown links of the targeted files to the end of `skill_agent\copy_clipboard\hist_copie.md` (if they are not already present).
- **Trigger `dc_` (Delete Backup)**: Delete the `skill_agent\copy_clipboard\hist_copie.md` file.

### 4. Report Success (Table)
- Output a highly concise response containing:
  1. **Nom court du package**.
  2. **Tableau des fichiers** (Nom du fichier | Lignes).
- Do not output the code in the chat.
