fix todo avec la facon la plus rapide


`kotlin
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
``n
Active skill file reference: [`t_.md`](file:///C:/Users/Abou%20Mohamed/AndroidStudioProjects/Light_App_Controles/app/src/main/java/skill_agent/t_.md)


---

## 📄 DropDownItem_ID6.kt

`kotlin
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
        DropdownMenuItem(
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

                Text(           //<--
                //TODO(1): ajou t un button qui toggle l affichage des absens ou non 
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
                        onLoadingChange = { isLoading = it },
                        onStatusChange = { generationStatus = it }
                    )
                }
            },
            enabled = !isLoading && activeStudentsCount > 0,
            trailingIcon = {
                Row {
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
                    selectedMonth = selectedMonth
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
``n

---

## 📄 generatePdfDocument_6.kt

`kotlin
package Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.getSessionDatesForMonth
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.toColorInt
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Constants
private const val SIZE_TEXT_ATTENDANCE_STATUS = 9f
private const val SIZE_TEXT_ABSENCE_COUNT = 9f
private const val SIZE_TEXT_NAME_AGE = 14f
private const val NOMB_ETUDION_PAR_PAGE_FIRST = 5
private const val NOMB_ETUDION_PAR_PAGE = 8
private const val COLUMN_HEIGHT_ETUDION = 60f
private const val FIRST_HEADER_HEIGHT = 20f
// If true: show ONLY students with zero absences, sorted by most-recently-created first.
// If false: show all students, sorted by unjustified-absence count (most absent first).
private const val affiche_que_aucune_n_ai_absent = true

fun generatePdfDocument_6(
    context: Context,
    cardsData: List<ParentCommunicationCardData_But6>,
    etudiants: List<M19Etudiant> = emptyList(),
    observations: List<M20ObsarvationEtudion> = emptyList(),
    selectedTeacher: Ousstad_Tahfid? = Ousstad_Tahfid.Abdelwahab_Osstad,
    selectedMonth: Calendar? = null
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_attendance_report_${System.currentTimeMillis()}.pdf")

        val pageWidth = 842
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        // Use selected month or current month
        val targetMonth = selectedMonth ?: Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale("ar"))
        val currentMonth = monthFormat.format(targetMonth.time)

        // Calculate total sessions for the selected month
        val totalSessions = calculateSessionsForMonth(targetMonth)
        var teacherNameArabic = selectedTeacher?.nom_arab ?: ""
        if (teacherNameArabic.contains("انتقالي")) {
            teacherNameArabic = "دراسة حالة من الادارة"
        }

        val absenceIcon = try {
            val iconStream = context.resources.openRawResource(
                context.resources.getIdentifier("absent", "drawable", context.packageName)
            )
            BitmapFactory.decodeStream(iconStream)
        } catch (e: Exception) {
            Log.w("PDF", "Absence icon (absent.png) not found")
            null
        }

        val justificationIcon = try {
            val iconStream = context.resources.openRawResource(
                context.resources.getIdentifier("tabrire", "drawable", context.packageName)
            )
            BitmapFactory.decodeStream(iconStream)
        } catch (_: Exception) {
            Log.w("PDF", "Justification icon (tabrire.png) not found")
            null
        }

        val sortedIndices = if (affiche_que_aucune_n_ai_absent) {
            etudiants.indices
                .filter { i ->
                    AbsenceStatistics.calculate(etudiants[i], observations, selectedMonth)
                        .totalAbsences == 0
                }
                .sortedByDescending { i -> etudiants[i].creationTimestamps }
        } else {
            etudiants.indices.sortedWith(
                compareByDescending<Int> {
                    AbsenceStatistics.calculate(etudiants[it], observations, selectedMonth).unjustifiedAbsences
                }.thenBy { "${etudiants[it].nom} ${etudiants[it].prenom}" }
            )
        }

        val studentsFirstPage = NOMB_ETUDION_PAR_PAGE_FIRST
        val studentsPerPage = NOMB_ETUDION_PAR_PAGE

        // Pagination is based on the effective (possibly filtered) student list
        val effectiveCount = sortedIndices.size
        val totalPages = if (effectiveCount <= studentsFirstPage) {
            1
        } else {
            1 + ((effectiveCount - studentsFirstPage + studentsPerPage - 1) / studentsPerPage)
        }

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val marginLeft = 20f
            val marginRight = 20f
            val marginBottom = 30f
            val contentWidth = pageWidth - marginLeft - marginRight

            var yPosition = 20f

            // Paint configurations
            val paintTitleRed = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#D32F2F".toColorInt()
            }

            val paintTitleBlack = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintTableHeader = TextPaint().apply {
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.WHITE
            }

            val paintAbsenceColumnHeader = TextPaint().apply {
                textSize = 7f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.WHITE
            }

            val paintTableCell = TextPaint().apply {
                textSize = SIZE_TEXT_NAME_AGE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintWarning = TextPaint().apply {
                textSize = SIZE_TEXT_ABSENCE_COUNT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#F44336".toColorInt()
            }

            val paintSuccess = TextPaint().apply {
                textSize = SIZE_TEXT_ATTENDANCE_STATUS
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#4CAF50".toColorInt()
            }

            val paintJustified = TextPaint().apply {
                textSize = 7f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#FF9800".toColorInt()
            }

            val paintIjaza = TextPaint().apply {
                textSize = 7f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#4CAF50".toColorInt()
            }

            val paintBorder = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            val paintHeaderBg = Paint().apply {
                color = "#2196F3".toColorInt()
                style = Paint.Style.FILL
            }

            val paintAlternateBg = Paint().apply {
                color = "#F5F5F5".toColorInt()
                style = Paint.Style.FILL
            }

            val paintSubHeaderBg = Paint().apply {
                color = "#1976D2".toColorInt()
                style = Paint.Style.FILL
            }

            val paintSubHeaderText = TextPaint().apply {
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.WHITE
            }

            val absencePaints = AbsenceDrawer.createAbsencePaints()

            // First page header
            if (pageIndex == 0) {
                try {
                    val logoStream = context.resources.openRawResource(
                        context.resources.getIdentifier("ecole_logo1", "drawable", context.packageName)
                    )
                    val logoBitmap = BitmapFactory.decodeStream(logoStream)

                    val logoWidth = 200f
                    val logoHeight = 80f
                    val logoX = marginLeft + (contentWidth - logoWidth) / 2

                    canvas.drawBitmap(
                        logoBitmap,
                        null,
                        RectF(logoX, yPosition, logoX + logoWidth, yPosition + logoHeight),
                        null
                    )
                    yPosition += logoHeight + 15f
                } catch (e: Exception) {
                    Log.w("PDF", "Logo not found, skipping")
                    yPosition += 10f
                }

                drawRTLText(
                    canvas,
                    "تقرير شهر $currentMonth لمتابعة الحضور",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f

                drawRTLText(
                    canvas,
                    "قسم المنظم",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleBlack,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 28f

                val studentCountLabel = "(${sortedIndices.size} طالب)"
                drawRTLText(
                    canvas,
                    "$teacherNameArabic  $studentCountLabel",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f
            }

            val reportColWidth = 75f
            val sessionColWidth = 80f
            val nameColWidth = 110f
            val numberColWidth = 30f

            val maxSessionsToShow = 8

            val colWidths = buildList {
                add(reportColWidth)
                repeat(minOf(totalSessions, maxSessionsToShow)) {
                    add(sessionColWidth)
                }
                add(nameColWidth)
                add(numberColWidth)
            }.toFloatArray()

            // Get session dates for the selected month
            val sessionDates = getSessionDatesForMonth(targetMonth)

            val headers = buildList {
                add("عدد\nالغيابات\nمن\n$totalSessions\nحصة")
                for (sessionIdx in minOf(totalSessions, maxSessionsToShow) - 1 downTo 0) {
                    add("حصة ${sessionIdx + 1}")
                }
                add("الاسم\nالكامل")
                add("رقم")
            }.toTypedArray()

            val rowHeight = COLUMN_HEIGHT_ETUDION
            val headerHeight = FIRST_HEADER_HEIGHT
            val subHeaderHeight = 28f
            val totalHeaderHeight = headerHeight + subHeaderHeight

            // Draw table header
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                yPosition + totalHeaderHeight, paintHeaderBg)

            for (i in headers.indices) {
                val isFirstCol = i == 0
                val isNameCol = i == headers.size - 2
                val isNumberCol = i == headers.lastIndex

                if (isFirstCol || isNameCol || isNumberCol) {
                    canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                        yPosition + totalHeaderHeight, paintBorder)

                    val headerPaint = if (isFirstCol) paintAbsenceColumnHeader else paintTableHeader

                    drawRTLText(
                        canvas, headers[i],
                        xPosition + 3f, yPosition + if (isNumberCol) 15f else 25f,
                        (colWidths[i] - 6f).toInt(),
                        headerPaint, Layout.Alignment.ALIGN_CENTER
                    )
                } else {
                    canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                        yPosition + headerHeight, paintBorder)
                    drawRTLText(
                        canvas, headers[i],
                        xPosition + 3f, yPosition + 6f, (colWidths[i] - 6f).toInt(),
                        paintTableHeader, Layout.Alignment.ALIGN_CENTER
                    )
                }
                xPosition += colWidths[i]
            }

            yPosition += headerHeight

            // Sub-headers for session dates
            xPosition = marginLeft
            xPosition += reportColWidth

            for (sessionIdx in minOf(totalSessions, maxSessionsToShow) - 1 downTo 0) {
                if (sessionIdx < sessionDates.size) {
                    val sessionDate = sessionDates[sessionIdx]

                    canvas.drawRect(xPosition, yPosition, xPosition + sessionColWidth,
                        yPosition + subHeaderHeight, paintSubHeaderBg)
                    canvas.drawRect(xPosition, yPosition, xPosition + sessionColWidth,
                        yPosition + subHeaderHeight, paintBorder)

                    val dayName = when (sessionDate.dayOfWeek) {
                        Calendar.SUNDAY -> "الأحد"
                        Calendar.THURSDAY -> "الخميس"
                        else -> ""
                    }

                    drawRTLText(
                        canvas, "$dayName\n${sessionDate.dayOfMonth}\n$currentMonth",
                        xPosition + 2f, yPosition + 3f, (sessionColWidth - 4f).toInt(),
                        paintSubHeaderText, Layout.Alignment.ALIGN_CENTER
                    )
                }

                xPosition += sessionColWidth
            }

            yPosition += subHeaderHeight

            // Calculate student indices for this page
            val (startIndex, endIndex) = if (pageIndex == 0) {
                Pair(0, minOf(studentsFirstPage, effectiveCount))
            } else {
                val previousStudents = studentsFirstPage + (pageIndex - 1) * studentsPerPage
                Pair(previousStudents, minOf(previousStudents + studentsPerPage, effectiveCount))
            }

            // Draw student rows
            for (idx in startIndex until endIndex) {
                val i = sortedIndices[idx]
                val student = cardsData[i]
                val etudiant = etudiants[i]

                if ((idx - startIndex) % 2 == 1) {
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                        yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Column 1: Absence statistics with color coding
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[0],
                    yPosition + rowHeight, paintBorder)

                val stats = AbsenceStatistics.calculate(etudiant, observations, selectedMonth)

                when {
                    stats.totalAbsences == 0 -> {
                        // Perfect attendance - green
                        val textePresence = if (teacherNameArabic == "دراسة حالة من الادارة") "كثيرة" else "تم\nحضور\nال$totalSessions\nحصص"
                        drawRTLText(
                            canvas, textePresence,
                            xPosition + 3f, yPosition + 12f, (colWidths[0] - 6f).toInt(),
                            paintSuccess, Layout.Alignment.ALIGN_CENTER
                        )
                    }
                    stats.unjustifiedAbsences > 0 -> {
                        // Has unjustified absences - red (most serious)
                        drawRTLText(
                            canvas, "${stats.unjustifiedAbsences}\nغ.م",
                            xPosition + 3f, yPosition + 10f, (colWidths[0] - 6f).toInt(),
                            paintWarning, Layout.Alignment.ALIGN_CENTER
                        )

                        // Show justified count below if exists
                        if (stats.justifiedAbsences > 0) {
                            drawRTLText(
                                canvas, "${stats.justifiedAbsences}م",
                                xPosition + 3f, yPosition + 32f, (colWidths[0] - 6f).toInt(),
                                paintJustified, Layout.Alignment.ALIGN_CENTER
                            )
                        }

                        // Show ijaza count at bottom if exists
                        if (stats.ijazaAbsences > 0) {
                            drawRTLText(
                                canvas, "${stats.ijazaAbsences}إ",
                                xPosition + 3f, yPosition + 45f, (colWidths[0] - 6f).toInt(),
                                paintIjaza, Layout.Alignment.ALIGN_CENTER
                            )
                        }
                    }
                    stats.ijazaAbsences > 0 -> {
                        // Only ijaza absences - green
                        val paintIjazaBig = TextPaint().apply {
                            textSize = SIZE_TEXT_ABSENCE_COUNT
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            isAntiAlias = true
                            color = "#4CAF50".toColorInt()
                        }
                        drawRTLText(
                            canvas, "${stats.ijazaAbsences}\nإجازة",
                            xPosition + 3f, yPosition + 15f, (colWidths[0] - 6f).toInt(),
                            paintIjazaBig, Layout.Alignment.ALIGN_CENTER
                        )
                    }
                    else -> {
                        // Only regular justified absences - orange
                        val paintJustifiedBig = TextPaint().apply {
                            textSize = SIZE_TEXT_ABSENCE_COUNT
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            isAntiAlias = true
                            color = "#FF9800".toColorInt()
                        }
                        drawRTLText(
                            canvas, "${stats.justifiedAbsences}\nمبرر",
                            xPosition + 3f, yPosition + 15f, (colWidths[0] - 6f).toInt(),
                            paintJustifiedBig, Layout.Alignment.ALIGN_CENTER
                        )
                    }
                }
                xPosition += colWidths[0]

                // Get absences by date for this student
                val absencesByDate = getAbsencesByDate(etudiant, observations, selectedMonth)

                // Session columns
                for (sessionIdx in minOf(totalSessions, maxSessionsToShow) - 1 downTo 0) {
                    val sessionCellStart = xPosition

                    canvas.drawRect(sessionCellStart, yPosition, sessionCellStart + sessionColWidth,
                        yPosition + rowHeight, paintBorder)

                    if (sessionIdx < sessionDates.size) {
                        val sessionDate = sessionDates[sessionIdx]

                        val absence = absencesByDate.entries.find {
                            it.key.dayOfMonth == sessionDate.dayOfMonth
                        }?.value

                        if (absence != null) {
                            AbsenceDrawer.drawAbsenceCell(
                                canvas = canvas,
                                cellX = sessionCellStart,
                                cellY = yPosition,
                                cellWidth = sessionColWidth,
                                cellHeight = rowHeight,
                                isAbsent = true,
                                isJustified = absence.isJustified,
                                justificationText = absence.justification,
                                absenceIcon = absenceIcon,
                                justificationIcon = justificationIcon,
                                paintAbsenceBg = absencePaints.absenceBg,
                                paintJustifiedBg = absencePaints.justifiedBg,
                                paintIjazaBg = absencePaints.ijazaBg,
                                paintAbsenceLabel = absencePaints.absenceLabel,
                                paintJustificationLabel = absencePaints.justificationLabel,
                                paintBorder = paintBorder
                            )
                        }
                    }

                    xPosition += sessionColWidth
                }

                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[colWidths.size - 2],
                    yPosition + rowHeight, paintBorder)
                val creationDateAr = formatArabicDate(etudiant.creationTimestamps)
                val nameWithDate = "${etudiant.nom} ${etudiant.prenom}\n(${etudiant.age} سنوات)\n$creationDateAr"
                drawRTLText(
                    canvas, nameWithDate,
                    xPosition + 5f, yPosition + 5f, (colWidths[colWidths.size - 2] - 10f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_NORMAL
                )
                xPosition += colWidths[colWidths.size - 2]

                // Column: Row number
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths.last(),
                    yPosition + rowHeight, paintBorder)

                val rowNumber = (idx + 1).toString()
                drawRTLText(
                    canvas, rowNumber,
                    xPosition + 2f, yPosition + 20f, (colWidths.last() - 4f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_CENTER
                )

                yPosition += rowHeight
            }

            // Footer
            yPosition = pageHeight - marginBottom - 20f
            val paintSmall = TextPaint().apply {
                textSize = 10f
                isAntiAlias = true
                color = Color.BLACK
            }
            drawRTLText(
                canvas, "صفحة ${pageIndex + 1} من $totalPages",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall,
                Layout.Alignment.ALIGN_CENTER
            )

            pdfDocument.finishPage(page)
        }

        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("AttendanceReport", "✅ PDF created: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("AttendanceReport", "❌ Error creating PDF", e)
        null
    }
}

/**
 * Format a timestamp as an Arabic date string, e.g. "20 جويلية 2024"
 */
private fun formatArabicDate(timestamp: Long): String {
    val arabicMonths = arrayOf(
        "جانفي", "فيفري", "مارس", "أفريل", "ماي", "جوان",
        "جويلية", "أوت", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val day  = cal.get(Calendar.DAY_OF_MONTH)
    val month = arabicMonths[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)
    return "$day $month $year"
}

/**
 * Helper function to calculate total sessions for a given month
 */
private fun calculateSessionsForMonth(month: Calendar): Int {
    val calendar = month.clone() as Calendar
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    var sessionCount = 0

    for (day in 1..maxDay) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
            sessionCount++
        }
    }

    return sessionCount
}

data class ParentCommunicationCardData_But6(
    val studentInfo: StudentInfo,
    val attendanceInfo: AttendanceInfo
) {
    data class StudentInfo(
        val fullName: String,
        val age: Int
    )

    data class AttendanceInfo(
        val totalAbsences: Int,
        val totalSessions: Int,
        val weeklyAbsences: List<List<AbsenceDay>>
    )

    data class AbsenceDay(
        val dayOfWeek: Int,
        val date: Date,
        val isJustified: Boolean,
        val justification: String = ""
    )

    companion object {
        fun fromEtudiant(
            etudiant: M19Etudiant,
            observations: List<M20ObsarvationEtudion> = emptyList(),
            weeklyAbsences: List<List<AbsenceDay>> = emptyList()
        ): ParentCommunicationCardData_But6 {
            return ParentCommunicationCardData_But6(
                studentInfo = StudentInfo(
                    fullName = "${etudiant.nom} ${etudiant.prenom} (${etudiant.age} سنوات)",
                    age = etudiant.age
                ),
                attendanceInfo = AttendanceInfo(
                    totalAbsences = AbsenceStatistics.calculate(etudiant, observations).unjustifiedAbsences,
                    totalSessions = getCurrentMonthSessions(),
                    weeklyAbsences = weeklyAbsences
                )
            )
        }

        private fun getCurrentMonthSessions(): Int {
            val calendar = Calendar.getInstance()
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            var sessionCount = 0
            for (day in 1..maxDay) {
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
                    sessionCount++
                }
            }
            return sessionCount
        }
    }
}
``n

---

## 📄 AbsenceDrawer.kt (Optimized & Commented)

`kotlin
/*
package Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.SessionDate
import Application5.App.Repository.M20ObsarvationEtudion
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import java.util.Calendar

object AbsenceDrawer {
    // [TRUNCATED FOR cc_se TO SAVE TOKENS & TIME]
    // Keeps only public signatures for structure verification

    fun drawAbsenceCell(
        canvas: Canvas,
        cellX: Float, cellY: Float,
        cellWidth: Float, cellHeight: Float,
        isAbsent: Boolean,
        isJustified: Boolean,
        justificationText: String = "",
        absenceIcon: Bitmap?,
        justificationIcon: Bitmap?,
        paintAbsenceBg: Paint,
        paintJustifiedBg: Paint,
        paintIjazaBg: Paint,
        paintAbsenceLabel: TextPaint,
        paintJustificationLabel: TextPaint,
        paintBorder: Paint
    ) {
        // Drawing code removed
    }

    fun createAbsencePaints(): AbsencePaints {
        return AbsencePaints(Paint(), Paint(), Paint(), TextPaint(), TextPaint())
    }
}

data class AbsencePaints(
    val paintAbsenceBg: Paint,
    val paintJustifiedBg: Paint,
    val paintIjazaBg: Paint,
    val paintAbsenceLabel: TextPaint,
    val paintJustificationLabel: TextPaint
)

fun getAbsencesByDate(
    etudiant: M19Etudiant,
    observations: List<M20ObsarvationEtudion>,
    selectedMonth: Calendar?
): Map<Int, M20ObsarvationEtudion> {
    return emptyMap()
}
*/
``n

---

## 📄 AbsenceDebugLogger.kt (Optimized & Commented)

`kotlin
/*
package Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.M20ObsarvationEtudion
import java.util.Calendar

object AbsenceDebugLogger {
    // [TRUNCATED FOR cc_se TO SAVE TOKENS & TIME]
    
    fun logFullDebugInfo(
        etudiant: M19Etudiant,
        observations: List<M20ObsarvationEtudion>,
        selectedMonth: Calendar?,
        selectedTeacher: String?
    ) {
        // Log removed
    }

    fun quickCheck(
        studentName: String,
        observations: List<M20ObsarvationEtudion>,
        selectedMonth: Calendar?
    ): String {
        return ""
    }
}
*/
``n

---

## 📄 AbsenceStatistics.kt (Optimized & Commented)

`kotlin
/*
package Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.M20ObsarvationEtudion
import java.util.Calendar

data class AbsenceStatistics(
    val totalAbsences: Int,
    val unjustifiedAbsences: Int,
    val justifiedAbsences: Int,
    val ijazaAbsences: Int
) {
    companion object {
        // [TRUNCATED FOR cc_se TO SAVE TOKENS & TIME]
        fun calculate(
            etudiant: M19Etudiant,
            observations: List<M20ObsarvationEtudion>,
            selectedMonth: Calendar? = null
        ): AbsenceStatistics {
            return AbsenceStatistics(0, 0, 0, 0)
        }
    }
}
*/
``n

---

## 📄 Utils.kt (Optimized & Commented)

`kotlin
/*
package Application5.App.View.DropDownItems.View.ButID6.Pdf_Generateur

import android.graphics.Canvas
import android.text.Layout
import android.text.TextPaint
import java.util.Calendar

// [TRUNCATED FOR cc_se TO SAVE TOKENS & TIME]

fun getCurrentMonthArabic(): String {
    return ""
}

fun getCurrentMonthSessions(): Int {
    return 0
}

fun drawRTLText(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    width: Int,
    paint: TextPaint,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
) {
    // Removed RTL draw implementation
}
*/
``n
