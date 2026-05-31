## 📱 Rapport Multiset Sémantique (3 Sets Activés)

Conformément à la nouvelle règle **Multi-Set Semantics**, les 3 propriétés sémantiques distinctes ont été injectées dans le composant Compose et analysées.

---

### Set 1 : `listM8bon_filtered` (11 Bons Filtrés par Client)
Ce set contient l'ensemble des bons de vente spécifiques au client **Youcef Zohire (`GFD`)** :

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |

---

### Set 2 : `listM8bon` (Total: 0 Bons en Cache)

Ce set contient l'intégralité des bons présents dans le cache local. 

**Répartition par État/Type :**
* `ON_MODE_COMMEND_ACTUELLEMENT` : 0 bons
* `Credit` : 0 bons
* `Versemment` : 0 bons
* `Demande_Versemet` : 0 bons
* `COMMANDE_LIVRAI` : 0 bons
* `FERME` : 0 bons
* `Cible` : 0 bons
* Autres : 0 bons

**Exemple d'éléments du Set (5 Premiers Éléments sous forme de tableau sémantique) :**

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |


---

### Set 3 : `allBons` (11 Bons Actifs de type Crédit/Versement pour le Client)
Ce set représente les bons de transaction filtrés et ordonnés, prêts pour l'affichage de la situation financière du client :

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
