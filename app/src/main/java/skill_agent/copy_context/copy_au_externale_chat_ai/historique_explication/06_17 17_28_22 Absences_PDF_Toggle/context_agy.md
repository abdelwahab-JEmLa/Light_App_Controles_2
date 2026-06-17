fix todo avec la facon la plus rapide

```kotlin
                //TODO(1): ajou t un button qui toggle l affichage des absens ou non 
                Text(           //<--
                    text = when {
                        isLoading && generationStatus.isNotEmpty() -> generationStatus
                        isLoading -> "جاري الإنشاء..."
                        activeStudentsCount > 0 -> "$nomFun\n$monthText - $displayTeacherText\n($activeStudentsCount طالب)"
                        else -> nomFun
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
```

Active skill file reference: [`t_.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_.md)
