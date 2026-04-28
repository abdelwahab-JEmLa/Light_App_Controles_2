package com.example.light_app_controles.B.Screens.Z.Screens.Apps.App.Modules

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.TextPaint
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
)

//<--
//TODO(1): fait que au pont affiche اسورة الى سوؤ 
fun generateHistorySchemaImage(
    context: Context,
    cardData: ParentCommunicationCardData_2,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): Uri? {
    return try {
        val scale = 2
        val imgWidth = 480
        val marginH = 20f
        val contentWidth = imgWidth - marginH * 2
        val rows = resolveObservations(cardData, viewModel)
        if (rows.isEmpty()) return null
        val paints = buildSchemaPaints()
        val measuredH = measureSchemaHeight(rows, imgWidth, marginH, contentWidth, paints)
        val totalHeight = (measuredH + 24f).toInt()
        val bitmap = Bitmap.createBitmap(imgWidth * scale, totalHeight * scale, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            drawColor(Color.parseColor("#FAFAFA"))
            scale(scale.toFloat(), scale.toFloat())
        }
        renderSchema(canvas, rows, cardData, imgWidth, marginH, contentWidth, paints)
        val fileName = "schema_${cardData.studentInfo.keyID.trim()}_${System.currentTimeMillis()}.jpg"
        saveSchemaJpg(context, bitmap, fileName).also { bitmap.recycle() }
    } catch (e: Exception) { null }
}

private fun resolveObservations(
    cardData: ParentCommunicationCardData_2,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): List<ObsRow> =
    viewModel.repo20ObsarvationEtudion.datasValue
        .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
        .sortedBy { it.creationTimestamps }   // oldest → newest so the chart reads left → right
        .takeLast(10)
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
            )
        }

private fun measureSchemaHeight(
    rows: List<ObsRow>,
    imgWidth: Int,
    marginH: Float,
    contentWidth: Float,
    paints: SchemaPaints
): Float {
    val dummy = Bitmap.createBitmap(imgWidth, 8000, Bitmap.Config.ARGB_8888)
    val y = renderSchema(Canvas(dummy), rows, null, imgWidth, marginH, contentWidth, paints)
    dummy.recycle()
    return y
}

