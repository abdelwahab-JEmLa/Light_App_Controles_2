# Skill - Context Transfer (conTr_)

This skill teaches the assistant how to recover, catalog, and overwrite context from past implementation sessions by reading and managing files inside the context transfer folder [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).

Whenever the user triggers "conT_", "conTr_", "/contexTrensefert", "context_transfer", "ctsave_", "ctc_", or "ctecrase_", the assistant must follow these instructions.

---

## Trigger Phrases
- "conT_"
- "conTr_"
- "/contexTrensefert"
- "context_transfer"
- "ctsave_"
- "ctc_"
- "ctecrase_"

---

## Steps to Execute

### When "ctc_" is triggered:
#### 1. List Available Contexts
Read all `.md` files in [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).
#### 2. Display a Table of Conversations
Render a clean Markdown table with:
- **Conversation File**: Clickable link to the context file.
- **ID de la Conversation**: The conversation ID extracted from the filename.
- **Petit Résumé**: A brief 1-2 sentence summary of what implementation context is captured in that file.

### When "ctecrase_" is triggered:
#### 1. Identify Target Context File
Look up the context file associated with the current conversation ID (or a specified ID) in [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).
#### 2. Overwrite Content
Overwrite the contents of the target file with the updated explanation/architecture details from the current conversation using the `write_to_file` tool with `Overwrite: true`.

---

### When standard triggers ("conT_", "conTr_", "/contexTrensefert", "context_transfer", "ctsave_") are triggered:
#### 1. Read Context Folder
Read the directory contents of [conversationsContext](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext).
Read each `.md` file in this directory to understand the templates and codebase structures (each context file is named using the format `<conversation_id>_agy.md`).
#### 2. Apply the Context to New Requests
Use the loaded context files as a reference template. For example, if the user asks to "create M13_Operation_Fab", replicate the exact directory layout, action files, ViewModel states, and database sync methods described in the context files (such as [f96cf255-aa97-44e3-82b7-4c6e83c2df8f_agy.md](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/contexTrensefert/conversationsContext/f96cf255-aa97-44e3-82b7-4c6e83c2df8f_agy.md)) for the new model.
