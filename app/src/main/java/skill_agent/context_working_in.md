# Skill - Context Working_IN.Feature (c_w_a, c_w_d, c_w_e)

This skill instructs the assistant on how to isolate the AI's working context solely to the `Working_IN.Feature` package (when requested with `c_w_a` or `agy_context_unique_workingIn_active`), restore full workspace context (when requested with `c_w_d` or `agy_context_unique_workingIn_desactive`), or check the current status of the context restriction (when requested with `c_w_e`).

---

## Trigger Phrases
- "agy_context_unique_workingIn_active"
- "agy_context_unique_workingIn_desactive"
- "c_w_a"
- "c_w_d"
- "c_w_e"

---

## Steps to Execute

### When "c_w_a" or "agy_context_unique_workingIn_active" is triggered:

#### 1. Create .antigravityignore in Project Root
Create or overwrite the `.antigravityignore` file at the root of the project (`C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\.antigravityignore`) with the following content:
```text
# Ignore everything by default
*

# Allow the path to Working_IN/Feature
!app/
!app/src/
!app/src/main/
!app/src/main/java/
!app/src/main/java/Working_IN/
!app/src/main/java/Working_IN/Feature/
!app/src/main/java/Working_IN/Feature/**

# Allow skill_agent configuration so skills still work
!app/src/main/java/skill_agent/
!app/src/main/java/skill_agent/**
```

#### 2. Create .geminiignore in Project Root
Create or overwrite the `.geminiignore` file at the root of the project with identical content.

#### 3. Report Activation Success
Provide the user with a detailed confirmation showing:
- A clear message stating that the workspace context is now focused exclusively on `Working_IN.Feature`.
- Direct clickable markdown links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore).

---

### When "c_w_d" or "agy_context_unique_workingIn_desactive" is triggered:

#### 1. Remove the Context Restrictions
Delete or clear the contents of `.antigravityignore` and `.geminiignore` files at the root of the project to allow the agent to see all files in the project.

#### 2. Report Deactivation Success
Provide the user with a clear message stating that the full workspace context is now restored and Antigravity can work on all packages of the project.

---

### When "c_w_e" is triggered:

#### 1. Check File Status
Check if the `.antigravityignore` and/or `.geminiignore` files exist in the project root and read their contents to verify if the restrict rule `*` is active.

#### 2. Report Current Status
- **If active**: Confirm that context restriction is **ACTIF** (only `Working_IN.Feature` is visible).
- **If inactive**: Confirm that context restriction is **INACTIF / DÉSACTIVÉ** (the full workspace context is visible).
- Show clickable links to [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore) and [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore).
