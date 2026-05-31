# Skill - Real-Time Semantics Inspector (sem_)

This skill instructs the assistant on how to automatically detect any dynamic developer TODO comment containing `sem_` or `filter` (e.g., `//TODO: sem_ $variable` or `//TODO: filter $variable`) next to any component or layout element, automatically inject the corresponding custom semantics block directly onto the modified component or the component marked with `//<--` (such as a Text or specific view), remove the comment, and **immediately dump the active Android UI hierarchy using ADB uiautomator dump without rebuilding the app**, then extract and display the 13 important credit transaction values in a beautiful table to the user in near real-time.

---

## Trigger Phrases
- "sem_"
- "sem_d"
- "Todo: sem_"
- "Todo: filter"

---

## Steps to Execute

### 1. Locate Dynamic Semantics TODO Comments
Search the codebase (`app/src/main/java`) for any dynamic semantics/filter comments using the `grep_search` tool:
- Query: `TODO: sem_` or `TODO: filter` (case-insensitive)
- Extract the file name, line number, and targeted variable/filter expression.

### 2. Inject Semantics Modifier
- Locate the modified component or the specific component/line marked with `//<--` (e.g., where the arrow comment `//<--` is placed next to the component).
- Inject a Jetpack Compose `.semantics` modifier directly on this component or layout element, setting the variable as a custom semantics property.
- **Critical Placement:** Inject the `.semantics` modifier directly into the modified component or the component marked with `//<--` rather than outer layout containers, ensuring the custom semantics property is attached precisely to that element:
  ```kotlin
  Text(
      text = "...",
      modifier = Modifier
          .padding(16.dp)
          .semantics(mergeDescendants = true) {
              set(value = $variable, key = SemanticsPropertyKey("$variable"))
          }
  )
  ```
- Remove the triggering `TODO` comment from the source file.

### 3. Dump and Pull UI Hierarchy via ADB (No Rebuild)
Do NOT run any Gradle compilation or build tasks. Directly capture the current active screen hierarchy from the device:
```powershell
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell uiautomator dump /sdcard/window_dump.xml
& "C:\Users\Abou Mohamed\AppData\Local\Android\Sdk\platform-tools\adb.exe" pull /sdcard/window_dump.xml
```

### 4. Parse and Display the Semantics Data
Read the pulled `window_dump.xml` using the `view_file` tool. Parse the node hierarchy corresponding to the Compose container and extract the active transaction data.
Construct and display a clean, beautiful Markdown table representing the main **13 important values** of the **`M8` Bons (`M8BonVent`)** under the **Credit** context:
1. **ID**: Short unique identifier of the bon (last 4 characters of `keyID`).
2. **Date & Heure**: Formatted timestamp/time of the transaction (`creationTimestamps` / `heurDebutInString`).
3. **État (Type)**: The transaction type state (`etateActuellementEst`).
4. **Montant Principal**: The main amount for the specific type transaction (`montant_principale_du_type`).
5. **Versement Fait**: The payment amount made (`versement_fait`).
6. **Ancien Crédit**: The previous debit balance of the client (`ancien_credit`).
7. **Nouveau Crédit**: The updated outstanding balance after this transaction (`new_credit_apre_tout_fait`).
8. **Crédit Cumulé**: The accumulated credit recorded (`sum_De_Credit_Fait`).
9. **Versement**: The active versement value (`versement`).
10. **Crédit Fait**: The active credit value (`credit_fait`).
11. **Nouvelle Situation**: The resulting customer situation balance (`new_situation`).
12. **Total Sauvegardé**: The total amount saved (`totale_saved`).
13. **Client**: The associated client parent ID (`parent_M2Client_KeyID`).

Use the following Markdown table structure containing all 13 columns to display the results:

| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `3tUR` | 30 Avril \| 22:27 | Credit | 10890.00 دج | - | - | 10890.00 دج | 10890.00 دج | - | 10890.00 دج | 10890.00 دج | - | `GFD` |
