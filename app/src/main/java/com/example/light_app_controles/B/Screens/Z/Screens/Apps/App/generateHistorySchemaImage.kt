package com.example.light_app_controles.B.Screens.Z.Screens.Apps.App.Modules

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawHeaderSection
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawStudentHeader
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

private data class ObsRow(
    val dateLabel: String,
    val takyimLabel: String,
    val takyimColor: Int,
    val takyimScore: Float,
    val typeLabel: String,       // "استدراك" / "تمام" / "أستاذ" / "غياب" — shown next to the badge
    val rangeLabel: String,      // "الفاتحة (1) ← البقرة (5)" — shown rotated below the dot
)

private const val TAG = "SchemaImage"

fun generateHistorySchemaImage(
    context: Context,
    cardData: ParentCommunicationCardData_2,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): Uri? {
    val studentId   = cardData.studentInfo.keyID
    val studentName = cardData.studentInfo.fullName
    Log.d(TAG, "▶ début génération — étudiant: $studentName (id=$studentId)")

    return try {
        val scale = 2
        val imgWidth = 480
        val marginH = 20f
        val contentWidth = imgWidth - marginH * 2

        val allObs = viewModel.repo20ObsarvationEtudion.datasValue
        Log.d(TAG, "  total observations en mémoire: ${allObs.size}")

        val rows = resolveObservations(cardData, viewModel)
        Log.d(TAG, "  observations filtrées pour cet étudiant: ${rows.size}")

        if (rows.isEmpty()) {
            Log.w(TAG, "⚠ فشل الإنشاء — لا توجد ملاحظات للطالب $studentName (id=$studentId)")
            return null
        }

        val paints = buildSchemaPaints()
        val measuredH = measureSchemaHeight(context, rows, cardData, imgWidth, marginH, contentWidth, paints)
        Log.d(TAG, "  hauteur mesurée: $measuredH px")

        val totalHeight = (measuredH + 24f).toInt()
        Log.d(TAG, "  création bitmap ${imgWidth * scale} × ${totalHeight * scale}")

        val bitmap = Bitmap.createBitmap(imgWidth * scale, totalHeight * scale, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            drawColor(Color.parseColor("#FAFAFA"))
            scale(scale.toFloat(), scale.toFloat())
        }
        renderSchema(canvas, rows, context, cardData, imgWidth, marginH, contentWidth, paints)

        val fileName = "schema_${studentId.trim()}_${System.currentTimeMillis()}.jpg"
        val uri = saveSchemaJpg(context, bitmap, fileName).also { bitmap.recycle() }

        if (uri != null) Log.d(TAG, "✅ image sauvegardée → $uri")
        else             Log.e(TAG, "❌ فشل الإنشاء — saveSchemaJpg a retourné null pour $studentName")

        uri
    } catch (e: Exception) {
        Log.e(TAG, "❌ فشل الإنشاء — exception pour l'étudiant $studentName (id=$studentId)", e)
        null
    }
}

private fun resolveObservations(
    cardData: ParentCommunicationCardData_2,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): List<ObsRow> =
    viewModel.repo20ObsarvationEtudion.datasValue
        .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
        .sortedBy { it.creationTimestamps }   // oldest → newest so the chart reads left → right
        .takeLast(5)                          // max 5 most-recent observations
        .map { obs ->
            val takyimName = obs.takyim.arabicName
            val typeLabel = when (obs.type) {
                M20ObsarvationEtudion.Type.Moukarrar_Itmamouhou    -> "استدراك"
                M20ObsarvationEtudion.Type.Tama_Hifdoha            -> "تمام"
                M20ObsarvationEtudion.Type.Ousstad_kama_Bil_moundat -> "أستاذ"
                M20ObsarvationEtudion.Type.Raeeb                   -> "غياب"
            }
            ObsRow(
                dateLabel   = getArabicDateSchema(obs.creationTimestamps),
                takyimLabel = takyimName,
                takyimColor = takyimToColor(takyimName),
                takyimScore = takyimToScore(takyimName),
                typeLabel   = typeLabel,
                rangeLabel  = "${formatAyaSchema(obs.min_soura, obs.min_aya)} ← ${formatAyaSchema(obs.ila_soura, obs.ila_aya)}",
            )
        }

