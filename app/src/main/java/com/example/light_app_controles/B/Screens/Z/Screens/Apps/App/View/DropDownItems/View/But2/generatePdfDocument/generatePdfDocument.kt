package Application5.App.View.DropDownItems.View.But2.generatePdfDocument

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawHifdTable
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawIstedrakMokarrarTable
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawObservationHistoryTable
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawFooterSection
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawHeaderSection
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawStudentHeader
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.forEachIndexed


fun generatePdfDocument(
    context: Context,
    cardsData: List<ParentCommunicationCardData_2>,
    compactHeightMode: Boolean = true,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.pdf")

        // A5 Portrait dimensions in points
        val pageWidth = 420
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        cardsData.forEachIndexed { index, cardData ->
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Margins - optimized
            val marginLeft = 30f
            val marginRight = 30f
            val marginTop = 35f
            val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

            // TextPaint configurations - REDUCED SIZES
            val paintArabic = TextPaint().apply {
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintArabicBold = TextPaint().apply {
                textSize = 17f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintArabicMediumBold = TextPaint().apply {
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintHeaderLarge = TextPaint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintVerySmall = TextPaint().apply {
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = Color.BLACK
            }

            // Paint for borders
            val paintBorder = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            var yPosition = drawHeaderSection(
                canvas = canvas,
                context = context,              // ← NEW
                marginLeft = marginLeft,
                marginTop = marginTop,
                pageWidth = pageWidth,
                marginRight = marginRight,
                contentWidth = contentWidth,
                paintHeaderLarge = paintHeaderLarge,
                paintSmall = paintSmall,
                paintVerySmall = paintVerySmall,
                compactMode = compactHeightMode
            )

            yPosition = drawStudentHeader(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicBold, paintBorder
            )

            // Draw Hifd table with repository access
            yPosition = drawHifdTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder,
                aCentralFacade = viewModel
            )

            // Draw Istedrak Mokarrar table (المقرر لاستدراك القديم)
            yPosition = drawIstedrakMokarrarTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder
            )

            yPosition = drawObservationHistoryTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintSmall, paintBorder,
                aCentralFacade = viewModel
            )

            drawFooterSection(
                canvas, cardData, marginLeft, pageHeight, pageWidth, marginRight, contentWidth,
                paintSmall, paintVerySmall, paintBorder,
                compactMode = compactHeightMode
            )

            pdfDocument.finishPage(page)
        }

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("ParentCommPdf", "✅ PDF créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("ParentCommPdf", "❌ Erreur lors de la création du PDF", e)
        null
    }
}
