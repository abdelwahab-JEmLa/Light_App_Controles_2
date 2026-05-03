package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.lazy.LazyListState
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

private class DrawnHolder { var value: Boolean = false }

class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
    val hasBeenDrawn: () -> Boolean,
)

@Composable
fun rememberCapturableLayer(@DrawableRes backgroundRes: Int? = null): CapturableLayerState {
    val ctx = LocalContext.current
    val gLayer = rememberGraphicsLayer()
    val drawnHolder = remember { DrawnHolder() }

    val mod = Modifier.drawWithContent {
        gLayer.record { this@drawWithContent.drawContent() }
        drawnHolder.value = true
        drawContent()
    }

    return CapturableLayerState(
        modifier = mod,
        hasBeenDrawn = { drawnHolder.value },
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

private data class CaptureEntry(
    val index: Int,
    val hasBeenDrawn: () -> Boolean,
    val capture: suspend () -> ImageBitmap,
)

class MultiCaptureController {
    private val entries = linkedMapOf<String, CaptureEntry>()

    fun register(key: String, index: Int, hasBeenDrawn: () -> Boolean, capture: suspend () -> ImageBitmap) {
        entries[key] = CaptureEntry(index, hasBeenDrawn, capture)
    }

    fun unregister(key: String) { entries.remove(key) }

    fun registeredKeys(): List<String> = entries.keys.toList()

    suspend fun captureAll(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().map { (k, e) -> k to e.capture() }

    suspend fun captureAllVisible(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList()
            .filter { (_, e) -> e.hasBeenDrawn() }
            .map { (k, e) -> k to e.capture() }

    suspend fun captureAllWithScroll(
        state: LazyListState,
        totalItemCount: Int,
        scrollSettleMs: Long = 300,
        restoreIndex: Int = 0,
        orderedKeys: List<String>? = null,
    ): List<Pair<String, ImageBitmap>> {
        data class R(val key: String, val listIndex: Int, val bmp: ImageBitmap)
        val results = mutableListOf<R>()
        val capturedKeys = mutableSetOf<String>()

        for (index in 0 until totalItemCount) {
            state.scrollToItem(index)
            delay(scrollSettleMs)
            for ((k, e) in entries.entries.toList()) {
                if (k !in capturedKeys && e.hasBeenDrawn()) {
                    results.add(R(k, e.index, e.capture()))
                    capturedKeys.add(k)
                }
            }
        }

        val sorted = if (orderedKeys != null) {
            results.sortedBy { r ->
                val pos = orderedKeys.indexOf(r.key)
                if (pos >= 0) pos else Int.MAX_VALUE
            }
        } else {
            results.sortedBy { it.listIndex }
        }.map { it.key to it.bmp }

        state.scrollToItem(restoreIndex)
        return sorted
    }

    suspend fun captureLastN(n: Int): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().takeLast(n).map { (k, e) -> k to e.capture() }
}

@Composable
fun rememberMultiCaptureController() = remember { MultiCaptureController() }

fun saveAllToMediaStore(
    bitmaps: List<Pair<Bitmap, String>>,
    context: Context,
    clientKeyID: String,
): List<android.net.Uri> {
    if (bitmaps.isEmpty()) return emptyList()

    val savedUris = mutableListOf<android.net.Uri>()
    val safeKey = clientKeyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
    val folderPath = "Download/Image_Compose_Screen/$safeKey"
    val resolver = context.contentResolver
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        resolver.delete(collection, "${MediaStore.Images.Media.RELATIVE_PATH} = ?", arrayOf("$folderPath/"))
    }

    val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSLESS
    else
        @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP

    bitmaps.forEach { (bmp, lbl) ->
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${lbl}.webp")
            put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "$folderPath/")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = resolver.insert(collection, cv) ?: return@forEach
        resolver.openOutputStream(uri)?.use { out -> bmp.compress(fmt, 100, out) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
        }
        savedUris.add(uri)
    }
    return savedUris
}