private fun measureSchemaHeight(
    context: Context,
    rows: List<ObsRow>,
    cardData: ParentCommunicationCardData_2,
    imgWidth: Int,
    marginH: Float,
    contentWidth: Float,
    paints: SchemaPaints
): Float {
    val dummy = Bitmap.createBitmap(imgWidth, 8000, Bitmap.Config.ARGB_8888)
    val y = renderSchema(Canvas(dummy), rows, context, cardData, imgWidth, marginH, contentWidth, paints)
    dummy.recycle()
    return y
}

private fun renderSchema(
    canvas: Canvas,
    rows: List<ObsRow>,
    context: Context?,
    cardData: ParentCommunicationCardData_2?,
    imgWidth: Int,
    marginH: Float,
    contentWidth: Float,
    paints: SchemaPaints
): Float {
    var y = 0f

    // ── Standard logo header (matches mokarrar layout) ────────────────────────
    if (context != null && cardData != null) {
        y = drawHeaderSection(
            canvas           = canvas,
            context          = context,
            marginLeft       = marginH,
            marginTop        = y,
            pageWidth        = imgWidth,
            marginRight      = marginH,
            contentWidth     = contentWidth.toInt(),
            paintHeaderLarge = paints.headerLarge,
            paintSmall       = paints.small,
            paintVerySmall   = paints.verySmall,
            compactMode      = true
        )
        y = drawStudentHeader(
            canvas           = canvas,
            cardData         = cardData,
            marginLeft       = marginH,
            yPosition        = y,
            pageWidth        = imgWidth,
            marginRight      = marginH,
            contentWidth     = contentWidth.toInt(),
            paintArabicBold  = paints.bold,
            paintBorder      = paints.border
        )
        y += 8f
    }

    y = drawChart(canvas, rows, marginH, y, contentWidth, paints)
    y += 16f
    return y
}

// ── Chart: line graph with X/Y axes showing takyim trend over time ────────────

