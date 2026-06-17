package Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.A

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// ✅ FIXED: Added logic to handle aya=0 → display "نهاية" (end of surah)
fun drawMokarrarCell(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    x: Float,
    y: Float,
    cellWidth: Int,
    paintArabicMediumBold: TextPaint,
    paintArabic: TextPaint,
    aCentralFacade: A_ViewModel_SeparatedAppsCodingPattern
) {
    var currentY = y

    // ═══════════════════════════════════════════════════
    // Title: المقرر لتحضيره - CENTERED & BOLD
    // ═══════════════════════════════════════════════════
    val titlePaint = TextPaint(paintArabicMediumBold).apply {
        textSize = paintArabicMediumBold.textSize + 2f  // Bigger: 17f
        isFakeBoldText = true
        color = Color(0xFF1976D2).toArgb()  // Blue color
    }

    drawRTLText(
        canvas = canvas,
        text = "المقرر لتحضيره",
        x = x,
        y = currentY,
        width = cellWidth,
        paint = titlePaint,
        alignment = Layout.Alignment.ALIGN_CENTER  // ✅ CENTERED
    )
    currentY += 22f

    // Parse mokarrarDetails
    val mokarrarText = cardData.hifdProgress.mokarrarDetails
    val lines = mokarrarText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    val limitSurahName = lines.getOrNull(0) ?: ""

    // Helper function to format aya display
    fun formatAyaDisplay(ayaNumber: String): String {
        return when {
            ayaNumber == "0" -> "نهاية"
            ayaNumber == "نهاية" -> "نهاية"
            ayaNumber.toIntOrNull() == 0 -> "نهاية"
            else -> "الآية $ayaNumber"
        }
    }

    var fromText = ""
    var toText = ""

    if (lines.size >= 3 || (lines.size == 2 && lines[1].endsWith("إلى"))) {
        // Different souras case
        val line1 = lines[1]
        val line2 = lines.getOrNull(2) ?: ""

        val fromMatch = Regex("من الآية (\\d+|نهاية)").find(line1)
        val startVerse = fromMatch?.groupValues?.get(1) ?: "1"

        val hasAyaWord = line2.contains("الآية")
        val startSurahName = if (hasAyaWord) {
            line2.substringBefore("الآية").trim()
        } else if (line2.contains("نهاية")) {
            line2.substringBefore("نهاية").trim()
        } else {
            line2.trim()
        }

        val toMatch = Regex("(الآية|إلى)\\s+(\\d+)").find(line2)
        val endVerse = toMatch?.groupValues?.get(2) 
            ?: if (line2.contains("نهاية")) "نهاية" else "1"

        fromText = "من $startSurahName ${formatAyaDisplay(startVerse)}"
        
        toText = if (endVerse == "نهاية") {
            "إلى نهاية $limitSurahName"
        } else {
            "إلى $limitSurahName ${formatAyaDisplay(endVerse)}"
        }
    } else {
        // Same soura case
        val line1 = lines.getOrNull(1) ?: ""
        val fromMatch = Regex("من الآية (\\d+|نهاية)").find(line1)
        val toMatch = Regex("إلى (\\d+|نهاية)").find(line1)
        
        val startVerse = fromMatch?.groupValues?.get(1) ?: "1"
        val endVerse = toMatch?.groupValues?.get(1) ?: "1"

        fromText = "من $limitSurahName ${formatAyaDisplay(startVerse)}"
        toText = if (endVerse == "نهاية") {
            "إلى نهاية $limitSurahName"
        } else {
            "إلى $limitSurahName ${formatAyaDisplay(endVerse)}"
        }
    }

    val fromPaint = TextPaint(paintArabic).apply {
        textSize = paintArabic.textSize + 2f  // Bigger: 15f
        color = Color(0xFF2E7D32).toArgb()  // Dark green
        isFakeBoldText = true
    }

    drawRTLText(
        canvas = canvas,
        text = fromText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = fromPaint,
        alignment = Layout.Alignment.ALIGN_CENTER  // ✅ CENTERED
    )
    currentY += 18f

    // ═══════════════════════════════════════════════════
    // Divider - COLORED
    // ═══════════════════════════════════════════════════
    val dividerPaint = Paint().apply {
        color = Color(0xFF1976D2).toArgb()  // Blue divider
        style = Paint.Style.STROKE
        strokeWidth = 1.5f  // Thicker
    }

    // Draw centered divider (80% width)
    val dividerMargin = cellWidth * 0.1f
    canvas.drawLine(
        x + dividerMargin,
        currentY,
        x + cellWidth - dividerMargin,
        currentY,
        dividerPaint
    )
    currentY += 8f

    // ═══════════════════════════════════════════════════
    // إلى Section - CENTERED & COLORED
    // ✅ FIXED: Now handles aya=0 case
    // ═══════════════════════════════════════════════════

    val toPaint = TextPaint(paintArabic).apply {
        textSize = paintArabic.textSize + 2f  // Bigger: 15f
        color = Color(0xFFD32F2F).toArgb()  // Dark red
        isFakeBoldText = true
    }

    drawRTLText(
        canvas = canvas,
        text = toText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = toPaint,
        alignment = Layout.Alignment.ALIGN_CENTER  // ✅ CENTERED
    )
    currentY += 18f

    // ═══════════════════════════════════════════════════
    // ملاحظات للإصلاح - CENTERED & ORANGE
    // ═══════════════════════════════════════════════════
    if (aCentralFacade != null) {
        val repo20 = aCentralFacade.repo20ObsarvationEtudion
        val latestObservation = repo20.datasValue
            .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
            .maxByOrNull { it.creationTimestamps }

        if (latestObservation != null) {
            val moulahadatList = latestObservation.getMoulahadatList()

            if (moulahadatList.isNotEmpty()) {
                // Add small divider before moulahadat
                val smallDividerPaint = Paint().apply {
                    color = Color(0xFFFF9800).toArgb()  // Orange
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }

                val smallDividerMargin = cellWidth * 0.2f
                canvas.drawLine(
                    x + smallDividerMargin,
                    currentY,
                    x + cellWidth - smallDividerMargin,
                    currentY,
                    smallDividerPaint
                )
                currentY += 6f

                // Display moulahadat - CENTERED & LARGER
                val moulahadatPaint = TextPaint(paintArabic).apply {
                    textSize = paintArabic.textSize  // 13f
                    color = Color(0xFFFF6F00).toArgb()  // Darker orange
                    isFakeBoldText = true
                }

                moulahadatList.forEach { moulahada ->
                    val moulahadaText = "• $moulahada"
                    drawRTLText(
                        canvas = canvas,
                        text = moulahadaText,
                        x = x,
                        y = currentY,
                        width = cellWidth,
                        paint = moulahadatPaint,
                        alignment = Layout.Alignment.ALIGN_CENTER  // ✅ CENTERED
                    )
                    currentY += 14f
                }
            }
        }
    }
}

/**
 * Extension function to extract moulahadat list from observation
 */
private fun Any.getMoulahadatList(): List<String> {
    return try {
        val field = this::class.java.getDeclaredField("moulahadat_takyim_li_islahiha")
        field.isAccessible = true
        val moulahadatString = field.get(this) as? String

        if (moulahadatString.isNullOrBlank()) {
            emptyList()
        } else {
            moulahadatString.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    } catch (e: Exception) {
        emptyList()
    }
}
