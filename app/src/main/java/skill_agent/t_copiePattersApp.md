# Skill - Copy Coding Patterns to Light App (t_copiePattersApp)

This skill instructs the assistant on how to automatically copy, adapt, and port coding patterns, feature implementations, and fixes (such as solutions to `TODO(1)` comments) from the `ClientJetPack` codebase to the local `Light_App_Controles` (light app) project codebase. It guides the assistant on adjusting package imports, database calls, parameters, and layout structures to fit the light app's requirements.

---

## Trigger Phrases
- "t_copiePattersApp"
- "t>copiePattersApp"
- "copiePattersApp"

---

## Steps to Execute

### 1. Identify Coding Patterns in the Client Codebase
- Search for the reference feature, fix, or `TODO(1)` in the `ClientJetPack` project (`D:\AndroidStudioProjects\ClientJetPack`).
- Run `git diff` or inspect target files to examine how the feature/logic was implemented (e.g. view models, screen layout, state management, or repository bindings).

### 2. Locate Target Files in the Light App
- Find the corresponding target files or layouts in the local `Light_App_Controles` project (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles`).
- Check package declarations, imports, and variables to identify differences (e.g., packages starting with `com.example.light_app_controles` instead of client-specific packages).

### 3. Adapt and Port the Coding Patterns
- Copy the core logic/components and manually modify them to match the light app's architecture.
- Adjust references to:
  - **Database & DAOs**: Use the light app's `appDatabase` DAOs and model names.
  - **Parameters**: Match parameter signatures of the constructors/methods in the light app.
  - **Package Imports**: Resolve package names (e.g. package locations of `Setter_LongDatas` or FAB components).
- Apply changes using editing tools (`replace_file_content` or `multi_replace_file_content`).

### 4. Verify Local Compilation
- Run a compilation check in the local `Light_App_Controles` project to verify the changes:
  ```powershell
  .\gradlew.bat compileDebugKotlin --offline --parallel --build-cache --configuration-cache
  ```
- Parse any compilation errors (e.g., unresolved references) and correct them immediately.

### 5. Report Success and Display Diffs
- Provide the user with clickable links to the modified files in the local project.
- Display a detailed git-style diff showing all modified files at the very end of your explanations.
