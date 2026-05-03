package Application5.App.View.DropDownItems.View.But2.generatePdfDocument

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.drawStudentHeader
import Application5.App.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawObservationHistoryTable
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

private const val TAG = "HistoryImage"

// ─────────────────────────────────────────────────────────────────────────────
// Public entry point — returns a content-URI for the generated JPG.
//
// Height is computed by doing a dry-run measure pass on a 1×1 canvas first,
// so the bitmap is exactly as tall as the content with no trailing whitespace.
// ─────────────────────────────────────────────────────────────────────────────

fun generateHistoryImage(
    context: Context,
    cardData: ParentCommunicationCardData_2,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): Uri? {
    return try {
        val scale       = 2          // 2× resolution for sharpness
        val pageWidth   = 420
        val marginLeft  = 30f
        val marginRight = 30f
        val marginTop   = 20f
        val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

        val paints = buildPaints()

        // ── 1. Measure pass: find real content height ─────────────────────────
        val measuredHeight = measureContentHeight(
            pageWidth    = pageWidth,
            marginLeft   = marginLeft,
            marginRight  = marginRight,
            marginTop    = marginTop,
            contentWidth = contentWidth,
            cardData     = cardData,
            paints       = paints,
            viewModel    = viewModel
        )

        val totalHeight = (measuredHeight + 20f).toInt()   // 20px bottom padding

        // ── 2. Render pass: draw onto correctly-sized bitmap ──────────────────
        val bitmap = Bitmap.createBitmap(
            pageWidth * scale,
            totalHeight * scale,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap).apply {
            drawColor(Color.WHITE)
            scale(scale.toFloat(), scale.toFloat())
        }

        var yPos = drawStudentHeader(
            canvas, cardData, marginLeft, marginTop, pageWidth, marginRight,
            contentWidth, paints.bold, paints.border
        )

        drawObservationHistoryTable(
            canvas, cardData, marginLeft, yPos, pageWidth, marginRight,
            contentWidth, paints.bold, paints.normal, paints.small, paints.border,
            aCentralFacade = viewModel
        )

        // ── 3. Save bitmap → JPG → URI ────────────────────────────────────────
        saveHistoryBitmap(context, bitmap, cardData.studentInfo.keyID)

    } catch (e: Exception) {
        Log.e(TAG, "❌ Failed to generate history image", e)
        null
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dry-run measure: draw onto a dummy 1×1 canvas just to get the final Y
// ─────────────────────────────────────────────────────────────────────────────

private fun measureContentHeight(
    pageWidth: Int,
    marginLeft: Float,
    marginRight: Float,
    marginTop: Float,
    contentWidth: Int,
    cardData: ParentCommunicationCardData_2,
    paints: HistoryPaints,
    viewModel: A_ViewModel_SeparatedAppsCodingPattern
): Float {
    // Use a real bitmap so Canvas doesn't throw on draw calls
    val dummy  = Bitmap.createBitmap(pageWidth, 4000, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(dummy)

    var yPos = drawStudentHeader(
        canvas, cardData, marginLeft, marginTop, pageWidth, marginRight,
        contentWidth, paints.bold, paints.border
    )

    yPos = drawObservationHistoryTable(
        canvas, cardData, marginLeft, yPos, pageWidth, marginRight,
        contentWidth, paints.bold, paints.normal, paints.small, paints.border,
        aCentralFacade = viewModel
    )

    dummy.recycle()
    return yPos
}

// ─────────────────────────────────────────────────────────────────────────────
// Save helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun saveHistoryBitmap(
    context: Context,
    bitmap: Bitmap,
    keyID: String
): Uri? {
    deleteSameDayHistoryImages(context, keyID)
    val fileName = "history_${keyID.trim()}_${System.currentTimeMillis()}.jpg"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        saveViaMediaStore(context, bitmap, fileName)
    else
        saveToPublicPictures(context, bitmap, fileName)
}

/** Deletes all history images for [keyID] that were saved today. */
private fun deleteSameDayHistoryImages(context: Context, keyID: String) {
    val relPath = "${Environment.DIRECTORY_PICTURES}/whatsapp_cards/history/"
    val prefix  = "history_${keyID.trim()}_"
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
                    Log.d(TAG, "🗑 deleted old history image: $uri")
                }
            }
    } else {
        @Suppress("DEPRECATION")
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "whatsapp_cards/history"
        )
        val todayStartMs = todayStartSec * 1000L
        dir.listFiles { f -> f.name.startsWith(prefix) && f.lastModified() >= todayStartMs }
            ?.forEach { f -> if (f.delete()) Log.d(TAG, "🗑 deleted old history file: ${f.name}") }
    }
}

private fun saveViaMediaStore(
    context: Context,
    bitmap: Bitmap,
    fileName: String
): Uri? {
    val resolver   = context.contentResolver
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME,  fileName)
        put(MediaStore.Images.Media.MIME_TYPE,     "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH,
            "${Environment.DIRECTORY_PICTURES}/whatsapp_cards/history/")
        put(MediaStore.Images.Media.IS_PENDING,    1)
    }

    val uri = resolver.insert(collection, values) ?: run {
        Log.e(TAG, "❌ MediaStore insert returned null")
        return null
    }

    return try {
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        Log.d(TAG, "✅ Saved via MediaStore: $fileName")
        uri
    } catch (e: Exception) {
        Log.e(TAG, "❌ MediaStore write failed", e)
        resolver.delete(uri, null, null)
        null
    }
}

@Suppress("DEPRECATION")
private fun saveToPublicPictures(
    context: Context,
    bitmap: Bitmap,
    fileName: String
): Uri? {
    return try {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "whatsapp_cards/history"
        ).also { it.mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (e: Exception) {
        Log.e(TAG, "❌ Public Pictures write failed", e)
        null
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Paint factory — mirrors the sizes used in generatePdfDocument
// ─────────────────────────────────────────────────────────────────────────────

private data class HistoryPaints(
    val bold:   TextPaint,
    val normal: TextPaint,
    val small:  TextPaint,
    val border: Paint
)

private fun buildPaints() = HistoryPaints(
    bold = TextPaint().apply {
        textSize    = 14f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
        color       = Color.BLACK
    },
    normal = TextPaint().apply {
        textSize    = 11f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
        color       = Color.BLACK
    },
    small = TextPaint().apply {
        textSize    = 9f
        typeface    = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
        color       = Color.BLACK
    },
    border = Paint().apply {
        color       = Color.BLACK
        style       = Paint.Style.STROKE
        strokeWidth = 1f
    }
)