private fun renderSchema(
    canvas: Canvas,
    rows: List<ObsRow>,
    cardData: ParentCommunicationCardData_2?,
    imgWidth: Int,
    marginH: Float,
    contentWidth: Float,
    paints: SchemaPaints
): Float {
    var y = 0f

    val headerH = 52f
    canvas.drawRect(
        0f, y, imgWidth.toFloat(), y + headerH,
        Paint().apply {
            shader = LinearGradient(
                0f, y, imgWidth.toFloat(), y + headerH,
                Color.parseColor("#1565C0"), Color.parseColor("#1E88E5"),
                Shader.TileMode.CLAMP
            )
        }
    )
    drawRTLText(canvas, "سجل تقدم الحفظ", marginH, y + 8f, contentWidth.toInt(), TextPaint(paints.titleWhite), Layout.Alignment.ALIGN_CENTER)
    if (cardData != null) {
        drawRTLText(canvas, cardData.studentInfo.fullName, marginH, y + 28f, contentWidth.toInt(), paints.subWhite, Layout.Alignment.ALIGN_CENTER)
    }
    y += headerH + 16f

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
    val xAxisH    = 56f          // taller to fit 2-row staggered dates
    val chartLeft = marginH
    val chartRight = marginH + contentWidth - yAxisW
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
    yLevels.forEach { (score, label) ->
        val gy = chartBot - score * chartH
        canvas.drawLine(chartLeft, gy, chartRight, gy, gridPaint)
        drawRTLText(canvas, label, chartRight + 4f, gy - 5f, yAxisW.toInt(), paints.legendText, Layout.Alignment.ALIGN_NORMAL)
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

        // ── Takyim badge + typeLabel badge side by side ───────────────────────
        val badgeH     = 14f
        val takyimW    = 52f
        val typeBadgeW = if (row.typeLabel.isBlank()) 0f else 36f
        val gap        = if (row.typeLabel.isBlank()) 0f else 2f
        val totalW     = takyimW + gap + typeBadgeW

        // Position the combined badge centered above the dot, raised enough to clear the dot
        val bLeft = (cx - totalW / 2f).coerceIn(chartLeft, chartRight - totalW)
        val bTop  = (cy - badgeH - 20f).coerceAtLeast(chartTop)

        // Takyim badge
        canvas.drawRoundRect(RectF(bLeft, bTop, bLeft + takyimW, bTop + badgeH), 3f, 3f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = row.takyimColor; style = Paint.Style.FILL })
        drawRTLText(canvas, row.takyimLabel, bLeft + 2f, bTop + 1f, (takyimW - 4f).toInt(), paints.badgeText, Layout.Alignment.ALIGN_CENTER)

        // Type label badge (e.g. "استدراك"), lighter tint of the same colour
        if (row.typeLabel.isNotBlank()) {
            val tLeft = bLeft + takyimW + gap
            canvas.drawRoundRect(RectF(tLeft, bTop, tLeft + typeBadgeW, bTop + badgeH), 3f, 3f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = row.takyimColor; alpha = 110; style = Paint.Style.FILL })
            drawRTLText(canvas, row.typeLabel, tLeft + 2f, bTop + 1f, (typeBadgeW - 4f).toInt(), paints.badgeText, Layout.Alignment.ALIGN_CENTER)
        }

        // ── Full date at the base, staggered to avoid overlap ─────────────────
        // Even index → first row (chartBot + 5), odd → second row (chartBot + 26)
        val dateY  = if (i % 2 == 0) chartBot + 5f else chartBot + 26f
        val labelW = 56
        val lx     = (cx - labelW / 2f).coerceIn(chartLeft, chartRight - labelW)
        drawRTLText(canvas, row.dateLabel, lx, dateY, labelW, paints.dateText, Layout.Alignment.ALIGN_CENTER)
    }

    return chartBot + xAxisH
}

// ── Save ──────────────────────────────────────────────────────────────────────

private fun saveSchemaJpg(context: Context, bitmap: Bitmap, fileName: String): Uri? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        saveSchemaViaMediaStore(context, bitmap, fileName)
    else
        saveSchemaToPublicPictures(context, bitmap, fileName)

private fun saveSchemaViaMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    val resolver   = context.contentResolver
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME,  fileName)
        put(MediaStore.Images.Media.MIME_TYPE,     "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/whatsapp_cards/schema/")
        put(MediaStore.Images.Media.IS_PENDING,    1)
    }
    val uri = resolver.insert(collection, values) ?: return null
    return try {
        resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        uri
    } catch (e: Exception) { resolver.delete(uri, null, null); null }
}

@Suppress("DEPRECATION")
private fun saveSchemaToPublicPictures(context: Context, bitmap: Bitmap, fileName: String): Uri? =
    try {
        val dir  = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "whatsapp_cards/schema").also { it.mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (e: Exception) { null }

// ── Paints ────────────────────────────────────────────────────────────────────

private data class SchemaPaints(
    val titleWhite: TextPaint,
    val subWhite:   TextPaint,
    val legendText: TextPaint,
    val dateText:   TextPaint,
    val badgeText:  TextPaint,
)

private fun buildSchemaPaints() = SchemaPaints(
    titleWhite = TextPaint().apply { textSize = 16f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);   isAntiAlias = true; color = Color.WHITE },
    subWhite   = TextPaint().apply { textSize = 11f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.parseColor("#BBDEFB") },
    legendText = TextPaint().apply { textSize =  7f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.DKGRAY },
    dateText   = TextPaint().apply { textSize =  7f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); isAntiAlias = true; color = Color.parseColor("#757575") },
    badgeText  = TextPaint().apply { textSize =  8f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);   isAntiAlias = true; color = Color.WHITE },
)

// ── Utilities ─────────────────────────────────────────────────────────────────

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