private fun drawChart(
    canvas: Canvas,
    rows: List<ObsRow>,
    marginH: Float,
    startY: Float,
    contentWidth: Float,
    paints: SchemaPaints
): Float {
    val yAxisW    = 62f
    val xAxisH    = 46f          // date-label rows only (range is now inside the chart)
    val chartLeft = marginH + yAxisW          // leave room on the LEFT for Y-axis labels
    val chartRight = marginH + contentWidth   // extend to full content width
    val chartW    = chartRight - chartLeft
    val chartTop  = startY
    val chartBot  = startY + 190f
    val chartH    = chartBot - chartTop

    val yLevels = listOf(
        1.00f to "ممتاز",
        0.83f to "جيد جداً",
        0.67f to "فوق الجيد",
        0.55f to "جيد",
        0.42f to "فوق المقبول",
        0.30f to "مقبول",
        0.10f to "لم يحفظ",
    )

    val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0E0E0"); strokeWidth = 0.5f; style = Paint.Style.STROKE
    }
    // ── Styled Y-axis labels: coloured pill + dot indicator ──────────────────
    val pillH  = 12f
    val pillW  = yAxisW - 6f
    val dotR   = 2.5f
    yLevels.forEach { (score, label) ->
        val gy         = chartBot - score * chartH
        val labelColor = takyimToColor(label)

        // Grid line (drawn first, behind everything)
        canvas.drawLine(chartLeft, gy, chartRight, gy, gridPaint)

        // Small filled dot sitting ON the grid line, at the axis edge
        canvas.drawCircle(chartLeft - dotR - 2f, gy, dotR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = labelColor; style = Paint.Style.FILL })

        // Pill background (very light tint of the label colour)
        val pillTop = gy - pillH / 2f
        canvas.drawRoundRect(
            RectF(marginH, pillTop, marginH + pillW, pillTop + pillH),
            pillH / 2f, pillH / 2f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = labelColor; alpha = 28; style = Paint.Style.FILL }
        )

        // Pill text in the matching colour
        drawRTLText(
            canvas, label,
            marginH + 2f, pillTop + 1f,
            (pillW - 4f).toInt(),
            TextPaint(paints.legendText).apply { color = labelColor },
            Layout.Alignment.ALIGN_CENTER
        )
    }

    val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9E9E9E"); strokeWidth = 1.5f; style = Paint.Style.STROKE
    }
    canvas.drawLine(chartLeft, chartTop, chartLeft, chartBot, axisPaint)
    canvas.drawLine(chartLeft, chartBot, chartRight, chartBot, axisPaint)

    if (rows.isEmpty()) return chartBot + xAxisH

    val n = rows.size
    fun px(i: Int) = if (n == 1) chartLeft + chartW / 2f else chartLeft + i * (chartW / (n - 1).toFloat())
    fun py(score: Float) = chartBot - score * chartH

    // Filled area under line
    if (n > 1) {
        val areaPath = Path().apply {
            moveTo(px(0), chartBot)
            rows.forEachIndexed { i, row -> lineTo(px(i), py(row.takyimScore)) }
            lineTo(px(n - 1), chartBot)
            close()
        }
        canvas.drawPath(areaPath, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1E88E5"); alpha = 30; style = Paint.Style.FILL
        })
    }

    // Connecting line
    if (n > 1) {
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1565C0"); strokeWidth = 2f; style = Paint.Style.STROKE
        }
        for (i in 0 until n - 1) {
            canvas.drawLine(px(i), py(rows[i].takyimScore), px(i + 1), py(rows[i + 1].takyimScore), linePaint)
        }
    }

    // Points + badges + X-axis date labels
    rows.forEachIndexed { i, row ->
        val cx = px(i); val cy = py(row.takyimScore)

        canvas.drawCircle(cx, cy, 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL })
        canvas.drawCircle(cx, cy, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = row.takyimColor; style = Paint.Style.FILL })

        // ── Range badge (min→ila) + typeLabel badge side by side ─────────────
        // The range text ("سورة X (n) ← سورة Y (m)") replaces the takyim label
        // inside the coloured badge; takyim level is already readable from the dot
        // position on the Y-axis.
        val badgeH     = 14f
        val rangeW     = 88f                          // wide enough for two soura names
        val typeBadgeW = if (row.typeLabel.isBlank() || row.typeLabel == "تمام") 0f else 36f
        val gap        = if (row.typeLabel.isBlank() || row.typeLabel == "تمام") 0f else 2f
        val totalW     = rangeW + gap + typeBadgeW

        val bLeft = (cx - totalW / 2f).coerceIn(chartLeft, chartRight - totalW)
        val bTop  = (cy - badgeH - 20f).coerceAtLeast(chartTop)

        // Range badge — coloured with the takyim colour
        canvas.drawRoundRect(RectF(bLeft, bTop, bLeft + rangeW, bTop + badgeH), 3f, 3f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = row.takyimColor; style = Paint.Style.FILL })
        drawRTLText(canvas, row.rangeLabel, bLeft + 2f, bTop + 1f, (rangeW - 4f).toInt(), paints.badgeText, Layout.Alignment.ALIGN_CENTER)

        // Type label badge — shown only for non-default types (استدراك / غياب / أستاذ)
        // "تمام" is the normal case and adds no information so it is suppressed.
        if (row.typeLabel.isNotBlank() && row.typeLabel != "تمام") {
            val tLeft = bLeft + rangeW + gap
            canvas.drawRoundRect(RectF(tLeft, bTop, tLeft + typeBadgeW, bTop + badgeH), 3f, 3f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = row.takyimColor; alpha = 110; style = Paint.Style.FILL })
            drawRTLText(canvas, row.typeLabel, tLeft + 2f, bTop + 1f, (typeBadgeW - 4f).toInt(), paints.badgeText, Layout.Alignment.ALIGN_CENTER)
        }

        // ── Date label below the X-axis, staggered even/odd ──────────────────
        val dateY  = if (i % 2 == 0) chartBot + 5f else chartBot + 26f
        val labelW = 56
        val lx     = (cx - labelW / 2f).coerceIn(chartLeft, chartRight - labelW)
        drawRTLText(canvas, row.dateLabel, lx, dateY, labelW, paints.dateText, Layout.Alignment.ALIGN_CENTER)
    }

    return chartBot + xAxisH
}

