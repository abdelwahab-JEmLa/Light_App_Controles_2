# Skill - Help Skills

This skill instructs the assistant on how to automatically display a clean documentation table containing all available custom skills, their trigger phrases, and a brief explanation of what each skill does, whenever the user requests "help_skill", and automatically synchronize and back up all custom skill configurations into the project's Java source directory under `app/src/main/java/skill_agent/`.

---

## Trigger Phrases
- "help_skill"
- "h_"
- "help_"

---

## Steps to Execute

### 1. Synchronize and Copy Skill Files to Java Folder
Copy all `.md` files from the CLI skills directory (`C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\`) to the project's main Java `skill_agent` folder (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\`). Create this folder if it does not already exist.

### 2. Read Available Skill Files
Scan the skills directory `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\` to list all `.md` files.

### 3. Format and Display the Help Table
Output a markdown table documenting each custom skill.

| Nom du Skill | Mots-clés (Triggers) | Description |
| :--- | :--- | :--- |
| **Annotated Screen Capture** (`screenshot.md`) | `scr_s` | Capture l'écran de l'appareil Android ou de l'émulateur connecté avec des boîtes de délimitation étiquetées et l'affiche. |
| **Tap Android FAB** (`tap.md`) | `tap` | Capture l'écran, localise le bouton flottant (FAB) ou l'élément ciblé, résout les coordonnées et simule un clic sur l'appareil. |
| **Fast Build and Install** (`build.md`) | `build` | Compile et installe rapidement l'application en mode hors-ligne optimisé (`--offline --parallel --build-cache --configuration-cache`). |
| **Fast Build, Install and Launch** (`launch.md`) | `lance_r` | Compile, installe l'application sur le téléphone et lance automatiquement son activité principale (`MainActivity`). |
| **Commit, Tag and Push** (`push_tagged.md`) | `push_taged`, `push_` | Commite proprement les modifications locales, génère/incrémente intelligemment un tag Git, l'applique et pousse le tout sur GitHub. |
| **Todo Bubelle - UI Bug Hunter** (`todo_bubelle.md`) | `todo_bubelle`, `todo_b`, `fix_ui`, `bubelle` | Capture l'écran, analyse les bulles d'erreur/anomalies UI, identifie le composable responsable, applique un correctif dans le code, relance l'app (`lance_r`) et prend un screenshot de vérification (`scr_s`). |
| **Help Skills** (`help_skill.md`) | `help_skill`, `h_`, `help_` | Affiche ce tableau d'aide documentant l'ensemble des Skills disponibles. |

### 4. Present Clickable Links
Always present the user with clickable links to the skill files in the skills directory for quick editing.
