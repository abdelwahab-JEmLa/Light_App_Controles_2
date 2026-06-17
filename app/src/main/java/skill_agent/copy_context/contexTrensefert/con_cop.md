# Context to External AI Copy (con_cop)

This skill automates the extraction and bundling of the current session's context and its related files so that the user can seamlessly copy them into an external Web AI Chat (like ChatGPT, Claude, etc.).

## Trigger Phrases
- "con_cop"
- "context_copy"

## Goal
To easily port the ongoing task's context and all modified/relevant code files into an external AI assistant by generating a context file, overwriting the copy skill's reference list with these items, and executing a direct copy to the clipboard.

## Steps to Execute

### 1. Launch Context Save (`ctsave_`)
First, implicitly execute the instructions defined for `ctsave_` (Context Transfer save). This will generate or update the current session's `.md` context file (located in `skill_agent/copy_context/contexTrensefert/conversationsContext/<session_id>_agy.md`). Ensure the context is fully summarized and saved before proceeding.

### 2. Overwrite `hist_copie.md`
Overwrite the contents of `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\references\hist_copie.md` to point to the newly generated/updated context file AND all the relevant source code files (`.kt`, `.xml`, etc.) that were modified or are currently in focus during this session.

Write the links into `hist_copie.md` using the exact following Markdown link format (one per line):
```markdown
### 🔗 [Fichier1.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/...)
### 🔗 [Fichier2.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/...)
### 🔗 [ID_Context_agy.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/copy_context/contexTrensefert/conversationsContext/...)
```

### 3. Launch Copy to Clipboard (`cc_`)
Finally, execute the instructions defined for `cc_` (from the `copy_skill`). Run the python script `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\skill_agent\copy_context\copy_skill\scripts\fast_cc.py` to bundle the contents of all files listed in `hist_copie.md` and copy them directly to the Windows Clipboard.

### 4. Output Confirmation
Confirm to the user that the context and files have been successfully bundled and copied to their clipboard, ready to be pasted into the external Web AI chat. Include clickable links to the files that were bundled.