// ── Save ──────────────────────────────────────────────────────────────────────

private fun saveSchemaJpg(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    // Extract keyID from "schema_{keyID}_{timestamp}.jpg"
    val keyID = fileName.removePrefix("schema_").substringBeforeLast("_")
    deleteSameDaySchemaImages(context, keyID)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        saveSchemaViaMediaStore(context, bitmap, fileName)
    else
        saveSchemaToPublicPictures(context, bitmap, fileName)
}

/** Deletes all schema images for [keyID] that were saved today. */
private fun deleteSameDaySchemaImages(context: Context, keyID: String) {
    val relPath = "${Environment.DIRECTORY_PICTURES}/whatsapp_cards/schema/"
    val prefix  = "schema_${keyID.trim()}_"
    val todayStartSec = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.timeInMillis / 1000L

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver   = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val selection  = "${MediaStore.Images.Media.RELATIVE_PATH} = ? AND " +
                         "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ? AND " +
                         "${MediaStore.Images.Media.DATE_ADDED} >= ?"
        val args = arrayOf(relPath, "$prefix%", todayStartSec.toString())
        resolver.query(collection, arrayOf(MediaStore.Images.Media._ID), selection, args, null)
            ?.use { cursor ->
                val col = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val uri = ContentUris.withAppendedId(collection, cursor.getLong(col))
                    resolver.delete(uri, null, null)
                    Log.d(TAG, "🗑 deleted old schema image: $uri")
                }
            }
    } else {
        @Suppress("DEPRECATION")
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "whatsapp_cards/schema"
        )
        val todayStartMs = todayStartSec * 1000L
        dir.listFiles { f -> f.name.startsWith(prefix) && f.lastModified() >= todayStartMs }
            ?.forEach { f -> if (f.delete()) Log.d(TAG, "🗑 deleted old schema file: ${f.name}") }
    }
}

private fun saveSchemaViaMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    val resolver   = context.contentResolver
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME,  fileName)
        put(MediaStore.Images.Media.MIME_TYPE,     "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/whatsapp_cards/schema/")
        put(MediaStore.Images.Media.IS_PENDING,    1)
    }
    val uri = resolver.insert(collection, values)
    if (uri == null) {
        Log.e(TAG, "❌ MediaStore.insert a retourné null — permission WRITE_EXTERNAL_STORAGE manquante ou volume indisponible")
        return null
    }
    return try {
        resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        uri
    } catch (e: Exception) {
        Log.e(TAG, "❌ écriture MediaStore échouée — fileName=$fileName", e)
        resolver.delete(uri, null, null); null
    }
}

