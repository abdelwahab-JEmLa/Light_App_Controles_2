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

### 2. Inspect and Include Visual/Image Context (if applicable)
- Check for image files starting with `img_` or named `img.jpg`/`img.png` on the Desktop (`C:\Users\Abou Mohamed\Desktop`) or in the workspace to capture visual bugs/output screens.
- View and analyze the image, include a description of the visual layout/bug in the context file, and explicitly state in the context file that this screenshot is attached for the external AI to visually inspect and review the interface layout/bug.
- Add the image link to `hist_copie.md` so the copying script places the actual image file in the clipboard alongside the code files.

### 3. Generate Context Summary (`ctsave_`) with Deep-Thinking Instructions
- Compile the architectural flow and analysis into a session context markdown file under `app/src/main/java/skill_agent/copy_context/copy_au_externale_chat_ai/historique_explication/<session_id>_agy.md`.
- **CRITICAL**: 
  - **Standard Case**: Include a clear directive in the context file instructing the external thinking AI to review the entire architectural flow of the gathered files (model, enum, generators, drawing cells), highlighting that the external AI is the primary reasoning/thinking model for this verification. Additionally, explicitly ask the external AI to be time-efficient and try not to take too much time during its reasoning process.
  - **Quick Case (`cc_sans_explication`/`cc_se`)**: If the user triggered `cc_sans_explication` (or `cc_se`), write the text `"fix todo avec la facon la plus rapide"`, followed by the exact code snippet showing where the TODO is located, and include a brief reference to `t_.md` as the active skill file. Do not write any other explanations, analysis, or details.

### 4. Overwrite `hist_copie.md`
- Write the Markdown links of the context file, all gathered source code files, and the visual assets to `app/src/main/java/skill_agent/copy_context/copy_skill/references/hist_copie.md` in the exact format:
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
