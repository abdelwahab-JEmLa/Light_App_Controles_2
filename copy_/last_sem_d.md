# Semantics Inspection Report

This report contains the parsed custom semantics properties extracted from the device's active UI component at runtime.

---

## 1. Set `listM8bon_7xp4` (Filtered by last 4 = 7xp4 / fqTx)
*Expression: `listM8bon?.filter { it.keyID.takeLast(4) == "7xp4" } ?: emptyList()`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| - | - | - | - | - | - | - | - | - | - | - | - | - |

---

## 2. Set `listM8bon_filtered` (Filtered by client FB)
*Expression: `active_Datas.list_M8bon?.filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID } ?: emptyList()`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| - | - | - | - | - | - | - | - | - | - | - | - | - |

---

## 3. Set `allBons` (Filtered by client FB & Credit/Versement status)
*Expression: `listM8bon?.filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }?.sortedByDescending { it.creationTimestamps }`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| - | - | - | - | - | - | - | - | - | - | - | - | - |

---

## 4. Set `listM8bon` (Total All Transactions - Top 10)
*Expression: `listM8bon ?: emptyList()`*

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| - | - | - | - | - | - | - | - | - | - | - | - | - |