@Suppress("DEPRECATION")
private fun saveSchemaToPublicPictures(context: Context, bitmap: Bitmap, fileName: String): Uri? =
    try {
        val dir  = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "whatsapp_cards/schema").also { it.mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (e: Exception) {
        Log.e(TAG, "❌ sauvegarde fichier public échouée — fileName=$fileName", e)
        null
    }

// ── Paints ────────────────────────────────────────────────────────────────────

private data class SchemaPaints(
    val titleWhite:  TextPaint,
    val subWhite:    TextPaint,
    val legendText:  TextPaint,
    val dateText:    TextPaint,
    val badgeText:   TextPaint,
    val rangeText:   Paint,       // kept for any future rotated use
    val rangeTP:     TextPaint,   // horizontal range label below the date
    // ── Extra paints required by drawHeaderSection / drawStudentHeader ────────
    val bold:        TextPaint,
    val headerLarge: TextPaint,
    val small:       TextPaint,
    val verySmall:   TextPaint,
    val border:      Paint,
)

private fun buildSchemaPaints() = SchemaPaints(
    titleWhite = TextPaint().apply { textSize = 16f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);   isAntiAlias = true; color = Color.WHITE },
    subWhite   = TextPaint().apply { textSize = 11f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.parseColor("#BBDEFB") },
    legendText = TextPaint().apply { textSize =  7f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.DKGRAY },
    dateText   = TextPaint().apply { textSize =  7f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.parseColor("#757575") },
    badgeText  = TextPaint().apply { textSize =  8f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);   isAntiAlias = true; color = Color.WHITE },
    rangeText  = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 7f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); color = Color.parseColor("#546E7A"); alpha = 200 },
    rangeTP    = TextPaint().apply { textSize =  7f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.parseColor("#546E7A"); alpha = 200 },
    // ── Extra paints for drawHeaderSection / drawStudentHeader ────────────────
    bold = TextPaint().apply {
        textSize    = 17f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
        color       = Color.BLACK
    },
    headerLarge = TextPaint().apply {
        textSize    = 14f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
        color       = Color.BLACK
    },
    small = TextPaint().apply {
        textSize    = 11f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
        color       = Color.BLACK
    },
    verySmall = TextPaint().apply {
        textSize    = 9f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
        color       = Color.BLACK
    },
    border = Paint().apply {
        color       = Color.BLACK
        style       = Paint.Style.STROKE
        strokeWidth = 1f
    },
)

// ── Utilities ─────────────────────────────────────────────────────────────────

/**
 * Formats a (soura, aya) pair into a human-readable Arabic label.
 * Aya = 0 is treated as "نهاية" (end of surah), matching the convention
 * used in [ParentCommunicationCardData_2.formatAyaForDisplay].
 *
 * Example output: "الفاتحة (1)"  /  "البقرة (نهاية)"
 */
private fun formatAyaSchema(soura: Application5.App.Repository.SOUAR, aya: Int): String {
    val ayaDisplay = if (aya == 0) "نهاية" else aya.toString()
    return "${soura.arabicName} ($ayaDisplay)"
}

private fun takyimToScore(takyim: String): Float = when (takyim) {
    "ممتاز"                -> 1.00f
    "جيد جداً", "جيد جدا" -> 0.83f
    "فوق الجيد"            -> 0.67f
    "جيد"                  -> 0.55f
    "فوق المقبول"          -> 0.42f
    "مقبول"                -> 0.30f
    "لم يحفظ"              -> 0.10f
    else                   -> 0.20f
}

private fun takyimToColor(takyim: String): Int = when (takyim) {
    "ممتاز"                -> Color.parseColor("#4CAF50")
    "جيد جداً", "جيد جدا" -> Color.parseColor("#2196F3")
    "فوق الجيد"            -> Color.parseColor("#03A9F4")
    "جيد"                  -> Color.parseColor("#9C27B0")
    "فوق المقبول"          -> Color.parseColor("#FF9800")
    "مقبول"                -> Color.parseColor("#FF5722")
    "لم يحفظ"              -> Color.parseColor("#F44336")
    else                   -> Color.parseColor("#9E9E9E")
}

private fun getArabicDateSchema(timestamp: Long): String {
    val cal    = Calendar.getInstance().apply { timeInMillis = timestamp }
    val days   = arrayOf("الأحد","الإثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت")
    val months = arrayOf("جانفي","فيفري","مارس","أفريل","ماي","جوان","جويلية","أوت","سبتمبر","أكتوبر","نوفمبر","ديسمبر")
    return "${days[cal.get(Calendar.DAY_OF_WEEK) - 1]} ${cal.get(Calendar.DAY_OF_MONTH)} ${months[cal.get(Calendar.MONTH)]}"
}
