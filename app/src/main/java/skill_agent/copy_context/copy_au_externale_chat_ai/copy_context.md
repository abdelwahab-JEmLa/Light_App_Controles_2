# Skill - Context Copier (copy_context / cont_copie)

Ce skill permet à l'assistant d'extraire un `TODO` d'un fichier, de rassembler automatiquement un maximum de fichiers pertinents (le contexte global) nécessaires à la résolution de ce TODO, de générer un fichier Markdown explicatif détaillant le contexte, et d'utiliser la fonctionnalité de copie pour envoyer tout ce paquet dans le presse-papiers.

## Objectif
L'objectif principal de ce skill est d'utiliser une IA rapide (comme Gemini 3.5 Flash) pour trouver le plus de fichiers possible afin de résoudre le TODO, de regrouper et copier ce contexte vers le chat d'une IA dotée de Deep Thinking (comme Claude / OpenAI o1). Une fois que l'IA externe a généré les correctifs, on utilisera le skill `zip_colle` pour intégrer et régler définitivement le TODO dans le projet local.

---

## Mots-clés (Triggers)
- `cont_copie`
- `con_copie`
- `copy_context`

---

## Étapes d'exécution (Steps to Execute)

### 1. Identifier le TODO et le Fichier Actif
- L'assistant identifie le fichier actif ou ciblé par l'utilisateur.
- Il lit le fichier pour localiser le commentaire `TODO` spécifique à résoudre.

### 2. Rechercher le Contexte Maximal (Max Files)
- L'assistant effectue des recherches (via `grep_search` ou lecture de fichiers) pour trouver **toutes les dépendances pertinentes** liées au TODO ou au fichier actif :
  - Modèles de données (ex: classes `M19Etudiant`, Entités Room).
  - Composants UI (ex: dialogues, autres écrans appelés).
  - ViewModels ou Repositories liés.
- **Gestion des images** : Si le TODO mentionne ou fait référence à une image (ex: capture d'écran `screen.png`, maquette, ou image dans le workspace), l'assistant doit impérativement lire et analyser l'image pour enrichir la description dans le `context_explicatif.md`. Cette image doit également être ajoutée à la liste des fichiers à copier.
- Le but est d'obtenir un contexte complet et 100% autonome pour l'IA externe.

### 3. Créer le Markdown Explicatif du Contexte
- Créer (ou écraser) le fichier explicatif : `app/src/main/java/skill_agent/copy_context/copy_skill/references/context_explicatif.md`.
- Ce fichier doit contenir :
  1. **L'Objectif principal** : Copie exacte du `TODO`.
  2. **Le Fichier Cible** : Où le `TODO` doit être implémenté.
  3. **L'Architecture / Dépendances** : Une explication concise de la façon dont les fichiers fournis s'articulent et de leur rôle.
  4. **Analyse de l'image** (si présente) : Résumé ou détails extraits de l'image (UI, structure, etc.).

### 4. Mettre à jour `hist_copie.md` (Comportement `c_`)
- L'assistant écrase le fichier `app/src/main/java/skill_agent/copy_context/copy_skill/references/hist_copie.md` (Overwrite: true).
- Il y insère les liens cliquables Markdown pour **tous les fichiers rassemblés** :
  - Le fichier contenant le TODO.
  - Le fichier `context_explicatif.md`.
  - Tous les fichiers de dépendances trouvés.
  - L'image associée (si présente dans le projet).
- Format requis dans `hist_copie.md` : `### 🔗 [NomDuFichier](file:///...)`.

### 5. Copier au Presse-papiers (`cc_`)
- Exécuter automatiquement le script de copie rapide :
  ```bash
  & "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
  ```
- Cela injecte immédiatement le contenu concaténé de tous ces fichiers dans le presse-papiers de Windows.

### 6. Rapport de Succès
- Afficher un tableau clair dans le chat listant tous les fichiers inclus dans le contexte et la confirmation que tout a été copié, prêt à être collé dans le chat de l'IA avec Deep Thinking.
