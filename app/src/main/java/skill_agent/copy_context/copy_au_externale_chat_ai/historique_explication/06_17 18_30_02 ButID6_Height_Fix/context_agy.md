Note for the AI: The helper files under files_edited/ are truncated and wrapped in block comments (/* ... */) strictly to avoid compilation conflicts in the local Android Studio environment. Please read their contents as reference APIs and implement your fix within the main active file.

fix todo avec la facon la plus rapide (ne pas ecrire "todo resolved" ou "TODO resolved" a la fin)

### TODO Location:
File: `DropDownItem_ID6.kt` (around line 134)
```kotlin
        DropdownMenuItem(      //<--
        //TODO(1): pk le height de button est trop comme au img_ regle le
```

### Visual/Image Context:
A screenshot has been attached to show the layout bug:
- In the active dropdown menu dialog, there is a giant lilac empty rectangle (card/DropdownMenuItem) stretching down and occupying almost the entire screen height.
- This lilac container is the `DropDownItem_ID6` card (colored in `primaryContainer` because `activeStudentsCount > 0`).
- The contents of `DropDownItem_ID6` (such as text "قائمة متابعة الغيابات (PDF)" and icons) are pushed or invisible.
- This issue is caused by using three `OutlinedButton`s inside the `trailingIcon` parameter of `DropdownMenuItem`. Since `OutlinedButton` has high minimum height constraints (and touch target expansion), placing multiple of them inside the `trailingIcon` slot breaks the layout measurements and causes the dropdown menu height to inflate/stretch uncontrollably.
- To fix this, we should replace `OutlinedButton` with a more compact button, such as `IconButton` or `OutlinedIconButton` with a custom size (e.g. `32.dp` or `36.dp`) and a smaller inner icon size (e.g. `16.dp` or `18.dp`).

## 📄 [DropDownItem_ID6.kt](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/com/example/light_app_controles/B/Screens/Z/Screens/Apps/App/Options/View/DropDownItems/View/Buttons_Moin10/ButID6/DropDownItem_ID6.kt)

