# Skill - Context Copier (cont_copie)

Ce skill permet à l'assistant d'extraire un `TODO` d'un fichier, de rassembler automatiquement un maximum de fichiers pertinents (le contexte global) nécessaires à la résolution de ce TODO, de générer un fichier Markdown explicatif détaillant le contexte, et d'utiliser la fonctionnalité de copie pour envoyer tout ce paquet dans le presse-papiers. Idéal pour transférer une tâche complexe à une autre IA.

---

## Mots-clés (Triggers)
- `cont_copie`
- `con_copie`

---

## Étapes d'exécution (Steps to Execute)

### 1. Identifier le TODO et le Fichier Actif
- L'assistant commence par identifier le fichier actif ou ciblé par l'utilisateur.
- Il lit le fichier pour trouver le commentaire `TODO` spécifique à résoudre.

### 2. Rechercher le Contexte Maximal (Max Files)
- L'assistant effectue des recherches (via `grep_search` ou lecture de fichiers) pour trouver **toutes les dépendances pertinentes** mentionnées dans le TODO ou le fichier actif :
  - Modèles de données (ex: classes `M19Etudiant`, Entités Room).
  - Composants UI (ex: dialogues, autres écrans appelés).
  - ViewModels ou Repositories liés.
- Le but est d'avoir un contexte complet (100% autonome) pour que l'autre IA ne manque d'aucune information.

### 3. Créer le Markdown Explicatif du Contexte
- Créer (ou écraser) un fichier explicatif, par exemple : `app/src/main/java/skill_agent/copy_skill/references/context_explicatif.md`.
- Ce fichier doit contenir :
  1. **L'Objectif principal** : Copie exacte du `TODO`.
  2. **Le Fichier Cible** : Où le `TODO` doit être implémenté.
  3. **L'Architecture / Dépendances** : Une explication courte de comment les autres fichiers fournis s'emboîtent et ce qu'ils font par rapport à la demande.

### 4. Mettre à jour `hist_copie.md` (Comportement `c_`)
- L'assistant écrase le fichier `app/src/main/java/skill_agent/copy_skill/references/hist_copie.md` (Overwrite: true).
- Il y insère les liens cliquables Markdown pour **tous les fichiers rassemblés** :
  - Le fichier contenant le TODO.
  - Le fichier `context_explicatif.md`.
  - Tous les fichiers de dépendances trouvés.
- Format requis dans `hist_copie.md` : `### 🔗 [NomDuFichier](file:///...)`.

### 5. Copier au Presse-papiers (`cc_`)
- Exécuter automatiquement le script de copie rapide :
  ```bash
  & "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_skill\run_cc.bat"
  ```
- Cela injecte immédiatement le contenu concaténé de tous ces fichiers dans le presse-papiers de Windows.

### 6. Rapport de Succès
- Afficher un tableau clair dans le chat listant tous les fichiers inclus dans le contexte et la confirmation que tout a été copié.
