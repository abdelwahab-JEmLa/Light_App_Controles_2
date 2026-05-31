# Semantics Inspection Report

This report contains the parsed custom semantics properties extracted from the device's active UI component at runtime.

---

## 1. Set `listM8bon_7xp4` (Filtered by last 4 = 7xp4 / fqTx)
*Expression: `listM8bon?.filter { it.keyID.takeLast(4) == "7xp4" } ?: emptyList()`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `fqTx` | 16 Avril | 22:57 | Versemment | - | 5830.00 دج | - | - | - | - | - | - | - | `CGFD` |

---

## 2. Set `listM8bon_filtered` (Filtered by client GFD)
*Expression: `active_Datas.list_M8bon?.filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID } ?: emptyList()`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `3tUR` | 30 Avril | 22:27 | Credit | - | - | - | - | - | - | 10890.00 دج | - | - | `CGFD` |
| `-VI-` | 30 Avril | 22:26 | Credit | - | - | - | - | - | - | 1890.00 دج | - | - | `CGFD` |
| `dmxr` | 30 Avril | 22:25 | COMMANDE_LIVRAI | - | - | - | - | - | - | - | - | - | `CGFD` |
| `D98g` | 28 Avril | 21:24 | Credit | - | - | - | - | - | - | 9000.00 دج | - | - | `CGFD` |
| `K-ma` | 28 Avril | 21:24 | Versemment | - | 6640.00 دج | - | - | - | - | - | - | - | `CGFD` |
| `WX9z` | 28 Avril | 11:53 | Demande_Versemet | - | - | - | - | - | - | - | - | - | `CGFD` |
| `wC9e` | 23 Avril | 23:51 | Credit | - | - | - | - | - | - | 15640.00 دج | - | - | `CGFD` |
| `2syQ` | 22 Avril | 19:10 | Credit | - | - | - | - | - | - | 14920.00 دج | - | - | `CGFD` |
| `uLj6` | 16 Avril | 22:57 | Credit | - | - | - | - | - | - | 10000.00 دج | - | - | `CGFD` |
| `fqTx` | 16 Avril | 22:57 | Versemment | - | 5830.00 دج | - | - | - | - | - | - | - | `CGFD` |
| `Iapt` | 9 Avril | 10:39 | Credit | - | - | - | - | - | - | 9045.00 دج | - | - | `CGFD` |

---

## 3. Set `allBons` (Filtered by client GFD & Credit/Versement status)
*Expression: `listM8bon?.filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }?.sortedByDescending { it.creationTimestamps }`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `3tUR` | 30 Avril | 22:27 | Credit | - | - | - | - | - | - | 10890.00 دج | - | - | `CGFD` |
| `-VI-` | 30 Avril | 22:26 | Credit | - | - | - | - | - | - | 1890.00 دج | - | - | `CGFD` |
| `dmxr` | 30 Avril | 22:25 | COMMANDE_LIVRAI | - | - | - | - | - | - | - | - | - | `CGFD` |
| `D98g` | 28 Avril | 21:24 | Credit | - | - | - | - | - | - | 9000.00 دج | - | - | `CGFD` |
| `K-ma` | 28 Avril | 21:24 | Versemment | - | 6640.00 دج | - | - | - | - | - | - | - | `CGFD` |
| `WX9z` | 28 Avril | 11:53 | Demande_Versemet | - | - | - | - | - | - | - | - | - | `CGFD` |
| `wC9e` | 23 Avril | 23:51 | Credit | - | - | - | - | - | - | 15640.00 دج | - | - | `CGFD` |
| `2syQ` | 22 Avril | 19:10 | Credit | - | - | - | - | - | - | 14920.00 دج | - | - | `CGFD` |
| `uLj6` | 16 Avril | 22:57 | Credit | - | - | - | - | - | - | 10000.00 دج | - | - | `CGFD` |
| `fqTx` | 16 Avril | 22:57 | Versemment | - | 5830.00 دج | - | - | - | - | - | - | - | `CGFD` |
| `Iapt` | 9 Avril | 10:39 | Credit | - | - | - | - | - | - | 9045.00 دج | - | - | `CGFD` |

---

## 4. Set `listM8bon` (Total All Transactions - Top 10)
*Expression: `listM8bon ?: emptyList()`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `e20O` | 2 Mai | 12:10 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `4CTi` |
| `Xk8q` | 2 Mai | 11:49 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `KqwI` |
| `_dcK` | 2 Mai | 11:42 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `ERi7` |
| `jDdk` | 2 Mai | 11:37 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `m-NO` |
| `rVeK` | 2 Mai | 11:32 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `obS8` |
| `FVgm` | 2 Mai | 11:07 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `7fyp` |
| `pnW0` | 2 Mai | 10:06 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `5qVh` |
| `8fPH` | 2 Mai | 10:02 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `Feev` |
| `eFEB` | 2 Mai | 09:54 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `n1Lx` |
| `5_D5` | 2 Mai | 08:49 | ON_MODE_COMMEND_ACTUELLEMENT | - | - | - | - | - | - | - | - | - | `5_D6` |
