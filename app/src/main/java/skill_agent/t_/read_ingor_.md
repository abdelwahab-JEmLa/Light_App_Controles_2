# Skill - Isolate & Fix File (read_ingor_<filename>)

Ce skill permet à l'assistant d'isoler temporairement un fichier unique lors d'une tâche de débogage ou d'édition afin de ne pas consommer les tokens avec le reste du projet, puis de restaurer le contexte initial une fois la tâche accomplie.

---

## Trigger Phrases
- `read_ingor_<filename>`
- `read_ignor_<filename>`
- `isole_<filename>`
- `read_i_list` (pour lister les fichiers de contexte disponibles)

---

## Steps to Execute

### === CAS A : L'utilisateur déclenche `read_i_list` ===

1. **Lister les contextes :**
Utilisez l'outil de listing pour trouver tous les fichiers se terminant par `_contex.md` dans le dossier `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\t_\contexs\`.
2. **Extraire les métadonnées :**
Lisez brièvement chaque fichier trouvé pour extraire la cible du TODO (souvent marquée par `**Cible :**`).
3. **Afficher le tableau :**
Présentez à l'utilisateur un tableau Markdown contenant tous les contextes extraits :
| ID (Conversation) | Titre / Cible du TODO | Fichier (Cliquable) |
|---|---|---|
| `<id>` | `<Texte du TODO>` | [`<id>_contex.md`](file:///.../<id>_contex.md) |

*(L'utilisateur pourra alors copier le nom du fichier pour lancer l'isolation).*

---

### === CAS B : L'utilisateur déclenche `read_ingor_<filename>` ===

#### 1. Sauvegarde (Backup)
Lisez le contenu actuel des fichiers `.antigravityignore` et `.geminiignore` (situés à la racine du projet).
Sauvegardez leur contenu exact dans un fichier temporaire local (par exemple `app/src/main/java/skill_agent/temp_ignore_backup.txt`).

#### 2. Isolation (Token Saving)
Écrasez le contenu de `.antigravityignore` et `.geminiignore` avec les règles d'isolation adaptées.
**🚨 ATTENTION (Nouvelle Session) :** Si l'utilisateur vous demande d'isoler via un ID ou un fichier (ex: `read_ingor_123_contex.md`), trouvez ce fichier dans `app/src/main/java/skill_agent/t_/contexs/`. 
Vous DEVEZ récupérer toutes les règles de la "Section 5. Règles d'Isolation" de ce fichier pour ne pas masquer les fichiers relatifs !

Si c'est une isolation simple sans fichier de contexte, appliquez ces règles :
```text
# Ignorer tout
*
# Ne pas ignorer les dossiers
!*/
# Toujours garder l'accès aux skills IA !
!*skill_agent/**
# Ne pas ignorer le fichier ciblé
!*<filename>
```
*(Dans le cas standard, remplacez `<filename>` par le nom exact du fichier).*

#### 3. Read and Fix
1. Si un fichier `_contex.md` est utilisé, **lisez attentivement sa section "Flux d'exécution attendu (Flow)"** pour comprendre exactement ce que vous devez corriger.
2. Lisez le contenu du fichier ciblé (qui est maintenant isolé).
3. Effectuez les modifications, réparations ou ajouts nécessaires (fixes) directement via les outils d'édition de code.

#### 4. Restauration du contexte (Restore)
Une fois les modifications terminées avec succès :
1. Restaurez le contenu d'origine de `.antigravityignore` et `.geminiignore` en lisant la sauvegarde.
2. Supprimez le fichier de sauvegarde temporaire.

#### 5. Report Success
Présentez à l'utilisateur :
- Un résumé de ce qui a été modifié.
- Un affichage du diff (Git-style) montrant les modifications du fichier.
- Une confirmation claire que les fichiers d'ignore ont été restaurés et que l'ancien contexte est de nouveau actif.
