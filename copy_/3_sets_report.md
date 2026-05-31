## 📱 Rapport Multiset Sémantique (3 Sets Activés)

Conformément à la nouvelle règle **Multi-Set Semantics**, les 3 propriétés sémantiques distinctes ont été injectées dans le composant Compose et analysées.

---

### Set 1 : `listM8bon_filtered` (11 Bons Filtrés par Client)
Ce set contient l'ensemble des bons de vente spécifiques au client **Youcef Zohire (`GFD`)** :

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `3tUR` | 30 Avril \| 22:27 | Credit | 10890.00 دج | - | - | 10890.00 دج | 10890.00 دج | - | 10890.00 دج | 10890.00 دج | - | `GFD` |
| `-VI-` | 30 Avril \| 22:26 | Credit | 1890.00 دج | - | - | 1890.00 دج | 1890.00 دج | - | 1890.00 دج | 1890.00 دج | - | `GFD` |
| `dmxr` | 30 Avril \| 22:25 | COMMANDE_LIVRAI | - | - | - | - | - | - | - | - | - | `GFD` |
| `D98g` | 28 Avril \| 21:24 | Credit | 9000.00 دج | - | - | 9000.00 دج | 9000.00 دج | - | 9000.00 دج | 9000.00 دج | - | `GFD` |
| `K-ma` | 28 Avril \| 21:24 | Versemment | 6640.00 دج | 6640.00 دج | - | - | - | - | - | - | - | `GFD` |
| `WX9z` | 28 Avril \| 11:53 | Demande_Versemet | - | - | - | - | - | - | - | - | - | `GFD` |
| `wC9e` | 23 Avril \| 23:51 | Credit | 15640.00 دج | - | - | 15640.00 دج | 15640.00 دج | - | 15640.00 دج | 15640.00 دج | - | `GFD` |
| `2syQ` | 22 Avril \| 19:10 | Credit | 14920.00 دج | - | - | 14920.00 دج | 14920.00 دج | - | 14920.00 دج | 14920.00 دج | - | `GFD` |
| `uLj6` | 16 Avril \| 22:57 | Credit | 10000.00 دج | - | - | 10000.00 دج | 10000.00 دج | - | 10000.00 دج | 10000.00 دج | - | `GFD` |
| `fqTx` | 16 Avril \| 22:57 | Versemment | 5830.00 دج | 5830.00 دج | - | - | - | - | - | - | - | `GFD` |
| `Iapt` | 9 Avril \| 10:39 | Credit | 9045.00 دج | - | - | 9045.00 دج | 9045.00 دج | - | 9045.00 دج | 9045.00 دج | - | `GFD` |

---

### Set 2 : `listM8bon` (Total: 310 Bons en Cache)

Ce set contient l'intégralité des bons présents dans le cache local. 

**Répartition par État/Type :**
* `ON_MODE_COMMEND_ACTUELLEMENT` : 199 bons
* `Credit` : 46 bons
* `Versemment` : 26 bons
* `Demande_Versemet` : 7 bons
* `COMMANDE_LIVRAI` : 8 bons
* `FERME` : 6 bons
* `Cible` : 10 bons
* Autres : 8 bons

**Exemple d'éléments du Set (5 Premiers Éléments sous forme de tableau sémantique) :**

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `l7JH` | 24 Décembre \| 12:34 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `COLu` |
| `vJcZ` | 24 Décembre \| 13:54 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `COLu` |
| `4F01` | 24 Décembre \| 13:54 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `9X6O` |
| `ccLf` | 24 Décembre \| 19:46 | Cette_Transaction_Type_Est_Credit | 7310.00 دج | - | - | 7310.00 دج | 7310.00 دج | 7.00 دج | 7310.00 دج | 7310.00 دج | - | `UZA` |
| `voGW` | 24 Décembre \| 20:55 | Versemment | 6000.00 دج | 6000.00 دج | - | - | - | - | - | - | - | `CDW` |


---

### Set 3 : `allBons` (11 Bons Actifs de type Crédit/Versement pour le Client)
Ce set représente les bons de transaction filtrés et ordonnés, prêts pour l'affichage de la situation financière du client :

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `3tUR` | 30 Avril \| 22:27 | Credit | 10890.00 دج | - | - | 10890.00 دج | 10890.00 دج | - | 10890.00 دج | 10890.00 دج | - | `GFD` |
| `-VI-` | 30 Avril \| 22:26 | Credit | 1890.00 دج | - | - | 1890.00 دج | 1890.00 دج | - | 1890.00 دج | 1890.00 دج | - | `GFD` |
| `dmxr` | 30 Avril \| 22:25 | COMMANDE_LIVRAI | - | - | - | - | - | - | - | - | - | `GFD` |
| `D98g` | 28 Avril \| 21:24 | Credit | 9000.00 دج | - | - | 9000.00 دج | 9000.00 دج | - | 9000.00 دج | 9000.00 دج | - | `GFD` |
| `K-ma` | 28 Avril \| 21:24 | Versemment | 6640.00 دج | 6640.00 دج | - | - | - | - | - | - | - | `GFD` |
| `WX9z` | 28 Avril \| 11:53 | Demande_Versemet | - | - | - | - | - | - | - | - | - | `GFD` |
| `wC9e` | 23 Avril \| 23:51 | Credit | 15640.00 دج | - | - | 15640.00 دج | 15640.00 دج | - | 15640.00 دج | 15640.00 دج | - | `GFD` |
| `2syQ` | 22 Avril \| 19:10 | Credit | 14920.00 دج | - | - | 14920.00 دج | 14920.00 دج | - | 14920.00 دج | 14920.00 دج | - | `GFD` |
| `uLj6` | 16 Avril \| 22:57 | Credit | 10000.00 دج | - | - | 10000.00 دج | 10000.00 دج | - | 10000.00 دج | 10000.00 دج | - | `GFD` |
| `fqTx` | 16 Avril \| 22:57 | Versemment | 5830.00 دج | 5830.00 دج | - | - | - | - | - | - | - | `GFD` |
| `Iapt` | 9 Avril \| 10:39 | Credit | 9045.00 دج | - | - | 9045.00 دج | 9045.00 دج | - | 9045.00 دج | 9045.00 دج | - | `GFD` |