```kotlin
package Application5.App.View.DropDownItems.View.ButID6

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.Repository.Data.Repo20ObsarvationEtudion
import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.MonthSelectionDialog
import Application5.App.Repository.Data.Repo19Etudiant
import Application5.App.View.DropDownItems.View.ButID6.Pdf.PdfSaverUtility_But6
import Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur.ParentCommunicationCardData_But6
import Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur.generatePdfDocument_6
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Utilisateur
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun DropDownItem_ID6(
    nomFun: String = "قائمة متابعة الغيابات (PDF)",
    selectedMonth: Calendar? = null,
    selectedTeacher: Ousstad_Tahfid? = null,
    aCentralFacade: A_ViewModel_SeparatedAppsCodingPattern,
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion = aCentralFacade.repo20ObsarvationEtudion,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }
    var showMonthDialog by remember { mutableStateOf(false) }
    var showTeacherDialog by remember { mutableStateOf(false) }
    var chosenMonth by remember { mutableStateOf(selectedMonth) }
    var chosenTeacher by remember { mutableStateOf(selectedTeacher) }
    // (true, original default) or all students sorted by absence count (false).
    var hideAbsentStudents by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // FIXED: Get the actual current teacher from focused values
    val currentUtilisateur = remember(aCentralFacade.activeCentralValues) {
        aCentralFacade.activeCentralValues.active_Ousstad_Tahfid
            ?: Utilisateur.Admin // Fallback to Admin if no teacher is selected
    }

    val activeStudentsCount = remember(repo19Etudiant.datasValue, chosenTeacher) {
        val students = repo19Etudiant.datasValue.filter { !it.exclue_de_l_affiche_au_classe }

        if (chosenTeacher != null && chosenTeacher != Ousstad_Tahfid.Non_Defini_Actuellemen) {
            // FIXED: Use the actual key property from the enum
            students.count { student ->
                student.parent_ousstad_key == chosenTeacher!!.key
            }
        } else {
            students.size
        }
    }

    // Month selection dialog
    if (showMonthDialog) {
        MonthSelectionDialog(
            onDismiss = { showMonthDialog = false },
            onMonthSelected = { month ->
                chosenMonth = month
                showMonthDialog = false
            }
        )
    }

    // Teacher selection dialog
    if (showTeacherDialog) {
        TeacherSelectionDialog(
            onDismiss = { showTeacherDialog = false },
            onTeacherSelected = { teacher ->
                chosenTeacher = teacher
                showTeacherDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.secondaryContainer
                activeStudentsCount > 0 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLoading) 8.dp else 4.dp)
    ) {
        DropdownMenuItem(      //<--
        //TODO(1): pk le height de button est trop comme au img_ regle le
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "جدول الحضور",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "ملف PDF",
                        tint = if (activeStudentsCount > 0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

            },
            text = {
                val monthText = if (chosenMonth != null) {
                    SimpleDateFormat("MMMM yyyy", Locale("ar")).format(chosenMonth!!.time)
                } else {
                    "الشهر الحالي"
                }

                val teacherText = when (chosenTeacher) {
                    null -> "جميع الأساتذة"
                    Ousstad_Tahfid.Non_Defini_Actuellemen -> "جميع الأساتذة"
                    else -> chosenTeacher!!.nom_arab
                }

                val displayTeacherText = if (teacherText.contains("انتقالي")) "دراسة حالة من الادارة" else teacherText

                Text(
                    text = when {
                        isLoading && generationStatus.isNotEmpty() -> generationStatus
                        isLoading -> "جاري الإنشاء..."
                        activeStudentsCount > 0 -> "$nomFun\n$monthText - $displayTeacherText\n($activeStudentsCount طالب)"
                        else -> nomFun
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                if (!isLoading && activeStudentsCount > 0) {
                    createAndOpenPdfDocument(
                        context = context,
                        repo19Etudiant = repo19Etudiant,
                        repo20Observation = repo20Observation,
                        selectedMonth = chosenMonth,
                        selectedTeacher = chosenTeacher,
                        hideAbsentStudents = hideAbsentStudents,
                        onLoadingChange = { isLoading = it },
                        onStatusChange = { generationStatus = it }
                    )
                }
            },
            enabled = !isLoading && activeStudentsCount > 0,
            trailingIcon = {
                Row {
                    // Absence display toggle button
                    OutlinedButton(
                        onClick = { hideAbsentStudents = !hideAbsentStudents },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (hideAbsentStudents) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (hideAbsentStudents) "إظهار الغائبين في القائمة" else "إخفاء الغائبين عن القائمة",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Month selector button
                    OutlinedButton(
                        onClick = { showMonthDialog = true },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "اختر الشهر",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Teacher selector button
                    OutlinedButton(
                        onClick = { showTeacherDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "اختر الأستاذ",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun TeacherSelectionDialog(
    onDismiss: () -> Unit,
    onTeacherSelected: (Ousstad_Tahfid?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر الأستاذ") },
        text = {
            androidx.compose.foundation.lazy.LazyColumn {
                // All teachers option
                item {
                    OutlinedButton(
                        onClick = { onTeacherSelected(null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("جميع الأساتذة")
                    }
                }

                // Individual teachers
                items(Ousstad_Tahfid.values().size) { index ->
                    val teacher = Ousstad_Tahfid.values()[index]
                    if (teacher != Ousstad_Tahfid.Non_Defini_Actuellemen) {
                        OutlinedButton(
                            onClick = { onTeacherSelected(teacher) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(teacher.nom_arab)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

fun createAndOpenPdfDocument(
    context: Context,
    repo19Etudiant: Repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion,
    selectedMonth: Calendar?,
    selectedTeacher: Ousstad_Tahfid?,
    hideAbsentStudents: Boolean = true,
    onLoadingChange: (Boolean) -> Unit,
    onStatusChange: (String) -> Unit
) {
    onLoadingChange(true)
    onStatusChange("جاري التحضير...")

    kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
        try {
            // FIXED: Filter students by teacher using the correct key property
            val activeEtudiants = repo19Etudiant.datasValue
                .filter { !it.exclue_de_l_affiche_au_classe }
                .filter { student ->
                    if (selectedTeacher != null && selectedTeacher != Ousstad_Tahfid.Non_Defini_Actuellemen) {
                        student.parent_ousstad_key == selectedTeacher.key
                    } else {
                        true
                    }
                }
                .sortedWith(
                    compareBy<M19Etudiant> { it.positon_don_classe }
                        .thenBy { it.creationTimestamps }
                )

            if (activeEtudiants.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "لا يوجد طلاب نشطون في القائمة", Toast.LENGTH_LONG).show()
                }
                onLoadingChange(false)
                onStatusChange("")
                return@launch
            }

            // Filter observations by month if specified
            val observations = if (selectedMonth != null) {
                val monthStart = Calendar.getInstance().apply {
                    timeInMillis = selectedMonth.timeInMillis
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                val monthEnd = Calendar.getInstance().apply {
                    timeInMillis = selectedMonth.timeInMillis
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }

                repo20Observation.datasValue.filter { obs ->
                    obs.creationTimestamps >= monthStart.timeInMillis &&
                            obs.creationTimestamps <= monthEnd.timeInMillis
                }
            } else {
                repo20Observation.datasValue
            }

            onStatusChange("جاري معالجة ${activeEtudiants.size} طالب...")

            // ── Diagnostic log ────────────────────────────────────────────────────────
            // Helps answer: "why are Raeeb observations present but not shown in the PDF?"
            //
            // Root cause checklist:
            //   1. The observation's day-of-month must match a session day (Sunday/Thursday).
            //      If it was recorded on a Mon-Wed/Fri-Sat it will count in totals but NEVER
            //      appear in the calendar columns (getAbsencesByDate silently drops it).
            //   2. The pre-filter above restricts observations to [monthStart, monthEnd].
            //      Any obs whose creationTimestamps falls even one second outside that range
            //      is invisible to the PDF.
            //   3. affiche_que_aucune_n_ai_absent=true means ONLY students with 0 absences
            //      are rendered — an absent student's row won't appear at all.
            run {
                val TAG = "AbsenceDebug_PDF"
                val sessionDays = setOf(java.util.Calendar.SUNDAY, java.util.Calendar.THURSDAY)
                val dayNames = mapOf(
                    java.util.Calendar.SUNDAY to "الأحد",
                    java.util.Calendar.MONDAY to "الإثنين",
                    java.util.Calendar.TUESDAY to "الثلاثاء",
                    java.util.Calendar.WEDNESDAY to "الأربعاء",
                    java.util.Calendar.THURSDAY to "الخميس",
                    java.util.Calendar.FRIDAY to "الجمعة",
                    java.util.Calendar.SATURDAY to "السبت"
                )
                val monthLabel = if (selectedMonth != null)
                    java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale("ar")).format(selectedMonth.time)
                else "الشهر الحالي"

                Log.e(TAG, "═══ PDF DIAGNOSTIC ═══")
                Log.e(TAG, "Selected month  : $monthLabel")
                Log.e(TAG, "Total obs passed: ${observations.size}")
                Log.e(TAG, "Active students : ${activeEtudiants.size}")

                activeEtudiants.forEach { etudiant ->
                    val raeebObs = observations.filter {
                        it.etudiant_keyID == etudiant.keyID &&
                                it.type == Application5.App.Repository.M20ObsarvationEtudion.Type.Raeeb
                    }
                    Log.e(TAG, "")
                    Log.e(TAG, "  Student: ${etudiant.nom} ${etudiant.prenom}")
                    Log.e(TAG, "  Raeeb obs in month: ${raeebObs.size}")
                    raeebObs.forEach { obs ->
                        val cal = java.util.Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
                        val dow  = cal.get(java.util.Calendar.DAY_OF_WEEK)
                        val isSession = dow in sessionDays
                        Log.e(TAG, "    Day ${cal.get(java.util.Calendar.DAY_OF_MONTH)} " +
                                "(${dayNames[dow]}) " +
                                "→ ${if (isSession) "✅ session day → WILL appear" else "❌ NOT a session day (الأحد/الخميس) → WON'T appear in calendar columns"} " +
                                "| justified=${obs.tabrire_riyab.isNotBlank()}")
                    }
                }
                Log.e(TAG, "═══════════════════════")
            }
            // ─────────────────────────────────────────────────────────────────────────

            val cardsData = activeEtudiants.map { etudiant ->
                ParentCommunicationCardData_But6.fromEtudiant(
                    etudiant = etudiant,
                    observations = observations
                )
            }

            onStatusChange("جاري إنشاء الجدول...")

            val pdfFile = withContext(Dispatchers.IO) {
                generatePdfDocument_6(
                    context = context,
                    cardsData = cardsData,
                    etudiants = activeEtudiants,
                    observations = observations,
                    selectedTeacher = selectedTeacher,
                    selectedMonth = selectedMonth,
                    affiche_que_aucune_n_ai_absent = hideAbsentStudents
                )
            }

            if (pdfFile == null || !pdfFile.exists()) {
                throw Exception("فشل إنشاء ملف PDF")
            }

            onStatusChange("جاري الحفظ...")

            val teacherName = selectedTeacher?.nom_arab
            val monthSuffix = if (selectedMonth != null) {
                "_${SimpleDateFormat("yyyy_MM", Locale.getDefault()).format(selectedMonth.time)}"
            } else {
                ""
            }
            val fileName = "قائمة_الطلاب_${teacherName}${monthSuffix}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

            val saveResult = withContext(Dispatchers.IO) {
                PdfSaverUtility_But6.savePdf(
                    context = context,
                    sourceFile = pdfFile,
                    fileName = fileName,
                    subFolder = "Tahfide_Quran"
                )
            }

            withContext(Dispatchers.Main) {
                saveResult.fold(
                    onSuccess = { savedPath ->
                        openPdfWithViewer(context, pdfFile)
                        Toast.makeText(
                            context,
                            "✅ تم إنشاء وحفظ قائمة ${activeEtudiants.size} طالب\n$savedPath",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            context,
                            "❌ خطأ في الحفظ: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("AttendanceReport", "❌ خطأ: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ خطأ: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            onLoadingChange(false)
            onStatusChange("")
        }
    }
}

private fun openPdfWithViewer(context: Context, pdfFile: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "⚠️ لا يوجد تطبيق PDF مثبت\nتم حفظ الملف في التنزيلات",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Log.e("AttendanceReport", "❌ خطأ في فتح PDF", e)
        Toast.makeText(context, "❌ خطأ في فتح الملف: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
```
