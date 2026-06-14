# Skill - Fix TODOs (t_)

This skill instructs the assistant on how to automatically search for, identify, and fix `TODO` comments in the active codebase, and display a detailed code diff at the end of the explanations.

Additionally, this skill supports the **`t_models`** sub-trigger, which automatically adds `appDatabase.kt` and the `Models` package to the active restricted context before proceeding with the standard steps.

If **`t_flow`** is triggered, the assistant will display a summary of the credit/payment settlement flow and wait for 2 seconds before continuing.

If **`t_usage`** is triggered, the assistant will also compute and display the percentage and amount of model tokens consumed so far, and what remains in the current session (out of the calibrated 200,000 token limit) at the end of the execution report.

If **`>clientApp`** or **`>ca`** is triggered, the assistant will automatically redirect and execute the **Client JetPack Fix TODOs & Coding Patterns (t_appClient_chain_todo)** skill to inspect relative/chained TODOs and search coding patterns or files in the external client codebase.

**Central Dispatcher Capability**: If a `TODO` comment contains a trigger phrase for another custom skill (e.g., `TODO: log_`, `TODO: sem_`, `TODO: con_c`, `TODO: cop_`, `TODO: room_d`, `TODO: extract`), the assistant must automatically chain and execute the corresponding custom skill's steps on that file/package, rather than applying a manual code fix.

---

## Trigger Phrases
- "t_"
- "t_models"
- "t_flow"
- "t_usage"
- ">clientApp"
- ">ca"
- "fix_todo"
- "fix_todos"


---

## Steps to Execute

### When "t_models" is triggered:

#### 1. Add appDatabase.kt and Models packages to Context (cwa_)
Automatically invoke the context addition (`cwa_`) rules for the following packages:
- **`AppDatabase`** (Package: `com.example.light_app_controles.Modules.Base.SQL.Daos`)
- **`Models`** (Package: `EntreApps.Shared.Models`)

Generate and append the folder rules to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore):

```text
# Added via t_models (com.example.light_app_controles.Modules.Base.SQL.Daos)
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/com/
!app/src/main/java/com/example/
!app/src/main/java/com/example/light_app_controles/
!app/src/main/java/com/example/light_app_controles/Modules/
!app/src/main/java/com/example/light_app_controles/Modules/Base/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/Daos/
!app/src/main/java/com/example/light_app_controles/Modules/Base/SQL/Daos/**

# Added via t_models (EntreApps.Shared.Models)
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/EntreApps/
!app/src/main/java/EntreApps/Shared/
!app/src/main/java/EntreApps/Shared/Models/
!app/src/main/java/EntreApps/Shared/Models/**
```

#### 2. Execute standard TODO fixing
Proceed directly to the standard steps below to locate and fix TODOs.

### When "t_flow" is triggered:

#### 1. Display Settlement Flow Summary (Flow de Règlement)
Before applying or explaining any code modifications, the assistant MUST display a summary of the credit/payment settlement flow:
- **Credit (Crédit)**: Enregistre les transactions à crédit (débit client).
- **Versemment (Versement)**: Enregistre les paiements effectués par le client.
- **Demande_Versemet (Demande Versement)**: Représente le montant demandé par le vendeur.
- **New_Situation_Credit (Nouvelle Situation)**: Calcule la nouvelle balance en temps réel en faisant la somme des crédits moins les versements de la période.
- **currentCreditBalance**: Solde actuel global du client mis à jour à chaque impression de bon.

#### 2. Wait 2 Seconds (Attendre 2 secondes)
The assistant must pause execution for 2 seconds (using a simulated or real pause/sleep) before continuing to apply the TODO fixes.

#### 3. Proceed to Standard TODO Fixing
Continue immediately with the standard steps to locate and resolve TODOs.

### When "t_usage" is triggered:

