---
name: zip_colle
description: Extract the latest downloaded zip/rar or direct kotlin file from downloads, find its matching file in the project, and overwrite/update it. Trigger this when the user says "zip_colle", "colle_", or "colle".
---

# Zip Colle (zip_colle)

Ce skill instruit l'assistant pour récupérer le dernier fichier téléchargé (archive `.rar`/`.zip` ou fichier Kotlin `.kt` direct), chercher leurs correspondances exactes dans le projet et les écraser avec les nouvelles versions.

## Trigger Phrases
- "zip_colle"
- "colle_"
- "colle"
- "ok_"
- "ok"

## Objectif
Mettre à jour rapidement des fichiers sources du projet avec de nouvelles versions envoyées par une IA externe (ou téléchargées) sous forme d'archive ou de fichier direct, sans avoir à copier-coller manuellement chaque fichier.

## Steps to Execute

### 1. Localiser le dernier téléchargement
Utilisez un script ou une commande (Python/PowerShell) pour lister les fichiers `.rar`, `.zip`, ou `.kt` dans le dossier des téléchargements (`C:\Users\Abou Mohamed\Downloads`) et sélectionnez celui qui a été modifié le plus récemment.

### 2. Gérer le fichier trouvé
- **Si c'est un fichier `.kt` direct** : Prenez ce fichier tel quel, et passez directement à l'étape 3 en l'utilisant comme unique fichier cible.
- **Si c'est une archive (`.zip` ou `.rar`)** :
  1. Créez un dossier temporaire (ex: `C:\Users\Abou Mohamed\Downloads\temp_zip_colle`) et extrayez-y l'archive.
  2. Si `.zip` : Utilisez un script Python (librairie `zipfile`) ou PowerShell (`Expand-Archive`).
  3. Si `.rar` : Utilisez `C:\Program Files\WinRAR\WinRAR.exe x` ou `7z x` via `run_command`. Si aucun utilitaire n'est disponible, demandez un `.zip` à l'utilisateur.

### 3. Trouver et Remplacer les fichiers `.kt`
Pour le fichier `.kt` direct, ou pour *chaque* fichier `.kt` extrait de l'archive :
1. **Recherche prioritaire dans l'historique** : Lisez le fichier de référence de copie `app/src/main/java/skill_agent/copy_context/copy_skill/references/hist_copie.md`. Si ce fichier contient un lien markdown vers un fichier du projet ayant le même nom de fichier exact (ex: `DropDownItem_ID6.kt`), récupérez et utilisez directement le chemin absolu spécifié dans le lien.
2. **Recherche de secours** : Si le nom du fichier n'est pas présent dans `hist_copie.md`, cherchez son chemin correspondant exact dans le projet (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\`) en utilisant son nom (ex: via `grep_search` ou une recherche récursive PowerShell).
3. Si un fichier correspondant est trouvé, écrasez son contenu avec celui du fichier téléchargé/extrait. 
   *(Utilisez l'outil `write_to_file` avec `Overwrite: true`, ou exécutez une commande de copie/déplacement).*

### 4. Nettoyage et Rapport
1. Si une archive a été extraite, supprimez le dossier d'extraction temporaire.
2. Présentez à l'utilisateur un compte rendu détaillé avec des **liens Markdown cliquables** (ex: `### 🔗 [Fichier.kt](file:///...)`) pour chaque fichier qui a été mis à jour avec succès dans le projet.
