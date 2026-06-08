# Skill - View Desktop Image 1 (scr_1)

This skill instructs the assistant on how to automatically navigate to the user's PC Desktop, locate an image named `image1` or `image_1` (with common formats like `.png`, `.jpg`, `.jpeg`), display it using the `view_file` tool, and analyze its content to help the user.

---

## Trigger Phrases
- `scr_1`
- `scr1`
- `image_1`
- `image1`

---

## Steps to Execute

### 1. Locate the Image on the Desktop
Search the PC Desktop (checking both `C:\Users\Abou Mohamed\Desktop` and the OneDrive Desktop fallback `C:\Users\Abou Mohamed\OneDrive\Desktop`) for files matching:
- `image1.png` / `image_1.png`
- `image1.jpg` / `image_1.jpg`
- `image1.jpeg` / `image_1.jpeg`

### 2. View the Image
- Once the image is found, use the `view_file` tool with its absolute path to load and view the image.
- If no matching image is found, print a clear error listing the paths checked and ask the user to verify the filename.

### 3. Analyze and Describe the Content
- Describe the visual layout of the image.
- Identify any text, Compose UI elements, errors, or bug descriptions displayed on the screen.
- Suggest solutions or codebase actions based on the visual findings.

---

## 🔗 Direct Links
* 🖼️ [scr_1 Skill Config](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent_pc/scr_1.md)
* ⚙️ [PC Help Page (hw_)](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent_pc/hw_.md)
