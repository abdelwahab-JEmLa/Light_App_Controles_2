# 🛠️ Antigravity Custom Skills

> [!NOTE]
> Chaque fois que vous affichez l'aide des Skills via **`h_`**, l'assistant synchronise et copie automatiquement tous les fichiers `.md` de configuration de vos Skills personnalisés vers le dossier du projet : [skill_agent](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/).

---

## 📋 Tableau Récapitulatif des Skills Disponibles

| Nom du Skill | Mots-clés (Triggers) | Description | Fichier de configuration |
| :--- | :--- | :--- | :--- |
| **Annotated Screen Capture** | `scr_s` | Capture l'écran de l'appareil Android ou de l'émulateur connecté avec des boîtes de délimitation étiquetées et l'affiche. | [screenshot.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/screenshot.md) |
| **Tap Android FAB** | `tap` | Capture l'écran, localise le bouton flottant (FAB) ou l'élément ciblé, résout les coordonnées et simule un clic sur l'appareil. | [tap.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/tap.md) |
| **Fast Build and Install** | `build` | Compile et installe rapidement l'application en mode hors-ligne optimisé (`--offline --parallel --build-cache --configuration-cache`). | [build.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/build.md) |
| **Fast Build, Install and Launch** | `lance_r`, `l_`, `l_r` | Compile, installe l'application sur le téléphone et lance automatiquement son activité principale (`MainActivity`). | [launch.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/launch.md) |
| **Commit, Tag and Push** | `push_taged`, `push_`, `p_`, `p_tag=cleanup` | Commite proprement les modifications locales, génère/incrémente intelligemment un tag Git, l'applique et pousse le tout sur GitHub. | [push_tagged.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/push_tagged.md) |
| **Todo Bubelle - UI Bug Hunter** | `todo_bubelle`, `todo_b`, `fix_ui`, `bubelle` | Capture l'écran, analyse les bulles d'erreur/anomalies UI, identifie le composable responsable, applique un correctif dans le code, relance l'app (`lance_r`) et prend un screenshot de vérification (`scr_s`). | [todo_bubelle.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/todo_bubelle.md) |
| **Context Unique Working_IN** | `agy_context_unique_workingIn_active`, `agy_context_unique_workingIn_desactive`, `c_w_a`, `c_w_d`, `c_w_e` | Active (isole), Désactive (restaure), ou vérifie l'état du contexte restreint uniquement sur `Working_IN.Feature`. | [context_working_in.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/context_working_in.md) |
| **Concise Code** | `consize_commants`, `co_` | Enlève les commentaires, les logs et les semantics des fichiers du contexte pour rendre le code le plus concis possible sans en altérer le fonctionnement. | [consize_comments.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/consize_comments.md) |
| **Fast Launch Preview** | `lp_`, `lance_preview` | Compile, installe et lance le preview de l'application de la façon la plus rapide possible. | [launch_preview.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/launch_preview.md) |
| **Help Skills** | `help_skill`, `h_`, `help_` | Affiche ce tableau d'aide documentant l'ensemble des Skills disponibles. | [help_skill.md](file:///C:/Users/Abou%20Mohamed/.gemini/antigravity-cli/skills/help_skill.md) |

---

> [!TIP]
> Vous pouvez taper directement **`h_`**, **`help_`** ou **`help_skill`** dans le chat de l'assistant pour afficher dynamiquement la documentation interactive et la liste de tous vos Skills personnalisés.
