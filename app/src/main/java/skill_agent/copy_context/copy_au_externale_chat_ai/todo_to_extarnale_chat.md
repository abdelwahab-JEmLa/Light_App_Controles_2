---
name: todo_to_extarnale_chat
description: Use this skill to automatically gather all related code files and context (including visual images/screenshots) to solve a given TODO, bundle them together, and copy them to the clipboard for pasting into an external Deep-Thinking Web AI (like Claude Sonnet/Thinking or Gemini Pro). Trigger this whenever the user requests "todo_to_extarnale_chat", "con_cop", "copy_context", "cont_copie", "con_copie", "cop_cont", "cc_", or mentions copying context/TODO to an external AI.
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

## Goal
To locate all files related to the target TODO (using fast search/grep tools), integrate image/screenshot context if available, generate an explanatory context summary, overwrite the clipboard backup file (`hist_copie.md`), and copy everything directly to the Windows Clipboard for the external Deep-Thinking AI.

---

## Steps to Execute

### 1. Identify Target TODO & Locate Relevant Files
- Locate the TODO in the codebase (using grep search or view_file).
- Identify as many related files (`.kt`, `.xml`, etc.) as possible that are needed to understand or fix the TODO.
- The fast AI acts as a search and retrieval engine to find files containing the business logic, UI layouts, or databases relevant to the TODO.

### 2. Inspect and Include Visual/Image Context (if applicable)
- **Check for Images**: If the user mentions `img_`, or if the TODO description, layout file, or user prompt refers to a UI bug/screenshot, look for `img.jpg` or any image starting with `img_` (such as `img_*.jpg`, `img_*.png`) on the Desktop (`C:\Users\Abou Mohamed\Desktop`) or in the workspace:
  - View the image file to inspect its content visually.
  - Write a descriptive textual explanation of the visual elements, layout structure, or error messages shown in the image.
  - Include this visual analysis directly in the generated context summary so the external Deep-Thinking AI understands the visual context.
  - Add the absolute file path of the image (e.g. `C:\Users\Abou Mohamed\Desktop\img.jpg`) to `hist_copie.md` using the format `### 🔗 [img.jpg](file:///C:/Users/Abou%20Mohamed/Desktop/img.jpg)` so that the python clipboard script copies the actual image file to the clipboard alongside the code files.

### 3. Generate Context Summary (`ctsave_`)
- Implicitly execute the instructions for `ctsave_` (Context Transfer save). This compiles the architectural flow, active database schemas, and current session details into a session context markdown file (e.g., `app/src/main/java/skill_agent/copy_context/contexTrensefert/conversationsContext/<session_id>_agy.md`).
- Ensure the context file clearly explains the goal, the TODO to be solved, and includes the visual context from Step 2.

### 4. Overwrite `hist_copie.md`
- Overwrite the contents of the copy reference list `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\references\hist_copie.md` with:
  1. The generated session context file link.
  2. All related source code files.
  3. Any related visual assets/image links.
- Write these links using the exact Markdown format (one per line):
  ```markdown
  ### 🔗 [Fichier.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/...)
  ```

### 5. Execute Clipboard Copy (`cc_`)
- Run the batch file to bundle the files listed in `hist_copie.md` and copy them to the Windows clipboard:
  ```bash
  "C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\run_cc.bat"
  ```

### 6. Display Summary to the User
- Present the user with a clean Markdown table listing:
  - **Context File**: Link to the generated context file.
  - **Copied Files**: A list of all code and image files bundled.
  - **Execution Time**: The time taken to perform the search and bundle.
  - Direct executable link: `[🚀 Exécuter la Copie (run_cc_silent.vbs)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/copy_skill/run_cc_silent.vbs)`.
