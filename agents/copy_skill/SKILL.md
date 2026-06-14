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
- **Case A: Default Trigger**: If the user triggers `cl_`, `cc_`, `cop_last`, `c_`, or `ca_` without a path, first check the **actual Windows Clipboard** using the native Python script to avoid PowerShell latency:
  `python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\agents\copy_skill\scripts\read_clipboard_files.py"`
  If the user manually copied files in Windows/Android Studio, this script will quickly extract their paths and line counts. If no files are in the clipboard, fallback to reading the **Last Copied Files** section above.
- **Case B: Dynamic Request**: If a path is provided with `cop_` or `c_`, scan that directory recursively for `.kt` files. Overwrite the **Active Reference Package** and **Last Copied Files** sections in this file (`SKILL.md`).

### 2. Action: Copy to Clipboard (`cl_`, `cc_`, `cop_last`, `cop_`)
- To ensure maximum execution speed, run the ultra-fast native Python scripts included in this skill's `scripts` directory instead of PowerShell `Set-Clipboard`:
  ```bash
  # Pour copier des fichiers (remplace Set-Clipboard -Path) :
  python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\agents\copy_skill\scripts\copy_files.py" "<file_path_1>" "<file_path_2>" ...
  
  # Pour copier du texte/des lignes :
  python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\agents\copy_skill\scripts\copy_lines.py" "Texte à copier"
  ```

### 3. Action: Manage Backup File (`c_`, `ca_`, `dc_`)
These triggers interact with the `agents\copy_skill\references\hist_copie.md` file without touching the clipboard.
- **Trigger `c_` (Delete & Recreate)**: Simply run `python "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\agents\copy_skill\scripts\read_clipboard_files.py"`. This script **automatically recreates** `hist_copie.md` with clickable links and prints the final Markdown table to save you a tool call. If the script outputs `EMPTY`, then manually recreate `hist_copie.md` using the fallback files.
- **Trigger `ca_` (Append Backup)**: Append only the clickable Markdown links of the targeted files to the end of `agents\copy_skill\references\hist_copie.md` (if they are not already present).
- **Trigger `dc_` (Delete Backup)**: Delete the `agents\copy_skill\references\hist_copie.md` file.

### 4. Report Success (Table)
- Output a highly concise response containing:
  1. **Lien de sauvegarde** : `[agents/copy_skill/references/hist_copie.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/agents/copy_skill/references/hist_copie.md)`.
  2. **Temps d'exécution** : Calcule et affiche le temps écoulé (en secondes) depuis la requête de l'utilisateur.
  3. **Nom court du package**.
  4. **Tableau des fichiers** (Nom du fichier | Lignes).
- Do not output the code in the chat.
