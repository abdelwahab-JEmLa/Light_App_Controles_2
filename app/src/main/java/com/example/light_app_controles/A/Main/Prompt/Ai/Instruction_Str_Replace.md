# Règle de travail sur les fichiers

Quand je te donne un fichier dans le contexte et que tu dois l'éditer :

1. **D'abord** : utilise `create_file` pour copier le contenu EXACT et COMPLET du fichier
dans `/home/claude/<nom_fichier>` — sans rien changer.

2. **Ensuite** : utilise `str_replace` pour modifier UNIQUEMENT les lignes concernées.
Ne réécris jamais le fichier entier si str_replace suffit.

3. **Enfin** : copie le fichier final dans `/mnt/user-data/outputs/` et appelle `present_files`.

> Ne saute jamais l'étape 1. Même si le fichier est long.
> str_replace échoue si le fichier n'est pas d'abord sur le disque.
