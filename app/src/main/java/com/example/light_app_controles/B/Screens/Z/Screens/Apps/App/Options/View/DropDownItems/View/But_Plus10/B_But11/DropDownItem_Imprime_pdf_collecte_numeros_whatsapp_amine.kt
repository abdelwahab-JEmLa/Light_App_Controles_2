package Application5.App.View.DropDownItems.View.But11

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.PdfSaverUtility_Tahfid
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DropDownItem_Imprime_pdf_collecte_numeros_whatsapp_amine(
    aCentralFacade: A_ViewModel_SeparatedAppsCodingPattern,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val activeOusstad = aCentralFacade.activeCentralValues.active_Ousstad_Tahfid

    fun generate() {
        isLoading = true
        scope.launch {
            try {
                val teacherName = activeOusstad?.nom_arab ?: "غير محدد"

                val barcodeResName = when (activeOusstad) {
                    Ousstad_Tahfid.Amine_Madrassa -> "whats_app_code_bar_amine"
                    Ousstad_Tahfid.Abdelwahab_Osstad -> "whats_app_code_bar_abdelwahab"
                    else -> "whats_app_code_bar_abdelwahab"
                }

                val pdfFile = withContext(Dispatchers.IO) {
                    generateWhatsAppCollectAminePdf20Pages(context, teacherName, barcodeResName)
                }

                val saveResult = withContext(Dispatchers.IO) {
                    if (pdfFile != null && pdfFile.exists()) {
                        val fileName = "فيشة_واتساب_${if (barcodeResName.contains("amine")) "أمين" else "عبدالوهاب"}_فردية_${
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        }.pdf"
                        PdfSaverUtility_Tahfid.savePdf(context, pdfFile, fileName, "Tahfide_Quran")
                    } else {
                        Result.failure(Exception("فشل إنشاء الملف"))
                    }
                }

                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = {
                            if (pdfFile != null) openPdfWithViewer(context, pdfFile)
                            Toast.makeText(context, "✅ تم إنشاء ملف PDF (20 صفحة فردية)", Toast.LENGTH_LONG).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "❌ خطأ في الحفظ: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("CollecteAmine", "❌ ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ خطأ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.ContactPhone, null, tint = MaterialTheme.colorScheme.secondary)
            },
            text = {
                Text(
                    text = if (isLoading) "جاري الإنشاء…" else "فيشة واتساب الأستاذ (20 صفحة فردية)",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { if (!isLoading) generate() },
            enabled = !isLoading
        )
    }
}

private fun generateWhatsAppCollectAminePdf20Pages(
    context: Context,
    teacherName: String,
    barcodeResName: String
): File? {
    return try {
        val pdfFile = File(context.cacheDir, "whatsapp_collect_amine_20pages_form_${System.currentTimeMillis()}.pdf")

        // A5 Portrait dimensions in points
        val pageWidth = 420
        val pageHeight = 595
        val marginLeft = 30f
        val marginRight = 30f
        val marginTop = 35f
        val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

        val pdfDocument = PdfDocument()

        val paintBold = TextPaint().apply {
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = Color.BLACK
        }
        val paintMedium = TextPaint().apply {
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = Color.BLACK
        }
        val paintVerySmall = TextPaint().apply {
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = Color.DKGRAY
        }
        val paintRed = TextPaint().apply {
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = Color.RED
        }
        val paintBorder = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        val paintLineDashed = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
        }

        val logoBitmap: Bitmap? = runCatching {
            val resId = context.resources.getIdentifier("ecole_logo1", "drawable", context.packageName)
            if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
        }.getOrNull()

        val barCodeBitmap: Bitmap? = runCatching {
            val resId = context.resources.getIdentifier(barcodeResName, "drawable", context.packageName)
            if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
        }.getOrNull()

        // Generate 20 pages
        for (pageIndex in 0 until 20) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var y = marginTop

            // ── Logo ─────────────────────────────────────────────────────────────
            if (logoBitmap != null) {
                val logoWidth = contentWidth * 0.4f
                val logoHeight = logoWidth * logoBitmap.height / logoBitmap.width
                val logoLeft = marginLeft + (contentWidth - logoWidth) / 2f
                canvas.drawBitmap(logoBitmap, null, RectF(logoLeft, y, logoLeft + logoWidth, y + logoHeight), null)
                y += logoHeight + 6f
            }

            // ── Header Title & Teacher Name ───────────────────────────────────────
            drawRTL(canvas, "قسم الأستاذ: $teacherName", marginLeft, y, contentWidth, paintBold)
            y += 20f

            // ── Poetry ────────────────────────────────────────────────────────────
            val poetry = "وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم\nقالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"
            drawRTL(canvas, poetry, marginLeft, y, contentWidth, paintVerySmall)
            y += 24f

            canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, paintBorder)
            y += 8f

            // ── Barcode Image (whats_app_code_bar_...png) ─────────────────────────
            if (barCodeBitmap != null) {
                val qrW = contentWidth * 0.4f
                val qrH = qrW * barCodeBitmap.height / barCodeBitmap.width
                val qrLeft = marginLeft + (contentWidth - qrW) / 2f
                val qrTop = y
                canvas.drawBitmap(barCodeBitmap, null, RectF(qrLeft, qrTop, qrLeft + qrW, qrTop + qrH), null)
                y += qrH + 6f
                drawRTL(canvas, "يرجى مسح الرمز أعلاه للانضمام لمجموعة الواتساب الخاصة بالقسم", marginLeft, y, contentWidth, paintVerySmall)
                y += 14f
            }

            // Red note text
            drawRTL(
                canvas,
                "إذا لم يكن بالإمكان المسح، يرجى كتابة رقم الهاتف أسفله وإرجاعها مع ابنكم",
                marginLeft, y, contentWidth, paintRed
            )
            y += 22f

            canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, paintBorder)
            y += 15f

            // ── The Table (1 Column, 2 Rows: Title & Write Place) ─────────────────
            val tableWidth = 260f
            val xStart = marginLeft + (contentWidth - tableWidth) / 2f
            val rowHeightVal = 30f

            // Row 1 (Title Background and Border)
            val bgTitlePaint = Paint().apply {
                color = 0xFFF0F0F0.toInt() // Light gray background
                style = Paint.Style.FILL
            }
            canvas.drawRect(xStart, y, xStart + tableWidth, y + rowHeightVal, bgTitlePaint)
            canvas.drawRect(xStart, y, xStart + tableWidth, y + rowHeightVal, paintBorder)
            drawRTL(canvas, "رقم الهاتف (واتساب):", xStart, y + 8f, tableWidth.toInt(), paintMedium, Layout.Alignment.ALIGN_CENTER)

            y += rowHeightVal

            // Row 2 (Write Place Border and Dotted Line)
            canvas.drawRect(xStart, y, xStart + tableWidth, y + rowHeightVal, paintBorder)
            val lineY = y + rowHeightVal - 8f
            canvas.drawLine(xStart + 15f, lineY, xStart + tableWidth - 15f, lineY, paintLineDashed)

            y += rowHeightVal + 20f

            drawRTL(
                canvas,
                "جزاكم الله خيراً على تعاونكم",
                marginLeft, y, contentWidth, paintVerySmall, Layout.Alignment.ALIGN_CENTER
            )

            pdfDocument.finishPage(page)
        }

        FileOutputStream(pdfFile).use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        pdfFile
    } catch (e: Exception) {
        Log.e("CollecteAmine", "❌ PDF pages form gen error", e)
        null
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
        Log.e("CollecteAmine", "❌ خطأ في فتح PDF", e)
        Toast.makeText(
            context,
            "❌ خطأ في فتح الملف: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun drawRTL(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    width: Int,
    paint: TextPaint,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
) {
    val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
        .setAlignment(alignment)
        .setTextDirection(TextDirectionHeuristics.RTL)
        .setIncludePad(false)
        .build()
    canvas.save()
    canvas.translate(x, y)
    layout.draw(canvas)
    canvas.restore()
}
