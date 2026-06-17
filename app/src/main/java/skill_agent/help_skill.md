# Skill - Help Skills

This skill instructs the assistant on how to automatically display a clean documentation table containing all available custom skills, their trigger phrases, and a brief explanation of what each skill does, whenever the user requests "help_skill", "h_", or "help_".

---

## Trigger Phrases
- "help_skill"
- "h_"
- "help_"
- "h_cwe_"
- "h_<trigger_phrase>"
- "h_consize"

---

## Steps to Execute

### 1. Format and Display the Help Table
Output two separate markdown tables documenting each custom skill.

#### 🛠️ Catalogue des Skills Généraux

| Nom du Skill | Mots-clés (Triggers) | Description |
| :--- | :--- | :--- |
| **Help Skills** (`help_skill.md`) | `help_skill`, `h_`, `help_`, `h_cwe_` | Affiche ce tableau d'aide ou explique en détail un skill spécifique (ex: h_cwe_). |
| **Copy Coding Patterns** (`t_copiePattersApp.md`) | `t_copiePattersApp`, `t>copiePattersApp`, `copiePattersApp`, `t_c_client` | Copie et adapte les patterns de code et implémentations du projet ClientJetPack vers le projet local Light App. |
| **Client Chained TODOs** (`t_appClient_chain_todo.md`) | `t_appClient_chain_todo`, `t>cli`, `>clientApp` | Résout et nettoie automatiquement les TODOs chaînés et dépendants (avec indicateurs comme `//<--`, `//...`) dans le projet externe ClientJetPack. |
| **Fix TODOs** (`t_/t_.md`) | `t_`, `t_models`, `t_flow`, `t_usage`, `>clientApp`, `fix_todo` | Résout automatiquement les TODOs ou délègue aux skills correspondants (ex: `TODO: log_`, `TODO: sem_`, `TODO: con_c`, `TODO: room_d`), puis affiche le diff de code à la fin. |
| **TODO Context Exporter** (`t_/t_contex_files_relative_pour_regle_todo.md`) | `t_contex`, `todo_context`, `save_c` | Exporte le contexte enrichi et auto-suffisant d'un TODO (plan d'implémentation, variables/types, imports, code large, pattern source, contraintes) dans `t_/contexs/<id>_contex.md`. |
| **Read and Isolate File** (`t_/read_ingor_.md`) | `read_ingor_<filename>`, `read_ignor_<filename>`, `isole_<filename>`, `read_ignor_last`, `read_i_list`, `consome_c_last`, `cc_last`, `ccl_` | Isole un fichier pour économiser les tokens et applique les correctifs. Mode TURBO (`ccl_`) : consomme le contexte enrichi SANS relire les .kt sources — ultra-rapide. |
| **Fix Specified Comments** (`t_ex_commantaire.md`) | `t_ex_commantaire`, `t_ex_commentaire`, `ex_commantaire`, `fix_ex_commantaire`, `t_exe_<file_name>`, `t_ex_<file_name>` | Navigue vers un fichier spécifique, y localise les commentaires d'instruction ou TODOs, applique le correctif demandé et supprime le commentaire. |
| **Commit, Tag and Push** (`push_tagged.md`) | `push_taged`, `push_`, `p_` | Commite proprement les modifications locales, génère/incrémente intelligemment un tag Git, l'applique et pousse le tout sur GitHub. |
| **Schedule App Launch & Tap Replay** (`tap/Schedule_agy_task_tap_l.md`) | `Schedule_agy_task_tap_l`, `schedule_tap_l`, `sch_tap_l`, `st_`, `arrete_sch` | Attend que l'app se lance depuis Android Studio pour exécuter tap_l, ou annule l'attente en cours (arrete_sch). |
| **Tap Android FAB** (`tap/tap.md`) | `tap`, `tap <target>`, `tap_l`, `shel_tap_l` | Exécute un clic à haute vitesse en analysant l'arbre XML de l'UI (ciblant le FAB ou un texte) ou instantanément via le dernier tap rejoué (tap_l). |
| **Real-Time Logcat Filter** (`log_f.md`) | `log_f`, `log_f ` followed by terms | Filtre et affiche en temps réel les logs de l'appareil par rapport aux mots-clés saisis dans le chat. |
| **AGY to Project Synchronizer** (`agy_to_project.md`) | `agy_to_project`, `agy_to_proj`, `a_t_p` | Synchronise et remplace les skills et le fichier `h_.md` du dossier AGY global vers le projet local. |
| **Room Database Query** (`room_d/room_d.md`) | `room_d`, `Todo: room_d` | Exécute des requêtes de base de données à chaud et les affiche sous forme de tableau Markdown. |
| **Firebase Cache Query** (`fb_db/fb_d.md`) | `fb_d`, `Todo: fb_d`, `firebase_search`, `fb_query` | Extrait et recherche dans le cache local SQLite de Firebase Realtime Database et génère les liens console associés. |
| **FragMap Data Flow** (`t_/fm_.md`) | `fm_`, `fm_flow` | Affiche le résumé du flux de synchronisation des bases de données FragMap (Room, CSV, Firebase). |
| **Semantics Inspector** (`sem_.md`) | `sem_`, `sem_d`, `Todo: sem_`, `Todo: filter` | Injecte la sémantique d'une variable ou filtre (`TODO: sem_` ou `TODO: filter`), puis extrait instantanément les données d'accessibilité avec un dump ADB sans re-compiler/re-déployer l'application. |
| **Concise Code** (`consize_comments.md`) | `consize_commants`, `co_`, `con_`, `con_c`, `TODO: con_c` | Enlève les commentaires, les logs et les semantics pour rendre le code le plus concis possible. Supporte le mode automatique individuel (`TODO: co_`) ou par package (`TODO: con_c`). |
| **Real-Time Logcat Inspector** (`log_.md`) | `log_`, `Todo: log_`, `logcat`, `adb_log` | Filtre les logs de l'appareil par rapport au tag ou au contexte spécifié dans le code ou le chat, et les affiche. |
| **Fast Build, Export & Deploy** (`build_.md`) | `build_`, `b_` | Compile l'application, crée la structure `Playe_Store\<Version>\0.\A_AllInOne\` sur le Bureau, compresse le dossier, puis déploie le ZIP et le dossier extrait sur la carte SD du téléphone. |
| **Fast Launch Preview** (`launch_preview.md`) | `lp_`, `lance_preview` | Compile, installe et lance le preview de l'application de la façon la plus rapide possible. |
| **Todo Bubelle - UI Bug Hunter** (`t_/todo_bubelle.md`) | `todo_bubelle`, `todo_b`, `fix_ui`, `bubelle` | Capture l'écran, analyse les bulles d'erreur/anomalies UI, identifie le composable responsable, applique un correctif dans le code, relance l'app (`lance_r`) et prend un screenshot de vérification (`scr_s`). |
| **Fast Build and Install** (`build.md`) | `build` | Compile et installe rapidement l'application en mode hors-ligne optimisé (`--offline --parallel --build-cache --configuration-cache`). |
| **AS Click Run** (`as_click_run.md`) | `as_click_run`, `as_run`, `r_`, `click_run` | Compile, installe et lance l'application sur l'appareil connecté de manière rapide via Gradle et ADB. |
| **Annotated Screen Capture** (`screenshot.md`) | `scr_s` | Capture l'écran de l'appareil Android ou de l'émulateur connecté avec des boîtes de délimitation étiquetées et l'affiche. |
| **Skill Creator** (`skill_creatore/skill-creator/SKILL.md`) | `skill-creator`, `create_skill` | Crée de nouveaux skills, modifie, améliore ou évalue les performances des skills existants. |

