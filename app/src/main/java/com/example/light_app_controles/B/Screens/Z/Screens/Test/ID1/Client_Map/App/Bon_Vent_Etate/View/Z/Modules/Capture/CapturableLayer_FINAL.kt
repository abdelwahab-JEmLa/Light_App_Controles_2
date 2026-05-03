package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
)

@Composable
fun rememberCapturableLayer(
    @DrawableRes backgroundRes: Int? = null,
): CapturableLayerState {
    val ctx = LocalContext.current
    val gLayer = rememberGraphicsLayer()

    val mod = Modifier.drawWithContent {
        gLayer.record { this@drawWithContent.drawContent() }
        drawContent()
    }

    return CapturableLayerState(
        modifier = mod,
        capture = {
            delay(100)
            val hw = gLayer.toImageBitmap()
            val sw = hw.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

            if (backgroundRes == null) return@CapturableLayerState sw.asImageBitmap()

            val out = Bitmap.createBitmap(sw.width, sw.height, Bitmap.Config.ARGB_8888)
            val cvs = Canvas(out)
            AppCompatResources.getDrawable(ctx, backgroundRes)?.let { drw ->
                drw.setBounds(0, 0, sw.width, sw.height)
                drw.draw(cvs)
            }
            cvs.drawBitmap(sw, 0f, 0f, null)
            out.asImageBitmap()
        },
    )
}

class MultiCaptureController {
    private val entries = linkedMapOf<String, suspend () -> ImageBitmap>()

    fun register(key: String, capture: suspend () -> ImageBitmap) {
        entries[key] = capture
    }

    fun unregister(key: String) {
        entries.remove(key)
    }

    /** Returns a snapshot of all currently-registered keys (for diagnostic logging). */
    fun registeredKeys(): List<String> = entries.keys.toList()

    suspend fun captureAll(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().map { (k, cap) -> k to cap() }

    /** Capture only the [n] most-recently-registered entries (last N items in the list). */
    suspend fun captureLastN(n: Int): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().takeLast(n).map { (k, cap) -> k to cap() }
}

@Composable
fun rememberMultiCaptureController() = remember { MultiCaptureController() }

fun saveAllToMediaStore(
    bitmaps: List<Pair<Bitmap, String>>,
    context: Context,
    clientKeyID: String,
) {
    if (bitmaps.isEmpty()) return

    val safeKey = clientKeyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
    val folderPath = "Download/Image_Compose_Screen/$safeKey"

    val resolver = context.contentResolver
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        resolver.delete(
            collection,
            "${MediaStore.Images.Media.RELATIVE_PATH} = ?",
            arrayOf("$folderPath/"),
        )
    }

    val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSLESS
    else
        @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP

    bitmaps.forEach { (bmp, lbl) ->
        val fname = "image_${lbl}.webp"
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fname)
            put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "$folderPath/")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(collection, cv) ?: return@forEach
        resolver.openOutputStream(uri)?.use { out -> bmp.compress(fmt, 100, out) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }, null, null)
        }
    }
}
