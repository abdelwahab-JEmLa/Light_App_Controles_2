# Skill - Help Skills

This skill instructs the assistant on how to automatically display a clean documentation table containing all available custom skills, their trigger phrases, and a brief explanation of what each skill does, whenever the user requests "help_skill", and automatically synchronize and back up all custom skill configurations into the project's Java source directory under `app/src/main/java/skill_agent/`.

---

## Trigger Phrases
- "help_skill"
- "h_"
- "help_"

---

## Steps to Execute

### 1. Verify and Synchronize Skill Files to Java Folder & Root h_.md
- **Check for Differences**: Compare files, folders, and subfolders in the global AGY skills folder (`C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\`) with the project's local Java `skill_agent` folder (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\`), and verify if the root `h_.md` file (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\h_.md`) is missing or differs from the latest version.
- **Update and Overwrite**: If any differences are found (modified content, missing files, or subfolders), copy/replace all files and folders from the global AGY skills directory to the project's `skill_agent` folder, and overwrite the root `h_.md` file completely to match.

### 2. Read Available Skill Files
Scan the skills directory `C:\Users\Abou Mohamed\.gemini\antigravity-cli\skills\` to list all `.md` files.

### 3. Format and Display the Help Table
Output a markdown table documenting each custom skill.

| Nom du Skill | Mots-clés (Triggers) | Description |
| :--- | :--- | :--- |
| **Semantics Inspector** (`sem_.md`) | `sem_d`, `sem_`, `Todo: sem_`, `Todo: filter` | Injecte la sémantique d'une variable ou filtre (`TODO: sem_` ou `TODO: filter`), puis extrait instantanément les données d'accessibilité avec un dump ADB sans re-compiler/re-déployer l'application. |
| **Annotated Screen Capture** (`screenshot.md`) | `scr_s` | Capture l'écran de l'appareil Android ou de l'émulateur connecté avec des boîtes de délimitation étiquetées et l'affiche. |
| **Tap Android FAB** (`tap.md`) | `tap` | Capture l'écran, localise le bouton flottant (FAB) ou l'élément ciblé, résout les coordonnées et simule un clic sur l'appareil. |
| **Fast Build and Install** (`build.md`) | `build` | Compile et installe rapidement l'application en mode hors-ligne optimisé (`--offline --parallel --build-cache --configuration-cache`). |
| **Fast Build, Export & Deploy** (`build_.md`) | `build_`, `b_` | Compile l'application, crée la structure `Playe_Store\<Version>\0.\A_AllInOne\` sur le Bureau, compresse le dossier, puis déploie le ZIP et le dossier extrait sur la carte SD du téléphone. |
| **Commit, Tag and Push** (`push_tagged.md`) | `push_taged`, `push_`, `p_` | Commite proprement les modifications locales, génère/incrémente intelligemment un tag Git, l'applique et pousse le tout sur GitHub. |
| **Todo Bubelle - UI Bug Hunter** (`todo_bubelle.md`) | `todo_bubelle`, `todo_b`, `fix_ui`, `bubelle` | Capture l'écran, analyse les bulles d'erreur/anomalies UI, identifie le composable responsable, applique un correctif dans le code, relance l'app (`lance_r`) et prend un screenshot de vérification (`scr_s`). |
| **Context Unique Working_IN** (`context_working_in.md`) | `agy_context_unique_workingIn_active`, `agy_context_unique_workingIn_desactive`, `c_w_a`, `c_w_d`, `c_w_e` | Active (isole), Désactive (restaure), ou vérifie l'état du contexte restreint uniquement sur `Working_IN.Feature`. |
| **Concise Code** (`consize_comments.md`) | `consize_commants`, `co_`, `con_`, `con_c`, `TODO: con_c` | Enlève les commentaires, les logs et les semantics pour rendre le code le plus concis possible. Supporte le mode automatique individuel (`TODO: co_`) ou par package (`TODO: con_c`). |
| **Fast Launch Preview** (`launch_preview.md`) | `lp_`, `lance_preview` | Compile, installe et lance le preview de l'application de la façon la plus rapide possible. |
| **Fix TODOs** (`t_.md`) | `t_`, `t_models`, `t_usage`, `fix_todo` | Résout automatiquement les TODOs ou délègue aux skills correspondants (ex: `TODO: log_`, `TODO: sem_`, `TODO: con_c`, `TODO: room_d`), puis affiche le diff de code à la fin. |
| **Copy Package / Sibling Files** (`cop_last.md`) | `cop_last`, `cop_`, `cl_`, `copy_package` | Copie tous les fichiers frères et sous-fichiers du package actif dans le presse-papiers, et l'enregistre pour les futurs appels. |
| **Real-Time Logcat Inspector** (`log_.md`) | `log_`, `Todo: log_`, `logcat`, `adb_log` | Filtre les logs de l'appareil par rapport au tag ou au contexte spécifié dans le code ou le chat, et les affiche. |
| **Room Database Query** (`room_d/room_d.md`) | `room_d`, `Todo: room_d` | Exécute des requêtes de base de données à chaud et les affiche sous forme de tableau Markdown. |
| **Help Skills** (`help_skill.md`) | `help_skill`, `h_`, `help_` | Affiche ce tableau d'aide documentant l'ensemble des Skills disponibles. |

### 4. Present Clickable Links
Always present the user with clickable links to the skill files in the skills directory for quick editing.

* ℹ️ [Help Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/help_skill.md)
* 🔍 [Semantics Inspector](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/sem_.md)
* 🛠️ [Fast Build and Install](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/build.md)
* 📦 [Fast Build, Export & Deploy](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/build_.md)
* 📸 [Annotated Screen Capture](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/screenshot.md)
* 👆 [Tap Android FAB](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/tap.md)
* 🐛 [Todo Bubelle - UI Bug Hunter](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/todo_bubelle.md)
* 📂 [Context Unique Working_IN](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/context_working_in.md)
* 📝 [Concise Code](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/consize_comments.md)
* ⚡ [Fast Launch Preview](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/launch_preview.md)
* ✅ [Fix TODOs and Launch](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_.md)
* 📋 [Copy Package / Sibling Files](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/cop_last.md)
* 📋 [Real-Time Logcat Inspector](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/log_.md)
* 📦 [Commit, Tag and Push](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/push_tagged.md)
* 💾 [Room Database Query](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/room_d/room_d.md)