#### 📂 Skills de Contexte & Copie (copy_context)

| Nom du Skill | Mots-clés (Triggers) | Description |
| :--- | :--- | :--- |
| **Context Unique Working_IN** (`copy_context/context_working_in.md`) | `agy_context_unique_workingIn_active`, `agy_context_unique_workingIn_desactive`, `c_w_a`, `c_w_d`, `c_w_e`, `cwa_<package>`, `cwa_`, `cwd_`, `cwe_` | Active (isole), Désactive (restaure), ou vérifie l'état du contexte restreint uniquement sur `Working_IN.Feature`. |
| **Context Map Generator** (`copy_context/context_working_in/contex_par_md_map/contex_par_md_ma.md`) | `cree_map`, `cree_map_ecrase_keep`, `cta_map`, `cw_map_d`, `map_colore`, `ctm_ref` | Gère le contexte actif via une arborescence ASCII annotée dans `files_affiched.md` (`++` = focus, `--` = ignore, `ctm_ref` = refresh et applique). |
| **Context Map Line Counter** (`copy_context/context_working_in/contex_par_md_map/loc_map.md`) | `??`, `loc_map`, `map_loc` | Calcule et affiche le nombre de lignes et de fichiers pour les dossiers/fichiers marqués avec `??` dans la carte de contexte. |
| **Context Transfer** (`copy_context/contexTrensefert/conTr_.md`) | `conT_`, `conTr_`, `/contexTrensefert`, `context_transfer`, `ctsave_`, `ctc_`, `ctecrase_`, `cwc_`, `ct_deepRead_`, `ct_l` | Documente l'architecture du projet et sert de guide pour transférer le contexte de développement des modules (CSV/Firebase/Room) d'une session à l'autre. |
| **Context Copier** (`copy_context/copy_au_externale_chat_ai/copy_context.md`) | `cont_copie`, `con_copie`, `copy_context` | Récupère le contexte global pour résoudre un TODO et le copie vers une IA dotée de Deep Thinking. |
| **Copy to Clipboard & Backup** (`copy_context/copy_skill/SKILL.md`) | `cl_`, `cc_`, `cop_last`, `cop_`, `c_`, `ca_`, `dc_` | Copie les fichiers dans le presse-papiers (`cl_`/`cc_`) et/ou les sauvegarde dans un fichier texte dans le dossier copy_skill (`c_`/`ca_`). |
| **Copy Package / Sibling Files** (`copy_context/cop_last.md`) | `cl_`, `cop_last`, `cop_`, `copy_package` | Copie, formate et charge le dernier lot ou un dossier de fichiers Kotlin directement dans le presse-papiers. |
| **Zip Colle** (`copy_context/copy_au_externale_chat_ai/zip_colle.md`) | `zip_colle` | Récupère le dernier téléchargement (archive zip/rar ou kt direct) et met à jour les fichiers sources du projet. |


