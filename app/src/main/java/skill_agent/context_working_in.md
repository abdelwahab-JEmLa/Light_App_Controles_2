# Skill - Context Working_IN.Feature (Active/Désactive)

This skill instructs the assistant on how to isolate the AI's working context solely to the `Working_IN.Feature` package (when requested with `agy_context_unique_workingIn_active`), or restore full workspace context (when requested with `agy_context_unique_workingIn_desactive`), by configuring or deleting the `.antigravityignore` and `.geminiignore` files.

---

## Trigger Phrases
- "agy_context_unique_workingIn_active"
- "agy_context_unique_workingIn_desactive"

---

## Steps to Execute

### When "agy_context_unique_workingIn_active" is triggered:

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
- Direct clickable markdown links to:
  - [.antigravityignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.antigravityignore)
  - [.geminiignore](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/.geminiignore)

---

### When "agy_context_unique_workingIn_desactive" is triggered:

#### 1. Remove the Context Restrictions
Delete or clear the contents of `.antigravityignore` and `.geminiignore` files at the root of the project to allow the agent to see all files in the project.

#### 2. Report Deactivation Success
Provide the user with a clear message stating that the full workspace context is now restored and Antigravity can work on all packages of the project.