#### 1. Calculate and Report Token Usage
At the end of the standard execution steps, estimate the total token count of the current conversation (using the transcript file size `transcript_full.jsonl` size in bytes / 4 + system context overhead of ~15,000 tokens) and calculate what percentage of the calibrated 200,000 token limit this represents. Output both the consumed token percentage and amount, as well as the remaining token amount and percentage prominently at the end of the report.

### When ">clientApp" or ">ca" is triggered:

#### 1. Dispatch to Client JetPack Fix TODOs & Coding Patterns Skill
Immediately redirect execution to the **Client JetPack Fix TODOs & Coding Patterns (t_appClient_chain_todo)** skill. Run its steps to locate outstanding relative/chained TODOs in the client codebase, search coding patterns or files, apply fixes, and report success. Do not execute the standard local codebase TODO steps.

---

### Standard Steps to Execute:

### 1. Locate outstanding TODOs in the codebase
The absolute fastest way to locate all TODO comments in the codebase is using the `grep_search` tool with the query `TODO` on the path `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main` (which covers code folders, custom PC skills, and files like `files_affiched.md` under `context_working_in`).
**CRITICAL Context-Isolation Rule**: When executing `t_` and `t_flow`, the assistant must **NOT** read, open, scan, or analyze any of the latest conversation history files or past session context files in the `conversationsContext` directory (specifically `app/src/main/java/skill_agent/contexTrensefert/conversationsContext`). Any matches found within this directory must be ignored, and the assistant must not access those files.
Note that some TODOs may be relative to/dependent on others, often indicated with specific markers like `//<--` or comments referring to previous/other TODOs (e.g. `//TODO(2.C Relative Au Todo(1):`). The assistant must carefully analyze these relationships and implement dependencies in the correct order.

### 1.5. Present Beginner-Friendly Summary
**CRITICAL RULE**: Before explaining the details of the code changes, the assistant MUST present a beginner-friendly summary of the problem, the list of concerned files to change, and a brief explanation of what changes will be made. The assistant should proceed directly to apply the modifications in the same turn without stopping to wait for user confirmation.

### 2. Implement the fixes in Code / Delegate to Skills
- **Skill Dispatcher Check**: For each found `TODO` comment, check if it contains a trigger for another custom skill:
  - If it contains `log_` (e.g. `TODO: log_`), execute the **Real-Time Logcat Inspector (log_)** skill steps.
  - If it contains `sem_` (e.g. `TODO: sem_`), execute the **Semantics Inspector (sem_)** skill steps.
  - If it contains `con_` (e.g. `TODO: con_c` or `TODO: co_`), execute the **Concise Code (consize_comments)** skill steps.
  - If it contains `cop_` (e.g. `TODO: cop_`), execute the **Copy Package / Sibling Files (cop_last)** skill steps.
  - If it contains `room_d` (e.g. `TODO: room_d`), execute the **Room Database Query (room_d)** skill steps.
  - If it contains `extract` (e.g. `TODO: extract` or `TODO: <extract ...>`), execute the **Extract Dependency (t_extract)** skill steps.
- **Standard Fixes**: If no skill trigger is matched, analyze the `TODO` comment requirements, apply the appropriate manual code fixes, and delete the comment using `replace_file_content` or `multi_replace_file_content`. Be sure to also thoroughly clean up any associated pointer/indicator comments (such as `//<--`, `//<-`, or any inline TODO pointers) from adjacent lines.
- **CRITICAL RULE**: Do NOT run compilation (like gradle compile) to verify errors during the `t_` skill execution. The `t_` skill is strictly to apply the code fixes without compiling. Compilation should only be run if explicitly requested by the user.

### 3. Report Success and Display Code Diffs
Provide the user with a detailed report including:
- Clickable links to the modified files.
- **Always display the time 1:30 to complete the quest (e.g., "Temps estimé pour terminer la quête : 1:30").**
- **A detailed git-style code diff showing all modified files at the very end of your explanations (unless the task delegated to the `sem_` or `room_d` skill, in which case DO NOT show the git-style code changes/diffs, only display the query/semantics details/infos).**