### 2. Present Clickable Links
Always present the user with clickable links to the skill files in the skills directory for quick editing.

* ℹ️ [Help Skill](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/help_skill.md)
* 📋 [Copy Coding Patterns](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_copiePattersApp.md)
* 📋 [Client Chained TODOs](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_appClient_chain_todo.md)
* ✅ [Fix TODOs and Launch](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/t_.md)
* 📤 [TODO Context Exporter](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/t_contex_files_relative_pour_regle_todo.md)
* 🗜️ [Read and Isolate File](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/read_ingor_.md)
* 📦 [Commit, Tag and Push](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/push_tagged.md)
* ⏰ [Schedule App Launch & Tap Replay](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/tap/Schedule_agy_task_tap_l.md)
* 👆 [Tap Android FAB](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/tap/tap.md)
* 📋 [Real-Time Logcat Filter](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/log_f.md)
* 🔄 [AGY to Project Synchronizer](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/agy_to_project.md)
* 💾 [Room Database Query](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/room_d/room_d.md)
* 💾 [Firebase Cache Query](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/fb_db/fb_d.md)
* 🔄 [FragMap Data Flow](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/fm_.md)
* 🔍 [Semantics Inspector](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/sem_.md)
* 📝 [Concise Code](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/consize_comments.md)
* 📋 [Real-Time Logcat Inspector](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/log_.md)
* 📦 [Fast Build, Export & Deploy](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/build_.md)
* ⚡ [Fast Launch Preview](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/launch_preview.md)
* 🐛 [Todo Bubelle - UI Bug Hunter](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/todo_bubelle.md)
* 🛠️ [Fast Build and Install](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/build.md)
* 📸 [Annotated Screen Capture](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/screenshot.md)
* 🚀 [AS Click Run](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/as_click_run.md)
* 📝 [Fix Specified Comments (t_ex_commantaire)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/t_ex_commantaire.md)
* 🛠️ [Skill Creator](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/skill_creatore/skill-creator/SKILL.md)

#### 📂 Liens des Configuration des Skills de Contexte & Copie (copy_context)

* 📂 [Context Unique Working_IN](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/context_working_in/context_working_in.md)
* 🗺️ [Context Map Generator](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/context_working_in/contex_par_md_map/contex_par_md_ma.md)
* 🗺️ [Context Map Line Counter](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/context_working_in/contex_par_md_map/loc_map.md)
* 🔄 [Context Transfer](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/contexTrensefert/conTr_.md)
* 📝 [Context Copier (copy_context)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/copy_context.md)
* 📋 [Copy to Clipboard & Backup](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_skill/SKILL.md)
* 📋 [Copy Package / Sibling Files](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/cop_last.md)
* 📦 [Zip Colle](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/zip_colle.md)


### 3. Detail and Explain a Specific Skill (e.g. h_cwe_ or h_<trigger>)
If the user's request matches `h_pc` (or `hw_`, `help_pc`, `help_hw`, `skillpc_`):
- Instantly redirect and load the PC Help Page at [hw_.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/skill_agent_pc/hw_.md) and display its content. Do not scan or search any directory.

If the user's request matches `h_consize` (or `h_consize_comments`, `h_co_`, `h_con_c`):
- Instantly redirect and load the Concise Code skill at [`consize_comments.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/consize_comments.md) and display its full content or a detailed explanation.

If the user's request matches `h_<trigger>` (such as `h_cwe_`):
- Identify the corresponding skill using the trigger (e.g., `cwe_` points to the **Context Unique Working_IN** skill).
- Open and read the markdown file of that skill (e.g., `copy_context/context_working_in/context_working_in.md`).
- Present a detailed explanation of the skill: its purpose, triggers, steps of execution, and relevant file paths.
