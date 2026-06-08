# Skill - TODO Context Exporter

Ce skill instruit l'assistant sur la façon d'extraire automatiquement le contexte d'un commentaire `TODO` dans le code et de générer un fichier Markdown propre (`contex.md`). Ce fichier contiendra les extraits de code environnants, les fichiers relatifs et le flux logique (flow) nécessaires pour qu'une IA puisse comprendre et résoudre le TODO sans avoir besoin de tout l'historique ou du backup du projet.

**RÈGLE D'OR STRICTE (CRITICAL RULE) :** Ce skill est STRICTEMENT de la lecture et de la compréhension (Read-Only). L'assistant ne doit faire **AUCUNE MODIFICATION** ni "edit" dans les fichiers du projet. Le `TODO` et le code existant doivent rester parfaitement intacts.

---

## Trigger Phrases
- "t_contex_files_relative_pour_regle_todo"
- "t_contex"
- "todo_context"

---

## Steps to Execute

### 1. Locate the TODO
Identifiez le ou les commentaires `TODO` pertinents dans le code source (via `grep_search`). S'il y en a plusieurs, demandez à l'utilisateur lequel cibler ou traitez le plus évident.

### 2. Extract Context & Relative Files
Pour le `TODO` ciblé, l'assistant doit :
- Extraire un large bloc de code autour du `TODO` (par exemple, 15-20 lignes avant et après) pour donner un contexte visuel clair (highlight).
- Identifier et lister **LE PLUS POSSIBLE de fichiers relatifs** (Models, ViewModels, Interfaces, composables parents, ActiveDatas, etc.) qui sont impliqués dans le flux d'exécution (flow) du `TODO`. Ne ratez AUCUN fichier dépendant dont l'IA pourrait avoir besoin, car s'il n'est pas listé ici, il sera ignoré et masqué par `read_ingor_` !
- **Rappel :** N'appliquez AUCUN correctif. NE SUPPRIMEZ PAS le TODO. Contentez-vous d'analyser.

### 3. Generate the Context File
Créez le dossier `contexs/` s'il n'existe pas, puis créez (ou écrasez s'il existe) le fichier de contexte en incluant votre identifiant unique de conversation actuel (Conversation ID) dans le nom, à l'emplacement suivant :
`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\t_\contexs\<conversation-id>_contex.md`

Le contenu du fichier `<conversation-id>_contex.md` DOIT être formaté de manière claire :

```md
# Contexte d'Isolation du TODO

**Fichier Principal :** `[Nom du fichier et chemin absolu]`
**Cible :** `[Le texte exact du TODO]`

## 1. Flux d'exécution attendu (Flow)
[L'assistant doit expliquer ici en langage simple le flux : comment le problème doit être approché, quelles sont les données d'entrée/sortie, et la logique métier attendue].

## 2. Fichiers Relatifs Impliqués
- `[Chemin du fichier relatif 1]` : [Brève explication de son rôle dans ce TODO]
- `[Chemin du fichier relatif 2]` : [Brève explication de son rôle dans ce TODO]

## 3. Code environnant (Highlighted Context)
\`\`\`kotlin
// ... (Code avant le TODO)
[Ligne -15 à -1]

// ---> TODO: [Texte] <---

[Ligne +1 à +15]
// ... (Code après le TODO)
\`\`\`

## 4. Extraits des fichiers relatifs
\`\`\`kotlin
// Extraits pertinents des modèles ou dépendances nécessaires pour comprendre le type des variables utilisées autour du TODO.
\`\`\`

## 5. Règles d'Isolation (Ignore Rules)
[L'assistant DOIT lister ici toutes les règles nécessaires pour la commande `read_ingor_`. N'oubliez SURTOUT PAS la règle `!*skill_agent/**` pour que l'IA puisse toujours lire ses propres skills et consignes !]
\`\`\`text
*
!*/
!*skill_agent/**
!*[Nom du Fichier Principal.kt]
!*[Nom du Fichier Relatif 1.kt]
!*[Nom du Fichier Relatif 2.kt]
// etc.
\`\`\`
```

### 4. Report Success
Une fois le fichier généré, informez l'utilisateur avec un message de succès et un lien cliquable vers le fichier généré :
[<conversation-id>_contex.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_/contexs/<conversation-id>_contex.md)
L'utilisateur pourra alors facilement copier le contenu de ce fichier pour le transmettre à une IA "vierge" afin de régler le TODO avec un contexte parfait.
