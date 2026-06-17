---
name: todo_to_extarnale_chat
description: Use this skill to automatically gather all related code files and context (including visual images/screenshots) to solve a given TODO, bundle them together, and copy them to the clipboard for pasting into an external Deep-Thinking Web AI (like Claude Sonnet/Thinking or Gemini Pro). Trigger this whenever the user requests "todo_to_extarnale_chat", "con_cop", "copy_context", "cont_copie", "con_copie", "cop_cont", "cc_", "cc_se", "cc_sans_explication", or mentions copying context/TODO to an external AI.
---

# Skill - Todo to External Chat Copy (todo_to_extarnale_chat)

This skill automates the process of using a fast AI assistant to quickly locate all relevant source files and visual/image assets related to a specific TODO, summarize the architectural context, bundle them into a single clipboard payload, and copy them. This allows the user to paste the complete problem context directly into a slower, Deep-Thinking external AI (like Claude Sonnet/Thinking) to generate the fix.

## Trigger Phrases
- "todo_to_extarnale_chat"
- "con_cop"
- "copy_context"
- "cont_copie"
- "con_copie"
- "cop_cont"
- "cc_"
- "cc_se"
- "cc_sans_explication"

## Goal
To locate all files related to the target TODO (using fast search/grep tools), integrate image/screenshot context if available, generate an explanatory context summary, overwrite the clipboard backup file (`hist_copie.md`), and copy everything directly to the Windows Clipboard for the external Deep-Thinking AI.

---

## Steps to Execute

### 1. Identify Target TODO & Locate Relevant Flow Files
- Locate the target TODO in the codebase.
- Trace and gather all related files that form the architectural flow of that TODO (such as data models e.g., `M19Etudiant.kt`, repository enums e.g., `SOUAR.kt`, generation modules e.g., `generatePdfDocument.kt`, drawing renderers e.g., `drawMokarrarCell.kt`, and custom skill files e.g., `t_.md`).
- Collect up to 20 files total. Do not exceed this limit to avoid cluttering the context window.

### 2. Inspect and Include Visual/Image Context (if requested)
- **CRITICAL CONDITIONAL RULE**: Only check for and include image files if the user explicitly mentions keywords such as "image", "screenshot", "capture", "visuel", "screen", "photo", or "png"/"jpg" in their request. If none of these keywords are mentioned, skip this step entirely and do NOT add any image files to `hist_copie.md` or the clipboard.
- If requested:
  - Check for image files starting with `img_` or named `img.jpg`/`img.png` on the Desktop (`C:\Users\Abou Mohamed\Desktop`) or in the workspace to capture visual bugs/output screens.
  - View and analyze the image, include a description of the visual layout/bug in the context file, and explicitly state in the context file that this screenshot is attached for the external AI to visually inspect and review the interface layout/bug.
  - Add the image link to `hist_copie.md` so the copying script places the actual image file in the clipboard alongside the code files.

### 3. Generate Context Summary (`ctsave_`) with Deep-Thinking Instructions
- Create a session folder inside `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/` named using the format:
  `<MM_dd HH_mm_ss> <Title>` (e.g. `06_17 17_28 Absences_PDF_Toggle`).
- Write the session context markdown file named `context_agy.md` inside this session folder.
- **CRITICAL**: 
  - **Standard Case**: Include a clear directive in the context file instructing the external thinking AI to review the entire architectural flow of the gathered files (model, enum, generators, drawing cells), highlighting that the external AI is the primary reasoning/thinking model for this verification. Additionally, explicitly ask the external AI to be time-efficient and try not to take too much time during its reasoning process.
  - **Quick Case (`cc_sans_explication`/`cc_se`)**: If the user triggered `cc_sans_explication` (or `cc_se`):
    1. Write the text `"fix todo avec la facon la plus rapide"`, followed by the exact code snippet showing where the TODO is located, and include a brief reference to `t_.md` as the active skill file. Do not write any other explanations, analysis, or details.
    2. **Optimize Context & Token Usage**: For secondary or less important files (e.g., large configuration files, verbose helper classes, or files with code that is not directly related to the TODO), create the directory `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/<MM_dd HH_mm_ss> <Title>/files_edited/`.
    3. Place the temporary truncated copies of these files in this `files_edited/` folder.
    4. In these copies, remove/truncate unnecessary lines of code (such as unrelated methods, large comments, or boilerplate code) to avoid distracting the external AI and to optimize token counts and copy/paste times. **CRITICAL**: Do NOT delete the `package` declaration (it must remain intact) or imports essential for the files.
    5. **CRITICAL COMPILER SAFETY**: Wrap the entire content of each file inside `files_edited/` in a multi-line block comment (`/*` at the very beginning of the file, and `*/` at the very end of the file). This is extremely important to prevent Android Studio's compiler from parsing them and throwing "Conflicting overloads" or "Duplicate class" errors, while keeping the contents fully readable for the external AI.
    6. Write the paths of these optimized temporary files in `hist_copie.md` instead of the original project paths, so the clipboard contains only the lean context.

### 4. Overwrite `hist_copie.md`
- Write the Markdown links of the context file, all gathered source code files, and the visual assets to `app/src/main/java/skill_agent/copy_context/copy_skill/references/hist_copie.md`.
- **CRITICAL**: For `cc_se`, also write a copy of this `hist_copie.md` file inside the created session folder at `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/<MM_dd HH_mm_ss> <Title>/hist_copie.md` to maintain a permanent record of the file links copied in that session.
- Format for links:
  ```markdown
  ### 🔗 [Filename.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/...)
  ```

### 5. Execute Clipboard Copy (`cc_`)
- Run the batch file to copy the files directly to the Windows Clipboard:
  ```bash
  "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
  ```

### 6. Display Summary to the User
- Present the user with a clean Markdown table listing:
  - **Context File**: Link to the generated context file.
  - **Copied Files**: A list of all code and image files bundled.
  - **Execution Time**: The time taken to perform the search and bundle.
  - Direct executable link: `[🚀 Exécuter la Copie (run_cc_silent.vbs)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_skill/run_cc_silent.vbs)`.
