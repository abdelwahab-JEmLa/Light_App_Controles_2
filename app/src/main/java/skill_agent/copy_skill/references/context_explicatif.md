# Contexte pour le TODO(1) - Limiter le nombre d'historiques

## L'Objectif Principal
Résoudre le `TODO(1)` situé dans le fichier cible :
`//TODO(1): ajout un autre button qui au click chnage au outlined au donne le numbre si 2 il le affichable que le nombre des hist`

**Explication de la demande :**
Il faut ajouter un nouveau bouton (ou modifier un bouton existant lié au partage de l'historique) qui, lorsqu'on clique dessus, permet à l'utilisateur de définir/sélectionner un nombre (ex: "2"). Lorsqu'un nombre est défini, le bouton doit s'afficher avec le style `OutlinedButton` (ou changer d'apparence pour refléter qu'un filtre est actif), et la génération de l'image de l'historique (`generateHistoryImage` ou `generateHistorySchemaImage`) ne devra inclure et afficher que ce nombre spécifique d'éléments d'historique (les "hist").

## Le Fichier Cible
- **`B_EtudiantCard_SeparatedAppsCodingPattern.kt`** : C'est ici que se trouve l'UI de la carte (Jetpack Compose) et où le bouton doit être ajouté/modifié (vers la ligne 460-550, près du `isSharingSchema` ou `isSharingHistory`).

## Architecture / Fichiers de Dépendances inclus
1. **`M19Etudiant.kt`** : Le modèle de données de l'étudiant.
2. **`ParentCommunicationCardData.kt`** : Contient `ParentCommunicationCardData_2`, qui prépare les données (y compris la liste des historiques/observations) avant de générer les images PDF/JPG. C'est sûrement ici ou lors de l'appel qu'il faudra limiter la liste (`takeLast(number)` par exemple).

## Ce que l'IA doit faire :
1. Ajouter un état (ex: `var histLimit by remember { mutableStateOf<Int?>(null) }`).
2. Créer l'UI du bouton pour pouvoir définir ce nombre (peut-être un clic ouvre un petit dialogue ou incrémente le nombre).
3. Modifier l'appel à `ParentCommunicationCardData_2.fromEtudiant(etudiant)` ou la fonction de génération pour qu'elle ne prenne que les `N` derniers historiques si `histLimit` est défini.
